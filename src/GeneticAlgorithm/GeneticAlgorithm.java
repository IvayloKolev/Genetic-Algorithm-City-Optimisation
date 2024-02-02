package GeneticAlgorithm;

import City.City;
import City.Gene;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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
            double travelCost) {

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

        for (int i = 0; i < simulationDays; i++) {
            for (City city : population) {
                city.simulate();
            }
        }

        for (int generation = 0; generation < generations; generation++) {

            // Evaluate fitness for the current population
            evaluateFitness(population);
            // Sort the population by fitness
            population.sort(Comparator.comparingDouble(City::getFitness).reversed());

            // Print the best fitness in each generation
            System.out.println("Generation " + (generation + 1) + ": Best Fitness - " + population.get(0).getFitness());

            // Select parents for crossover
            ArrayList<City> parents = Selection.selectParents(population, selectionMethod, selectionMethodParameter);
            ArrayList<Gene> parentsGenes = new ArrayList<>();
            for (City city : parents) {
                Gene gene = Gene.encode(city);
                parentsGenes.add(gene);
            }

            // Perform crossover to generate offspring
            ArrayList<Gene> offspring = Crossover.crossover(parentsGenes, crossoverMethod, populationSize);

            // Mutate the offspring
            Mutation.mutate(offspring, mutationChance);

            // Decode the offspring genes to create a new population of cities
            ArrayList<City> newPopulation = new ArrayList<>();
            for (Gene gene : offspring) {
                City city = Gene.decode(gene);
                newPopulation.add(city);
            }

            // Replace the old population with the new one
            population = newPopulation;
            // Sort the population by fitness
            population.sort(Comparator.comparingDouble(City::getFitness).reversed());

        }

        evaluateFitness(population);

        // After the specified number of generations, print the details of the best city
        City bestCity = population.get(0);
        String bestCityOutput
                = "\nBest City After " + generations + " Generations:"
                + "\nFitness: " + bestCity.getFitness()
                + "\nTotal Money: " + bestCity.getTotalMoney()
                + "\nInactive People: " + bestCity.countInactivePeople()
                + "\nActive People: " + bestCity.countActivePeople()
                + "\nRichest Person: \n" + bestCity.findRichestPerson().toString();

        System.out.println(bestCityOutput);
        System.out.println(bestCity.toStringGridLayout());

        // Create a Map to return two different data types from 1 method
        Map<String, Object> outputDetails = new HashMap<>();
        outputDetails.put("bestCity", bestCity);
        outputDetails.put("bestCityOutput", bestCityOutput);

        return outputDetails;

    }

}
