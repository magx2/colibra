package pl.grzeslowski.colibra.graph.listeners

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import pl.grzeslowski.colibra.graph.GraphRepository
import pl.grzeslowski.colibra.graph.Node
import pl.grzeslowski.colibra.graph.NodeNotFound
import pl.grzeslowski.colibra.graph.NodesNotFound
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.ServerMessage
import pl.grzeslowski.colibra.spring.testProfileName


@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles(testProfileName)
@Suppress("SpringKotlinAutowiredMembers")
internal class RemoveEdgeListenerTest {
    @Autowired
    lateinit var listener: RemoveEdgeListener
    @MockBean
    lateinit var graphRepository: GraphRepository

    lateinit var channel: ColibraChannel

    @BeforeEach
    internal fun setUp() {
        channel = Mockito.mock(ColibraChannel::class.java)
    }

    @Test
    fun `should return false if message is not in proper format`() {

        // given
        val message = ClientMessage("REMOVE SMTH")

        // when
        val onNewMessage = listener.onNewMessage(message, channel)

        // then
        Assertions.assertThat(onNewMessage).isFalse()
    }

    @Test
    fun `should return true if message is in proper format`() {

        // given
        val message = ClientMessage("REMOVE EDGE N1 N2")

        // when
        val onNewMessage = listener.onNewMessage(message, channel)

        // then
        Assertions.assertThat(onNewMessage).isTrue()
    }

    @Test
    fun `should return remove edge from repository`() {

        // given
        val nodeName1 = "N1"
        val nodeName2 = "N2"
        val message = ClientMessage("REMOVE EDGE $nodeName1 $nodeName2")

        // when
        listener.onNewMessage(message, channel)

        // then
        Mockito.verify(graphRepository).removeEdge(Node(nodeName1), Node(nodeName2))
        Mockito.verify(channel).write(ServerMessage("EDGE REMOVED"))
    }

    @Test
    fun `should write message if node from edge is not found`() {

        // given
        val nodeName1 = "N1"
        val nodeName2 = "N2"
        val message = ClientMessage("REMOVE EDGE $nodeName1 $nodeName2")
        BDDMockito.given(graphRepository.removeEdge(Node(nodeName1), Node(nodeName2)))
                .willThrow(NodeNotFound(Node(nodeName1)))

        // when
        listener.onNewMessage(message, channel)

        // then
        Mockito.verify(channel).write(ServerMessage("ERROR: NODE NOT FOUND"))
    }

    @Test
    fun `should write message if nodes from edge are not found`() {

        // given
        val nodeName1 = "N1"
        val nodeName2 = "N2"
        val message = ClientMessage("REMOVE EDGE $nodeName1 $nodeName2")
        BDDMockito.given(graphRepository.removeEdge(Node(nodeName1), Node(nodeName2)))
                .willThrow(NodesNotFound(Node(nodeName1), Node(nodeName2)))

        // when
        listener.onNewMessage(message, channel)

        // then
        Mockito.verify(channel).write(ServerMessage("ERROR: NODE NOT FOUND"))
    }
}
