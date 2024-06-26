package xyz.didx.gleibnif.signal

import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig._
import pureconfig.generic.derivation.default._
import sttp.client3._
import sttp.client3.circe._
import sttp.model._
import xyz.didx.gleibnif.signal.messages.SignalMessage
import xyz.didx.gleibnif.signal.messages.SignalMessageCodec.signalMessageDecoder
import xyz.didx.gleibnif.signal.messages.SignalMessageCodec.signalSendMessage
import xyz.didx.gleibnif.signal.messages.SignalSendMessage
import xyz.didx.gleibnif.signal.messages.SignalSimpleMessage

case class SignalConfig(
    signalUrl: String,
    signalUser: String,
    signalPhone: String,
    signalTimeout: Int = 5
) derives ConfigReader:
  override def toString: String =
    s"SignalConfig(url: $signalUrl, user: $signalUser, phone: $signalPhone)"

case class SignalBot(backend: SttpBackend[IO, Any]):
  type ErrorOr[A] = EitherT[IO, Exception, A]
  def getConf(): SignalConfig =
    ConfigSource.default.at("signal-conf").load[SignalConfig] match
      case Left(error) =>
        SignalConfig("", "", "")
      case Right(conf) => conf

  val signalConf = getConf()

  def init(): Unit = ()

  def register(voiceMode: Boolean): IO[Either[Exception, String]] =
    val request = basicRequest
      .contentType("application/json")
      .body(s"""{"use_voice": $voiceMode}""")
      .post(uri"${signalConf.signalUrl}/register/${signalConf.signalPhone}")

    val response = request.send(backend)

    response.map(c =>
      c.code match
        case s: StatusCode if s.isSuccess =>
          Right(s"Signalbot register: $s")
        case s: StatusCode =>
          Left(new Exception(s"Signalbot register: $s"))
    )

  def verify(pin: String): IO[Either[Exception, String]] =
    val request = basicRequest
      .contentType("application/json")
      .body(s"""{"pin": $pin""")
      .post(uri"${signalConf.signalUrl}/verify/${signalConf.signalPhone}")
    val response = request.send(backend)
    response.map(c =>
      c.code match
        case s: StatusCode if s.isSuccess =>
          Right(s"Signalbot verify: $s")
        case s: StatusCode =>
          Left(new Exception(s"Signalbot verify: $s"))
    )

  def send(message: SignalSendMessage): IO[Either[Exception, String]] =
    val request = basicRequest
      .contentType("application/json")
      .body(message.asJson.noSpaces)
      .post(uri"${signalConf.signalUrl}/v2/send")

    val response = request.send(backend)
    response.map(c =>
      c.code match
        case s: StatusCode if s.isSuccess =>
          Right(s"Signalbot Send: $s - Message sent")
        case s: StatusCode =>
          Left(new Exception(s"Signalbot Send: $s"))
    )

  def receive(): IO[
    Either[ResponseException[String, Error], List[SignalSimpleMessage]]
  ] =
    val request = basicRequest
      .contentType("application/json")
      .response(asJson[List[SignalMessage]])
      .get(
        uri"${signalConf.signalUrl}/v1/receive/${signalConf.signalPhone}?timeout=${signalConf.signalTimeout}"
      )

    val response = request.send(backend)
    response map (r =>
      r.body match
        case Left(error) => Left(error)
        case Right(messages) =>
          messages
            .map(msg =>
              msg.envelope.dataMessage.map(dm =>
                Right(
                  SignalSimpleMessage(
                    msg.envelope.sourceNumber,
                    msg.envelope.sourceName,
                    dm.message
                  )
                )
              )
            )
            .flatten
            .sequence
    )
