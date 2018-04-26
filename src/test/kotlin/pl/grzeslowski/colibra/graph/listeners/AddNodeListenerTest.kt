package pl.grzeslowski.colibra.graph.listeners

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import pl.grzeslowski.colibra.graph.GraphRepository
import pl.grzeslowski.colibra.graph.Node
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.spring.testProfileName

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles(testProfileName)
internal class AddNodeListenerTest {
    @Autowired
    lateinit var listener: AddNodeListener
    @MockBean
    lateinit var graphRepository: GraphRepository

    lateinit var channel: ColibraChannel

    @BeforeEach
    internal fun setUp() {
        channel = mock(ColibraChannel::class.java)
    }

    @Test
    fun `should return false if message is not in proper format`() {

        // given
        val message = ClientMessage("ADD SMTH")

        // when
        val onNewMessage = listener.onNewMessage(message, channel)

        // then
        assertThat(onNewMessage).isFalse()
    }

    @Test
    fun `should return true if message is in proper format`() {

        // given
        val message = ClientMessage("ADD NODE TEST-NODE")

        // when
        val onNewMessage = listener.onNewMessage(message, channel)

        // then
        assertThat(onNewMessage).isTrue()
    }

    @Test
    fun `should return add node to repository`() {

        // given
        val nodeName = "TEST-NODE"
        val message = ClientMessage("ADD NODE $nodeName")

        // when
        listener.onNewMessage(message, channel)

        // then
        verify(graphRepository).addNode(Node(nodeName))
    }
}
