package pl.grzeslowski.colibra.server

data class ClientMessage(val message: String)

data class ServerMessage(val message: String) {
    constructor(message: Any) : this(message.toString())
}