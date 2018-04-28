package pl.grzeslowski.colibra.graph

import java.util.Collections.unmodifiableSet
import java.util.stream.Collectors

class Graph private constructor(private val nodes: Set<Node>, private val edges: Set<Edge>) {
    val adjacencyList: Map<Node, Set<AdjacencyNode>> by lazy {
        val adjacencyList = HashMap<Node, HashSet<AdjacencyNode>>()

        nodes.forEach { node -> adjacencyList[node] = HashSet() }
        edges.forEach { edge ->
            val set = adjacencyList[edge.from]!!
            set.add(AdjacencyNode(edge.to, edge.weight))
        }

        adjacencyList
    }

    constructor() : this(setOf(), setOf())

    fun addNode(node: Node): Graph {
        if (containsNode(node)) {
            throw NodeAlreadyExists(node)
        }
        val newNodes = HashSet<Node>(nodes)
        newNodes.add(node)
        return Graph(unmodifiableSet(newNodes), edges)
    }

    fun addEdge(edge: Edge): Graph {
        checkNodes(edge.from, edge.to)
        val newEdges = HashSet<Edge>(edges)
        newEdges.add(edge)
        return Graph(nodes, unmodifiableSet(newEdges))
    }

    fun checkNodes(node1: Node, node2: Node) {
        val containsFrom = containsNode(node1)
        val containsTo = containsNode(node2)
        if (containsFrom.not() && containsTo.not()) {
            throw NodesNotFound(node1, node2)
        } else if (containsFrom.not()) {
            throw NodeNotFound(node1)
        } else if (containsTo.not()) {
            throw NodeNotFound(node2)
        }
    }

    fun removeNode(node: Node) =
            if (containsNode(node)) {
                Graph(
                        nodes.stream()
                                .filter { n -> n != node }
                                .collect(Collectors.toSet()),
                        edges.stream()
                                .filter { edge -> edge.from != node }
                                .filter { edge -> edge.to != node }
                                .collect(Collectors.toSet())
                )
            } else {
                throw NodeNotFound(node)
            }

    fun removeEdge(from: Node, to: Node) =
            Graph(
                    nodes,
                    edges.stream()
                            .filter { edge -> edge.from != from }
                            .filter { edge -> edge.to != to }
                            .collect(Collectors.toSet())
            )

    fun containsNode(node: Node) =
            nodes.stream()
                    .anyMatch { n -> n == node }

    fun edges() = edges

    fun nodes() = nodes

    override fun toString(): String {
        val nodesName = nodes.stream()
                .map { it.name }
                .collect(Collectors.joining(",", "nodes = [", "]"))
        val edgesName = edges.stream()
                .map { it.toString() }
                .collect(Collectors.joining(",", "edges = [", "]"))
        return "Graph[$nodesName, $edgesName]"
    }
}

data class Node(val name: String)

data class Edge(val from: Node, val to: Node, val weight: Int)

data class AdjacencyNode(val node: Node, val weight: Int)
