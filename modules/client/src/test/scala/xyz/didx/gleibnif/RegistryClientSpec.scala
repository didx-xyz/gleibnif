package xyz.didx.gleibnif

import cats.data.EitherT
import cats.effect.IO
import cats.effect.unsafe.implicits._
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import munit.Clue.generate
import munit.FunSuite
import sttp.client3.HttpURLConnectionBackend
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import xyz.didx.gleibnif.didcomm.DIDCodec
import xyz.didx.gleibnif.didcomm.DIDCodec.decodeDIDDoc
import xyz.didx.gleibnif.didcomm.DIDCodec.encodeDIDDoc
import xyz.didx.gleibnif.didcomm.DIDDoc
import xyz.didx.gleibnif.didcomm.Service
import xyz.didx.gleibnif.didcomm.ServiceEndpointNodes
import xyz.didx.gleibnif.didops.RegistryRequest
import xyz.didx.gleibnif.didops.RegistryResponseCodec.encodeRegistryRequest
import xyz.didx.gleibnif.didops.RegistryServiceClient
import xyz.didx.gleibnif.openai.OpenAIAgent
import xyz.didx.gleibnif.signal.*
import xyz.didx.gleibnif.signal.messages.SignalMessageCodec.memberDecoder
import xyz.didx.gleibnif.signal.messages.SignalSimpleMessage

import java.net.URI

class RegistryClientSpec extends FunSuite {
  val baseURL = "https://api.godiddy.com/0.1.0/universal-registrar/"
  val apiKey  = "c2850992-32fd-4ccf-9352-77aa329eef13"
  val document1 = """{
  "didDocument": {
    "@context": [
      "https//www.w3.org/ns/did/v1"
    ],
    "controller": "did:example:bcehfew7h32f32h7af3",

    "service": [
      {
        "id": "#dwn",
        "type": "DecentralizedWebNode", 
        "serviceEndpoint": {
          "nodes": [
            "https://dwn.example.com",
            "https://example.org/dwn"
          ]
        }
      }
    ],
    "verificationMethod": [

    ]
  },
  "options": {
    "network": "danube"
  },
  "secret": {
  }
}""".stripMargin
  val client  = RegistryServiceClient(baseURL, apiKey)
  val backend = AsyncHttpClientCatsBackend.resource[IO]()

  test("RegistryClient should be able to create a DID") {
    val doc = DIDDoc(
      "",
      Some("did:example:123456789"),
      Some(Set("tel:12345k;name=Ian de Beer")),
      None,
      None,
      None,
      None,
      None,
      None,
      Some(
        Set(
          Service(
            id = new URI("#dwn"),
            `type` = Set("DecentralizedWebNode"),
            serviceEndpoint = Set(
              ServiceEndpointNodes(
                nodes = Set(
                  new URI("https://dwn.example.com"),
                  new URI("https://example.org/dwn")
                )
              )
            )
          )
        )
      )
    )
    val reg      = RegistryRequest(doc)
    val document = reg.asJson.spaces2
    val dd: IO[String] = EitherT(backend.use { b =>
      client.createDID("indy", document, b)
    }).value.flatMap {
      case Left(e)  => IO.raiseError(e)
      case Right(r) => IO.pure(r)
    }

    val did: String = dd.unsafeRunSync()

    println(did)

    assert(did.startsWith("did:"))

  }

}
