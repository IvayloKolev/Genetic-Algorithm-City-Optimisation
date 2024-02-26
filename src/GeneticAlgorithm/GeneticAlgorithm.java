package GeneticAlgorithm;

import City.City;
import City.Gene;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The GeneticAlgorithm class provides methods for initializing and running a
 * genetic algorithm to evolve populations of City instances.
 *
 * @author Ivaylo Kolev 2005549
 */
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
            evaluateCityFitness(city);
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
    public static double evaluateCityFitness(City city) {
        double fitness = city.getTotalMoney() - (city.countInactivePeople() * 100);

        // If fitness is negative, set it to 0
        fitness = Math.max(0, Math.floor(fitness * 100) / 100);

        city.setFitness(fitness);

        return fitness;
    }

    /**
     * Runs the genetic algorithm according to the parameters.
     *
     * @param generations The number of generations to run the genetic
     * algorithm.
     * @param simulationDays The number of simulation days for each generation.
     * @param selectionMethod The selection method used for parent selection.
     * @param selectionMethodParameter The parameter for the selection method.
     * @param crossoverMethod The crossover method used for generating
     * offspring.
     * @param mutationChance The chance of mutation for the offspring.
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
     * @return A Map containing the best City instance and its details after the
     * specified generations.
     * @throws java.lang.InterruptedException
     */
    public Map<String, Object> runGeneticAlgorithm(
            int generations,
            int simulationDays,
            SelectionMethod selectionMethod,
            double selectionMethodParameter,
            CrossoverMethod crossoverMethod,
            double mutationChance,
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
            double travelCost) throws InterruptedException {

        ArrayList<City> population = initialPopulation(
                populationSize,
                width,
                height,
                numHouses,
                numShops,
                numOffices,
                shopAverageSpend,
                officeAverageSalary,
                variation,
                centerBias,
                startingMoney,
                travelCost);

        ExecutorService executor = Executors.newFixedThreadPool(populationSize);

        try {
            for (int generation = 0; generation < generations; generation++) {

                runSimulationTasks(executor, population, simulationDays);

                evaluateAndSortPopulation(population);

                printBestFitness(generation, population);

                ArrayList<Gene> offspring = generateOffspring(
                        population,
                        selectionMethod,
                        selectionMethodParameter,
                        crossoverMethod);

                mutateOffspring(offspring, mutationChance);

                population = createNewPopulation(offspring);

                evaluateAndSortPopulation(population);
            }

            // Simulate the final population
            runSimulationTasks(executor, population, simulationDays);

            // Evaluate fitness for the final population
            evaluateAndSortPopulation(population);

            City bestCity = population.get(0);
            String bestCityOutput = generateBestCityOutput(generations, bestCity);

            System.out.println(bestCityOutput);
            System.out.println(bestCity.toStringGridLayout());

            Map<String, Object> outputDetails = new HashMap<>();
            outputDetails.put("bestCity", bestCity);
            outputDetails.put("bestCityOutput", bestCityOutput);

            return outputDetails;
        } finally {
            shutdownExecutor(executor);
        }
    }

    /**
     * Runs simulation tasks for each city in parallel using the provided
     * ExecutorService.
     *
     * @param executor The ExecutorService to run simulation tasks.
     * @param population The list of cities to simulate.
     * @param simulationDays The number of simulation days for each city.
     * @throws InterruptedException If the execution is interrupted.
     */
    private void runSimulationTasks(ExecutorService executor, ArrayList<City> population, int simulationDays) throws InterruptedException {
        List<Callable<Void>> tasks = new ArrayList<>();
        for (City city : population) {
            tasks.add(() -> {
                for (int i = 0; i < simulationDays; i++) {
                    city.simulate();
                }
                return null;
            });
        }
        executor.invokeAll(tasks);
    }

    /**
     * Evaluates the fitness of each city in the population, sorts them based on
     * fitness, and returns a sorted list.
     *
     * @param population The list of cities to evaluate and sort.
     */
    private void evaluateAndSortPopulation(ArrayList<City> population) {
        evaluateFitness(population);
        population.sort(Comparator.comparingDouble(City::getFitness).reversed());
    }

    /**
     * Prints the best fitness of the current generation.
     *
     * @param generation The current generation number.
     * @param population The list of cities.
     */
    private void printBestFitness(int generation, ArrayList<City> population) {
        System.out.println("Generation " + (generation + 1) + ": Best Fitness - " + population.get(0).getFitness());
    }

    /**
     * Generates offspring using crossover and returns a list of their genes.
     *
     * @param population The list of cities.
     * @param selectionMethod The selection method used for parent selection.
     * @param selectionMethodParameter The parameter for the selection method.
     * @param crossoverMethod The crossover method used for generating
     * offspring.
     * @return A list of genes representing the offspring.
     */
    private ArrayList<Gene> generateOffspring(ArrayList<City> population, SelectionMethod selectionMethod, double selectionMethodParameter, CrossoverMethod crossoverMethod) {
        ArrayList<City> parents = Selection.selectParents(population, selectionMethod, selectionMethodParameter);
        ArrayList<Gene> parentsGenes = encodeParents(parents);
        return Crossover.crossover(parentsGenes, crossoverMethod, population.size());
    }

    /**
     * Encodes a list of cities into genes.
     *
     * @param parents The list of cities to encode.
     * @return A list of genes representing the encoded cities.
     */
    private ArrayList<Gene> encodeParents(ArrayList<City> parents) {
        ArrayList<Gene> parentsGenes = new ArrayList<>();
        for (City city : parents) {
            Gene gene = Gene.encode(city);
            parentsGenes.add(gene);
        }
        return parentsGenes;
    }

    /**
     * Mutates the genes of the offspring with the given mutation chance.
     *
     * @param offspring The list of genes to mutate.
     * @param mutationChance The chance of mutation.
     */
    private void mutateOffspring(ArrayList<Gene> offspring, double mutationChance) {
        Mutation.mutate(offspring, mutationChance);
    }

    /**
     * Creates a new population of cities by decoding the offspring genes.
     *
     * @param offspring The list of genes representing the offspring.
     * @return A new population of cities.
     */
    private ArrayList<City> createNewPopulation(ArrayList<Gene> offspring) {
        ArrayList<City> newPopulation = new ArrayList<>();
        for (Gene gene : offspring) {
            City city = Gene.decode(gene);
            newPopulation.add(city);
        }
        return newPopulation;
    }

    /**
     * Shuts down the provided ExecutorService and waits for all threads to
     * finish.
     *
     * @param executor The ExecutorService to shut down.
     * @throws InterruptedException If the execution is interrupted.
     */
    private void shutdownExecutor(ExecutorService executor) throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    /**
     * Generates a formatted output string for the best city after a specified
     * number of generations.
     *
     * @param generations The number of generations.
     * @param bestCity The best city.
     * @return A formatted output string.
     */
    private String generateBestCityOutput(int generations, City bestCity) {
        double roundedFitness = Math.round(bestCity.getFitness() * 100.0) / 100.0;
        double roundedMoney = Math.round(bestCity.getTotalMoney() * 100.0) / 100.0;

        return "\nBest City After " + generations + " Generations:"
                + "\nFitness: " + roundedFitness
                + "\nTotal Money: " + roundedMoney
                + "\nInactive People: " + bestCity.countInactivePeople()
                + "\nActive People: " + bestCity.countActivePeople()
                + "\nRichest Person: \n" + bestCity.findRichestPerson().toString();
    }
}
