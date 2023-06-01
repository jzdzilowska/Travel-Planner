package test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sol.City;
import sol.Dijkstra;
import sol.Transport;
import sol.TravelGraph;
import src.IDijkstra;
import src.TransportType;
import test.simple.SimpleEdge;
import test.simple.SimpleGraph;
import test.simple.SimpleVertex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Your Dijkstra's tests should all go in this class!
 * The test we've given you will pass if you've implemented Dijkstra's correctly, but we still
 * expect you to write more tests using the City and Transport classes.
 * You are welcome to write more tests using the Simple classes, but you will not be graded on
 * those.
 *
 * TODO: Recreate the test below for the City and Transport classes
 * TODO: Expand on your tests, accounting for basic cases and edge cases
 */
public class DijkstraTest {

    private static final double DELTA = 0.001;

    private SimpleGraph graph;
    private TravelGraph ourGraph1;
    private TravelGraph ourGraph2;
    private SimpleVertex a;
    private SimpleVertex b;
    private SimpleVertex c;
    private SimpleVertex d;
    private SimpleVertex e;


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

        this.a = new SimpleVertex("a");
        this.b = new SimpleVertex("b");
        this.c = new SimpleVertex("c");
        this.d = new SimpleVertex("d");
        this.e = new SimpleVertex("e");

        this.graph.addVertex(this.a);
        this.graph.addVertex(this.b);
        this.graph.addVertex(this.c);
        this.graph.addVertex(this.d);
        this.graph.addVertex(this.e);

        this.graph.addEdge(this.a, new SimpleEdge(100, this.a, this.b));
        this.graph.addEdge(this.a, new SimpleEdge(3, this.a, this.c));
        this.graph.addEdge(this.a, new SimpleEdge(1, this.a, this.e));
        this.graph.addEdge(this.c, new SimpleEdge(6, this.c, this.b));
        this.graph.addEdge(this.c, new SimpleEdge(2, this.c, this.d));
        this.graph.addEdge(this.d, new SimpleEdge(1, this.d, this.b));
        this.graph.addEdge(this.d, new SimpleEdge(5, this.e, this.d));
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
     * A sample test that tests Dijkstra's on a simple graph. Checks that the shortest path using Dijkstra's is what we
     * expect.
     */
    @Test
    public void testSimple() {
        IDijkstra<SimpleVertex, SimpleEdge> dijkstra = new Dijkstra<>();
        Function<SimpleEdge, Double> edgeWeightCalculation = e -> e.weight;
        // a -> c -> d -> b
        List<SimpleEdge> path = dijkstra.getShortestPath(this.graph, this.a, this.b, edgeWeightCalculation);
        assertEquals(6, SimpleGraph.getTotalEdgeWeight(path), DELTA);
        assertEquals(3, path.size());

        // c -> d -> b
        path = dijkstra.getShortestPath(this.graph, this.c, this.b, edgeWeightCalculation);
        assertEquals(3, SimpleGraph.getTotalEdgeWeight(path), DELTA);
        assertEquals(2, path.size());
    }

    /**
     * Test that checks a cyclic route between two cities by comparing both the size, and the actual values
     * of the method's output
     */
    @Test
    public void testCycles() {
        IDijkstra<City, Transport> dijkstra = new Dijkstra<>();
        Function<Transport, Double> priceCalculation = e -> e.getPrice();

        // checks the size
        Assert.assertEquals(1, dijkstra.getShortestPath(this.ourGraph1, this.ourGraph1.getCity("Hamilton"), this.ourGraph1.getCity("Cordoba"), priceCalculation).size());

        // checks the actual values
        String path = "[Hamilton -> Cordoba, Type: plane, Cost: $3.0, Duration: 10.0 minutes]";
        Assert.assertEquals(path, dijkstra.getShortestPath(this.ourGraph1, this.ourGraph1.getCity("Hamilton"), this.ourGraph1.getCity("Cordoba"), priceCalculation).toString());
    }

