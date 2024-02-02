package Building;

/**
 * The Road class represents a road in the city.
 *
 * @author Ivaylo Kolev 2005549
 */
public class Road extends Building {

    /**
     * Constructs a Road object at the specified position.
     *
     * @param x The x-coordinate of the road.
     * @param y The y-coordinate of the road.
     */
    public Road(int x, int y) {
        super(BuildingType.ROAD, x, y);
    }
}
