package GeneticAlgorithm;

import java.util.ArrayList;

/**
 * Represents different crossover methods in a genetic algorithm.
 *
 * @author Ivaylo Kolev 2005549
 */
public enum CrossoverMethod {
    OnePoint("One Point"),
    TwoPoint("Two Point"),
    Uniform("Uniform");

    private final String displayName;

    CrossoverMethod(String displayName) {
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
        for (CrossoverMethod value : values()) {
            stringValues.add(value.getDisplayName());
        }
        return stringValues;
    }
}
