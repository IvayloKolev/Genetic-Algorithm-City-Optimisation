package Building;

/**
 * Represents a shop building in the city simulation.
 *
 * @author Ivaylo Kolev 2005549
 */
public class Shop extends Building {

    private double averageSpend;
    private double variation;

    /**
     * Constructs a new Shop.
     *
     * @param x The x-coordinate of the shop's position.
     * @param y The y-coordinate of the shop's position.
     * @param averageSpend The average spending in the shop.
     * @param variation The variation factor affecting the spending.
     */
    public Shop(int x, int y, double averageSpend, double variation) {
        super(BuildingType.SHOP, x, y);
        this.variation = variation;
        this.averageSpend = calculateValueWithVariation(averageSpend, variation);
    }

    /**
     * Copy Constructor
     *
     * @param toBeCopiedShop The Shop object to be copied.
     */
    public Shop(Shop toBeCopiedShop) {
        super(toBeCopiedShop);
        this.averageSpend = toBeCopiedShop.averageSpend;
    }

    public Shop(int x, int y) {
        super(BuildingType.SHOP, x, y);
    }

    // Getters and Setters
    public double getAverageSpend() {
        return averageSpend;
    }

    public void setAverageSpend(double averageSpend) {
        this.averageSpend = averageSpend;
    }

    public double getVariation() {
        return variation;
    }

    public void setVariation(double variation) {
        this.variation = variation;
    }

}
