package pl.grzeslowski.colibra.chat

import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.server.*
import pl.grzeslowski.colibra.server.session.NameRepository

@Component
class ByeListener(private val nameRepository: NameRepository) : NewMessageListener, TimeoutListener {
    private val byeMessage = "BYE MATE!"

    override fun onNewMessage(clientMessage: ClientMessage, channel: ColibraChannel) =
            when (clientMessage.message) {
                byeMessage -> {
                    sendBye(channel)
                    true
                }
                else -> false
            }

    override fun timeout(channel: ColibraChannel) = sendBye(channel)

    private fun sendBye(channel: ColibraChannel) {
        val name = nameRepository.getName(channel.getSession())
        val time = System.currentTimeMillis() - channel.getSession().timestamp
        channel.write(ServerMessage("BYE ${name.value}, WE SPOKE FOR $time MS"))
    }
}
