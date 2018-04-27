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
import pl.grzeslowski.colibra.graph.Edge
import pl.grzeslowski.colibra.graph.GraphRepository
import pl.grzeslowski.colibra.graph.Node
import pl.grzeslowski.colibra.graph.NodeNotFound
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.ServerMessage
import pl.grzeslowski.colibra.spring.testProfileName

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles(testProfileName)
@Suppress("SpringKotlinAutowiredMembers")
internal class AddEdgeListenerTest {
    @Autowired
    lateinit var listener: AddEdgeListener
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
        val message = ClientMessage("ADD SMTH")

        // when
        val onNewMessage = listener.onNewMessage(message, channel)

        // then
        Assertions.assertThat(onNewMessage).isFalse()
    }

    @Test
    fun `should return true if message is in proper format`() {

        // given
        val message = ClientMessage("ADD EDGE N1 N2 100")

        // when
        val onNewMessage = listener.onNewMessage(message, channel)

        // then
        Assertions.assertThat(onNewMessage).isTrue()
    }

    @Test
    fun `should return add edge to repository`() {

        // given
        val nodeName1 = "N1"
        val nodeName2 = "N2"
        val weight = 100
        val message = ClientMessage("ADD EDGE $nodeName1 $nodeName2 $weight")

        // when
        listener.onNewMessage(message, channel)

        // then
        Mockito.verify(graphRepository).addEdge(Edge(Node(nodeName1), Node(nodeName2), weight))
        Mockito.verify(channel).write(ServerMessage("EDGE ADDED"))
    }

    @Test
    fun `should write message if node is duplicate`() {

        // given
        val nodeName1 = "N1"
        val nodeName2 = "N2"
        val weight = 100
        val message = ClientMessage("ADD EDGE $nodeName1 $nodeName2 $weight")
        val eddge = Edge(Node(nodeName1), Node(nodeName2), weight)
        BDDMockito.given(graphRepository.addEdge(eddge)).willThrow(NodeNotFound(Node(nodeName1)))

        // when
        listener.onNewMessage(message, channel)

        // then
        Mockito.verify(channel).write(ServerMessage("ERROR: NODE NOT FOUND"))
    }
}
