package pl.grzeslowski.colibra.server.netty

import io.netty.channel.*
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.LineBasedFrameDecoder
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.util.CharsetUtil
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope("prototype")
@Component
class NettyChannelInitializer(private val context: ApplicationContext,
                              private val serverMessageChannelHandler: ServerMessageChannelHandler,
                              private val clientMessageChannelHandler: ClientMessageChannelHandler) : ChannelInitializer<SocketChannel>() {
    private val log = LoggerFactory.getLogger(NettyChannelInitializer::class.java)
    private val charset = CharsetUtil.UTF_8

    override fun initChannel(socketChannel: SocketChannel) {
        log.trace("Init channel, local address {}, remote address {}",
                socketChannel.localAddress(),
                socketChannel.remoteAddress())
        val pipeline = socketChannel.pipeline()
        pipeline.addLast(LineBasedFrameDecoder(Int.MAX_VALUE))
        pipeline.addLast(StringDecoder(charset))
        pipeline.addLast(clientMessageChannelHandler)
        pipeline.addLast(StringEncoder(charset))
        pipeline.addLast(serverMessageChannelHandler)
        pipeline.addLast(newNettyChannelHandler())
    }

    private fun newNettyChannelHandler() = context.getBean("nettyChannelHandler", NettyChannelHandler::class.java)
}
