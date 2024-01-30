package GeneticAlgorithm;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
import Building.Building;
import Building.BuildingType;
import static Building.BuildingType.getRandomBuildingType;
import Building.House;
import Building.Office;
import Building.Position;
import Building.Shop;
import City.Gene;
import Debug.Debug;
import static GeneticAlgorithm.Crossover.hasConflict;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Class for handling mutation operations in the genetic algorithm.
 */
public class Mutation {

    private static Random random = new Random();
    private static Debug debug = new Debug();

    /**
     * Applies mutation to the offspring genes with a certain probability.
     *
     * @param offspring The list of genes to be mutated.
     * @param mutationChance The chance for mutation to occur, best kept at low
     * values like 0.01 ~ 0.05.
     */
    public static void mutate(ArrayList<Gene> offspring, double mutationChance) {
        for (Gene gene : offspring) {
            if (random.nextDouble() < mutationChance) {
                debug.write("Mutation applied to gene " + gene.toString());

                // Randomly choose a mutation type
                int mutationType = random.nextInt(5);
                debug.write("Selected mutation type: " + mutationType);

                switch (mutationType) {
                    case 0 -> {
                        debug.write("Mutating starting money");
                        mutateStartingMoney(gene);
                    }
                    case 1 -> {
                        debug.write("Mutating travel cost");
                        mutateTravelCost(gene);
                    }
                    case 2 -> {
                        debug.write("Mutating building positions");
                        mutateBuildingPositions(gene);
                    }
                    case 3 -> {
                        debug.write("Mutating building types");
                        mutateBuildingTypes(gene);
                    }
                    case 4 -> {
                        debug.write("Mutating shop and office parameters");
                        mutateShopAndOfficeParameters(gene);
                    }
                }
            }
        }
    }

    /**
     * Mutates the starting money of the gene.
     *
     * @param gene The gene to be mutated.
     */
    private static void mutateStartingMoney(Gene gene) {
        double currentStartingMoney = gene.getStartingMoney();
        double mutationAmount = random.nextDouble() * 10 - 5; // Random value between -5 and 5
        gene.setStartingMoney(currentStartingMoney + mutationAmount);
        debug.write("Starting money mutated to: " + gene.getStartingMoney());
    }

    /**
     * Mutates the travel cost of the gene.
     *
     * @param gene The gene to be mutated.
     */
    private static void mutateTravelCost(Gene gene) {
        double currentTravelCost = gene.getTravelCost();
        double mutationAmount = random.nextDouble() * 0.2 - 0.1; // Random value between -0.1 and 0.1
        gene.setTravelCost(currentTravelCost + mutationAmount);
        debug.write("Travel cost mutated to: " + gene.getTravelCost());
    }

    /**
     * Mutates a subset of the building positions in the gene by moving
     * buildings to neighboring free spaces.
     *
     * @param gene The gene to be mutated.
     */
    private static void mutateBuildingPositions(Gene gene) {
        ArrayList<Building> buildings = gene.getBuildingsList();
        double mutationPercent = 0.1;

        // Calculate the number of buildings to mutate based on the percentage
        int buildingsToMutate = (int) (mutationPercent * buildings.size());

        // Shuffle the buildings to randomly select the subset for mutation
        Collections.shuffle(buildings);

        for (int i = 0; i < buildingsToMutate; i++) {
            Building currentBuilding = buildings.get(i);

            int originalX = currentBuilding.getX();
            int originalY = currentBuilding.getY();

            // Check neighboring spaces for availability
            ArrayList<Position> availableSpaces = getAvailableNeighborSpaces(gene, originalX, originalY);

            if (!availableSpaces.isEmpty()) {
                // Randomly choose one of the available spaces
                Position newPosition = availableSpaces.get(random.nextInt(availableSpaces.size()));

                // Move the building to the new position
                currentBuilding.setX(newPosition.getX());
                currentBuilding.setY(newPosition.getY());

                debug.write("Building moved to new position: " + currentBuilding.getPosition());
            }
        }
    }

    /**
     * Mutates the building types of the gene.
     *
     * @param gene The gene to be mutated.
     */
    private static void mutateBuildingTypes(Gene gene) {
        ArrayList<Building> buildings = gene.getBuildingsList();

        // Select a subset of buildings to mutate (e.g., 10% of buildings)
        int numBuildingsToMutate = (int) (buildings.size() * 0.1);

        debug.write("Mutating building types of " + numBuildingsToMutate + " buildings.");

        for (int i = 0; i < numBuildingsToMutate; i++) {
            Building buildingToMutate = buildings.get(random.nextInt(buildings.size()));

            // Choose a new building type randomly
            BuildingType newBuildingType = getRandomBuildingType();

            // Create a new building based on the new type
            Building newBuilding = createBuilding(newBuildingType, buildingToMutate.getPosition().getX(), buildingToMutate.getPosition().getY());

            // Handle specific logic for shops and offices
            if (newBuildingType == BuildingType.SHOP && buildingToMutate instanceof Shop) {
                // If the new type is SHOP, copy average spend from another random shop
                copyShopAverageSpend((Shop) newBuilding, gene);
            } else if (newBuildingType == BuildingType.OFFICE && buildingToMutate instanceof Office) {
                // If the new type is OFFICE, copy salary from another random office
                copyOfficeSalary((Office) newBuilding, gene);
            }

            // Replace the old building with the new one in the gene
            buildings.set(buildings.indexOf(buildingToMutate), newBuilding);

            debug.write("Building type mutated: " + buildingToMutate.getType() + " -> " + newBuildingType);
        }
    }

