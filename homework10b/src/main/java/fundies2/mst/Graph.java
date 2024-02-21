package fundies2.mst;

import java.util.*;

/**
 * A graph.
 *
 * @param <T> the type of data held in nodes of the graph
 */
public class Graph<T> {
    private final List<Node<T>> nodes = new ArrayList<>();
    private final List<Edge<T>> edges = new ArrayList<>();
    private static long modCount = 0;

    /**
     * Constructs an empty graph.
     */
    public Graph() {
    }

    /**
     * Gets the edges of this graph.
     *
     * @return the edges of this graph
     */
    public List<Edge<T>> getEdges() {
        return edges;
    }

    private void addEdge(Edge<T> edge) {
        edges.add(edge);
        modCount++;
    }

    /**
     * Adds an edge to this graph.
     *
     * @param node1 the node at one end of the edge
     * @param node2 the node at the other end of the edge
     * @param weight the weight of the edge
     */
    public void addEdge(Node<T> node1, Node<T> node2, int weight) {
        addEdge(new Edge<>(node1, node2, weight));
    }

    /**
     * Returns an iterator over the edges of a Minimum Spanning Tree of
     * this graph in the order they are added by Kruskal's Algorithm.
     *
     * @return an iterator over the edges of this graph's MST
     */
    public Iterator<Edge<T>> getKruskalIterator() {
        return new KruskalIterator<>(this);
    }

    private static class KruskalIterator<T> implements Iterator<Edge<T>> {
        private int position = 0;
        private final long modCountOnCreation = modCount;
        private List<Edge<T>> set;

        KruskalIterator(Graph<T> graph) {
            set = new ArrayList<>();
            PriorityQueue<Edge> priorityQueue = new PriorityQueue<>(graph.edges);

            while (!priorityQueue.isEmpty()) {
                Edge<T> edge = priorityQueue.poll();
                Node<T> node1 = edge.node1;
                Node<T> node2 = edge.node2;
                if (node1.find().equals(node2.find())) {
                    continue;
                }
                node1.union(node2);
                set.add(edge);
            }
        }

        private void checkModCount() {
            if (modCount != modCountOnCreation) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public boolean hasNext() {
            checkModCount();
            return position < set.size();
        }

        @Override
        public Edge<T> next() {
            checkModCount();
            if (hasNext()) {
                return set.get(position++);
            }
            throw new NoSuchElementException();
        }
    }

    public void addNode(T data) {
        nodes.add(new Node<>(data));
    }

    public List<Node<T>> getNodes() {
        return nodes;
    }

    public static class Node<T> {
        private final T data;
        private Node<T> parent;
        private int rank;

        public Node(T data) {
            this.data = data;
            this.parent = this;
            this.rank = 0;
        }

        public T getData() {
            return data;
        }

        /**
         * Finds the representative of the set to which this node belongs.
         *
         * @return the representative
         */
        public Node<T> find() {
            Node<T> node = this;
            while (node.parent != node) {
                node = node.parent;
            }
            return node;
        }

        /**
         * Merges the two sets to which this node and the other node belong.
         *
         * @param other the other node
         */
        public void union(Node<T> other) {
            Node<T> root1 = this.find();
            Node<T> root2 = other.find();
            if (root1 != root2) {
                if (root1.rank > root2.rank) {
                    root2.parent = root1;
                } else {
                    root1.parent = root2.parent;
                    if (root1.rank == root2.rank) {
                        root2.rank++;
                    }
                }
            }
        }
    }

    public static class Edge<T> implements Comparable<Edge<T>> {
        private final Node<T> node1;
        private final Node<T> node2;
        private final int weight;

        public Edge(Node<T> node1, Node<T> node2, int weight) {
            this.node1 = node1;
            this.node2 = node2;
            this.weight = weight;
        }

        public Node<T> getNode1() {
            return node1;
        }

        public Node<T> getNode2() {
            return node2;
        }

        public int getWeight() {
            return weight;
        }

        @Override
        public int compareTo(Edge<T> other) {
            return Integer.signum(this.weight - other.weight);
        }
    }
}
