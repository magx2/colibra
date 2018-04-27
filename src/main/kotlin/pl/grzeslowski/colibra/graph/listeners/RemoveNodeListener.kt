package pl.grzeslowski.colibra.graph.listeners

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.graph.GraphRepository
import pl.grzeslowski.colibra.graph.Node
import pl.grzeslowski.colibra.graph.NodeNotFound
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.NewMessageListener
import pl.grzeslowski.colibra.server.ServerMessage

@Component
class RemoveNodeListener(private val graphRepository: GraphRepository) : NewMessageListener {
    private val logger = LoggerFactory.getLogger(RemoveNodeListener::class.java)
    private val prefix = "REMOVE NODE "
    override fun onNewMessage(clientMessage: ClientMessage, channel: ColibraChannel): Boolean =
            if (clientMessage.message.startsWith(prefix)) {
                val nodeName = clientMessage.message.substring(prefix.length)
                try {
                    graphRepository.removeNode(Node(nodeName))
                    channel.write(ServerMessage("NODE REMOVED"))
                } catch (ex: NodeNotFound) {
                    logger.error("Cannot remove  node", ex)
                    channel.write(ServerMessage("ERROR: NODE NOT FOUND"))
                }
                true
            } else {
                false
            }
}