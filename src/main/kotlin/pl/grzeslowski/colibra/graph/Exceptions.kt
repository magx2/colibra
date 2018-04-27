package pl.grzeslowski.colibra.graph

class NodeAlreadyExists(node: Node) : RuntimeException("Node $node already exists!")

class NodeNotFound(node: Node) : RuntimeException("Node $node not found!")

class NodesNotFound(node1: Node, node2: Node) : RuntimeException("Nodes $node1, $node2 not found!")
