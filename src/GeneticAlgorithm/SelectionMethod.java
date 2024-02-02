package GeneticAlgorithm;

import java.util.ArrayList;

/**
 * Represents different selection methods in a genetic algorithm.
 *
 * @author Ivaylo Kolev 2005549
 */
public enum SelectionMethod {
    Fitness_Proportional("Fitness_Proportional"),
    Linear_Ranking("Linear_Ranking"),
    Tournament("Tournament"),
    Boltzmann("Boltzmann");

    private final String displayName;

    SelectionMethod(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ArrayList<String> getStringValues() {
        ArrayList<String> stringValues = new ArrayList<>();
        for (SelectionMethod value : values()) {
            stringValues.add(value.getDisplayName());
        }
        return stringValues;
    }
}
