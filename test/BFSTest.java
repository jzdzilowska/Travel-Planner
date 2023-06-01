package test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sol.BFS;
import sol.City;
import sol.Transport;
import sol.TravelGraph;
import src.IBFS;
import src.TransportType;
import test.simple.SimpleEdge;
import test.simple.SimpleGraph;
import test.simple.SimpleVertex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

/**
 * Your BFS tests should all go in this class!
 * The test we've given you will pass if you've implemented BFS correctly, but we still expect
 * you to write more tests using the City and Transport classes.
 * You are welcome to write more tests using the Simple classes, but you will not be graded on
 * those.
 *
 * TODO: Recreate the test below for the City and Transport classes
 * TODO: Expand on your tests, accounting for basic cases and edge cases
 */
public class BFSTest {

    private static final double DELTA = 0.001;

    private SimpleVertex a;
    private SimpleVertex b;
    private SimpleVertex c;
    private SimpleVertex d;
    private SimpleVertex e;
    private SimpleVertex f;
    private SimpleGraph graph;
    private TravelGraph ourGraph1;
    private TravelGraph ourGraph2;


    /**
     * Creates a simple graph.
     * You'll find a similar method in each of the Test files.
     * Normally, we'd like to use @Before, but because each test may require a different setup,
     * we manually call the setup method at the top of the test.
     *
     * TODO: create more setup methods!
     */
    public void makeSimpleGraph() {
        this.graph = new SimpleGraph();

        this.a = new SimpleVertex("a");
        this.b = new SimpleVertex("b");
        this.c = new SimpleVertex("c");
        this.d = new SimpleVertex("d");
        this.e = new SimpleVertex("e");
        this.f = new SimpleVertex("f");

        this.graph.addVertex(this.a);
        this.graph.addVertex(this.b);
        this.graph.addVertex(this.c);
        this.graph.addVertex(this.d);
        this.graph.addVertex(this.e);
        this.graph.addVertex(this.f);

        this.graph.addEdge(this.a, new SimpleEdge(1, this.a, this.b));
        this.graph.addEdge(this.b, new SimpleEdge(1, this.b, this.c));
        this.graph.addEdge(this.c, new SimpleEdge(1, this.c, this.e));
        this.graph.addEdge(this.d, new SimpleEdge(1, this.d, this.e));
        this.graph.addEdge(this.a, new SimpleEdge(100, this.a, this.f));
        this.graph.addEdge(this.f, new SimpleEdge(100, this.f, this.e));
    }

    /**
     * A sample test that tests BFS on a simple graph. Checks that running BFS gives us the path we expect.
     */
    @Test
    public void testBasicBFS() {
        this.makeSimpleGraph();
        BFS<SimpleVertex, SimpleEdge> bfs = new BFS<>();
        List<SimpleEdge> path = bfs.getPath(this.graph, this.a, this.e);
        assertEquals(SimpleGraph.getTotalEdgeWeight(path), 200.0, DELTA);
        assertEquals(path.size(), 2);
    }

    /**
     * Creates a graph that tests two different method of transportation between cities with differing weights;
     * Tests cyclic routes, and routes that when prioritizing one of the weights, require taking a path
     * worse in terms of the second weight.
     */
    @Before
    public void createOurGraph1() {
        this.ourGraph1 = new TravelGraph();

        City cordoba = new City("Cordoba");
        City hamilton = new City("Hamilton");
        City southSentinelVillage = new City("South Sentinel Village");

        this.ourGraph1.addVertex(cordoba);
        this.ourGraph1.addVertex(hamilton);
        this.ourGraph1.addVertex(southSentinelVillage);

        this.ourGraph1.addEdge(cordoba, new Transport(cordoba, hamilton, TransportType.PLANE, 3, 10));
        this.ourGraph1.addEdge(hamilton, new Transport(hamilton, cordoba, TransportType.PLANE, 3, 10));
        this.ourGraph1.addEdge(cordoba, new Transport(cordoba, southSentinelVillage, TransportType.TRAIN, 1, 20));
        this.ourGraph1.addEdge(southSentinelVillage, new Transport(southSentinelVillage, cordoba, TransportType.PLANE, 2, 2));
        this.ourGraph1.addEdge(southSentinelVillage, new Transport(southSentinelVillage, hamilton, TransportType.PLANE, 1, 10));
    }


