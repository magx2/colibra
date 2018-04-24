package pl.grzeslowski.colibra.server.netty

import io.netty.channel.Channel
import org.slf4j.LoggerFactory
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.ServerMessage
import pl.grzeslowski.colibra.server.session.Session

class NettyChanel(private val channel: Channel,
                  private val session: Session) : ColibraChannel {
    private val log  = LoggerFactory.getLogger(NettyChanel::class.java)

    override fun getSession() = session

    override fun write(serverMessage: ServerMessage) {
        log.trace("write {}", serverMessage)
        channel.writeAndFlush(serverMessage)
    }

    override fun close() {
        log.trace("close")
        channel.close()
    }
}