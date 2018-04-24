package pl.grzeslowski.colibra.server.session

import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

interface NameRepository {
    fun setName(session: Session, name: Uuid)

    fun getName(session: Session): Uuid
}

@Repository
private class InMemoryNameRepository : NameRepository {
    private val map = ConcurrentHashMap<Session, Uuid>()

    override fun setName(session: Session, name: Uuid) {
        map[session] = name
    }

    override fun getName(session: Session) =
            map[session] ?: throw RuntimeException("There is no name for session $session!")
}