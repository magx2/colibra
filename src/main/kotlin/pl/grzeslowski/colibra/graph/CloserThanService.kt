package pl.grzeslowski.colibra.graph

import org.springframework.stereotype.Service
import java.util.SortedSet
import java.util.TreeSet
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.Comparator
import kotlin.collections.HashSet

interface CloserThanService {
    fun closerThan(graph: Graph, node: Node, weight: Int): SortedSet<Node>
}

@Service
class CloserThatServiceImpl : CloserThanService {
    private val comparator = Comparator<Node> { o1, o2 -> o1.name.compareTo(o2.name) }

    override fun closerThan(graph: Graph, node: Node, weight: Int): SortedSet<Node> =
            if (graph.containsNode(node)) {
                closerThan(graph.edges(), node, weight)
                        .collect(Collectors.toCollection({ TreeSet<Node>(comparator) }))
            } else {
                throw NodeNotFound(node)
            }

    private fun closerThan(edges: Set<Edge>, node: Node, weight: Int, sumWeight: Int = 0, nodes: Set<Node> = setOf()): Stream<Node> =
            Stream.concat(
                    edges.stream()
                            .filter { edge -> edge.from == node }
                            .filter { edge -> sumWeight + edge.weight <= weight }
                            .flatMap { edge ->
                                closerThan(removeEdge(edge, edges), edge.to, weight, sumWeight + edge.weight, addNode(edge.to, nodes))
                            },
                    nodes.stream())

    private fun addNode(node: Node, nodes: Set<Node>): Set<Node> {
        val new = HashSet(nodes)
        new.add(node)
        return new
    }

    private fun removeEdge(edge: Edge, edges: Set<Edge>) =
            edges.stream()
                    .filter { e -> e != edge }
                    .collect(Collectors.toSet())

}
