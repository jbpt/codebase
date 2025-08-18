package org.jbpt.pm.mspd.utilities;
import java.util.*;

public class DCI {
    private double minFirst;
    private double maxFirst;
    private double minSecond;
    private double maxSecond;
    private int gridSize; // Number of divisions along each axis

    public DCI(int gridSize) {
        this.gridSize = gridSize;
    }
    
    public double calculateAverageDistance(Map<String, List<DoublePair>> groupCounter) {
        Map<String, Set<String>> solutionToCubes = new HashMap<>();
        double cubeWidthFirst = (maxFirst - minFirst) / gridSize;
        double cubeWidthSecond = (maxSecond - minSecond) / gridSize;

        // Step 1: Map solutions to occupied cubes
        for (Map.Entry<String, List<DoublePair>> entry : groupCounter.entrySet()) {
            String solutionName = entry.getKey();
            List<DoublePair> pairs = entry.getValue();
            Set<String> occupiedCubes = new HashSet<>();

            for (DoublePair pair : pairs) {
                int cubeX = (int) ((pair.getFirst() - minFirst) / cubeWidthFirst);
                int cubeY = (int) ((pair.getSecond() - minSecond) / cubeWidthSecond);

                // Ensure cube indices are within bounds
                cubeX = Math.min(cubeX, gridSize - 1);
                cubeY = Math.min(cubeY, gridSize - 1);

                String cubeId = cubeX + "," + cubeY; // Unique identifier for the cube
                occupiedCubes.add(cubeId);
            }

            solutionToCubes.put(solutionName, occupiedCubes);
        }

        // Step 2: Calculate average distance for each solution
        double totalDistance = 0.0;
        int totalCubes = gridSize * gridSize;

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                String currentCube = x + "," + y;

                for (Map.Entry<String, Set<String>> entry : solutionToCubes.entrySet()) {
                    String solutionName = entry.getKey();
                    Set<String> occupiedCubes = entry.getValue();

                    // Find the closest occupied cube for the current solution
                    double minDistance = Double.MAX_VALUE;

                    for (String occupiedCube : occupiedCubes) {
                        String[] occupiedParts = occupiedCube.split(",");
                        int occupiedX = Integer.parseInt(occupiedParts[0]);
                        int occupiedY = Integer.parseInt(occupiedParts[1]);

                        // Calculate Euclidean distance
                        double distance = Math.sqrt(Math.pow((x - occupiedX) * cubeWidthFirst, 2) +
                                                    Math.pow((y - occupiedY) * cubeWidthSecond, 2));
                        minDistance = Math.min(minDistance, distance);
                    }

                    // Apply the condition: 1 - (distance)^2 / 3
                    double value = 1 - Math.pow(minDistance, 2) / 3;
                    if (value < 0) {
                        value = 0;
                    }

                    totalDistance += value;
                }
            }
        }

        // Step 3: Calculate the average distance
        return totalDistance / totalCubes;
    }
    public double calculateRealDistanceToHyperbox(String hyperbox, Set<String> occupiedCubes) {
        // Parse the coordinates of the target hyperbox
        String[] parts = hyperbox.split(",");
        int targetX = Integer.parseInt(parts[0]);
        int targetY = Integer.parseInt(parts[1]);

        double cubeWidthFirst = (maxFirst - minFirst) / gridSize;
        double cubeWidthSecond = (maxSecond - minSecond) / gridSize;

        // Calculate the real-world coordinates of the target hyperbox center
        double targetCenterX = minFirst + (targetX + 0.5) * cubeWidthFirst;
        double targetCenterY = minSecond + (targetY + 0.5) * cubeWidthSecond;

        double minDistance = Double.MAX_VALUE;

        // Iterate through all occupied cubes to find the shortest real distance
        for (String occupiedCube : occupiedCubes) {
            String[] occupiedParts = occupiedCube.split(",");
            int occupiedX = Integer.parseInt(occupiedParts[0]);
            int occupiedY = Integer.parseInt(occupiedParts[1]);
            double occupiedCenterX = minFirst + (occupiedX + 0.5) * cubeWidthFirst;
            double occupiedCenterY = minSecond + (occupiedY + 0.5) * cubeWidthSecond;

            // Calculate Euclidean distance
            double distance = Math.sqrt(Math.pow(targetCenterX - occupiedCenterX, 2) + Math.pow(targetCenterY - occupiedCenterY, 2));
            minDistance = Math.min(minDistance, distance);
        }

        return minDistance;
    }
            // Calculate the real-world coordinates of the occupied hyperbox center

    // Method to calculate the grid boundaries
    public void calculateBounds(Map<String, List<DoublePair>> groupCounter) {
        minFirst = Double.MAX_VALUE;
        maxFirst = Double.MIN_VALUE;
        minSecond = Double.MAX_VALUE;
        maxSecond = Double.MIN_VALUE;

        for (List<DoublePair> pairs : groupCounter.values()) {
            for (DoublePair pair : pairs) {
                minFirst = Math.min(minFirst, pair.getFirst());
                maxFirst = Math.max(maxFirst, pair.getFirst());
                minSecond = Math.min(minSecond, pair.getSecond());
                maxSecond = Math.max(maxSecond, pair.getSecond());
            }
        }
    }

    // Method to assign solutions to hypercubes and calculate percentages
    public Map<String, Double> calculateHyperCubeOccupancy(Map<String, List<DoublePair>> groupCounter) {
        Map<String, Set<String>> solutionToCubes = new HashMap<>();
        double cubeWidthFirst = (maxFirst - minFirst) / gridSize;
        double cubeWidthSecond = (maxSecond - minSecond) / gridSize;

        // Step 1: Map solutions to occupied cubes
        for (Map.Entry<String, List<DoublePair>> entry : groupCounter.entrySet()) {
            String solutionName = entry.getKey();
            List<DoublePair> pairs = entry.getValue();
            Set<String> occupiedCubes = new HashSet<>();

            for (DoublePair pair : pairs) {
                int cubeX = (int) ((pair.getFirst() - minFirst) / cubeWidthFirst);
                int cubeY = (int) ((pair.getSecond() - minSecond) / cubeWidthSecond);

                // Ensure cube indices are within bounds
                cubeX = Math.min(cubeX, gridSize - 1);
                cubeY = Math.min(cubeY, gridSize - 1);

                String cubeId = cubeX + "," + cubeY; // Unique identifier for the cube
                occupiedCubes.add(cubeId);
            }

            solutionToCubes.put(solutionName, occupiedCubes);
        }

        // Step 2: Calculate the average distance for each solution
        Map<String, Double> solutionAverageDistance = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry : solutionToCubes.entrySet()) {
            String solutionName = entry.getKey();
            Set<String> occupiedCubes = entry.getValue();

            double totalDistance = 0.0;
            int totalCubes = gridSize * gridSize;

            // Iterate through all hypercubes in the grid
            for (int x = 0; x < gridSize; x++) {
                for (int y = 0; y < gridSize; y++) {
                    String currentCube = x + "," + y;

                    // Find the closest occupied cube for the current solution
                    double minDistance = Double.MAX_VALUE;

                    for (String occupiedCube : occupiedCubes) {
                        String[] occupiedParts = occupiedCube.split(",");
                        int occupiedX = Integer.parseInt(occupiedParts[0]);
                        int occupiedY = Integer.parseInt(occupiedParts[1]);

                        // Calculate Euclidean distance
                        double distance = Math.sqrt(Math.pow((x - occupiedX) * cubeWidthFirst, 2) +
                                                    Math.pow((y - occupiedY) * cubeWidthSecond, 2));
                        minDistance = Math.min(minDistance, distance);
                    }

                    // Apply the condition: 1 - (distance)^2 / 3
                    double value = 1 - Math.pow(minDistance, 2) / 3;
                    if (value < 0) {
                        value = 0;
                    }

                    totalDistance += value;
                }
            }

            // Calculate the average distance for the current solution
            double averageDistance = (totalDistance / totalCubes)*100;
            solutionAverageDistance.put(solutionName, Double.parseDouble(String.format("%.2f",averageDistance)));
        }

        return solutionAverageDistance;
    }
    public static void main(String[] args) {
        // Example usage
        Map<String, List<DoublePair>> groupCounter = new LinkedHashMap<>();
        groupCounter.put("Solution1", Arrays.asList(
                new DoublePair("1.0", "2.0", "Solution1"),
                new DoublePair("1.5", "2.5", "Solution1")
        ));
        groupCounter.put("Solution2", Arrays.asList(
                new DoublePair("3.0", "4.0", "Solution2"),
                new DoublePair("3.5", "4.5", "Solution2")
        ));

        DCI grid = new DCI(10); // 10x10 grid
        grid.calculateBounds(groupCounter);
        Map<String, Double> occupancy = grid.calculateHyperCubeOccupancy(groupCounter);

        for (Map.Entry<String, Double> entry : occupancy.entrySet()) {
            System.out.println("Solution: " + entry.getKey() + ", Occupied Percentage: " + entry.getValue() + "%");
        }
    }
}