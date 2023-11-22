import java.io.*;
import java.util.*;

public class ShortestPathAlgorithm {
    private static Map<String, Map<String, Integer>> graphCost = new HashMap<>();
    private static Map<String, Map<String, Integer>> graphLatency = new HashMap<>();
    private static Map<String, Map<String, Integer>> graphBandwidth = new HashMap<>();

    public static void main(String[] args) {
        String csvOutputFile = "output.csv"; // Output CSV file path
        String csvFile = "input.csv"; // Replace with your CSV file path

        try (Scanner fileScanner = new Scanner(new File(csvFile))) {
            fileScanner.nextLine(); // Skip the header row
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                String srcNode = parts[0];
                String destNode = parts[1];
                int cost = Integer.parseInt(parts[2]);
                int latency = Integer.parseInt(parts[3]);
                int bandwidth = Integer.parseInt(parts[4]);
                String status = parts[5];

                if ("Active".equalsIgnoreCase(status)) {
                    graphCost.computeIfAbsent(srcNode, k -> new HashMap<>()).put(destNode, cost);
                    graphLatency.computeIfAbsent(srcNode, k -> new HashMap<>()).put(destNode, latency);
                    graphBandwidth.computeIfAbsent(srcNode, k -> new HashMap<>()).put(destNode, bandwidth);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Ask user for the parameter choice (Cost, Latency, Bandwidth)
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose parameter (Cost/Latency/Bandwidth): ");
        String parameterChoice = scanner.nextLine();

        // Ask user for the algorithm choice (Dijkstra/Bellman-Ford)
        System.out.print("Choose algorithm (Dijkstra/Bellman-Ford): ");
        String algorithmChoice = scanner.nextLine();

        // Create and write paths to the output CSV file based on the chosen parameter and algorithm
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvOutputFile))) {
            writer.println("Source Node,Destination Node,Path,Total " + parameterChoice);

            for (String src : graphCost.keySet()) {
                for (String dest : graphCost.get(src).keySet()) {
                    List<String> path;
                    int totalParameter;
                    switch (algorithmChoice.toLowerCase()) {
                        case "dijkstra":
                            path = dijkstraShortestPath(src, dest, parameterChoice.equals("Cost") ? graphCost :
                                    parameterChoice.equals("Latency") ? graphLatency : graphBandwidth);
                            totalParameter = calculateTotalParameter(path,
                                    parameterChoice.equals("Cost") ? graphCost :
                                            parameterChoice.equals("Latency") ? graphLatency : graphBandwidth);
                            break;
                        case "bellman-ford":
                            path = bellmanFordShortestPath(src, dest, parameterChoice.equals("Cost") ? graphCost :
                                    parameterChoice.equals("Latency") ? graphLatency : graphBandwidth);
                            totalParameter = calculateTotalParameter(path,
                                    parameterChoice.equals("Cost") ? graphCost :
                                            parameterChoice.equals("Latency") ? graphLatency : graphBandwidth);
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid algorithm choice.");
                    }

                    writer.println(src + "," + dest + "," + String.join(" -> ", path) + "," + totalParameter);
                }
            }
            System.out.println("CSV file created successfully: " + csvOutputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> dijkstraShortestPath(String source, String destination, Map<String, Map<String, Integer>> graph) {
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

            if (!graph.containsKey(current)) continue;

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

    private static String minDistanceNode(Map<String, Integer> distances, Set<String> visited) {
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

    private static List<String> bellmanFordShortestPath(String source, String destination, Map<String, Map<String, Integer>> graph) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();

        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }

        distances.put(source, 0);

        for (int i = 0; i < graph.size() - 1; i++) {
            for (String node : graph.keySet()) {
                if (!graph.containsKey(node)) continue;

                for (String neighbor : graph.get(node).keySet()) {
                    int parameter = graph.get(node).get(neighbor);
                    if (distances.get(node) != Integer.MAX_VALUE && distances.get(node) + parameter < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
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

    private static int calculateTotalParameter(List<String> path, Map<String, Map<String, Integer>> graph) {
        int totalParameter = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String currentNode = path.get(i);
            String nextNode = path.get(i + 1);
            totalParameter += graph.get(currentNode).get(nextNode);
        }
        return totalParameter;
    }
}
