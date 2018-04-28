package pl.grzeslowski.colibra.graph.listeners

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mockito
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

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles(testProfileName)
internal class ShortestPathListenerTest {
    @Autowired
    lateinit var listener: ShortestPathListener
    @MockBean
    lateinit var graphRepository: GraphRepository
    @MockBean
    lateinit var routeFinder: RouteFinder

    lateinit var channel: ColibraChannel
    lateinit var graph: Graph

    @BeforeEach
    internal fun setUp() {
        channel = Mockito.mock(ColibraChannel::class.java)

        graph = Graph()
        given(graphRepository.getReadOnlyGraph()).willReturn(graph)
    }

    @Test
    fun `should return false if message is not in proper format`() {

        // given
        val message = ClientMessage("SHORTEST SMTH")

        // when
        val onNewMessage = listener.onNewMessage(message, channel)

        // then
        assertThat(onNewMessage).isFalse()
    }

    @Test
    fun `should return true if message is in proper format`() {

        // given
        val message = ClientMessage("SHORTEST PATH TEST-NODE1 TEST-NODE2")

        // when
        val onNewMessage = listener.onNewMessage(message, channel)

        // then
        Assertions.assertThat(onNewMessage).isTrue()
    }

    @Test
    fun `should return shortest path`() {

        // given
        val node1 = Node("n1")
        val node2 = Node("n2")
        val message = ClientMessage("SHORTEST PATH ${node1.name} ${node2.name}")

        val weight = 101
        given(routeFinder.shortestPath(graph, node1, node2)).willReturn(weight)

        // when
        listener.onNewMessage(message, channel)

        // then
        verify(channel).write(ServerMessage(weight))
    }

    @Test
    fun `should throw exception if node was not found`() {

        // given
        val node1 = Node("n1")
        val node2 = Node("n2")
        val message = ClientMessage("SHORTEST PATH ${node1.name} ${node2.name}")

        given(routeFinder.shortestPath(graph, node1, node2)).willThrow(NodeNotFound::class.java)

        // when
        listener.onNewMessage(message, channel)

        // then
        verify(channel).write(ServerMessage("ERROR: NODE NOT FOUND"))
    }
}
