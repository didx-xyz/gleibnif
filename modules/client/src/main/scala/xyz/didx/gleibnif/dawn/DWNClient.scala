package xyz.didx.gleibnif.dawn

final case class DWNClient(did: String):
  def getDWNEndpoint(): String = s"https://dawn.mn8.dev/did/$did"
