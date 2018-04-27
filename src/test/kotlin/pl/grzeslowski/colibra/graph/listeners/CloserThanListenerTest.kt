package pl.grzeslowski.colibra.graph.listeners

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import pl.grzeslowski.colibra.graph.*
import pl.grzeslowski.colibra.server.ClientMessage
import pl.grzeslowski.colibra.server.ColibraChannel
import pl.grzeslowski.colibra.server.ServerMessage
import pl.grzeslowski.colibra.spring.testProfileName
import java.util.TreeSet
import kotlin.Comparator


@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles(testProfileName)
internal class CloserThanListenerTest {
    @Autowired
    lateinit var listener: CloserThanListener
    @Autowired
    lateinit var graphHelper: GraphHelper
    @MockBean
    lateinit var graphRepository: GraphRepository
    @MockBean
    lateinit var closerThanService: CloserThanService

    lateinit var channel: ColibraChannel
    lateinit var graph: Graph

    @BeforeEach
    internal fun setUp() {
        channel = Mockito.mock(ColibraChannel::class.java)

        graph = EmptyGraph
        given(graphRepository.getReadOnlyGraph()).willReturn(graph)
    }

    @Test
    fun `should return false if message is not in proper format`() {

        // given
        val message = ClientMessage("CLOSER SMTH")

        // when
        val onNewMessage = listener.onNewMessage(message, channel)

        // then
        Assertions.assertThat(onNewMessage).isFalse()
    }

    @Test
    fun `should return true if message is in proper format`() {

        // given
        val message = ClientMessage("CLOSER THAN 5 node")

        // when
        val onNewMessage = listener.onNewMessage(message, channel)

        // then
        Assertions.assertThat(onNewMessage).isTrue()
    }

    @Test
    fun `should invoke closer than on CloserThanService instance`() {

        // given
        val weight = 5
        val nodeName = "TEST-NODE"
        val message = ClientMessage("CLOSER THAN $weight $nodeName")

        // when
        listener.onNewMessage(message, channel)

        // then
        verify(closerThanService).closerThan(graph, Node(nodeName), weight)
    }

    @Test
    fun `should write to channel nodes in order`() {

        // given
        val weight = 5
        val nodeName = "TEST-NODE"
        val message = ClientMessage("CLOSER THAN $weight $nodeName")

        val expectedSet = TreeSet<Node>(Comparator { o1, o2 -> o1.name.compareTo(o2.name) })
        expectedSet.add(Node("1"))
        expectedSet.add(Node("2"))
        expectedSet.add(Node("3"))
        given(closerThanService.closerThan(graph, Node(nodeName), weight)).willReturn(expectedSet)

        // when
        listener.onNewMessage(message, channel)

        // then
        verify(channel).write(ServerMessage("1,2,3"))
    }

    @Test
    fun `should write error if node is not found`() {

        // given
        val weight = 5
        val nodeName = "TEST-NODE"
        val message = ClientMessage("CLOSER THAN $weight $nodeName")

        given(closerThanService.closerThan(graph, Node(nodeName), weight)).willThrow(NodeNotFound::class.java)

        // when
        listener.onNewMessage(message, channel)

        // then
        verify(channel).write(ServerMessage("ERROR: NODE NOT FOUND"))
    }
}
