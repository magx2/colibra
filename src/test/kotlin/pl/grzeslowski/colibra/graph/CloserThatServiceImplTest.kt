package pl.grzeslowski.colibra.graph

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import pl.grzeslowski.colibra.spring.testProfileName


@Suppress("SpringKotlinAutowiring")
@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles(testProfileName)
internal class CloserThatServiceImplTest {
    @Autowired
    lateinit var service: CloserThatServiceImpl
    @Autowired
    lateinit var graphHelper: GraphHelper

    val node = Node("main")

    @Test
    fun `should return empty list on empty graph`() {

        // given
        val graph = graphHelper.newGraph(setOf(node), setOf())

        // when
        val closerThan = service.closerThan(graph, node, 100)

        // then
        assertThat(closerThan).isEmpty()
    }

    @Test
    fun `should return set with all nodes in order`() {

        // given
        val node1 = Node("z")
        val node2 = Node("d")
        val node3 = Node("f")
        val graph = graphHelper.newGraph(
                setOf(node, node1, node2, node3),
                setOf(
                        Edge(node, node1, 1),
                        Edge(node1, node2, 2),
                        Edge(node1, node3, 3)
                )
        )

        // when
        val closerThan = service.closerThan(graph, node, 5)

        // then
        assertThat(closerThan).containsExactly(node2, node3, node1)
    }

    @Test
    fun `should return set even if graph has cycles`() {

        // given
        val node1 = Node("z")
        val node2 = Node("d")
        val graph = graphHelper.newGraph(
                setOf(node, node1, node2),
                setOf(
                        Edge(node, node1, 1),
                        Edge(node1, node2, 2),
                        Edge(node2, node, 3)
                )
        )

        // when
        val closerThan = service.closerThan(graph, node, 100)

        // then
        assertThat(closerThan).containsExactly(node2, node1)
    }

    @Test
    fun `should return set with nodes that are connected with main node`() {

        // given
        val node1 = Node("z")
        val node2 = Node("d")
        val node3 = Node("f")
        val node4 = Node("s")
        val node5 = Node("v")
        val graph = graphHelper.newGraph(
                setOf(node, node1, node2, node3, node4, node5),
                setOf(
                        Edge(node, node1, 1),
                        Edge(node1, node2, 2),
                        Edge(node1, node3, 3),
                        Edge(node4, node5, 1),
                        Edge(node4, node, 1),
                        Edge(node5, node, 1)
                )
        )

        // when
        val closerThan = service.closerThan(graph, node, 5)

        // then
        assertThat(closerThan).containsExactly(node2, node3, node1)
    }

    @Test
    fun `should return set with nodes that are connected with main node and are close enough`() {

        // given
        val node1 = Node("z")
        val node2 = Node("d")
        val node3 = Node("f")
        val node4 = Node("a")
        val node5 = Node("v")
        val graph = graphHelper.newGraph(
                setOf(node, node1, node2, node3, node4, node5),
                setOf(
                        Edge(node, node1, 10),
                        Edge(node, node4, 1),
                        Edge(node1, node2, 20),
                        Edge(node1, node3, 30),
                        Edge(node3, node4, 30),
                        Edge(node4, node5, 50)
                )
        )

        // when
        val closerThan = service.closerThan(graph, node, 41)

        // then
        assertThat(closerThan).containsExactly(node4, node2, node3, node1)
    }

    @Test
    fun `should throw NodeNotFound if main node is not in graph`() {
        // given
        val node1 = Node("z")
        val node2 = Node("d")
        val node3 = Node("f")
        val graph = graphHelper.newGraph(
                setOf(node1, node2, node3),
                setOf(
                        Edge(node1, node2, 2),
                        Edge(node1, node3, 3)
                )
        )

        // when
        val closerThan = Executable { service.closerThan(graph, node, 4) }

        // then
        assertThrows(NodeNotFound::class.java, closerThan)
    }
}
