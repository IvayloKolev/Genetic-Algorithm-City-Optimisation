package Building;

import java.util.Random;

/**
 * Enumeration representing different types of buildings in the city. Each
 * building type has an associated symbol used for display.
 *
 * @author Ivaylo Kolev 2005549
 */
public enum BuildingType {
    HOUSE('H'), // Represents a House
    SHOP('S'), // Represents a Shop
    OFFICE('O'), // Represents an Office
    ROAD('+'), // Represents a Road
    EMPTY(' '), // Represents an Empty Space
    DEFAULT(' '); // Default

    /**
     * The symbol associated with the building type.
     */
    private final char symbol;

    /**
     * Constructs a building type with the specified symbol.
     *
     * @param symbol The symbol associated with the building type.
     */
    BuildingType(char symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the symbol associated with the building type.
     *
     * @return The symbol of the building type.
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Gets a random building type from the set {HOUSE, SHOP, OFFICE}.
     *
     * @return A random building type.
     */
    public static BuildingType getRandomBuildingType() {
        Random random = new Random();
        BuildingType[] buildingTypes = {HOUSE, SHOP, OFFICE};
        return buildingTypes[random.nextInt(buildingTypes.length)];
    }
}
