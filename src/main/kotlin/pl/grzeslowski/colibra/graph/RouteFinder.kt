package pl.grzeslowski.colibra.graph

import org.springframework.stereotype.Service

interface RouteFinder {
    fun shortestPath(graph: Graph, from: Node, to: Node): Int
}

@Service
class RouteFinderImpl : RouteFinder {
    private val undefined = Int.MAX_VALUE

    /**
     * Nothing new see <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm">Dijkstra's algorithm</a>
     */
    override fun shortestPath(graph: Graph, from: Node, to: Node): Int {
        graph.checkNodes(from, to)
        val unvisitedSet = HashSet<Node>(graph.nodes())
        val distances = HashMap<Node, Int>()
        graph.nodes().forEach { node -> distances[node] = undefined }
        distances[from] = 0

        var current = from
        while (unvisitedSet.isNotEmpty()) {
            val currentDistance = distances[current]!!
            unvisitedNeighbors(current, unvisitedSet, graph)
                    .forEach { unvisitedNeighbor ->
                        val unvisitedNeighborDistance = distances[unvisitedNeighbor.node]!!
                        val newDistance = currentDistance + unvisitedNeighbor.weight
                        if (newDistance < unvisitedNeighborDistance) {
                            distances[unvisitedNeighbor.node] = newDistance
                        }
                    }
            unvisitedSet.remove(current)
            val newCurrent = distances.entries
                    .stream()
                    .filter { entry -> unvisitedSet.contains(entry.key) }
                    .sorted { o1, o2 -> o1.value.compareTo(o2.value) }
                    .map { it.key }
                    .findFirst()
            if (newCurrent.isPresent) {
                current = newCurrent.get()
            } else {
                break
            }

            if (distances[current] == undefined) {
                break
            }

            if (current == to) {
                break
            }
        }

        return distances[to]!!
    }

    private fun unvisitedNeighbors(node: Node, unvisitedSet: HashSet<Node>, graph: Graph) =
            graph.adjacencyList[node]!!
                    .stream()
                    .filter { neighbour -> unvisitedSet.contains(neighbour.node) }
}
