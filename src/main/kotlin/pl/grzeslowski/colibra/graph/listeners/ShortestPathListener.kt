package pl.grzeslowski.colibra.graph.listeners

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.graph.CloserThanService
import pl.grzeslowski.colibra.graph.GraphRepository
import pl.grzeslowski.colibra.graph.Node
import pl.grzeslowski.colibra.graph.NodeNotFound
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.NewMessageListener
import pl.grzeslowski.colibra.server.ServerMessage
import java.util.stream.Collectors

@Component
class ShortestPathListener(private val graphRepository: GraphRepository,
                           private val closerThanService: CloserThanService) : NewMessageListener {
    private val logger = LoggerFactory.getLogger(ShortestPathListener::class.java)
    private val prefix = "CLOSER THAN "
    override fun onNewMessage(clientMessage: ClientMessage, channel: ColibraChannel): Boolean =
            if (clientMessage.message.startsWith(prefix)) {
                val splitted = clientMessage.message.split(" ")
                val weight = splitted[2].toInt()
                val node = Node(splitted[3])
                try {
                    val graph = graphRepository.getReadOnlyGraph()
                    val closerThan = closerThanService.closerThan(graph, node, weight)
                    val msg = if (closerThan.isEmpty().not()) {
                        closerThan.stream()
                                .map { it.name }
                                .collect(Collectors.joining(","))
                    } else {
                        "ERROR: NODE NOT FOUND"
                    }
                    channel.write(ServerMessage(msg))
                } catch (ex: NodeNotFound) {
                    logger.error("Cannot find node", ex)
                    channel.write(ServerMessage("ERROR: NODE NOT FOUND"))
                }
                true
            } else {
                false
            }
}