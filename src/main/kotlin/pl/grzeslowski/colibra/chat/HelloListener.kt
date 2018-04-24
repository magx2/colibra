package pl.grzeslowski.colibra.chat

import io.netty.channel.Channel
import jdk.nashorn.internal.runtime.regexp.joni.Config.log
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.NewChannelListener
import pl.grzeslowski.colibra.server.NewMessageListener
import pl.grzeslowski.colibra.server.ServerMessage

@Component
class HelloListener : NewChannelListener {
    override fun onNewChannel(channel: ColibraChannel) {
        channel.write(ServerMessage("HI, I'M ${uuid(channel)}"))
    }

    private fun uuid(channel: ColibraChannel) = channel.getSession().uuid.value
}