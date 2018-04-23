package pl.grzeslowski.colibra.server.netty

import io.netty.channel.*
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.socket.SocketChannel
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope("prototype")
@Component
class NettyChannelInitializer(private val context: ApplicationContext) : ChannelInitializer<SocketChannel>() {
    override fun initChannel(socketChannel: SocketChannel) {
        val pipeline = socketChannel.pipeline()
        pipeline.addLast(newChannelInboundHandler())
        pipeline.addLast(newChannelOutboundHandler())
        pipeline.addLast(newNettyChannelHandler())
    }

    private fun newChannelOutboundHandler() = context.getBean("channelOutboundHandler", ChannelOutboundHandler::class.java)

    private fun newChannelInboundHandler() = context.getBean("channelInboundHandler", ChannelInboundHandler::class.java)

    private fun newNettyChannelHandler() = context.getBean("nettyChannelHandler", NettyChannelHandler::class.java)
}
