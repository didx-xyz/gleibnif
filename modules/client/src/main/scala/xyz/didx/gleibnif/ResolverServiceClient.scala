package xyz.didx.gleibnif

import cats.effect.IO
import io.circe._
import io.circe.parser._
import sttp.client3._
import sttp.client3.circe._
import xyz.didx.gleibnif.didcomm.DIDCodec
import xyz.didx.gleibnif.didcomm.DIDCodec.decodeDIDDoc
import xyz.didx.gleibnif.didcomm.DIDDoc

case class ResolverServiceClient(
    resolverURI: String,
    apiKey: String,
    contentType: String = "application/did+ld+json"
):

  def resolve(did: String): IO[Either[Exception, DIDDoc]] =
    val request =
      basicRequest
        .get(uri"$resolverURI$did")
        .header("Accept", contentType)
        .header("Authorization", s"Bearer ${apiKey}")
        .response(asJson[DIDDoc])
    val backend  = HttpClientSyncBackend()
    val response = request.send(backend)
    IO.delay(response.body)
