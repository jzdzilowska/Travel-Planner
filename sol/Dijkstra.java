package sol;

import src.IDijkstra;
import src.IEdge;
import src.IGraph;
import src.IVertex;

import java.util.*;
import java.util.function.Function;

/**
 * Implementation for Dijkstra's algorithm
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
public class Dijkstra<V extends IVertex<E>, E extends IEdge<V>> implements IDijkstra<V, E> {

    @Override
    public List<E> getShortestPath(IGraph<V, E> graph, V source, V destination,
                                   Function<E, Double> edgeWeight) {
        Map<V, Double> values = new HashMap<>();
        Map<V, E> cameFrom = new HashMap<>();
        for (V vertex : graph.getVertices()) {
            values.put(vertex, Double.POSITIVE_INFINITY);
        }
        values.put(source, 0.0);
        PriorityQueue<V> toCheckQueue = new PriorityQueue<>(Comparator.comparing(values::get));
        toCheckQueue.offer(source);

        while (!toCheckQueue.isEmpty()) {
            V checkingV = toCheckQueue.poll();

            if (checkingV.equals(destination)) {
                break;
            }

            for (E edge : checkingV.getOutgoing()) {
                V neighbor = edge.getTarget();
                double potentialRouteDistance = values.get(checkingV) + edgeWeight.apply(edge);
                if (potentialRouteDistance < values.get(neighbor)) {
                    values.put(neighbor, potentialRouteDistance);
                    cameFrom.put(neighbor, edge);
                    if (!toCheckQueue.contains(neighbor)) {
                        toCheckQueue.offer(neighbor);
                    } else {
                        toCheckQueue.remove(neighbor);
                        toCheckQueue.offer(neighbor);
                    }
                }
            }
        }

        if (!cameFrom.containsKey(destination)) {
            return new ArrayList<>();
        }

        ArrayList<E> shortestPath = new ArrayList<>();
        V currentVertex = destination;
        while (!currentVertex.equals(source)) {
            E edge = cameFrom.get(currentVertex);
            shortestPath.add(edge);
            currentVertex = edge.getSource();
        }
        Collections.reverse(shortestPath);
        return shortestPath;
    }
}

