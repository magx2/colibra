package pl.grzeslowski.colibra.graph

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class EmptyGraphTest {
    private val graph = EmptyGraph

    private val node1 = Node("node1")
    private val node2 = Node("node2")

    private val edge = Edge(node1, node2, 100)

    @Test
    fun `should not contain any edges`() {
        assertThat(graph.edges()).isEmpty()
    }

    @Test
    fun `should not contain node`() {
        assertThat(graph.containsNode(node1)).isFalse()
    }

    @Test
    fun `should thr error on removing node`() {
        assertThrows(NodeNotFound::class.java, { graph.removeNode(node1) })
    }

    @Test
    fun `should thr error on removing edge`() {
        assertThrows(NodesNotFound::class.java, { graph.removeEdge(edge.from, edge.to) })
    }

    @Test
    fun `should add node`() {

        // when
        val graphWithNode = graph.addNode(node1)

        // then
        assertThat(graphWithNode.containsNode(node1)).isTrue()
    }

    @Test
    fun `should add edge`() {

        // when
        val graphWithEdge = graph.addEdge(edge)

        // then
        assertThat(graphWithEdge.edges()).containsExactly(edge)
    }
}
