package pl.grzeslowski.colibra.server.netty

import io.netty.handler.codec.LineBasedFrameDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.util.CharsetUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.support.BeanDefinitionDsl
import java.nio.charset.Charset

@Configuration
class ChannelHandlerConfiguration {
    @Bean
    @Scope("prototype")
    fun channelInboundHandler() = LineBasedFrameDecoder(Int.MAX_VALUE)

    @Bean
    @Scope("prototype")
    fun channelOutboundHandler() = StringEncoder(CharsetUtil.UTF_8)
}