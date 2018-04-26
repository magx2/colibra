package pl.grzeslowski.colibra.server.netty

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.server.Server
import pl.grzeslowski.colibra.server.ServerProperties
import pl.grzeslowski.colibra.spring.NotTestProfile

@Component
@NotTestProfile
class NettyServer(private val serverProperties: ServerProperties,
                  channelInitializer: NettyChannelInitializer) : Server {
    private val log = LoggerFactory.getLogger(NettyServer::class.java)
    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private val channelFuture: ChannelFuture

    init {
        log.info("Starting server on port {}", serverProperties.port)

        channelFuture = ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(channelInitializer)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .bind(serverProperties.port)

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