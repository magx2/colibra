package pl.grzeslowski.colibra.graph.listeners

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.graph.GraphRepository
import pl.grzeslowski.colibra.graph.Node
import pl.grzeslowski.colibra.graph.NodeNotFound
import pl.grzeslowski.colibra.graph.RouteFinder
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.NewMessageListener
import pl.grzeslowski.colibra.server.ServerMessage

@Component
class ShortestPathListener(private val graphRepository: GraphRepository,
                           private val routeFinder: RouteFinder) : NewMessageListener {
    private val logger = LoggerFactory.getLogger(RemoveNodeListener::class.java)
    private val prefix = "SHORTEST PATH "
    override fun onNewMessage(clientMessage: ClientMessage, channel: ColibraChannel): Boolean =
            if (clientMessage.message.startsWith(prefix)) {
                val splitted = clientMessage.message.split(" ")
                val node1 = Node(splitted[2])
                val node2 = Node(splitted[3])
                try {
                    val graph = graphRepository.getReadOnlyGraph()
                    val shortestPath = routeFinder.shortestPath(graph, node1, node2)
                    channel.write(ServerMessage(shortestPath))
                } catch (ex: NodeNotFound) {
                    logger.error("Cannot find node", ex)
                    channel.write(ServerMessage("ERROR: NODE NOT FOUND"))
                }
                true
            } else {
                false
            }
}