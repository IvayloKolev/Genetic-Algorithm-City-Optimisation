package Building;

/**
 * Represents an office building in the city simulation.
 *
 * @author Ivaylo Kolev 2005549
 */
public class Office extends Building {

    private double salary;
    private double variation;

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
        this.variation = variation;
        this.salary = calculateValueWithVariation(averageSalary, variation);
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

    public Office(int x, int y) {
        super(BuildingType.OFFICE, x, y);
    }

    // Getters and Setters
    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getVariation() {
        return variation;
    }

    public void setVariation(double variation) {
        this.variation = variation;
    }

}
