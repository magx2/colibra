package pl.grzeslowski.colibra.graph.listeners

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.graph.Edge
import pl.grzeslowski.colibra.graph.GraphRepository
import pl.grzeslowski.colibra.graph.Node
import pl.grzeslowski.colibra.graph.NodeNotFound
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.NewMessageListener
import pl.grzeslowski.colibra.server.ServerMessage

@Component
class AddEdgeListener(private val graphRepository: GraphRepository) : NewMessageListener {
    private val logger = LoggerFactory.getLogger(AddEdgeListener::class.java)
    private val prefix = "ADD EDGE "
    override fun onNewMessage(clientMessage: ClientMessage, channel: ColibraChannel): Boolean =
            if (clientMessage.message.startsWith(prefix)) {
                val splitted = clientMessage.message.split(" ")
                val nodeName1 = splitted[2]
                val nodeName2 = splitted[3]
                val weight = splitted[4]
                try {
                    graphRepository.addEdge(Edge(Node(nodeName1), Node(nodeName2), weight.toInt()))
                    channel.write(ServerMessage("EDGE ADDED"))
                } catch (ex: NodeNotFound) {
                    logger.error("Cannot find node", ex)
                    channel.write(ServerMessage("ERROR: NODE NOT FOUND"))
                }
                true
            } else {
                false
            }
}