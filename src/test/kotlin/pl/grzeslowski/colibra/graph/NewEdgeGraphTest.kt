package pl.grzeslowski.colibra.graph

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class NewEdgeGraphTest {
    private val node1 = Node("1")
    private val node2 = Node("2")
    private val node3 = Node("3")
    private val node4 = Node("4")
    private val node5 = Node("5")
    private val node6 = Node("6")

    private val edge1 = Edge(node1, node2, 10)
    private val edge2 = Edge(node3, node4, 11)
    private val edge3 = Edge(node5, node6, 12)

    private val graph = NewEdgeGraph(edge1,
            NewEdgeGraph(edge2,
                    NewEdgeGraph(edge3,
                            NewNodeGraph(node1,
                                    NewNodeGraph(node2,
                                            NewNodeGraph(node3,
                                                    NewNodeGraph(node4,
                                                            NewNodeGraph(node5,
                                                                    NewNodeGraph(node6,
                                                                            EmptyGraph)))))))))

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
    fun `should remove first node`() {

        // when
        val graphWithNode = graph.removeNode(node6)

        // then
        assertThat(graphWithNode.containsNode(node6)).isFalse()
        assertThat(graphWithNode.edges()).containsExactlyInAnyOrder(edge1, edge2, edge3)
    }

    @Test
    fun `should remove last node`() {

        // when
        val graphWithNode = graph.removeNode(node1)

        // then
        assertThat(graphWithNode.containsNode(node1)).isFalse()
        assertThat(graphWithNode.edges()).containsExactlyInAnyOrder(edge1, edge2, edge3)
    }

    @Test
    fun `should contains all nodes`() {
        assertThatGraphContainsAllNodes()
    }

    @Test
    fun `should throw exception when first node already exists`() {
        Assertions.assertThrows(NodeAlreadyExists::class.java, { graph.addNode(node1) })
    }

    @Test
    fun `should throw exception when last node already exists`() {
        Assertions.assertThrows(NodeAlreadyExists::class.java, { graph.addNode(node3) })
    }

    @Test
    fun `should throw exception when first edge already exists`() {
        Assertions.assertThrows(EdgeAlreadyExists::class.java, { graph.addEdge(edge1) })
    }

    @Test
    fun `should throw exception when last edge already exists`() {
        Assertions.assertThrows(EdgeAlreadyExists::class.java, { graph.addEdge(edge3) })
    }

    private fun assertThatGraphContainsAllNodes() = assertThatGraphContainsAllNodes(graph)

    private fun assertThatGraphContainsAllNodes(graph: Graph) {
        assertThat(graph.containsNode(node1)).isTrue()
        assertThat(graph.containsNode(node2)).isTrue()
        assertThat(graph.containsNode(node3)).isTrue()
        assertThat(graph.containsNode(node4)).isTrue()
        assertThat(graph.containsNode(node5)).isTrue()
        assertThat(graph.containsNode(node6)).isTrue()
    }
}
