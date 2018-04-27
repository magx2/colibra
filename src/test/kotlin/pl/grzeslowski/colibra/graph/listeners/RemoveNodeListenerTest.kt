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
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.ServerMessage
import pl.grzeslowski.colibra.spring.testProfileName

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles(testProfileName)
internal class RemoveNodeListenerTest {
    @Autowired
    lateinit var listener: RemoveNodeListener
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
        val message = ClientMessage("REMOVE NODE TEST-NODE")

        // when
        val onNewMessage = listener.onNewMessage(message, channel)

        // then
        Assertions.assertThat(onNewMessage).isTrue()
    }

    @Test
    fun `should return add node to repository`() {

        // given
        val nodeName = "TEST-NODE"
        val message = ClientMessage("REMOVE NODE $nodeName")

        // when
        listener.onNewMessage(message, channel)

        // then
        Mockito.verify(graphRepository).removeNode(Node(nodeName))
    }

    @Test
    fun `should write message if node is duplicate`() {

        // given
        val nodeName = "TEST-NODE"
        val message = ClientMessage("REMOVE NODE $nodeName")
        val node = Node(nodeName)
        BDDMockito.given(graphRepository.removeNode(node)).willThrow(NodeNotFound(node))

        // when
        listener.onNewMessage(message, channel)

        // then
        Mockito.verify(channel).write(ServerMessage("ERROR: NODE NOT FOUND"))
    }
}
