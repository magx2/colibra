package pl.grzeslowski.colibra.graph

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*
import java.util.stream.Stream

internal class NewNodeGraphTest {
    private val node1 = Node("1")
    private val node2 = Node("2")
    private val node3 = Node("3")

    private val edge1 = Edge(node1, node2, 10)
    private val edge2 = Edge(node2, node3, 11)
    private val edge3 = Edge(node1, node3, 12)

    private val graph = NewEdgeGraph(edge1,
            NewEdgeGraph(edge2,
                    NewEdgeGraph(edge3,
                            NewNodeGraph(node1,
                                    NewNodeGraph(node2,
                                            NewNodeGraph(node3, EmptyGraph))))))

    @Test
    fun `should contains all nodes`() {
        assertThat(graph.containsNode(node1)).isTrue()
        assertThat(graph.containsNode(node2)).isTrue()
        assertThat(graph.containsNode(node3)).isTrue()
    }

    @Test
    fun `should add node`() {

        // given
        val newNode = Node("new")

        // when
        val graphWithNewNode = graph.addNode(newNode)

        // then
        assertThat(graphWithNewNode.containsNode(newNode)).isTrue()
        assertThat(graphWithNewNode.containsNode(node1)).isTrue()
        assertThat(graphWithNewNode.containsNode(node2)).isTrue()
        assertThat(graphWithNewNode.containsNode(node3)).isTrue()
    }

    @Test
    fun `should remove first node`() {

        // when
        val graphWithoutNode = graph.removeNode(node3)

        // then
        assertThat(graphWithoutNode.containsNode(node1)).isTrue()
        assertThat(graphWithoutNode.containsNode(node2)).isTrue()
        assertThat(graphWithoutNode.containsNode(node3)).isFalse()
    }

    @Test
    fun `should remove last node`() {

        // when
        val graphWithoutNode = graph.removeNode(node1)

        // then
        assertThat(graphWithoutNode.containsNode(node1)).isFalse()
        assertThat(graphWithoutNode.containsNode(node2)).isTrue()
        assertThat(graphWithoutNode.containsNode(node3)).isTrue()
    }

    @Test
    fun `should add edge`() {

        // given
        val edge = Edge(node1, node2, 101)

        // when
        val graphWithEdge = graph.addEdge(edge)

        // then
        assertThat(graphWithEdge.edges()).contains(edge)
        assertThat(graphWithEdge.containsNode(node1)).isTrue()
        assertThat(graphWithEdge.containsNode(node2)).isTrue()
        assertThat(graphWithEdge.containsNode(node3)).isTrue()
    }

    @Test
    fun `should remove first edge`() {

        // when
        val edges = graph.removeEdge(edge3.from, edge3.to).edges()

        // then
        assertThat(edges).containsExactlyInAnyOrder(edge1, edge2)
    }

    @Test
    fun `should remove last edge`() {

        // when
        val edges = graph.removeEdge(edge1.from, edge1.to).edges()

        // then
        assertThat(edges).containsExactlyInAnyOrder(edge2, edge3)
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
    fun `should not throw stack overflow when there is a lot of edges`() {

        // given
        var graphWithALotOfNodes: Graph = graph
        Stream.generate { randomNode() }
                .parallel()
                .limit(100_000)
                .forEach { node ->
                    graphWithALotOfNodes = graphWithALotOfNodes.addNode(node)
                }

        // when
        val edges = graphWithALotOfNodes.edges()

        // then
        assertThat(edges).isNotEmpty
    }

    private fun randomNode(): Node {
        val random = Random()
        return Node("random-" + random.nextInt())
    }
}
