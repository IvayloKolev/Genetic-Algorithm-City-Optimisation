package Building;

import java.util.Random;

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

        // Generate a random value within [-variation, +variation]
        Random random = new Random();
        double variationFactor = -variation + (2 * variation * random.nextDouble());

        double averageSpendTemp = averageSpend + (averageSpend * variationFactor);

        this.averageSpend = Math.floor(averageSpendTemp * 100) / 100;
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

    // Getters and Setters
    public double getAverageSpend() {
        return averageSpend;
    }
}
