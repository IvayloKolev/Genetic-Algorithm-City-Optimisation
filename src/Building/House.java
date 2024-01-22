package Building;

/**
 * Represents a house in the city.
 *
 * @author Ivaylo Kolev 2005549
 */
public class House extends Building {

    /**
     * Constructs a house with the specified position.
     *
     * @param x The x-coordinate of the house.
     * @param y The y-coordinate of the house.
     */
    public House(int x, int y) {
        super(BuildingType.HOUSE, x, y);
    }

    /**
     * Copy Constructor for House.
     *
     * @param toBeCopiedHouse The House object to be copied.
     */
    public House(House toBeCopiedHouse) {
        super(toBeCopiedHouse);
    }

}
