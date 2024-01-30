package GeneticAlgorithm;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
import Building.Building;
import City.City;
import City.Gene;
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

            city.populate(startingMoney, travelCost);
        }

        return initialPopulation;
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
     * Evaluates the fitness of a single city.
     *
     * @param city The city to evaluate.
     * @return The fitness value for the city.
     */
    public double evaluateSingleCityFitness(City city) {
        double fitness = city.getTotalMoney() - (city.countInactivePeople() * 100);

        // If fitness is negative, set it to 0
        fitness = Math.max(0, Math.floor(fitness * 100) / 100);

        city.setFitness(fitness);

        return fitness;
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

    public static ArrayList<City> crossover(Gene parent1, Gene parent2, CrossoverMethod crossoverMethod) {
        ArrayList<City> offspring = new ArrayList<>();

        switch (crossoverMethod) {
            case ONE_POINT -> {
                // Implement one-point crossover logic
                // ...
            }
            case TWO_POINT -> {
                // Implement two-point crossover logic
                // ...
            }
            case UNIFORM -> {
                // Implement uniform crossover logic
                // ...
            }
            default ->
                throw new IllegalArgumentException("Invalid crossover method: " + crossoverMethod);
        }

        return offspring;
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

        // Create a list to store the offspring
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

    // Placeholder method for applying mutation to the offspring cities
    public static void mutate(ArrayList<City> offspring) {
        // Implement mutation logic here
    }

    // Placeholder method for running the genetic algorithm
    public static void runGeneticAlgorithm(int populationSize, int generations, int cityWidth, int cityHeight) {
        // Implement the overall genetic algorithm process here
    }

}
