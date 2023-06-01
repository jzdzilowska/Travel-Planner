package sol;

import src.*;
import test.simple.SimpleEdge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementation for TravelController
 */
public class TravelController implements ITravelController<City, Transport> {
    private TravelGraph graph;

    /**
     * Constructor for TravelController
     */
    public TravelController() {
    }

    @Override
    public String load(String citiesFile, String transportFile) {
        this.graph = new TravelGraph();
        TravelCSVParser parser = new TravelCSVParser();

        Function<Map<String, String>, Void> addVertex = map -> {
            this.graph.addVertex(new City(map.get("name")));
            return null;
        };

        Function<Map<String, String>, Void> addEdge = map -> {
            City sourceCity = this.graph.getCity(map.get("origin"));
            City destCity = this.graph.getCity(map.get("destination"));
            TransportType type = TransportType.fromString(map.get("type"));
            double price = Double.parseDouble(map.get("price"));
            double duration = Double.parseDouble(map.get("duration"));
            Transport edge = new Transport(sourceCity, destCity, type, price, duration);
            this.graph.addEdge(sourceCity, edge);
            return null;
        };
        try {
            // pass in string for CSV and function to create City (vertex) using city name
            parser.parseLocations(citiesFile, addVertex);
        } catch (IOException e) {
            return "Error parsing file: " + citiesFile;
        }
        try {
            parser.parseTransportation(transportFile, addEdge);
        } catch (IOException e) {
            return "Error parsing file: " + transportFile;
        }
        return "Successfully loaded cities and transportation files.";
    }

    @Override
    public List<Transport> fastestRoute(String source, String destination) {
        Dijkstra dijkstra = new Dijkstra();
        Function<Transport, Double> edgeWeightCalculation = e -> e.getMinutes();
        return dijkstra.getShortestPath(this.graph, this.graph.getCity(source), this.graph.getCity(destination), edgeWeightCalculation);
    }

    @Override
    public List<Transport> cheapestRoute(String source, String destination) {
        Dijkstra dijkstra = new Dijkstra();
        Function<Transport, Double> edgeWeightCalculation = e -> e.getPrice();
        return dijkstra.getShortestPath(this.graph, this.graph.getCity(source), this.graph.getCity(destination), edgeWeightCalculation);
    }

    @Override
    public List<Transport> mostDirectRoute(String source, String destination) {
        IBFS bfs = new BFS();
        return bfs.getPath(this.graph, this.graph.getCity(source), this.graph.getCity(destination));
    }

    /**
     * Returns the graph stored by the controller
     *
     * NOTE: You __should not__ be using this in your implementation, this is purely meant to be used for the visualizer
     *
     * @return The TravelGraph currently stored in the TravelController
     */
    public TravelGraph getGraph() {
        return this.graph;
    }
}
