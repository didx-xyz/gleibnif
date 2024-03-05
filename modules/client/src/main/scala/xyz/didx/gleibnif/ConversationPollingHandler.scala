package xyz.didx.gleibnif

import cats.data.EitherT
import cats.effect.IO
import cats.effect.kernel.Resource
import cats.syntax.traverse._
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import sttp.client3.SttpBackend
import xyz.didx.gleibnif.config.ConfigReaders._
import xyz.didx.gleibnif.connection.RedisStorage
import xyz.didx.gleibnif.didcomm.DIDDoc
import xyz.didx.gleibnif.didcomm.Service
import xyz.didx.gleibnif.didcomm.ServiceEndpointNodes
import xyz.didx.gleibnif.didops.RegistryRequest
import xyz.didx.gleibnif.didops.RegistryResponseCodec.encodeRegistryRequest
import xyz.didx.gleibnif.didops.RegistryServiceClient
import xyz.didx.gleibnif.logging.LogWriter.err
import xyz.didx.gleibnif.logging.LogWriter.info
import xyz.didx.gleibnif.logging.LogWriter.logNonEmptyList
import xyz.didx.gleibnif.openai.OpenAIAgent
import xyz.didx.gleibnif.passkit.PasskitAgent
import xyz.didx.gleibnif.signal.SignalBot
import xyz.didx.gleibnif.signal._
import xyz.didx.gleibnif.signal.messages.Member
import xyz.didx.gleibnif.signal.messages.SignalMessage
import xyz.didx.gleibnif.signal.messages.SignalMessageCodec.memberDecoder
import xyz.didx.gleibnif.signal.messages.SignalSendMessage
import xyz.didx.gleibnif.signal.messages.SignalSimpleMessage

import java.net.URI

class ConversationPollingHandler(using logger: Logger[IO]):
  val appConf      = getConf(using logger)
  val registryConf = getRegistryConf(using logger)
  val signalConf   = getSignalConf(using logger)
  val registryClient = RegistryServiceClient(
    registryConf.registrarUrl.toString(),
    registryConf.apiKey
  )
  val redisStorage: Resource[cats.effect.IO, RedisStorage] =
    RedisStorage.create(appConf.redisUrl.toString())

  def converse(
      backend: SttpBackend[cats.effect.IO, Any]
  ): IO[Either[Exception, List[String]]] =

    // def callServices(backend:  SttpBackend[cats.effect.IO, Any],redisStorage: Resource[cats.effect.IO, RedisStorage]): IO[Either[Exception, List[String]]] =
    val signalBot   = SignalBot(backend)
    val openAIAgent = OpenAIAgent(backend)

    val message = EitherT {
      signalBot.receive().flatTap(m => logNonEmptyList[SignalSimpleMessage](m))
    }
    def extractEmail(text: String): Option[String] =
      val emailRegex =
        "\\b[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\\b".r
      emailRegex.findFirstIn(text)

    /*    def handleConversation(m: SignalSimpleMessage) = {
        import xyz.didx.gleibnif.dawn.Aspects.*
        redisStorage.use {redis =>
          redis.getDidByPhoneNumber(m.phone) match
            case Some(did:DID) =>
              val conversation = ConversationAgent(did).extractIntent( m.text)
              redis.writeToRedis(did.toString, Action, conversation)
              signalBot.send(SignalSendMessage(List[String](), s"Conversation stored for $did", signalConf.signalPhone, List(m.phone)))
            case None =>
              signalBot.send(SignalSendMessage(List[String](), s"Conversation not found for $m", signalConf.signalPhone, List(m.phone)))

        }




    } */
    def processKeywords(
        k: SignalSimpleMessage
    ): EitherT[IO, Exception, String] = {
      EitherT(k match
        case m: SignalSimpleMessage if m.text.toLowerCase().contains("https://maps.google.com") =>
          ??? // openAIAgent.keywords(m.text.split("\\|")(2))

        case _ =>
          signalBot.send(
            SignalSendMessage(
              List[String](),
              s"I have extracted the following keywords: ${k.keywords.mkString(",")}",
              signalConf.signalPhone,
              List(k.phone)
            )
          )
      )
    }

    (for
      mt <- message.map(
        _.filter(_.text.length > 0)
          .partition(_.text.toLowerCase().startsWith("@admin|add"))
      )

      adminMsg <- mt._1
        .map(m =>
          val member =
            decode[Member](m.text.split("\\|")(2)).getOrElse(Member("", ""))
          val doc = DIDDoc(
            did = "",
            controller = Some(s"${appConf.dawnControllerDID}"),
            alsoKnownAs = Some(Set(s"tel:${member.number}.};name=${member.name}")),
            services = Some(
              Set(
                Service(
                  id = new URI("#dwn"),
                  `type` = Set("DecentralizedWebNode"),
                  serviceEndpoint = Set(
                    ServiceEndpointNodes(
                      nodes = appConf.dawnServiceUrls
                    )
                  )
                )
              )
            )
          )
          val reg      = RegistryRequest(doc)
          val document = reg.asJson.spaces2
          for
            did <- EitherT( // (backend.use { b =>
              registryClient
                .createDID(registryConf.didMethod, document, backend)
            )
            member <- EitherT.fromEither(decode[Member](m.text.split("\\|")(2)))
            pass   <- PasskitAgent(member.name, did, appConf.dawnUrl).signPass()
            r <- EitherT( // backend.use { b =>
              signalBot.send(
                SignalSendMessage(
                  List[String](
                    s"data:application/vnd.apple.pkpass;filename=did.pkpass;base64,${pass}"
                  ),
                  s"${member.name}, ${appConf.dawnWelcomeMessage}",
                  s"${signalConf.signalPhone}",
                  List[String](member.number)
                )
              )
            )
            _ <- EitherT.liftF(
              logger.info(s"sent badge with $did to : ${member.name} ")
            )
          // })
          yield r
        )
        .sequence

      keywords: List[SignalSimpleMessage] <- mt._2
        .map((m: SignalSimpleMessage) => openAIAgent.extractKeywords(m))
        .sequence
      s: List[String] <-
        keywords.traverse(processKeywords)
    yield s).value
