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
            city.setFitness(Math.floor(fitness * 100) / 100);
        }

        // Sort the population based on fitness
        Collections.sort(population, Comparator.comparingDouble(City::getFitness).reversed());

        return population;
    }

    // Placeholder method for selecting cities as parents for reproduction
    public static ArrayList<City> selectParents(ArrayList<City> population) {
        // Implement parent selection logic here
        return null;
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

    // Placeholder method for crossover (recombination) of parent cities to create offspring
    public static ArrayList<City> crossover(ArrayList<City> parents) {
        // Implement crossover logic here
        return null;
    }

    // Placeholder method for applying mutation to the offspring cities
    public static void mutate(ArrayList<City> offspring) {
        // Implement mutation logic here
    }

    // Placeholder method for replacing the old population with the new generation
    public static void replacePopulation(ArrayList<City> population, List<City> newGeneration) {
        // Implement population replacement logic here
    }

    // Placeholder method for running the genetic algorithm
    public static void runGeneticAlgorithm(int populationSize, int generations, int cityWidth, int cityHeight) {
        // Implement the overall genetic algorithm process here
    }
}
