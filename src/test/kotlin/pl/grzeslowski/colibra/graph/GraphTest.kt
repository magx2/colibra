package pl.grzeslowski.colibra.graph

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.util.*
import java.util.stream.Stream

internal class GraphTest {
    private val node1 = Node("1")
    private val node2 = Node("2")
    private val node3 = Node("3")
    private val node4 = Node("4")
    private val node5 = Node("5")
    private val node6 = Node("6")

    private val edge1 = Edge(node1, node2, 10)
    private val edge2 = Edge(node3, node4, 11)
    private val edge3 = Edge(node5, node6, 12)

    private val graph = Graph()
            .addNode(node1)
            .addNode(node2)
            .addNode(node3)
            .addNode(node4)
            .addNode(node5)
            .addNode(node6)
            .addEdge(edge1)
            .addEdge(edge2)
            .addEdge(edge3)

    @Test
    fun `should not contain any edges`() {
        assertThat(Graph().edges()).isEmpty()
    }

    @Test
    fun `should not contain node`() {
        assertThat(Graph().containsNode(node1)).isFalse()
    }

    @Test
    fun `should thr error on removing node`() {
        assertThrows(NodeNotFound::class.java, { Graph().removeNode(node1) })
    }

    @Test
    fun `should contains all init edges`() {

        // when
        val edges = graph.edges()

        // then
        assertThat(edges).containsExactlyInAnyOrder(edge1, edge2, edge3)
    }

    @Test
    fun `should contains added edge`() {

        // given
        val addedEdge = Edge(node1, node6, 101)

        // when
        val edges = graph.addEdge(addedEdge).edges()

        // then
        assertThat(edges).containsExactlyInAnyOrder(edge1, edge2, edge3, addedEdge)
    }

    @Test
    fun `should remove last edge`() {

        // when
        val edges = graph.removeEdge(edge3.from, edge3.to).edges()

        // then
        assertThat(edges).containsExactlyInAnyOrder(edge1, edge2)
    }

    @Test
    fun `should remove first edge`() {

        // when
        val edges = graph.removeEdge(edge1.from, edge1.to).edges()

        // then
        assertThat(edges).containsExactlyInAnyOrder(edge2, edge3)
    }

    @Test
    fun `should add node`() {

        // given
        val newNode = Node("new")

        // when
        val graphWithNode = graph.addNode(newNode)

        // then
        assertThat(graphWithNode.containsNode(newNode)).isTrue()
        assertThat(graphWithNode.edges()).containsExactlyInAnyOrder(edge1, edge2, edge3)
    }

    @Test
    fun `should remove last node`() {

        // when
        val graphWithNode = graph.removeNode(node6)

        // then
        assertThat(graphWithNode.containsNode(node6)).isFalse()
        assertThat(graphWithNode.edges()).containsExactlyInAnyOrder(edge1, edge2)
    }

    @Test
    fun `should remove first node`() {

        // when
        val graphWithNode = graph.removeNode(node1)

        // then
        assertThat(graphWithNode.containsNode(node1)).isFalse()
        assertThat(graphWithNode.edges()).containsExactlyInAnyOrder(edge2, edge3)
    }

    @Test
    fun `should contains all nodes`() {
        assertThat(graph.containsNode(node1)).isTrue()
        assertThat(graph.containsNode(node2)).isTrue()
        assertThat(graph.containsNode(node3)).isTrue()
        assertThat(graph.containsNode(node4)).isTrue()
        assertThat(graph.containsNode(node5)).isTrue()
        assertThat(graph.containsNode(node6)).isTrue()
    }

    @Test
    fun `should throw exception when first node already exists`() {
        assertThrows(NodeAlreadyExists::class.java, { graph.addNode(node1) })
    }

    @Test
    fun `should throw exception when last node already exists`() {
        assertThrows(NodeAlreadyExists::class.java, { graph.addNode(node3) })
    }

    @Test
    fun `should throw exception when from node in edge does not exists`() {

        // given
        val edge = Edge(Node("999"), node6, 100)

        // when
        val addEdge = Executable { graph.addEdge(edge) }

        // then
        assertThrows(NodeNotFound::class.java, addEdge)
    }

    @Test
    fun `should throw exception when to node in edge does not exists`() {

        // given
        val edge = Edge(node6, Node("999"), 100)

        // when
        val addEdge = Executable { graph.addEdge(edge) }

        // then
        assertThrows(NodeNotFound::class.java, addEdge)
    }

    @Test
    fun `should throw exception when from and to nodes in edge does not exists`() {

        // given
        val edge = Edge(Node(("998")), Node("999"), 100)

        // when
        val addEdge = Executable { graph.addEdge(edge) }

        // then
        assertThrows(NodesNotFound::class.java, addEdge)
    }

    @Test
    fun `should not throw stack overflow when there is a lot of edges`() {

        // given
        var graphWithALotOfEdges = graph
        Stream.generate { randomEdge() }
                .parallel()
                .limit(100_000)
                .forEach { edge ->
                    graphWithALotOfEdges = graphWithALotOfEdges.addEdge(edge)
                }

        // when
        val edges = graphWithALotOfEdges.edges()

        // then
        assertThat(edges).isNotEmpty
    }

    private fun randomEdge(): Edge {
        val random = Random()
        val from = when (random.nextInt(6)) {
            0 -> node1
            1 -> node2
            2 -> node3
            3 -> node4
            4 -> node5
            5 -> node6
            else -> throw IllegalStateException("Bad random")
        }
        val to = when (random.nextInt(6)) {
            0 -> node1
            1 -> node2
            2 -> node3
            3 -> node4
            4 -> node5
            5 -> node6
            else -> throw IllegalStateException("Bad random")
        }
        return Edge(from, to, random.nextInt(100))
    }
}