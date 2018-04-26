package pl.grzeslowski.colibra.graph.listeners

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.graph.GraphRepository
import pl.grzeslowski.colibra.graph.Node
import pl.grzeslowski.colibra.graph.NodeAlreadyExists
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.NewMessageListener
import pl.grzeslowski.colibra.server.ServerMessage

@Component
class AddNodeListener(private val graphRepository: GraphRepository) : NewMessageListener {
    private val logger = LoggerFactory.getLogger(AddNodeListener::class.java)
    private val prefix = "ADD NODE "
    override fun onNewMessage(clientMessage: ClientMessage, channel: ColibraChannel): Boolean =
            if (clientMessage.message.startsWith(prefix)) {
                val nodeName = clientMessage.message.substring(prefix.length)
                try {
                    graphRepository.addNode(Node(nodeName))
                    channel.write(ServerMessage("NODE ADDED"))
                } catch (ex: NodeAlreadyExists) {
                    logger.error("Cannot add node", ex)
                    channel.write(ServerMessage("ERROR: NODE ALREADY EXISTS"))
                }
                true
            } else {
                false
            }
}
