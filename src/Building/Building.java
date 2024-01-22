package Building;

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

    public int getY() {
        return this.getPosition().getY();
    }

}
