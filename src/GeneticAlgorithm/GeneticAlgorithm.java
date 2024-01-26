package GeneticAlgorithm;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
import City.City;
import Debug.Debug;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {

    private static final Random random = new Random();
    private static final Debug debug = new Debug();

    /**
     * Initializes the population of City instances for the genetic algorithm.
     *
     * @param populationSize The size of the population.
     * @param width The width of each city.
     * @param height The height of each city.
     * @param numHouses The number of houses in each city.
     * @param numShops The number of shops in each city.
     * @param numOffices The number of offices in each city.
     * @param shopAverageSpend The average spending in shops.
     * @param officeAverageSalary The average salary in offices.
     * @param variation The possible variation for shop spending and salaries.
     * @param centerBias Bias factor for clumping buildings in the center of the
     * city.
     * @param startingMoney The starting money for each city.
     * @param travelCost The travel cost for each city.
     * @return A list containing the initialized City instances.
     */
    public ArrayList<City> initialPopulation(
            int populationSize,
            int width,
            int height,
            int numHouses,
            int numShops,
            int numOffices,
            double shopAverageSpend,
            double officeAverageSalary,
            double variation,
            double centerBias,
            double startingMoney,
            double travelCost) {

        ArrayList<City> initialPopulation = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            City city = City.initializeRandomCity(
                    width,
                    height,
                    numHouses,
                    numShops,
                    numOffices,
                    shopAverageSpend,
                    officeAverageSalary,
                    variation,
                    centerBias);
            initialPopulation.add(city);
        }

        initializeCityPopulation(initialPopulation, startingMoney, travelCost);

        return initialPopulation;
    }

    /**
     * Populates the given cities with starting money and travel cost.
     *
     * @param cities The list of cities to populate.
     * @param startingMoney The starting money for each city.
     * @param travelCost The travel cost for each city.
     */
    private void initializeCityPopulation(ArrayList<City> cities, double startingMoney, double travelCost) {
        for (City city : cities) {
            city.populate(startingMoney, travelCost);
        }
    }

    /**
     * Evaluates the fitness of each city in the population, sorts them based on
     * fitness, and returns a sorted list.
     *
     * @param population The list of cities to evaluate and sort.
     * @return A sorted list of cities based on fitness.
     */
    public static ArrayList<City> evaluateFitness(ArrayList<City> population) {
        // Evaluate fitness for each city
        for (City city : population) {
            double fitness = city.getTotalMoney() - (city.countInactivePeople() * 100);

            // If fitness is negative, set it to 0
            city.setFitness(Math.max(0, Math.floor(fitness * 100) / 100));
        }

        // Sort the population based on fitness
        Collections.sort(population, Comparator.comparingDouble(City::getFitness).reversed());

        return population;
    }

    /**
     * Method for selecting cities as parents for reproduction.
     *
     * @param population The population of cities.
     * @param selectionMethod The preferred selection method.
     * @param parameters Additional parameters required by the selection method.
     * @return An ArrayList of selected parents.
     */
    public static ArrayList<City> selectParents(ArrayList<City> population, SelectionMethod selectionMethod, Object... parameters) {
        int numberOfParents = Math.max(2, (int) (0.1 * population.size()));
        ArrayList<City> selectedParents;

        switch (selectionMethod) {
            case FITNESS_PROPORTIONAL -> {
                selectedParents = selectParentsUsingFitnessProportional(population, numberOfParents);
            }
            case LINEAR_RANKING -> {
                if (parameters.length > 0 && parameters[0] instanceof Double) {
                    double selectionPressure = (Double) parameters[0];
                    selectedParents = selectParentsUsingLinearRanking(population, numberOfParents, selectionPressure);
                } else {
                    throw new IllegalArgumentException("Linear ranking selection requires a selection pressure parameter.");
                }
            }
            case TOURNAMENT -> {
                if (parameters.length > 0 && parameters[0] instanceof Integer) {
                    int tournamentSize = (Integer) parameters[0];
                    selectedParents = selectParentsUsingTournament(population, numberOfParents, tournamentSize);
                } else {
                    throw new IllegalArgumentException("Tournament selection requires a tournament size parameter.");
                }
            }
            case BOLTZMANN -> {
                if (parameters.length > 0 && parameters[0] instanceof Double) {
                    double temperature = (Double) parameters[0];
                    selectedParents = selectParentsUsingBoltzmann(population, numberOfParents, temperature);
                } else {
                    throw new IllegalArgumentException("Boltzmann selection requires a temperature parameter.");
                }
            }
            default ->
                throw new IllegalArgumentException("Invalid selection method: " + selectionMethod);
        }

        return selectedParents;
    }

    // Helper method for selecting parents using Fitness Proportional Selection
    private static ArrayList<City> selectParentsUsingFitnessProportional(ArrayList<City> population, int numberOfParents) {
        ArrayList<City> selectedParents = new ArrayList<>();
        for (int i = 0; i < numberOfParents; i++) {
            City parent = fitnessProportionalSelection(population);
            selectedParents.add(parent);
        }
        return selectedParents;
    }

    // Helper method for selecting parents using Linear Ranking Selection
    private static ArrayList<City> selectParentsUsingLinearRanking(ArrayList<City> population, int numberOfParents, double selectionPressure) {
        ArrayList<City> selectedParents = new ArrayList<>();
        for (int i = 0; i < numberOfParents; i++) {
            City parent = linearRankingSelection(population, selectionPressure);
            selectedParents.add(parent);
        }
        return selectedParents;
    }

    // Helper method for selecting parents using Tournament Selection
    private static ArrayList<City> selectParentsUsingTournament(ArrayList<City> population, int numberOfParents, int tournamentSize) {
        ArrayList<City> selectedParents = new ArrayList<>();
        for (int i = 0; i < numberOfParents; i++) {
            City parent = tournamentSelection(population, tournamentSize);
            selectedParents.add(parent);
        }
        return selectedParents;
    }

    // Helper method for selecting parents using Boltzmann Selection
    private static ArrayList<City> selectParentsUsingBoltzmann(ArrayList<City> population, int numberOfParents, double temperature) {
        ArrayList<City> selectedParents = new ArrayList<>();
        for (int i = 0; i < numberOfParents; i++) {
            City parent = boltzmannSelection(population, temperature);
            selectedParents.add(parent);
        }
        return selectedParents;
    }

    /**
     * Fitness Proportional Selection (Roulette Wheel Selection).
     *
     * @param population The list of cities to select from.
     * @return The selected city.
     */
    public static City fitnessProportionalSelection(List<City> population) {
        double totalFitness = population.stream().mapToDouble(City::getFitness).sum();
        double randomValue = random.nextDouble() * totalFitness;

        double cumulativeFitness = 0;
        for (City city : population) {
            cumulativeFitness += city.getFitness();
            if (cumulativeFitness >= randomValue) {
                debug.write("Selected by Fitness Proportional Selection: " + city);
                return city;
            }
        }

        // This should not happen under normal circumstances
        City fallbackCity = population.get(random.nextInt(population.size()));
        debug.write("Fallback - Selected by Fitness Proportional Selection: " + fallbackCity);
        return fallbackCity;
    }

    /**
     * Tournament Selection.
     *
     * @param population The list of cities to select from.
     * @param tournamentSize The size of the tournament.
     * @return The selected city.
     */
    public static City tournamentSelection(List<City> population, int tournamentSize) {
        List<City> tournament = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            tournament.add(population.get(random.nextInt(population.size())));
        }

        City selectedCity = tournament.stream().max(Comparator.comparingDouble(City::getFitness)).orElse(null);
        debug.write("Selected by Tournament Selection: " + selectedCity);
        return selectedCity;
    }

    /**
     * Boltzmann Selection.
     *
     * @param population The list of cities to select from.
     * @param temperature The temperature parameter.
     * @return The selected city.
     */
    public static City boltzmannSelection(List<City> population, double temperature) {
        double totalWeight = 0;
        for (City city : population) {
            totalWeight += Math.exp(city.getFitness() / temperature);
        }

        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0;

        for (City city : population) {
            cumulativeWeight += Math.exp(city.getFitness() / temperature);
            if (cumulativeWeight >= randomValue) {
                debug.write("Selected by Boltzmann Selection: " + city);
                return city;
            }
        }

        // This should not happen under normal circumstances
        City fallbackCity = population.get(random.nextInt(population.size()));
        debug.write("Fallback - Selected by Boltzmann Selection: " + fallbackCity);
        return fallbackCity;
    }

    /**
     * Linear Ranking Selection.
     *
     * @param population The list of cities to select from (assumed to be sorted
     * by fitness).
     * @param selectionPressure The selection pressure parameter (typically
     * between 1.5 and 2.0).
     * @return The selected city.
     */
    public static City linearRankingSelection(List<City> population, double selectionPressure) {
        int populationSize = population.size();

        double totalProb = calculateTotalLinearRankingProbability(selectionPressure, populationSize);
        double randomValue = new Random().nextDouble() * totalProb;
        double cumulativeProb = 0;

        for (City city : population) {
            cumulativeProb += calculateLinearRankingProbability(population.indexOf(city) + 1, selectionPressure, populationSize);
            if (cumulativeProb >= randomValue) {
                debug.write("Selected by Linear Ranking Selection: " + city);
                return city;
            }
        }

        // Fallback if selection doesn't happen (unlikely)
        City fallbackCity = population.get(new Random().nextInt(populationSize));
        debug.write("Fallback - Selected by Linear Ranking Selection: " + fallbackCity);
        return fallbackCity;
    }

    /**
     * Calculate the probability for linear ranking selection.
     *
     * @param rank The rank of the individual.
     * @param selectionPressure The selection pressure parameter.
     * @param populationSize The size of the population.
     * @return The calculated probability.
     */
    private static double calculateLinearRankingProbability(int rank, double selectionPressure, int populationSize) {
        return (2.0 - selectionPressure) / populationSize + 2 * (rank - 1) * (selectionPressure - 1) / (populationSize * (populationSize - 1));
    }

    /**
     * Calculate the total probability for linear ranking selection.
     *
     * @param selectionPressure The selection pressure parameter.
     * @param populationSize The size of the population.
     * @return The total calculated probability.
     */
    private static double calculateTotalLinearRankingProbability(double selectionPressure, int populationSize) {
        double totalProb = 0;
        for (int i = 1; i <= populationSize; i++) {
            totalProb += calculateLinearRankingProbability(i, selectionPressure, populationSize);
        }
        return totalProb;
    }

    public static ArrayList<City> crossover(ArrayList<City> parents) {
        return null;
    }

    /**
     * Single-Point Crossover.
     *
     * @param parent1 The first parent's gene.
     * @param parent2 The second parent's gene.
     * @return An arrayList containing two strings representing the offspring
     * genes.
     */
    public static ArrayList<String> singlePointCrossover(String parent1, String parent2) {
        int crossoverPoint = chooseCrossoverPoint(splitGene(parent1));
        ArrayList<String> output = new ArrayList();

        StringBuilder child1 = new StringBuilder();
        StringBuilder child2 = new StringBuilder();

        // Copy genes up to the crossover point
        child1.append(parent1, 0, crossoverPoint);
        child2.append(parent2, 0, crossoverPoint);

        // Copy remaining genes
        child1.append(parent2, crossoverPoint, parent2.length());
        child2.append(parent1, crossoverPoint, parent1.length());

        output.add(child1.toString());
        output.add(child2.toString());

        return output;
    }

    /**
     * Two-Point Crossover.
     *
     * @param parent1 The first parent's gene.
     * @param parent2 The second parent's gene.
     * @return An array containing two strings representing the offspring genes.
     */
    public static String[] twoPointCrossover(String parent1, String parent2) {
        int crossoverPoint1 = chooseCrossoverPoint(splitGene(parent1));
        int crossoverPoint2 = chooseCrossoverPoint(splitGene(parent2));

        StringBuilder child1 = new StringBuilder();
        StringBuilder child2 = new StringBuilder();

        // Copy genes up to the first crossover point
        child1.append(parent1, 0, crossoverPoint1);
        child2.append(parent2, 0, crossoverPoint1);

        // Copy genes between the two crossover points
        child1.append(parent2, crossoverPoint1, crossoverPoint2);
        child2.append(parent1, crossoverPoint1, crossoverPoint2);

        // Copy remaining genes
        child1.append(parent1, crossoverPoint2, parent1.length());
        child2.append(parent2, crossoverPoint2, parent2.length());

        return new String[]{child1.toString(), child2.toString()};
    }

    /**
     * Splits the gene into substrings based on building type.
     *
     * @param gene The gene representation of the city.
     * @return ArrayList of substrings containing city and building information.
     */
    public static ArrayList<String> splitGene(String gene) {
        ArrayList<String> substrings = new ArrayList<>();

        // Split the gene by spaces
        String[] parts = gene.split(" ");

        // Add the first substring with city information
        StringBuilder cityInfo = new StringBuilder();
        int index = 0;
        while (index < parts.length && !parts[index].equals("H") && !parts[index].equals("S") && !parts[index].equals("O")) {
            cityInfo.append(parts[index]).append(" ");
            index++;
        }
        substrings.add(cityInfo.toString().trim());

        while (index < parts.length) {
            StringBuilder buildingInfo = new StringBuilder();
            buildingInfo.append(parts[index++]).append(" ");

            if (parts[index - 1].equals("H")) {
                buildingInfo.append(parts[index++]).append(" ");
                buildingInfo.append(parts[index++]).append(" ");
            }

            if (parts[index - 1].equals("O")) {
                buildingInfo.append(parts[index++]).append(" ");
                buildingInfo.append(parts[index++]).append(" ");
                buildingInfo.append(parts[index++]).append(" ");
            }

            if (parts[index - 1].equals("S")) {
                buildingInfo.append(parts[index++]).append(" ");
                buildingInfo.append(parts[index++]).append(" ");
                buildingInfo.append(parts[index++]).append(" ");
            }

            substrings.add(buildingInfo.toString().trim());
        }

        return substrings;
    }

    /**
     * Chooses a crossover point for the gene representation.
     *
     * @param geneParts ArrayList of substrings containing city and building
     * information.
     * @return The index of the chosen crossover point.
     */
    public static int chooseCrossoverPoint(ArrayList<String> geneParts) {
        // Ensure the index is within the bounds of the gene parts, skipping the first part
        return Math.min(1 + random.nextInt(geneParts.size() - 1), geneParts.size());
    }

    /**
     * Splits the gene into two parts at the given crossover point.
     *
     * @param geneParts ArrayList of substrings containing city and building
     * information.
     * @param crossoverPoint The index of the chosen crossover point.
     * @return An array containing two strings representing the two parts of the
     * gene.
     */
    public static String[] splitGeneAtCrossoverPoint(ArrayList<String> geneParts, int crossoverPoint) {
        StringBuilder part1 = new StringBuilder();
        StringBuilder part2 = new StringBuilder();

        // Append the first part of the gene
        for (int i = 0; i < crossoverPoint; i++) {
            part1.append(geneParts.get(i)).append(" ");
        }

        // Append the second part of the gene
        for (int i = crossoverPoint; i < geneParts.size(); i++) {
            part2.append(geneParts.get(i)).append(" ");
        }

        // Trim any trailing spaces
        String[] result = {part1.toString().trim(), part2.toString().trim()};
        return result;
    }

    // Placeholder method for applying mutation to the offspring cities
    public static void mutate(ArrayList<City> offspring) {
        // Implement mutation logic here
    }

    // Placeholder method for running the genetic algorithm
    public static void runGeneticAlgorithm(int populationSize, int generations, int cityWidth, int cityHeight) {
        // Implement the overall genetic algorithm process here
    }
}