    /**
     * Creates a graph that tests secluded cities, one-sided routes,
     * different methods of transportation on the same path (but with differing values which influence the choice
     * depending on the function)
     */
    @Before
    public void createOurGraph2() {
        this.ourGraph2 = new TravelGraph();

        City chicago = new City("Chicago");
        City krakow = new City("Krakow");
        City boston = new City("Boston");
        City montgomery = new City("Montgomery");

        this.ourGraph2.addVertex(chicago);
        this.ourGraph2.addVertex(krakow);
        this.ourGraph2.addVertex(boston);
        this.ourGraph2.addVertex(montgomery);

        this.ourGraph2.addEdge(krakow, new Transport(krakow, chicago, TransportType.PLANE, 100, 50000));
        this.ourGraph2.addEdge(chicago, new Transport(chicago, boston, TransportType.TRAIN, 20, 10));
        this.ourGraph2.addEdge(chicago, new Transport(chicago, boston, TransportType.PLANE, 10, 20));
        this.ourGraph2.addEdge(boston, new Transport(boston, chicago, TransportType.BUS, 500, 1440));
    }

    /**
     * Test that checks a cyclic route between two cities by comparing both the size, and the actual values
     * of the method's output
     */
    @Test
    public void testCycles() {
        IBFS<City, Transport> bfs = new BFS<>();

        // checks the size
        Assert.assertEquals(1, bfs.getPath(this.ourGraph1, this.ourGraph1.getCity("Hamilton"), this.ourGraph1.getCity("Cordoba")).size());

        // checks the actual values
        String path = "[Hamilton -> Cordoba, Type: plane, Cost: $3.0, Duration: 10.0 minutes]";
        assertEquals(path, bfs.getPath(this.ourGraph1, this.ourGraph1.getCity("Hamilton"), this.ourGraph1.getCity("Cordoba")).toString());
    }

    /**
     * Tests route from city to the same city
     */
    @Test
    public void sameCity() {
        IBFS<City, Transport> bfs = new BFS<>();

        // checks the size of the output
        Assert.assertEquals(0, bfs.getPath(this.ourGraph1, this.ourGraph1.getCity("Cordoba"), this.ourGraph1.getCity("Cordoba")).size());
        Assert.assertEquals(0, bfs.getPath(this.ourGraph1, this.ourGraph1.getCity("Cordoba"), this.ourGraph1.getCity("Cordoba")).size());

        // checks the value of the output
        ArrayList<Transport> emptyArr = new ArrayList<>();
        Assert.assertEquals(emptyArr.toString(), bfs.getPath(this.ourGraph1, this.ourGraph1.getCity("Cordoba"), this.ourGraph1.getCity("Cordoba")).toString());
        Assert.assertTrue(bfs.getPath(this.ourGraph1, this.ourGraph1.getCity("Cordoba"), this.ourGraph1.getCity("Cordoba")).isEmpty());
    }

    /**
     * Tests the possibility of getting from a secluded (unconnected) city to another one
     */
    @Test
    public void testSecluded() {
        IBFS<City, Transport> bfs = new BFS<>();
        Assert.assertTrue(bfs.getPath(this.ourGraph2, this.ourGraph2.getCity("Montgomery"), this.ourGraph2.getCity("Boston")).isEmpty());
    }

    /**
     * Tests one-sided routes (whether it's possible to get from a destination-only city to a source city)
     */
    @Test
    public void testOneSided() {
        IBFS<City, Transport> bfs = new BFS<>();
        Assert.assertEquals(0, bfs.getPath(this.ourGraph2, this.ourGraph2.getCity("Chicago"), this.ourGraph2.getCity("Krakow")).size());
    }
}
