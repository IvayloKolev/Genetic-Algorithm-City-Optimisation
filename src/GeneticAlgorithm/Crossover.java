package GeneticAlgorithm;

import Building.Building;
import City.Gene;
import Debug.Debug;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class Crossover {

    private static final Random random = new Random();
    private static final Debug debug = new Debug();

    /**
     * Performs crossover between parent genes in the given ArrayList using the
     * specified crossover method.
     *
     * @param parentGenes The ArrayList of parent genes.
     * @param crossoverMethod The crossover method to be applied.
     * @param populationSize The desired size of the offspring population.
     * @return An ArrayList containing the offspring resulting from crossover.
     */
    public static ArrayList<Gene> crossover(ArrayList<Gene> parentGenes, CrossoverMethod crossoverMethod, int populationSize) {
        ArrayList<Gene> offspring = new ArrayList<>();

        while (offspring.size() < populationSize) {
            // Select two random parent genes
            Gene parent1 = parentGenes.get(random.nextInt(parentGenes.size()));
            Gene parent2 = parentGenes.get(random.nextInt(parentGenes.size()));

            // Ensure that the two parents are not the same gene
            while (parent1.equals(parent2)) {
                parent2 = parentGenes.get(random.nextInt(parentGenes.size()));
            }
            // Perform crossover
            switch (crossoverMethod) {
                case OnePoint ->
                    offspring.addAll(onePointCrossover(parent1, parent2));
                case TwoPoint ->
                    offspring.addAll(twoPointCrossover(parent1, parent2));
                case Uniform ->
                    offspring.addAll(uniformCrossover(parent1, parent2));
                default ->
                    throw new IllegalArgumentException("Invalid crossover method: " + crossoverMethod);
            }
        }

        // Ensure the offspring list does not exceed the desired population size
        return new ArrayList<>(offspring.subList(0, populationSize));
    }

    /**
     * One-point crossover method.
     *
     * @param parent1 The first parent gene.
     * @param parent2 The second parent gene.
     * @return An ArrayList containing the offspring resulting from crossover.
     */
    public static ArrayList<Gene> onePointCrossover(Gene parent1, Gene parent2) {
        // Select a crossover point
        int crossoverPoint = selectCrossoverPoint(parent1, parent2);

        // Create offspring 1
        ArrayList<Building> offspringBuildings1 = new ArrayList<>(parent1.getBuildingsList().subList(0, crossoverPoint));
        offspringBuildings1.addAll(parent2.getBuildingsList().subList(crossoverPoint, parent2.getBuildingsList().size()));

        // Create offspring 2
        ArrayList<Building> offspringBuildings2 = new ArrayList<>(parent2.getBuildingsList().subList(0, crossoverPoint));
        offspringBuildings2.addAll(parent1.getBuildingsList().subList(crossoverPoint, parent1.getBuildingsList().size()));

        // Create gene objects for the offspring
        Gene offspring1 = new Gene();
        offspring1.setWidth(parent1.getWidth());
        offspring1.setHeight(parent1.getHeight());
        offspring1.setStartingMoney(parent1.getStartingMoney());
        offspring1.setTravelCost(parent1.getTravelCost());
        offspring1.setBuildingsList(offspringBuildings1);

        Gene offspring2 = new Gene();
        offspring2.setWidth(parent2.getWidth());
        offspring2.setHeight(parent2.getHeight());
        offspring2.setStartingMoney(parent2.getStartingMoney());
        offspring2.setTravelCost(parent2.getTravelCost());
        offspring2.setBuildingsList(offspringBuildings2);

        // Check for position conflicts
        checkForPositionConflicts(offspring1);
        checkForPositionConflicts(offspring2);

        // Create a list to store the offspring
        ArrayList<Gene> offspringList = new ArrayList<>();
        offspringList.add(offspring1);
        offspringList.add(offspring2);

        return offspringList;
    }

    /**
     * Two-point crossover method.
     *
     * @param parent1 The first parent gene.
     * @param parent2 The second parent gene.
     * @return An ArrayList containing the offspring resulting from crossover.
     */
    public static ArrayList<Gene> twoPointCrossover(Gene parent1, Gene parent2) {
        // Select two distinct crossover points
        int crossoverPoint1 = selectCrossoverPoint(parent1, parent2);
        int crossoverPoint2 = selectCrossoverPoint(parent1, parent2);

        // Ensure that crossoverPoint1 is smaller than crossoverPoint2
        if (crossoverPoint1 > crossoverPoint2) {
            int temp = crossoverPoint1;
            crossoverPoint1 = crossoverPoint2;
            crossoverPoint2 = temp;
        }

        // Create offspring 1
        ArrayList<Building> offspringBuildings1 = new ArrayList<>(parent1.getBuildingsList().subList(0, crossoverPoint1));
        offspringBuildings1.addAll(parent2.getBuildingsList().subList(crossoverPoint1, crossoverPoint2));
        offspringBuildings1.addAll(parent1.getBuildingsList().subList(crossoverPoint2, parent1.getBuildingsList().size()));

        // Create offspring 2
        ArrayList<Building> offspringBuildings2 = new ArrayList<>(parent2.getBuildingsList().subList(0, crossoverPoint1));
        offspringBuildings2.addAll(parent1.getBuildingsList().subList(crossoverPoint1, crossoverPoint2));
        offspringBuildings2.addAll(parent2.getBuildingsList().subList(crossoverPoint2, parent2.getBuildingsList().size()));

        // Create gene objects for the offspring
        Gene offspring1 = new Gene();
        offspring1.setWidth(parent1.getWidth());
        offspring1.setHeight(parent1.getHeight());
        offspring1.setStartingMoney(parent1.getStartingMoney());
        offspring1.setTravelCost(parent1.getTravelCost());
        offspring1.setBuildingsList(offspringBuildings1);

        Gene offspring2 = new Gene();
        offspring2.setWidth(parent2.getWidth());
        offspring2.setHeight(parent2.getHeight());
        offspring2.setStartingMoney(parent2.getStartingMoney());
        offspring2.setTravelCost(parent2.getTravelCost());
        offspring2.setBuildingsList(offspringBuildings2);

        // Check for position conflicts
        checkForPositionConflicts(offspring1);
        checkForPositionConflicts(offspring2);

        // Create a list to store the offspring
        ArrayList<Gene> offspringList = new ArrayList<>();
        offspringList.add(offspring1);
        offspringList.add(offspring2);

        return offspringList;
    }

    /**
     * Uniform crossover method.
     *
     * @param parent1 The first parent gene.
     * @param parent2 The second parent gene.
     * @return An ArrayList containing the offspring resulting from crossover.
     */
    public static ArrayList<Gene> uniformCrossover(Gene parent1, Gene parent2) {
        ArrayList<Building> offspringBuildings1 = new ArrayList<>();
        ArrayList<Building> offspringBuildings2 = new ArrayList<>();

        for (int i = 0; i < parent1.getBuildingsList().size(); i++) {
            if (Math.random() < 0.5) {
                offspringBuildings1.add(parent1.getBuildingsList().get(i));
                offspringBuildings2.add(parent2.getBuildingsList().get(i));
            } else {
                offspringBuildings1.add(parent2.getBuildingsList().get(i));
                offspringBuildings2.add(parent1.getBuildingsList().get(i));
            }
        }

        Gene offspring1 = new Gene();
        offspring1.setWidth(parent1.getWidth());
        offspring1.setHeight(parent1.getHeight());
        offspring1.setStartingMoney(parent1.getStartingMoney());
        offspring1.setTravelCost(parent1.getTravelCost());
        offspring1.setBuildingsList(offspringBuildings1);

        Gene offspring2 = new Gene();
        offspring2.setWidth(parent2.getWidth());
        offspring2.setHeight(parent2.getHeight());
        offspring2.setStartingMoney(parent2.getStartingMoney());
        offspring2.setTravelCost(parent2.getTravelCost());
        offspring2.setBuildingsList(offspringBuildings2);

        // Check for position conflicts
        checkForPositionConflicts(offspring1);
        checkForPositionConflicts(offspring2);

        ArrayList<Gene> offspringList = new ArrayList<>();
        offspringList.add(offspring1);
        offspringList.add(offspring2);

        return offspringList;
    }

    /**
     * Selects a random crossover point in the gene's building list.
     *
     * @param parent1 The first parent gene.
     * @param parent2 The second parent gene.
     * @return The index of the crossover point.
     */
    private static int selectCrossoverPoint(Gene parent1, Gene parent2) {
        int maxLength = Math.min(parent1.getBuildingsList().size(), parent2.getBuildingsList().size());

        // Ensure there is at least one element in the building lists
        if (maxLength <= 0) {
            throw new IllegalArgumentException("Both parent gene's building lists must have at least one element for crossover.");
        }

        // Returns an integer that is smaller than the size of the smaller list to avoid errors
        return random.nextInt(maxLength);
    }

    /**
     * Checks for position conflicts in the new gene's buildings and resolves
     * them.
     *
     * @param gene The gene containing the buildings.
     */
    public static void checkForPositionConflicts(Gene gene) {
        ArrayList<Building> buildings = gene.getBuildingsList();
        boolean conflictsFound = false;

        for (int i = 0; i < buildings.size(); i++) {
            Building currentBuilding = buildings.get(i);

            // Check for conflicts with other buildings
            for (int j = 0; j < buildings.size(); j++) {
                if (i != j) {
                    Building otherBuilding = buildings.get(j);
                    if (otherBuilding.getPosition().equals(currentBuilding.getPosition())) {
                        debug.write("Conflict detected between building " + i + " and building " + j);
                        resolveConflict(gene, currentBuilding);
                        conflictsFound = true;
                    }
                }
            }
        }

        // Add a debug message if no conflicts were found
        if (!conflictsFound) {
            debug.write("No conflicts found in the gene.");
        }
    }

    private static void resolveConflict(Gene gene, Building building) {
        int originalX = building.getX();
        int originalY = building.getY();

        int newX = originalX;
        int newY = originalY;

        while (hasConflict(gene, newX, newY)) {
            debug.write("Conflict resolution needed for building at (" + newX + ", " + newY + ")");
            // Randomly choose a direction (up, down, left, right)
            int direction = (int) (Math.random() * 4);

            // Move the building in the chosen direction
            switch (direction) {
                case 0 ->
                    newY += 2; // Move up
                case 1 ->
                    newY -= 2; // Move down
                case 2 ->
                    newX += 2; // Move right
                case 3 ->
                    newX -= 2; // Move left
            }

            // Ensure the new position is within bounds
            newX = Math.max(0, Math.min(gene.getWidth() - 1, newX));
            newY = Math.max(0, Math.min(gene.getHeight() - 1, newY));

            debug.write("Attempting new position: (" + newX + ", " + newY + ")");
        }
    }

    static boolean hasConflict(Gene gene, int x, int y) {
        // Check if there is already a building at the specified position
        for (Building building : gene.getBuildingsList()) {
            if (building.getX() == x && building.getY() == y) {
                return true;
            }
        }
        return false;
    }

}
