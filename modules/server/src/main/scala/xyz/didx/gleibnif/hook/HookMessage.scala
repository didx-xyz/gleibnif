package xyz.didx.gleibnif.hook

import com.google.protobuf.Message
import xyz.didx.gleibnif.DWNMessage

type DID = String
final case class HookMessage(
    interface: String,
    kind: String,
    action: String,
    name: String,
    owner: DID
) extends DWNMessage:
  enum HookAction:
    case RegisterHookRequest
    case RegisterHookResponse
    case UnregisterHookRequest
    case UnregisterHookResponse
    case HookRequest
    case HookResponse
