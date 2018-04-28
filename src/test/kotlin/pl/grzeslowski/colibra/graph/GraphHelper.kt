package pl.grzeslowski.colibra.graph

import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.Stream

@Service
class GraphHelper {
    fun newGraph(nodes: Set<Node>, edges: Set<Edge>): Graph {
        var graph = Graph()
        nodes.forEach { graph = graph.addNode(it) }
        edges.forEach { graph = graph.addEdge(it) }
        return graph
    }

    fun randomBigGraph(initGraph: Graph = Graph()): Graph {
        val random = Random()
        val nodes = IntStream.range(0, 1_000 + random.nextInt(1_000))
                .mapToObj { Node("node-$it") }
                .collect(Collectors.toList())

        var graphWithALotOfEdges = initGraph
        nodes.forEach { node ->
            graphWithALotOfEdges = graphWithALotOfEdges.addNode(node)
        }
        Stream.generate { randomEdge(nodes) }
                .parallel()
                .limit(100_000)
                .forEach { edge ->
                    graphWithALotOfEdges = graphWithALotOfEdges.addEdge(edge)
                }

        // when
        return graphWithALotOfEdges
    }

    private fun randomEdge(nodes: List<Node>): Edge {
        val random = Random()
        val from = nodes[random.nextInt(nodes.size)]
        val to = nodes[random.nextInt(nodes.size)]
        return Edge(from, to, random.nextInt(100))
    }
}