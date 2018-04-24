package pl.grzeslowski.colibra.server.netty

import com.sun.deploy.trace.Trace.flush
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.channel.SimpleChannelInboundHandler
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.server.session.Uuid
import reactor.core.publisher.Flux
import java.util.*

@Scope("prototype")
//@Sharable
@Component
class NettyChannelHandler(private val uuid: Uuid) : SimpleChannelInboundHandler<String>() {
    private val log = LoggerFactory.getLogger(NettyChannelHandler::class.java)

    override fun channelRegistered(ctx: ChannelHandlerContext) {
        super.channelRegistered(ctx)
        log.trace("channelRegistered {}", ctx.name())

    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        val msg = "HI, I'M ${uuid.value}\n"
        log.trace(msg)
        val cf = ctx.writeAndFlush(msg)
        if (cf.isSuccess) {
            log.trace("success")
        } else {
            log.trace("not success {}", cf.cause())
        }
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext) {
        log.trace("channelUnregistered {}", ctx.name())
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: String) {
        log.trace("channelRead0 {}:{}", ctx.name(), msg)
    }
}