package pl.grzeslowski.colibra.graph

import org.springframework.stereotype.Repository

interface GraphRepository {
    fun addNode(node: Node)

    fun addEdge(edge: Edge)

    fun removeNode(node: Node)

    fun removeEdge(edge: Edge)

    fun getReadOnlyGraph(): Graph
}

@Repository
class InMemoryGraphRepository : GraphRepository {
    private val lock = Any()
    private var graph: Graph = EmptyGraph

    override fun addNode(node: Node) {
        synchronized(lock) {
            graph = graph.addNode(node)
        }
    }

    override fun addEdge(edge: Edge) {
        synchronized(lock) {
            graph = graph.addEdge(edge)
        }
    }

    override fun removeNode(node: Node) {
        synchronized(lock) {
            graph = graph.removeNode(node)
        }
    }

    override fun removeEdge(edge: Edge) {
        synchronized(lock) {
            graph = graph.removeEdge(edge)
        }
    }

    override fun getReadOnlyGraph() = graph
}