package pl.grzeslowski.colibra.graph

import org.springframework.stereotype.Service
import java.util.SortedSet
import java.util.TreeSet
import java.util.stream.Collectors.toCollection
import kotlin.Comparator
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.collections.set

interface CloserThanService {
    fun closerThan(graph: Graph, node: Node, weight: Int): SortedSet<Node>
}

@Service
class CloserThatServiceImpl : CloserThanService {
    private val undefined = Int.MAX_VALUE
    private val comparator = Comparator<Node> { o1, o2 -> o1.name.compareTo(o2.name) }

    override fun closerThan(graph: Graph, node: Node, weight: Int): SortedSet<Node> {
        if (graph.containsNode(node).not()) {
            throw NodeNotFound(node)
        }

        val adjacencyList = graph.adjacencyList

        val distances = HashMap<Node, Int>()
        distances[node] = 0

        val toVisitNodes = HashSet<Node>()
        toVisitNodes.add(node)

        while (toVisitNodes.isNotEmpty()) {
            val current = removeFirst(toVisitNodes)
            val currentDistance = distances[current] ?: undefined

            val neighbours = adjacencyList[current]!!
            neighbours.forEach { neighbour ->
                val neighbourDistance = distances[neighbour.node] ?: undefined
                val newDistance = currentDistance + neighbour.weight
                if (neighbourDistance == undefined || neighbourDistance > newDistance) {
                    distances[neighbour.node] = newDistance
                }
                if (newDistance <= weight) {
                    toVisitNodes.add(neighbour.node)
                }
            }
        }

        return distances.entries
                .stream()
                .filter { it.value <= weight }
                .map { it.key }
                .filter { it != node }
                .collect(toCollection({ TreeSet(comparator) }))
    }

    private fun removeFirst(nodes: MutableSet<Node>): Node {
        val iterator = nodes.iterator()
        val node = iterator.next()
        iterator.remove()
        return node
    }
}
