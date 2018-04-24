package pl.grzeslowski.colibra.chat

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.NewMessageListener
import pl.grzeslowski.colibra.server.ServerMessage
import pl.grzeslowski.colibra.server.session.NameRepository
import pl.grzeslowski.colibra.server.session.Uuid

@Component
class ClientIntroduceListener(private val nameRepository: NameRepository) : NewMessageListener {
    private val log = LoggerFactory.getLogger(ClientIntroduceListener::class.java)
    private val clientHiPrefix = "HI, I'M "

    override fun onNewMessage(clientMessage: ClientMessage, channel: ColibraChannel) =
            if (clientMessage.message.startsWith(clientHiPrefix)) {
                val name = clientMessage.message.substring(clientHiPrefix.length)
                log.debug("Got msg from client $name")
                channel.write(ServerMessage("HI $name"))
                nameRepository.setName(channel.getSession(), Uuid(name))
                true
            } else {
                false
            }
}