package pl.grzeslowski.colibra.server.netty

import org.slf4j.LoggerFactory
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.channels.NewChannel
import pl.grzeslowski.colibra.server.Server
import pl.grzeslowski.colibra.server.ServerProperties
import reactor.core.publisher.Flux

@Component
class NettyServer(private val serverProperties: ServerProperties,
                  channelInitializer: NettyChannelInitializer) : Server {
    private val log = LoggerFactory.getLogger(NettyServer::class.java)
    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private val channelFuture: ChannelFuture
    final val newChannelPublisher: Flux<NewChannel>

    init {
        log.info("Starting server on port {}", serverProperties.port)

        val serverBootstrap = ServerBootstrap()
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(channelInitializer)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)

        channelFuture = serverBootstrap.bind(serverProperties.port)

        newChannelPublisher = Flux.empty()

        log.debug("Server started on port {}", serverProperties.port)
    }

    override fun close() {
        log.info("Closing server on port {}", serverProperties.port)
        try {
            channelFuture.channel().closeFuture().sync()
        } finally {
            workerGroup.shutdownGracefully()
            bossGroup.shutdownGracefully()
        }
    }
}