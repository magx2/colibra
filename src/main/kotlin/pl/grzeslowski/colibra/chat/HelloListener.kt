package pl.grzeslowski.colibra.chat

import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.NewChannelListener
import pl.grzeslowski.colibra.server.ServerMessage

@Component
class HelloListener : NewChannelListener {
    override fun onNewChannel(channel: ColibraChannel) {
        channel.write(ServerMessage("HI, I'M ${uuid(channel)}"))
    }

    private fun uuid(channel: ColibraChannel) = channel.getSession().uuid.value
}