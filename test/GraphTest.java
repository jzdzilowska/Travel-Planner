package test;

import org.junit.Assert;
import org.junit.Test;
import sol.City;
import sol.TravelGraph;
import test.simple.SimpleEdge;
import test.simple.SimpleGraph;
import test.simple.SimpleVertex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Your Graph method tests should all go in this class!
 * The test we've given you will pass, but we still expect you to write more tests using the
 * City and Transport classes.
 * You are welcome to write more tests using the Simple classes, but you will not be graded on
 * those.
 *
 * TODO: Recreate the test below for the City and Transport classes
 * TODO: Expand on your tests, accounting for basic cases and edge cases
 */
public class GraphTest {
    private SimpleGraph graph;

    private SimpleVertex a;
    private SimpleVertex b;
    private SimpleVertex c;

    private SimpleEdge edgeAB;
    private SimpleEdge edgeBC;
    private SimpleEdge edgeCA;
    private SimpleEdge edgeAC;

    /**
     * Creates a simple graph.
     * You'll find a similar method in each of the Test files.
     * Normally, we'd like to use @Before, but because each test may require a different setup,
     * we manually call the setup method at the top of the test.
     *
     * TODO: create more setup methods!
     */
    private void createSimpleGraph() {
        this.graph = new SimpleGraph();

        this.a = new SimpleVertex("A");
        this.b = new SimpleVertex("B");
        this.c = new SimpleVertex("C");

        this.graph.addVertex(this.a);
        this.graph.addVertex(this.b);
        this.graph.addVertex(this.c);

        // create and insert edges
        this.edgeAB = new SimpleEdge(1, this.a, this.b);
        this.edgeBC = new SimpleEdge(1, this.b, this.c);
        this.edgeCA = new SimpleEdge(1, this.c, this.a);
        this.edgeAC = new SimpleEdge(1, this.a, this.c);

        this.graph.addEdge(this.a, this.edgeAB);
        this.graph.addEdge(this.b, this.edgeBC);
        this.graph.addEdge(this.c, this.edgeCA);
        this.graph.addEdge(this.a, this.edgeAC);
    }

    /**
     * Sample test for the graph. Tests that the number of vertices and the vertices in the graph are what we expect.
     */
    @Test
    public void testGetVertices() {
        this.createSimpleGraph();

        // test getVertices to check this method AND insertVertex
        assertEquals(this.graph.getVertices().size(), 3);
        assertTrue(this.graph.getVertices().contains(this.a));
        assertTrue(this.graph.getVertices().contains(this.b));
        assertTrue(this.graph.getVertices().contains(this.c));
    }

    /**
     * Tests the addition of vertices to the graph and the getCity method
     */
    @Test
    public void testGetCity() {
        City Boston = new City("Boston");
        City bigCity = new City("Big City");
        TravelGraph graph = new TravelGraph();
        graph.addVertex(Boston);
        graph.addVertex(bigCity);

        Assert.assertEquals(Boston, graph.getCity("Boston"));
        Assert.assertEquals(bigCity, graph.getCity("Big City"));
        Assert.assertEquals(2, graph.getVertices().size());
    }
}
