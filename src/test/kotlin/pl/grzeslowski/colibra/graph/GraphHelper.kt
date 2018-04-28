package pl.grzeslowski.colibra.graph

import org.springframework.stereotype.Service

@Service
class GraphHelper {
    fun newGraph(nodes: Set<Node>, edges: Set<Edge>): Graph {
        var graph = Graph()
        nodes.forEach { graph = graph.addNode(it) }
        edges.forEach { graph = graph.addEdge(it) }
        return graph
    }
}