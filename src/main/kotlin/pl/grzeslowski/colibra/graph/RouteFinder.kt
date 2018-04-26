package pl.grzeslowski.colibra.graph

import org.springframework.stereotype.Service
import java.util.stream.Collectors

interface RouteFinder {
    fun shortestPath(graph: Graph, a: Node, b: Node): Int
}

@Service
class RouteFinderImpl : RouteFinder {
    override fun shortestPath(graph: Graph, a: Node, b: Node) = shortestPath(a, b, 0, graph.edges())

    private fun shortestPath(a: Node, b: Node, weight: Int, edges: Set<Edge>): Int =
            findAllEdges(a, edges)
                    .stream()
                    .mapToInt { edge ->
                        if (endsIn(b, edge)) {
                            weight + edge.weight
                        } else {
                            shortestPath(oppositeNode(a, edge), b, weight + edge.weight, removeEdge(edge, edges))
                        }
                    }
                    .min()
                    .orElse(Int.MAX_VALUE)

    private fun findAllEdges(node: Node, edges: Set<Edge>) =
            edges.stream()
                    .filter { edge -> endsIn(node, edge) }
                    .collect(Collectors.toSet())

    private fun endsIn(node: Node, edge: Edge) = edge.a == node || edge.b == node

    private fun oppositeNode(node: Node, edge: Edge) =
            when (node) {
                edge.a -> edge.b
                edge.b -> edge.a
                else -> throw IllegalArgumentException("Node $node is not in edge $edge!")
            }

    private fun removeEdge(edge: Edge, edges: Set<Edge>) =
            edges.stream()
                    .filter { it != edge }
                    .collect(Collectors.toSet())
}