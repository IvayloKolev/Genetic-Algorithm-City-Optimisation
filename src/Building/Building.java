package Building;

import java.util.Random;

/**
 * Represents a building in the city.
 *
 * @author Ivaylo Kolev 2005549
 */
public class Building {

    private BuildingType type;
    private Position position;

    /**
     * Constructs a new building with the given type and position.
     *
     * @param type The type of the building.
     * @param x The x-coordinate of the building's position.
     * @param y The y-coordinate of the building's position.
     */
    public Building(BuildingType type, int x, int y) {
        this.type = type;
        this.position = new Position(x, y);
    }

    /**
     * Copy Constructor.
     *
     * @param toBeCopiedBuilding The Building Object to be copied.
     */
    public Building(Building toBeCopiedBuilding) {
        if (toBeCopiedBuilding != null) {
            this.type = toBeCopiedBuilding.getType();
            this.position = toBeCopiedBuilding.getPosition();
        } else {
            // Handle the case where toBeCopiedBuilding is null
            this.type = BuildingType.DEFAULT;
            this.position = new Position(0, 0);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;  // Both references point to the same object
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;  // Different classes or obj is null
        }

        Building otherBuilding = (Building) obj;
        return this.getPosition().equals(otherBuilding.getPosition());
    }

    /**
     * Common method to calculate the value with variation.
     *
     * @param baseValue The base value (average spend or salary).
     * @param variation The variation factor affecting the value.
     * @return The calculated value with variation.
     */
    protected static double calculateValueWithVariation(double baseValue, double variation) {
        Random random = new Random();
        double variationFactor = -variation + (2 * variation * random.nextDouble());
        double valueWithVariation = baseValue + (1 + variationFactor);
        return Math.floor(valueWithVariation * 100) / 100;
    }

    // Getters
    public BuildingType getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public int getX() {
        return this.getPosition().getX();
    }

    public void setX(int x) {
        this.position.setX(x);
    }

    public int getY() {
        return this.getPosition().getY();
    }

    public void setY(int y) {
        this.position.setY(y);
    }

}
