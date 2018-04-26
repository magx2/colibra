package pl.grzeslowski.colibra.graph.listeners

import org.springframework.stereotype.Component
import pl.grzeslowski.colibra.graph.GraphRepository
import pl.grzeslowski.colibra.graph.Node
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.NewMessageListener

@Component
class AddNodeListener(private val graphRepository: GraphRepository) : NewMessageListener {
    private val prefix = "ADD NODE "
    override fun onNewMessage(clientMessage: ClientMessage, channel: ColibraChannel): Boolean =
            if (clientMessage.message.startsWith(prefix)) {
                val nodeName = clientMessage.message.substring(prefix.length)
                graphRepository.addNode(Node(nodeName))
                true
            } else {
                false
            }
}