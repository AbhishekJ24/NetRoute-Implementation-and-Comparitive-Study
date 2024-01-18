package Code;

import java.util.*;

class Dijkstra {
    public static List<String> dijkstraShortestPath(String source, String destination,
            Map<String, Map<String, Integer>> graph) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }

        distances.put(source, 0);

        while (!visited.contains(destination)) {
            String current = minDistanceNode(distances, visited);
            visited.add(current);

            if (!graph.containsKey(current))
                continue;

            for (String neighbor : graph.get(current).keySet()) {
                int parameter = graph.get(current).get(neighbor);
                int alt = distances.get(current) + parameter;
                if (alt < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, alt);
                    previous.put(neighbor, current);
                }
            }
        }

        List<String> path = new ArrayList<>();
        String node = destination;
        while (node != null) {
            path.add(node);
            node = previous.get(node);
        }
        Collections.reverse(path);
        return path;
    }

    public static String minDistanceNode(Map<String, Integer> distances, Set<String> visited) {
        int minDistance = Integer.MAX_VALUE;
        String minNode = null;

        for (String node : distances.keySet()) {
            if (!visited.contains(node) && distances.get(node) <= minDistance) {
                minDistance = distances.get(node);
                minNode = node;
            }
        }
        return minNode;
    }
}