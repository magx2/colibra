package pl.grzeslowski.colibra.graph

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import pl.grzeslowski.colibra.spring.testProfileName

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles(testProfileName)
internal class RouteFinderImplTest {
    @Autowired
    lateinit var finder: RouteFinderImpl
    @Autowired
    lateinit var graphHelper: GraphHelper

    private val node1 = Node("node1")
    private val node2 = Node("node2")
    private val node3 = Node("node3")
    private val node4 = Node("node4")
    private val node5 = Node("node5")
    private val node6 = Node("node6")
    private val node7 = Node("node7")
    private val node8 = Node("node8")
    private val node9 = Node("node9")

    private val edge12 = Edge(node1, node2, 100)
    private val edge16 = Edge(node1, node6, 500)
    private val edge24 = Edge(node2, node4, 20)
    private val edge25 = Edge(node2, node5, 50)
    private val edge13 = Edge(node1, node3, 10)
    private val edge34 = Edge(node3, node4, 15)
    private val edge37 = Edge(node3, node7, 10)
    private val edge38 = Edge(node3, node8, 200)
    private val edge78 = Edge(node7, node8, 5)
    private val edge68 = Edge(node8, node6, 25)
    private val edge19 = Edge(node1, node9, 1)

    @Test
    fun `should not find path in empty graph`() {

        // given
        val graph = Graph()

        // when
        val shortestPath = finder.shortestPath(graph, node1, node2)

        // then
        assertThat(shortestPath).isEqualTo(Int.MAX_VALUE)
    }

    @Test
    fun `should find path in simple graph`() {

        // given
        val graph = graphHelper.newGraph(setOf(node1, node2), setOf(edge12))

        // when
        val shortestPath = finder.shortestPath(graph, node1, node2)

        // then
        assertThat(shortestPath).isEqualTo(edge12.weight)
    }

    @Test
    fun `should find path in two route graph`() {

        // given
        val graph = graphHelper.newGraph(
                setOf(node1, node2, node3, node4),
                setOf(edge12, edge13, edge24, edge34)
        )

        // when
        val shortestPath = finder.shortestPath(graph, node1, node4)

        // then
        assertThat(shortestPath).isEqualTo(edge13.weight + edge34.weight)
    }

    @Test
    fun `should find path in longer graph`() {

        // given
        val graph = graphHelper.newGraph(
                setOf(node1, node2, node3, node4, node5, node6, node7, node8, node9),
                setOf(edge12, edge13, edge19, edge24, edge34, edge16, edge25, edge16, edge37, edge38, edge78, edge68)
        )

        // when
        val shortestPath = finder.shortestPath(graph, node1, node6)

        // then
        assertThat(shortestPath).isEqualTo(edge13.weight + edge37.weight + edge78.weight + edge68.weight)
    }

    @Test
    fun `should not throw stack overflow if graph is BIG`() {

        // given
        val bigGraph = graphHelper.randomBigGraph(graphHelper.newGraph(
                setOf(node1, node2, node3, node4, node5, node6, node7, node8, node9),
                setOf(edge12, edge13, edge19, edge24, edge34, edge16, edge25, edge16, edge37, edge38, edge78, edge68)
        ))

        // when
        finder.shortestPath(bigGraph, node1, node2)
    }
}
