package pl.grzeslowski.colibra.graph

class NodeAlreadyExists(node: Node) : RuntimeException("Node $node already exists!")

class EdgeAlreadyExists(edge: Edge) : RuntimeException("Edge $edge already exists!")

class NodeNotFound(node: Node) : RuntimeException("Node $node not found!")

class EdgeNotFound(edge: Edge) : RuntimeException("Edge $edge not found!")
