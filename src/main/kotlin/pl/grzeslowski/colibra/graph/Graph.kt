package pl.grzeslowski.colibra.graph

import java.util.*
import java.util.Collections.unmodifiableSet

sealed class Graph {
    abstract fun addNode(node: Node): Graph

    abstract fun addEdge(edge: Edge): Graph

    abstract fun removeNode(node: Node): Graph

    abstract fun removeEdge(edge: Edge): Graph

    abstract fun containsNode(node: Node): Boolean

    abstract fun edges(): Set<Edge>
}

data class Node(val name: String)

data class Edge(val a: Node, val b: Node, val weight: Int)

class NewNodeGraph(val newNode: Node,
                   val parentGraph: Graph) : Graph() {
    override fun addNode(node: Node) = NewNodeGraph(node, this)

    override fun addEdge(edge: Edge) = NewEdgeGraph(edge, this)

    override fun removeNode(node: Node) =
            when (node) {
                newNode -> parentGraph
                else -> NewNodeGraph(newNode, parentGraph.removeNode(node))
            }

    override fun removeEdge(edge: Edge) = NewNodeGraph(newNode, parentGraph.removeEdge(edge))

    override fun containsNode(node: Node) =
            when (node) {
                newNode -> true
                else -> parentGraph.containsNode(node)
            }

    override fun edges() = parentGraph.edges()
}


class NewEdgeGraph(val newEdge: Edge,
                   val parentGraph: Graph) : Graph() {
    override fun addNode(node: Node) = NewNodeGraph(node, this)

    override fun addEdge(edge: Edge) = NewEdgeGraph(edge, this)

    override fun removeNode(node: Node) = NewEdgeGraph(newEdge, parentGraph.removeNode(node))

    override fun removeEdge(edge: Edge) =
            when (edge) {
                newEdge -> parentGraph
                else -> NewEdgeGraph(newEdge, parentGraph.removeEdge(edge))
            }

    override fun containsNode(node: Node) = parentGraph.containsNode(node)

    override fun edges(): Set<Edge> {
        val parentEdges = parentGraph.edges()
        val edges = HashSet(parentEdges)
        edges.add(newEdge)
        return unmodifiableSet(edges)
    }
}

object EmptyGraph : Graph() {
    override fun addNode(node: Node) = NewNodeGraph(node, this)

    override fun addEdge(edge: Edge) = NewEdgeGraph(edge, this)

    override fun removeNode(node: Node) = throw NodeNotFound(node)

    override fun removeEdge(edge: Edge) = throw EdgeNotFound(edge)

    override fun containsNode(node: Node) = false

    override fun edges() = setOf<Edge>()
}