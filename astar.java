package Proj;
import java.util.*;
public class astar {
    static List<String> aStarShortestPath(String source, String destination, Map<String, Map<String, Integer>> graph,
                                          Map<String, Integer> xCoordinates, Map<String, Integer> yCoordinates) {
        Map<String, Integer> gScores = new HashMap<>();
        Map<String, Integer> fScores = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (String node : graph.keySet()) {
            gScores.put(node, Integer.MAX_VALUE);
            fScores.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }

        gScores.put(source, 0);
        fScores.put(source, calculateEuclideanDistance(source, destination, xCoordinates, yCoordinates));

        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(fScores::get));
        priorityQueue.add(source);

        while (!priorityQueue.isEmpty()) {
            String current = priorityQueue.poll();

            if (current.equals(destination)) {
                return reconstructPath(previous, current);
            }

            visited.add(current);

            for (String neighbor : graph.getOrDefault(current, Collections.emptyMap()).keySet()) {
                if (visited.contains(neighbor)) {
                    continue;
                }

                int tentativeGScore = gScores.get(current) + graph.get(current).get(neighbor);

                // Null check before calling intValue()
                if (gScores.get(neighbor) == null || tentativeGScore < gScores.get(neighbor)) {
                    previous.put(neighbor, current);
                    gScores.put(neighbor, tentativeGScore);
                    fScores.put(neighbor, gScores.get(neighbor) + calculateEuclideanDistance(neighbor, destination, xCoordinates, yCoordinates));

                    if (!priorityQueue.contains(neighbor)) {
                        priorityQueue.add(neighbor);
                    }
                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    private static int calculateEuclideanDistance(String node1, String node2, Map<String, Integer> xCoordinates, Map<String, Integer> yCoordinates) {
        Integer x1 = xCoordinates.getOrDefault(node1, 0);
        Integer y1 = yCoordinates.getOrDefault(node1, 0);
        Integer x2 = xCoordinates.getOrDefault(node2, 0);
        Integer y2 = yCoordinates.getOrDefault(node2, 0);

        return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    private static List<String> reconstructPath(Map<String, String> previous, String current) {
        List<String> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }
        Collections.reverse(path);
        return path;
    }


}