    /**
     * Mutates the shop and office parameters of a random subset of 10% of
     * buildings in the gene.
     *
     * @param gene The gene to be mutated.
     */
    private static void mutateShopAndOfficeParameters(Gene gene) {
        ArrayList<Building> buildings = gene.getBuildingsList();

        // Select a random subset of 10% of buildings
        int numBuildingsToMutate = (int) (buildings.size() * 0.1);

        debug.write("Mutating shop and office parameters of " + numBuildingsToMutate + " buildings.");

        for (int i = 0; i < numBuildingsToMutate; i++) {
            Building buildingToMutate = buildings.get(random.nextInt(buildings.size()));

            // Check if the building is not a house
            if (buildingToMutate.getType() != BuildingType.HOUSE) {
                if (buildingToMutate.getType() == BuildingType.SHOP) {
                    mutateShopParameters((Shop) buildingToMutate);
                } else if (buildingToMutate.getType() == BuildingType.OFFICE) {
                    mutateOfficeParameters((Office) buildingToMutate);
                }
            }
        }
    }

    // Helper Methods
    /**
     * Returns a list of neighboring spaces that are available for moving a
     * building.
     *
     * @param gene The gene containing the buildings.
     * @param x The x-coordinate of the original building position.
     * @param y The y-coordinate of the original building position.
     * @return An ArrayList of available neighboring spaces.
     */
    private static ArrayList<Position> getAvailableNeighborSpaces(Gene gene, int x, int y) {
        ArrayList<Position> availableSpaces = new ArrayList<>();

        // Check up, down, left, right neighbors
        checkAndAddIfAvailable(gene, availableSpaces, x, y + 2);
        checkAndAddIfAvailable(gene, availableSpaces, x, y - 2);
        checkAndAddIfAvailable(gene, availableSpaces, x + 2, y);
        checkAndAddIfAvailable(gene, availableSpaces, x - 2, y);

        return availableSpaces;
    }

    /**
     * Checks if the specified position is within bounds and available for
     * moving a building. If available, adds the position to the list.
     *
     * @param gene The gene containing the buildings.
     * @param availableSpaces The list of available spaces to be updated.
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     */
    private static void checkAndAddIfAvailable(Gene gene, ArrayList<Position> availableSpaces, int x, int y) {
        if (isValidPosition(gene, x, y) && !hasConflict(gene, x, y)) {
            availableSpaces.add(new Position(x, y));
        }
    }

    /**
     * Checks if the specified position is within the bounds of the gene.
     *
     * @param gene The gene containing the buildings.
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return True if the position is within bounds, false otherwise.
     */
    private static boolean isValidPosition(Gene gene, int x, int y) {
        return x >= 0 && x < gene.getWidth() && y >= 0 && y < gene.getHeight();
    }

    /**
     * Creates a new building based on the given type and position.
     *
     * @param type The type of the new building.
     * @param x The x-coordinate of the building's position.
     * @param y The y-coordinate of the building's position.
     * @return The new building.
     */
    private static Building createBuilding(BuildingType type, int x, int y) {
        switch (type) {
            case HOUSE -> {
                return new House(x, y);
            }
            case SHOP -> {
                return new Shop(x, y);
            }
            case OFFICE -> {
                return new Office(x, y);
            }
            default ->
                throw new IllegalArgumentException("Unknown building type: " + type);
        }
    }

    /**
     * Copies the average spend from another random shop in the gene.
     *
     * @param shopToMutate The shop to be mutated.
     * @param gene The gene containing the buildings.
     */
    private static void copyShopAverageSpend(Shop shopToMutate, Gene gene) {
        Shop sourceShop = (Shop) getRandomBuildingOfType(gene, BuildingType.SHOP);

        // Copy average spend
        shopToMutate.setAverageSpend(sourceShop.getAverageSpend());
    }

    /**
     * Copies the office salary from another random office in the gene.
     *
     * @param officeToMutate The office to be mutated.
     * @param gene The gene containing the buildings.
     */
    private static void copyOfficeSalary(Office officeToMutate, Gene gene) {
        Office sourceOffice = (Office) getRandomBuildingOfType(gene, BuildingType.OFFICE);

        // Copy salary
        officeToMutate.setSalary(sourceOffice.getSalary());
    }

    /**
     * Gets a random building of a specific type from the gene.
     *
     * @param gene The gene containing the buildings.
     * @param type The building type to filter.
     * @return A random building of the specified type.
     */
    private static Building getRandomBuildingOfType(Gene gene, BuildingType type) {
        ArrayList<Building> buildingsOfType = new ArrayList<>();

        for (Building building : gene.getBuildingsList()) {
            if (building.getType() == type) {
                buildingsOfType.add(building);
            }
        }

        if (!buildingsOfType.isEmpty()) {
            return buildingsOfType.get(random.nextInt(buildingsOfType.size()));
        } else {
            // If no building of the specified type is found, return null or handle it as needed
            return null;
        }
    }

    /**
     * Mutates the parameters of a shop.
     *
     * @param shop The shop to be mutated.
     */
    private static void mutateShopParameters(Shop shop) {
        double currentAverageSpend = shop.getAverageSpend();
        double mutationAmount = random.nextDouble() * 10 - 5; // Random value between -5 and 5
        shop.setAverageSpend(currentAverageSpend + mutationAmount);
        debug.write("Shop average spend mutated to: " + shop.getAverageSpend());
    }

    /**
     * Mutates the parameters of an office.
     *
     * @param office The office to be mutated.
     */
    private static void mutateOfficeParameters(Office office) {
        double currentSalary = office.getSalary();
        double mutationAmount = random.nextDouble() * 10 - 5; // Random value between -5 and 5
        office.setSalary(currentSalary + mutationAmount);
        debug.write("Office salary mutated to: " + office.getSalary());
    }
}
