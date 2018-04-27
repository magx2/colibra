package pl.grzeslowski.colibra.graph

import org.springframework.stereotype.Service

@Service
class GraphHelper {
    fun newGraph(nodes: Set<Node>, edges: Set<Edge>): Graph {
        val withNodes = nodes.stream().reduce(EmptyGraph, { g, n -> g.addNode(n) }, this::graphCombiner)
        return edges.stream().reduce(withNodes, { g, edge -> g.addEdge(edge) }, this::graphCombiner)
    }

    private fun graphCombiner(g1: Graph, g2: Graph): Graph =
            when (g1) {
                is EmptyGraph -> g2
                is NewNodeGraph -> graphCombiner(g1.parentGraph, g2.addNode(g1.newNode))
                is NewEdgeGraph -> graphCombiner(g1.parentGraph, g2.addEdge(g1.newEdge))
            }
}