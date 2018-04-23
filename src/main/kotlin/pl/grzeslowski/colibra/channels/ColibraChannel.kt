package pl.grzeslowski.colibra.channels

import pl.grzeslowski.colibra.server.ServerMessage

interface ColibraChannel {
    fun write(message: ServerMessage)
    fun writeAndFlush(message: ServerMessage)
    fun flush()
}