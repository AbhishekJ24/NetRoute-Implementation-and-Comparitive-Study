package Code;

import java.util.*;

public class BellmanFord {
    public static List<String> bellmanFordShortestPath(String source, String destination,
            Map<String, Map<String, Integer>> graph) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();

        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }

        distances.put(source, 0);

        for (int i = 0; i < graph.size() - 1; i++) {
            for (String node : graph.keySet()) {
                if (!graph.containsKey(node))
                    continue;

                for (String neighbor : graph.get(node).keySet()) {
                    int parameter = graph.get(node).get(neighbor);
                    if (distances.get(node) != Integer.MAX_VALUE
                            && distances.get(node) + parameter < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                        distances.put(neighbor, distances.get(node) + parameter);
                        previous.put(neighbor, node);
                    }
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
}
