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
     * @param selectionMethod The preferred selection method.
     * @param parameters Additional parameters required by the selection method.
     * @return An ArrayList of selected parents.
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

}
