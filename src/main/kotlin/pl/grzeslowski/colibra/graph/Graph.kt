package pl.grzeslowski.colibra.graph

import java.util.Collections.unmodifiableSet
import java.util.stream.Collectors

class Graph private constructor(private val nodes: Set<Node>, private val edges: Set<Edge>) {
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
        val containsFrom = containsNode(edge.from)
        val containsTo = containsNode(edge.to)
        if (containsFrom.not() && containsTo.not()) {
            throw NodesNotFound(edge.from, edge.to)
        } else if (containsFrom.not()) {
            throw NodeNotFound(edge.from)
        } else if (containsTo.not()) {
            throw NodeNotFound(edge.to)
        }
        val newEdges = HashSet<Edge>(edges)
        newEdges.add(edge)
        return Graph(nodes, unmodifiableSet(newEdges))
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
