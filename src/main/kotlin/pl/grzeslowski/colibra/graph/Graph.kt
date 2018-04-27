package pl.grzeslowski.colibra.graph

import java.util.*
import java.util.Collections.unmodifiableSet

sealed class Graph {
    abstract fun addNode(node: Node): Graph

    abstract fun addEdge(edge: Edge): Graph

    abstract fun removeNode(node: Node): Graph

    abstract fun removeEdge(from: Node, to: Node): Graph

    abstract fun removeEdge(from: Node, to: Node, fromFound: Boolean, toFound: Boolean): Graph

    abstract fun containsNode(node: Node): Boolean

    abstract fun edges(): Set<Edge>
}

data class Node(val name: String)

data class Edge(val from: Node, val to: Node, val weight: Int)

class NewNodeGraph(val newNode: Node,
                   val parentGraph: Graph) : Graph() {
    init {
        if (parentGraph.containsNode(newNode)) {
            throw NodeAlreadyExists(newNode)
        }
    }

    override fun addNode(node: Node) = NewNodeGraph(node, this)

    override fun addEdge(edge: Edge) = NewEdgeGraph(edge, this)

    override fun removeNode(node: Node) =
            when (node) {
                newNode -> parentGraph
                else -> NewNodeGraph(newNode, parentGraph.removeNode(node))
            }

    override fun removeEdge(from: Node, to: Node) = removeEdge(from, to, false, false)

    override fun removeEdge(from: Node, to: Node, fromFound: Boolean, toFound: Boolean): Graph {
        val newParentGraph = when (newNode) {
            from -> parentGraph.removeEdge(from, to, true, toFound)
            to -> parentGraph.removeEdge(from, to, fromFound, true)
            else -> parentGraph.removeEdge(from, to, fromFound, toFound)
        }
        return NewNodeGraph(newNode, newParentGraph)
    }

    override fun containsNode(node: Node) =
            when (node) {
                newNode -> true
                else -> parentGraph.containsNode(node)
            }

    override fun edges() = parentGraph.edges()
}


class NewEdgeGraph(val newEdge: Edge,
                   val parentGraph: Graph) : Graph() {
    init {
        if (parentGraph.containsNode(newEdge.from).not()) {
            throw NodeNotFound(newEdge.from)
        }
        if (parentGraph.containsNode(newEdge.to).not()) {
            throw NodeNotFound(newEdge.to)
        }
    }

    override fun addNode(node: Node) = NewNodeGraph(node, this)

    override fun addEdge(edge: Edge) = NewEdgeGraph(edge, this)

    override fun removeNode(node: Node) =
            when (node) {
                newEdge.from -> parentGraph.removeNode(node)
                newEdge.to -> parentGraph.removeNode(node)
                else -> NewEdgeGraph(newEdge, parentGraph.removeNode(node))
            }

    override fun removeEdge(from: Node, to: Node) = removeEdge(from, to, false, false)

    override fun removeEdge(from: Node, to: Node, fromFound: Boolean, toFound: Boolean) =
            if (newEdge.from == from && newEdge.to == to) {
                parentGraph.removeEdge(from, to, fromFound, toFound)
            } else {
                NewEdgeGraph(newEdge, parentGraph.removeEdge(from, to, fromFound, toFound))
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

    override fun removeEdge(from: Node, to: Node) = removeEdge(from, to, false, false)

    override fun removeEdge(from: Node, to: Node, fromFound: Boolean, toFound: Boolean) =
            if (fromFound.not() && toFound.not()) {
                throw NodesNotFound(from, to)
            } else if (fromFound.not()) {
                throw NodeNotFound(from)
            } else if (toFound.not()) {
                throw NodeNotFound(to)
            } else {
                this
            }

    override fun containsNode(node: Node) = false

    override fun edges() = setOf<Edge>()
}