package pl.grzeslowski.colibra.graph.listeners

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.graph.GraphRepository
import pl.grzeslowski.colibra.graph.Node
import pl.grzeslowski.colibra.graph.NodeNotFound
import pl.grzeslowski.colibra.graph.NodesNotFound
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.NewMessageListener
import pl.grzeslowski.colibra.server.ServerMessage

@Component
class RemoveEdgeListener(private val graphRepository: GraphRepository) : NewMessageListener {
    private val logger = LoggerFactory.getLogger(RemoveEdgeListener::class.java)
    private val prefix = "REMOVE EDGE "
    override fun onNewMessage(clientMessage: ClientMessage, channel: ColibraChannel): Boolean =
            if (clientMessage.message.startsWith(prefix)) {
                val splitted = clientMessage.message.split(" ")
                val nodeName1 = splitted[2]
                val nodeName2 = splitted[3]
                try {
                    graphRepository.removeEdge(Node(nodeName1), Node(nodeName2))
                    channel.write(ServerMessage("EDGE REMOVED"))
                } catch (ex: NodeNotFound) {
                    logger.error("Cannot find node", ex)
                    channel.write(ServerMessage("ERROR: NODE NOT FOUND"))
                } catch (ex: NodesNotFound) { // NOTE: there is no multi catch in kotlin :(
                    logger.error("Cannot find nodes", ex)
                    channel.write(ServerMessage("ERROR: NODE NOT FOUND"))
                }
                true
            } else {
                false
            }
}