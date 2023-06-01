package sol;

import src.IBFS;
import src.IEdge;
import src.IGraph;
import src.IVertex;

import java.util.*;

/**
 * Implementation for BFS, implements IBFS interface
 * @param <V> The type of the vertices
 * @param <E> The type of the edges
 */
public class BFS<V extends IVertex<E>, E extends IEdge<V>> implements IBFS<V, E> {

    @Override
    public List<E> getPath(IGraph<V, E> graph, V start, V end) {
        LinkedList<V> citiesToCheck = new LinkedList<>();
        HashSet<V> visitedCities = new HashSet<>();
        HashMap<V, E> transportToGetToCity = new HashMap<>();

        citiesToCheck.add(start);

        while (!(citiesToCheck.isEmpty())) {
            V checkingCity = citiesToCheck.removeFirst();
            visitedCities.add(checkingCity);

            if (checkingCity == end) { // base case
                ArrayList<E> path = new ArrayList<>();
                if (!transportToGetToCity.isEmpty()) {
                    path.add(transportToGetToCity.get(end));
                    V prevCity = path.get(0).getSource();
                    while (prevCity != start) {
                        path.add(0, transportToGetToCity.get(prevCity));
                        prevCity = path.get(0).getSource();
                    }
                }
                return path;
            }
            for (E transport: checkingCity.getOutgoing()) {
                if (!visitedCities.contains(transport.getTarget())) {
                    visitedCities.add(transport.getTarget()); // do we need this
                    citiesToCheck.addLast(transport.getTarget());
                    transportToGetToCity.put(transport.getTarget(), transport);
                }
            }
        }
        return new ArrayList<>();
    }
}
