package fundies2.mst;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NodeTest {

    @Test
    public void findOneItemInSet() {
        Graph.Node<String> mexico = new Graph.Node<>("Mexico");
        assertEquals(mexico, mexico.find());

        Graph.Node<Integer> one = new Graph.Node<>(1);
        assertEquals(one, one.find());
    }

    @Test
    public void findMultipleItemsInSet() {
        Graph.Node<String> mexico = new Graph.Node<>("Mexico");
        Graph.Node<String> canada = new Graph.Node<>("Canada");
        mexico.union(canada);
        assertEquals(mexico.find(), canada.find());

        Graph.Node<String> usa = new Graph.Node<>("USA");
        mexico.union(usa);
        assertEquals(mexico.find(), usa.find());
        assertEquals(canada.find(), usa.find());

        Graph.Node<Integer> one = new Graph.Node<>(1);
        Graph.Node<Integer> two = new Graph.Node<>(2);
        Graph.Node<Integer> three = new Graph.Node<>(3);
        Graph.Node<Integer> four = new Graph.Node<>(4);
        Graph.Node<Integer> five = new Graph.Node<>(5);
        one.union(two);
        two.union(three);
        four.union(five);
        three.union(five);
        assertEquals(one.find(), two.find());
        assertEquals(two.find(), five.find());
        assertEquals(four.find(), three.find());
        assertEquals(five.find(), one.find());
    }
}
