package pl.grzeslowski.colibra.graph

import org.springframework.stereotype.Repository

interface GraphRepository {
    fun getGraph(): Graph

    fun saveGraph(graph: Graph)
}

@Repository
class InMemoryGraphRepository : GraphRepository {
    private var graph: Graph = EmptyGraph

    override fun getGraph() = graph

    // TODO add some docs...
    override fun saveGraph(graph: Graph) {
        this.graph = graph
    }
}