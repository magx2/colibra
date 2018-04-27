package pl.grzeslowski.colibra.server.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.ReadTimeoutException
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.server.*
import pl.grzeslowski.colibra.server.session.Session

@Scope("prototype")
//@Sharable
@Component
class NettyChannelHandler(private val session: Session,
                          private val newChannelListeners: Set<NewChannelListener>,
                          private val newMessageListener: Set<NewMessageListener>,
                          private val timeoutListener: TimeoutListener) : SimpleChannelInboundHandler<ClientMessage>() {
    private val log = LoggerFactory.getLogger(NettyChannelHandler::class.java)

    override fun channelActive(ctx: ChannelHandlerContext) {
        newChannelListeners.forEach { listener -> listener.onNewChannel(createColibraChannel(ctx)) }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        when(cause) {
            is ReadTimeoutException -> {
                log.debug("Timeout $session")
                timeoutListener.timeout(createColibraChannel(ctx))
            }
            else -> {
                log.error("Closing channel because of exception!", cause)
                ctx.channel().close()
            }
        }
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ClientMessage) {
        log.trace("channelRead0 {}:{}", ctx.name(), msg)
        val channel = createColibraChannel(ctx)
        val someoneHandledThisMessage = newMessageListener.stream()
                .map { listener -> listener.onNewMessage(msg, channel) }
                .filter { it }
                .findAny()
                .orElseGet { false }
        if (!someoneHandledThisMessage) {
            log.warn("Nobody handled this message $msg")
            channel.write(ServerMessage("SORRY, I DIDN'T UNDERSTAND THAT"))
        }
    }

    private fun createColibraChannel(ctx: ChannelHandlerContext) = NettyChanel(ctx.channel(), session)
}