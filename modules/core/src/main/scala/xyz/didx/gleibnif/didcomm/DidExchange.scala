package xyz.didx.gleibnif.didcomm

import cats.effect.IO

class DidExchange {
  def runDidExchange(inviter: Party, invitee: Party): IO[Either[Error, State]] =
    ???
}
