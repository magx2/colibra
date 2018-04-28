package pl.grzeslowski.colibra.graph

import org.springframework.stereotype.Service
import java.util.*

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

        var queue: Element? = initQueue(Element(from, null, null), graph)

        while (unvisitedSet.isNotEmpty() && queue != null) {
            val current = queue.node
            if (queue.next != null) {
                queue = queue.next!!
                queue.prev = null
            } else {
                break
            }

            if (distances[current] == undefined) {
                break
            }

            if (current == to) {
                break
            }

            val currentDistance = distances[current]!!
            unvisitedNeighbors(current, unvisitedSet, graph)
                    .forEach { unvisitedNeighbor ->
                        val unvisitedNeighborDistance = distances[unvisitedNeighbor.node]!!
                        val newDistance = currentDistance + unvisitedNeighbor.weight
                        if (newDistance < unvisitedNeighborDistance) {
                            queue = setNewDistance(distances, unvisitedNeighbor.node, newDistance, queue!!)
                        }
                    }
            unvisitedSet.remove(current)
        }

        return distances[to]!!
    }

    private fun initQueue(queue: Element, graph: Graph): Element {
        var last = queue
        graph.nodes().forEach { node ->
            if (node != queue.node) {
                val new = Element(node, last, null)
                last.next = new
                last = new
            }
        }
        return queue
    }

    private fun remove(queue: Element, node: Node): Element? {
        var element: Element? = queue
        while (true) {
            when {
                element == null -> return null
                element.node == node -> return when {
                    element.prev == null -> queue.next
                    element.next == null -> {
                        element.prev = null
                        queue
                    }
                    else -> {
                        val prev = element.prev!!
                        val next = element.next!!
                        prev.next = next
                        next.prev = prev
                        queue
                    }
                }
                else -> element = element.next
            }
        }
    }

    private fun setNewDistance(distances: HashMap<Node, Int>, unvisitedNeighbor: Node, newDistance: Int, queue: Element): Element {
        distances[unvisitedNeighbor] = newDistance

        val nodeInQueue = findNodeInQueue(queue, unvisitedNeighbor)


        while (true) {
            if (nodeInQueue.prev == null) {
                return nodeInQueue
            }
            val prev = nodeInQueue.prev!!
            val prevDistance = distances[prev.node]!!
            if (prevDistance > newDistance) {
                val oldNext = nodeInQueue.next
                val oldPrevPrev = prev.prev

                nodeInQueue.prev = oldPrevPrev
                nodeInQueue.next = prev
                if (oldPrevPrev != null) {
                    oldPrevPrev.next = nodeInQueue
                }

                prev.prev = nodeInQueue
                prev.next = oldNext

                if (oldNext != null) {
                    oldNext.prev = prev
                }
                if (oldPrevPrev != null) {
                    oldPrevPrev.next = nodeInQueue
                }
            } else {
                var returnQueue = nodeInQueue
                while (returnQueue.prev != null) {
                    returnQueue = returnQueue.prev!!
                }
                return returnQueue
            }
        }
    }

    private fun findNodeInQueue(queue: Element, unvisitedNeighbor: Node): Element {
        var nodeInQueue: Element = queue
        while (nodeInQueue.node != unvisitedNeighbor) {
            nodeInQueue = nodeInQueue.next!!
        }
        return nodeInQueue
    }

    private fun unvisitedNeighbors(node: Node, unvisitedSet: HashSet<Node>, graph: Graph) =
            graph.adjacencyList[node]!!
                    .stream()
                    .filter { neighbour -> unvisitedSet.contains(neighbour.node) }
}

private data class Element(val node: Node, var prev: Element?, var next: Element?) {
    override fun toString(): String {
        return "Element($node, prev = ${prev != null}, next = ${next != null})"
    }
}