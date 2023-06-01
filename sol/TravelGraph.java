package sol;

import src.IGraph;
import src.IVertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation for TravelGraph
 */
public class TravelGraph implements IGraph<City, Transport> {
    private HashMap<String, City> cities;

    public TravelGraph() {
        this.cities = new HashMap<>();
    }

    @Override
    public void addVertex(City vertex) {
        if (!this.cities.containsKey(vertex.toString())) {
            this.cities.put(vertex.toString(), vertex);
        }
    }

    @Override
    public void addEdge(City origin, Transport edge) {
        origin.addOut(edge);
    }

    @Override
    public Set<City> getVertices() {
        return new HashSet<>(this.cities.values());
    }

    @Override
    public City getEdgeSource(Transport edge) {
        return edge.getSource();
    }

    @Override
    public City getEdgeTarget(Transport edge) {
        return edge.getTarget();
    }

    @Override
    public Set<Transport> getOutgoingEdges(City fromVertex) {
        return fromVertex.getOutgoing();
    }

    /**
    * Method returns a city based on the inputted String 
    */
    public City getCity(String name) {
        City city = this.cities.get(name);
        if (city != null) {
            return city;
        } else return null;
    }
}
