package GeneticAlgorithm;

import City.City;
import Debug.Debug;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class Selection {

    private static final Random random = new Random();
    private static final Debug debug = new Debug();

    /**
     * Method for selecting cities as parents for reproduction.
     *
     * @param population The population of cities.
     * @param selectionMethod The preferred selection method. Supported methods
     * are: - Fitness_Proportional - Linear_Ranking - Tournament - Boltzmann
     * @param parameters Additional parameters required by the selection method.
     * The required parameters vary based on the selection method.
     * @return An ArrayList of selected parents.
     *
     * @throws IllegalArgumentException If the selection method is invalid or if
     * the required parameters are not provided.
     */
    public static ArrayList<City> selectParents(ArrayList<City> population, SelectionMethod selectionMethod, Object... parameters) {
        int numberOfParents = Math.max(2, (int) (0.1 * population.size()));
        ArrayList<City> selectedParents;

        switch (selectionMethod) {
            case Fitness_Proportional -> {
                selectedParents = selectParentsUsingFitnessProportional(population, numberOfParents);
            }
            case Linear_Ranking -> {
                if (parameters.length > 0 && parameters[0] instanceof Double) {
                    double selectionPressure = (Double) parameters[0];
                    selectedParents = selectParentsUsingLinearRanking(population, numberOfParents, selectionPressure);
                } else {
                    throw new IllegalArgumentException("Linear ranking selection requires a selection pressure parameter.");
                }
            }
            case Tournament -> {
                if (parameters.length > 0 && parameters[0] instanceof Integer) {
                    int tournamentSize = (Integer) parameters[0];
                    selectedParents = selectParentsUsingTournament(population, numberOfParents, tournamentSize);
                } else {
                    throw new IllegalArgumentException("Tournament selection requires a tournament size parameter.");
                }
            }
            case Boltzmann -> {
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

    /**
     * Selects parents from a population using Linear Ranking Selection.
     *
     * This method applies Linear Ranking Selection to choose a specified number
     * of parents from the given population based on the provided selection
     * pressure.
     *
     * @param population The list of City objects representing the population.
     * @param numberOfParents The number of parents to be selected.
     * @param selectionPressure The selection pressure parameter used in Linear
     * Ranking Selection.
     * @return An ArrayList of City objects representing the selected parents.
     * @throws IllegalArgumentException If the selection pressure is not within
     * the valid range.
     */
    private static ArrayList<City> selectParentsUsingLinearRanking(ArrayList<City> population, int numberOfParents, double selectionPressure) {
        ArrayList<City> selectedParents = new ArrayList<>();
        for (int i = 0; i < numberOfParents; i++) {
            City parent = linearRankingSelection(population, selectionPressure);
            selectedParents.add(parent);
        }
        return selectedParents;
    }

    /**
     * Selects parents from a population using Tournament Selection.
     *
     * This method applies Tournament Selection to choose a specified number of
     * parents from the given population based on the provided tournament size.
     *
     * @param population The list of City objects representing the population.
     * @param numberOfParents The number of parents to be selected.
     * @param tournamentSize The size of the tournament used in Tournament
     * Selection.
     * @return An ArrayList of City objects representing the selected parents.
     * @throws IllegalArgumentException If the tournament size is not within the
     * valid range.
     */
    private static ArrayList<City> selectParentsUsingTournament(ArrayList<City> population, int numberOfParents, int tournamentSize) {
        ArrayList<City> selectedParents = new ArrayList<>();
        for (int i = 0; i < numberOfParents; i++) {
            City parent = tournamentSelection(population, tournamentSize);
            selectedParents.add(parent);
        }
        return selectedParents;
    }

    /**
     * Selects parents from a population using Boltzmann Selection.
     *
     * This method applies Boltzmann Selection to choose a specified number of
     * parents from the given population based on the provided temperature.
     *
     * @param population The list of City objects representing the population.
     * @param numberOfParents The number of parents to be selected.
     * @param temperature The temperature parameter used in Boltzmann Selection.
     * @return An ArrayList of City objects representing the selected parents.
     * @throws IllegalArgumentException If the temperature is not within the
     * valid range.
     */
    private static ArrayList<City> selectParentsUsingBoltzmann(ArrayList<City> population, int numberOfParents, double temperature) {
        ArrayList<City> selectedParents = new ArrayList<>();
        for (int i = 0; i < numberOfParents; i++) {
            City parent = boltzmannSelection(population, temperature);
            selectedParents.add(parent);
        }
        return selectedParents;
    }

    /**
     * Fitness Proportional Selection (Roulette Wheel Selection). This selection
     * method simulates a roulette wheel, where the probability of selecting a
     * city is proportional to its fitness. The higher the fitness, the larger
     * the slice on the wheel. A random value within the total fitness range is
     * generated, and the method iterates through the population, accumulating
     * fitness values until the accumulated fitness exceeds the random value.
     * The selected city is returned. If the selection fails, a fallback city is
     * chosen randomly.
     *
     * @param population The list of cities to select from.
     * @return The selected city.
     */
    public static City fitnessProportionalSelection(List<City> population) {
        // Calculate the total fitness of the population
        double totalFitness = population.stream().mapToDouble(City::getFitness).sum();

        // Generate a random value within the total fitness range
        double randomValue = random.nextDouble() * totalFitness;

        double cumulativeFitness = 0;
        for (City city : population) {
            // Accumulate the cumulative fitness
            cumulativeFitness += city.getFitness();

            // Check if the accumulated fitness exceeds the random value
            if (cumulativeFitness >= randomValue) {
                debug.write("Selected by Fitness Proportional Selection: " + city);
                return city;
            }
        }

        // Fallback: This should not happen under normal circumstances
        City fallbackCity = population.get(random.nextInt(population.size()));
        debug.write("Fallback - Selected by Fitness Proportional Selection: " + fallbackCity);
        return fallbackCity;
    }

    /**
     * Tournament Selection. This selection method involves holding tournaments
     * among randomly chosen individuals from the population. A tournament is
     * created by randomly selecting individuals, and the fittest individual
     * from the tournament is selected. This process is repeated for the
     * specified number of tournaments, and the selected city is returned.
     *
     * @param population The list of cities to select from.
     * @param numberOfTournaments The number of tournaments held between genes.
     * @return The selected city.
     */
    public static City tournamentSelection(List<City> population, int numberOfTournaments) {
        // Create a tournament by randomly selecting individuals
        List<City> tournament = new ArrayList<>();
        for (int i = 0; i < numberOfTournaments; i++) {
            tournament.add(population.get(random.nextInt(population.size())));
        }

        // Select the fittest individual from the tournament
        City selectedCity = tournament.stream().max(Comparator.comparingDouble(City::getFitness)).orElse(null);
        debug.write("Selected by Tournament Selection: " + selectedCity);
        return selectedCity;
    }

    /**
     * Boltzmann Selection. This selection method introduces a temperature
     * parameter that influences the likelihood of selecting cities. The total
     * weight is calculated based on the Boltzmann formula, where the weight of
     * each city is determined by its fitness and the temperature. A random
     * value is generated within the total weight range, and the method iterates
     * through the population, accumulating weights until the accumulated weight
     * exceeds the random value. The selected city is returned. If the selection
     * fails, a fallback city is chosen randomly.
     *
     * @param population The list of cities to select from.
     * @param temperature The temperature parameter.
     * @return The selected city.
     */
    public static City boltzmannSelection(List<City> population, double temperature) {
        // Calculate the total weight based on the Boltzmann formula
        double totalWeight = 0;
        for (City city : population) {
            totalWeight += Math.exp(city.getFitness() / temperature);
        }

        // Generate a random value within the total weight range
        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0;

        for (City city : population) {
            // Accumulate the cumulative weight
            cumulativeWeight += Math.exp(city.getFitness() / temperature);

            // Check if the accumulated weight exceeds the random value
            if (cumulativeWeight >= randomValue) {
                debug.write("Selected by Boltzmann Selection: " + city);
                return city;
            }
        }

        // Fallback: This should not happen under normal circumstances
        City fallbackCity = population.get(random.nextInt(population.size()));
        debug.write("Fallback - Selected by Boltzmann Selection: " + fallbackCity);
        return fallbackCity;
    }

    /**
     * Linear Ranking Selection. This selection method ranks the population
     * based on fitness, assuming the population is sorted in descending order.
     * The total probability for linear ranking selection is calculated,
     * considering the selection pressure. A random value is generated within
     * the total probability range, and the method iterates through the
     * population, accumulating probabilities until the accumulated probability
     * exceeds the random value. The selected city is returned. If the selection
     * fails, a fallback city is chosen randomly.
     *
     * @param population The list of cities to select from.
     * @param selectionPressure The selection pressure parameter (typically
     * between 1.5 and 2.0).
     * @return The selected city.
     */
    public static City linearRankingSelection(List<City> population, double selectionPressure) {
        int populationSize = population.size();

        // Calculate the total probability for linear ranking selection
        double totalProb = calculateTotalLinearRankingProbability(selectionPressure, populationSize);
        double randomValue = new Random().nextDouble() * totalProb;
        double cumulativeProb = 0;

        for (City city : population) {
            // Accumulate the cumulative probability
            cumulativeProb += calculateLinearRankingProbability(population.indexOf(city) + 1, selectionPressure, populationSize);

            // Check if the accumulated probability exceeds the random value
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

}
