package GeneticAlgorithm;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
import City.City;
import java.util.ArrayList;
import java.util.List;

public class GeneticAlgorithm {

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
     * Evaluates the fitness of each city in the population.
     *
     * @param population The list of cities to evaluate.
     */
    public static void evaluateFitness(ArrayList<City> population) {
        for (City city : population) {
            city.setFitness(city.getTotalMoney() - (city.countInactivePeople() * 100));
        }
    }

    // Placeholder method for selecting cities as parents for reproduction
    public static ArrayList<City> selectParents(List<City> population) {
        // Implement parent selection logic here
        return null;
    }

    // Placeholder method for crossover (recombination) of parent cities to create offspring
    public static ArrayList<City> crossover(List<City> parents) {
        // Implement crossover logic here
        return null;
    }

    // Placeholder method for applying mutation to the offspring cities
    public static void mutate(List<City> offspring) {
        // Implement mutation logic here
    }

    // Placeholder method for replacing the old population with the new generation
    public static void replacePopulation(List<City> population, List<City> newGeneration) {
        // Implement population replacement logic here
    }

    // Placeholder method for running the genetic algorithm
    public static void runGeneticAlgorithm(int populationSize, int generations, int cityWidth, int cityHeight) {
        // Implement the overall genetic algorithm process here
    }
}