    /**
     * Test that checks routes that require taking a path worse in terms of another priority
     * (in this case, taking the cheaper route takes more time, and comprises more segments).
     */
    @Test
    public void testPriority() {
        IDijkstra<City, Transport> dijkstra = new Dijkstra<>();
        Function<Transport, Double> priceCalculation = e -> e.getPrice();

        // checks the size - route that's cheaper but longer
        Assert.assertEquals(2, dijkstra.getShortestPath(this.ourGraph1, this.ourGraph1.getCity("Cordoba"), this.ourGraph1.getCity("Hamilton"), priceCalculation).size());

        // checks the actual values - route that's cheaper but longer
        String path = "[Cordoba -> South Sentinel Village, Type: train, Cost: $1.0, Duration: 20.0 minutes, South Sentinel Village -> Hamilton, Type: plane, Cost: $1.0, Duration: 10.0 minutes]";
        Assert.assertEquals(path, dijkstra.getShortestPath(this.ourGraph1, this.ourGraph1.getCity("Cordoba"), this.ourGraph1.getCity("Hamilton"), priceCalculation).toString());
    }

    /**
     * Tests route from city to the same city
     */
    @Test
    public void sameCity() {
        IDijkstra<City, Transport> dijkstra = new Dijkstra<>();
        Function<Transport, Double> priceCalculation = e -> e.getPrice();
        Function<Transport, Double> durationCalculation = e -> e.getMinutes();

        // checks the size of the output
        Assert.assertEquals(0, dijkstra.getShortestPath(this.ourGraph1, this.ourGraph1.getCity("Cordoba"), this.ourGraph1.getCity("Cordoba"), priceCalculation).size());
        Assert.assertEquals(0, dijkstra.getShortestPath(this.ourGraph1, this.ourGraph1.getCity("Cordoba"), this.ourGraph1.getCity("Cordoba"), durationCalculation).size());

        // checks the value of the output
        ArrayList<Transport> emptyArr = new ArrayList<>();
        Assert.assertEquals(emptyArr.toString(), dijkstra.getShortestPath(this.ourGraph1, this.ourGraph1.getCity("Cordoba"), this.ourGraph1.getCity("Cordoba"), priceCalculation).toString());
        Assert.assertTrue(dijkstra.getShortestPath(this.ourGraph1, this.ourGraph1.getCity("Cordoba"), this.ourGraph1.getCity("Cordoba"), priceCalculation).isEmpty());
    }

    /**
     * Tests the possibility of getting from a secluded (unconnected) city to another one
     */
    @Test
    public void testSecluded() {
        IDijkstra<City, Transport> dijkstra = new Dijkstra<>();
        Function<Transport, Double> priceCalculation = e -> e.getPrice();

        Assert.assertTrue(dijkstra.getShortestPath(this.ourGraph2, this.ourGraph2.getCity("Montgomery"), this.ourGraph2.getCity("Boston"), priceCalculation).isEmpty());
    }

    /**
     * Tests routes with multiple transport types for one path where the priority influences the result
     */
    @Test
    public void testDifferentTransports() {
        IDijkstra<City, Transport> dijkstra = new Dijkstra<>();
        Function<Transport, Double> durationCalculation = e -> e.getMinutes();
        Function<Transport, Double> priceCalculation = e -> e.getPrice();

        String fasterPath = "[Chicago -> Boston, Type: train, Cost: $20.0, Duration: 10.0 minutes]";
        String cheaperPath = "[Chicago -> Boston, Type: plane, Cost: $10.0, Duration: 20.0 minutes]";
        Assert.assertEquals(fasterPath, dijkstra.getShortestPath(this.ourGraph2, this.ourGraph2.getCity("Chicago"), this.ourGraph2.getCity("Boston"), durationCalculation).toString());
        Assert.assertEquals(cheaperPath, dijkstra.getShortestPath(this.ourGraph2, this.ourGraph2.getCity("Chicago"), this.ourGraph2.getCity("Boston"), priceCalculation).toString());
    }

    /**
     * Tests one-sided routes (whether it's possible to get from a destination-only city to a source city)
     */
    @Test
    public void testOneSided() {
        IDijkstra<City, Transport> dijkstra = new Dijkstra<>();
        Function<Transport, Double> priceCalculation = e -> e.getPrice();

        Assert.assertEquals(0, dijkstra.getShortestPath(this.ourGraph2, this.ourGraph2.getCity("Chicago"), this.ourGraph2.getCity("Krakow"), priceCalculation).size());
    }
}
