package Building;

import java.util.Random;

/**
 * Represents an office building in the city simulation.
 *
 * @author Ivaylo Kolev 2005549
 */
public class Office extends Building {

    private double salary;
    private double variation;
    private double variationFactor;

    /**
     * Constructs a new Office.
     *
     * @param x The x-coordinate of the office's position.
     * @param y The y-coordinate of the office's position.
     * @param averageSalary The average salary provided by the office.
     * @param variation The variation factor affecting the salary.
     */
    public Office(int x, int y, double averageSalary, double variation) {
        super(BuildingType.OFFICE, x, y);

        // Generate a random value within [-variation, +variation]
        Random random = new Random();
        this.variationFactor = -variation + (2 * variation * random.nextDouble());
        this.variation = variation;

        double averageSalaryTemp = averageSalary + (averageSalary * variationFactor);

        this.salary = Math.floor(averageSalaryTemp * 100) / 100;
    }

    /**
     * Copy Constructor
     *
     * @param toBeCopiedOffice The Office object to be copied.
     */
    public Office(Office toBeCopiedOffice) {
        super(toBeCopiedOffice);
        this.salary = toBeCopiedOffice.salary;
    }

    // Getters and Setters
    public double getSalary() {
        return salary;
    }

    public double getVariation() {
        return variation;
    }

    public double getVariationFactor() {
        return variationFactor;
    }

}
