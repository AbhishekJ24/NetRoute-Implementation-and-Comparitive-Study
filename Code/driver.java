package Code;

import java.io.*;
import java.util.*;
import static Code.Dijkstra.dijkstraShortestPath;
import static Code.BellmanFord.bellmanFordShortestPath;
import static Code.astar.aStarShortestPath;

public class driver {
    private static Map<String, Map<String, Integer>> graphCost = new HashMap<>();
    private static Map<String, Map<String, Integer>> graphLatency = new HashMap<>();
    private static Map<String, Map<String, Integer>> graphBandwidth = new HashMap<>();
    private static Map<String, Integer> xCoordinates = new HashMap<>(); // Initialize xCoordinates
    private static Map<String, Integer> yCoordinates = new HashMap<>(); // Initialize yCoordinates

    public static void main(String[] args) {
        String csvOutputFile = "output.csv";
        String csvFile = "Code/input.csv";

        try (Scanner fileScanner = new Scanner(new File(csvFile))) {
            fileScanner.nextLine();
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                String srcNode = parts[0];
                String destNode = parts[1];
                int cost = Integer.parseInt(parts[2]);
                int latency = Integer.parseInt(parts[3]);
                int bandwidth = Integer.parseInt(parts[4]);
                int x = Integer.parseInt(parts[5]);
                int y = Integer.parseInt(parts[6]);
                xCoordinates.put(srcNode, x);
                yCoordinates.put(srcNode, y);
                String status = parts[7];

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

        // User choice for cost, latency, or bandwidth
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose algorithm (Dijkstra/Bellman-Ford/A*): ");
        String algorithmChoice = scanner.nextLine();

        // Creating output CSV file
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvOutputFile))) {
            writer.println(
                    "Source Node,Destination Node,Path" + (isDijkstraOrBellmanFord(algorithmChoice) ? ",Total" : ""));

            // Move the parameter choice input outside the nested loop
            String parameterChoice = "";
            if (isDijkstraOrBellmanFord(algorithmChoice)) {
                System.out.print("Choose parameter (Cost/Latency/Bandwidth): ");
                parameterChoice = scanner.nextLine();
            }

            for (String src : graphCost.keySet()) {
                for (String dest : graphCost.get(src).keySet()) {
                    List<String> path = null;
                    int totalParameter = 0;
                    switch (algorithmChoice.toLowerCase()) {
                        case "dijkstra":
                        case "bellman-ford":
                            // Removed the parameter choice input from here
                            path = algorithmChoice.equalsIgnoreCase("dijkstra")
                                    ? dijkstraShortestPath(src, dest, getGraphByParameter(parameterChoice))
                                    : bellmanFordShortestPath(src, dest, getGraphByParameter(parameterChoice));
                            totalParameter = calculateTotalParameter(path, getGraphByParameter(parameterChoice));
                            break;
                        case "a*":
                            path = aStarShortestPath(src, dest, getGraphByParameter("Cost"), xCoordinates,
                                    yCoordinates);
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid algorithm choice.");
                    }
                    writer.print(src + "," + dest + "," + String.join(" -> ", path));
                    if (isDijkstraOrBellmanFord(algorithmChoice)) {
                        writer.println("," + totalParameter);
                    } else {
                        writer.println();
                    }
                }
            }
            System.out.println("CSV file created successfully: " + csvOutputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isDijkstraOrBellmanFord(String algorithmChoice) {
        return "dijkstra".equalsIgnoreCase(algorithmChoice) || "bellman-ford".equalsIgnoreCase(algorithmChoice);
    }

    private static Map<String, Map<String, Integer>> getGraphByParameter(String parameterChoice) {
        return switch (parameterChoice.toLowerCase()) {
            case "cost" -> graphCost;
            case "latency" -> graphLatency;
            case "bandwidth" -> graphBandwidth;
            default -> throw new IllegalArgumentException("Invalid parameter choice.");
        };
    }

    public static int calculateTotalParameter(List<String> path, Map<String, Map<String, Integer>> graph) {
        int totalParameter = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String currentNode = path.get(i);
            String nextNode = path.get(i + 1);
            totalParameter += graph.get(currentNode).get(nextNode);
        }
        return totalParameter;
    }
}