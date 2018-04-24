package pl.grzeslowski.colibra.server

import pl.grzeslowski.colibra.server.session.Session

interface ColibraChannel : AutoCloseable {
    fun getSession() : Session

    fun write(serverMessage: ServerMessage)
}