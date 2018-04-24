package pl.grzeslowski.colibra.server.netty

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.MessageToMessageEncoder
import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ServerMessage

@Component
@Sharable
class ServerMessageChannelHandler : MessageToMessageEncoder<ServerMessage>() {
    override fun encode(ctx: ChannelHandlerContext, msg: ServerMessage, out: MutableList<Any>) {
        out.add(msg.message + "\n")
    }
}

@Component
@Sharable
class ClientMessageChannelHandler : MessageToMessageDecoder<String>() {
    override fun decode(ctx: ChannelHandlerContext, msg: String, out: MutableList<Any>) {
        out.add(ClientMessage(msg))
    }
}
