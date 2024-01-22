package Person;

import Building.House;
import Building.Position;
import Building.Building;
import Building.Office;
import Building.Shop;
import City.City;
import Debug.Debug;
import static Person.FirstNames.getRandomFirstName;
import static Person.LastNames.getRandomLastName;
import java.util.ArrayList;

/**
 * Represents a person in the city simulation.
 *
 * @author Ivaylo Kolev 2005549
 */
public class Person {

    private String name;
    private double money;
    private double travelCost;
    private Position currentPosition;
    private House house;
    private Office office;
    private City city;
    private Boolean active;

    /**
     * Constructs a new Person with specified attributes.
     *
     * @param money The initial amount of money the person has.
     * @param travelCost The cost of travel for the person.
     * @param house The house where the person resides.
     * @param office The office where the person works.
     * @param city The city the person belongs to.
     */
    public Person(double money, double travelCost, House house, Office office, City city) {
        this.name = getRandomFirstName() + " " + getRandomLastName();
        this.money = money;
        this.travelCost = travelCost;
        this.house = house;
        this.currentPosition = this.house.getPosition();
        this.office = office;
        this.city = city;
        this.active = true;
    }

    /**
     * Copy Constructor
     *
     * @param toBeCopiedPerson The Person object to be copied.
     */
    public Person(Person toBeCopiedPerson) {
        this.name = toBeCopiedPerson.name;
        this.money = toBeCopiedPerson.money;
        this.travelCost = toBeCopiedPerson.travelCost;
        this.currentPosition = toBeCopiedPerson.currentPosition;
        this.active = toBeCopiedPerson.active;
        this.house = toBeCopiedPerson.house;
        this.office = toBeCopiedPerson.office;
        this.city = toBeCopiedPerson.city;
    }

    /**
     * Simulates the person going to work.
     */
    public void goToWork() {

        Debug debug = new Debug();

        // Check if the person is inactive or has insufficient money
        if (!active || this.getMoney() <= 0) {
            debug.write("Person " + this.getName() + " can't go to work - inactive or insufficient money");
            this.setInactive();
            return;
        }

        int distance = calculateManhattanDistance(house.getPosition(), office.getPosition());
        double finalCost = travelCost * distance;

        // Check if the person has enough money for travel
        if (!this.hasMoney(finalCost)) {
            this.setInactive();
            return;
        }

        money -= finalCost;
        this.setCurrentPosition(this.office.getPosition());
        money += office.getAverageSalary();
        money = Math.floor(money * 100) / 100;
        debug.write("Person " + this.getName() + " is going to work.\nDistance from house to office: " + distance
                + "\nTravel cost deducted from money: " + travelCost
                + "\nMoney made at work: " + office.getAverageSalary()
                + "\nRemaining money: " + money);
    }

    /**
     * Simulates the person going shopping.
     */
    public void goShopping() {

        Debug debug = new Debug();

        // Check if the person is inactive or has insufficient money
        if (!active || this.getMoney() <= 0) {
            debug.write("Person " + this.getName() + " can't go shopping - inactive or insufficient money");
            this.setInactive();
            return;
        }

        ArrayList<Building> shops = city.getShops();
        Shop closestShop = findClosestShop(shops);

        if (closestShop != null) {
            int distance = calculateManhattanDistance(currentPosition, closestShop.getPosition());
            double finalCost = travelCost * distance;

            // Check if the person has enough money for travel
            if (!this.hasMoney(finalCost)) {
                this.setInactive();
                return;
            }

            money -= finalCost;
            this.setCurrentPosition(closestShop.getPosition());

            // Check if the person has enough money for shopping
            if (!this.hasMoney(closestShop.getAverageSpend())) {
                this.setInactive();
                return;
            }

            money -= closestShop.getAverageSpend();
            money = Math.floor(money * 100) / 100;
            debug.write("Person " + this.getName() + " is going shopping.\nDistance from current position to shop: " + distance
                    + "\nTravel cost deducted from money: " + travelCost
                    + "\nMoney spent at shop: " + closestShop.getAverageSpend()
                    + "\nRemaining money: " + money);
        } else {
            debug.write("No shops available for shopping.");
        }
    }

    /**
     * Simulates the person going home.
     */
    public void goHome() {

        Debug debug = new Debug();

        // Check if the person is inactive or has insufficient money
        if (!active || this.getMoney() <= 0) {
            debug.write("Person " + this.getName() + " can't go home - inactive or insufficient money");
            this.setInactive();
            return;
        }

        int distance = calculateManhattanDistance(currentPosition, house.getPosition());
        double finalCost = travelCost * distance;

        // Check if the person has enough money for travel
        if (!this.hasMoney(finalCost)) {
            this.setInactive();
            return;
        }

        money -= finalCost;
        money = Math.floor(money * 100) / 100;
        this.setCurrentPosition(house.getPosition());
        debug.write("Person " + this.getName() + " is going home.\nDistance from current position to house: " + distance
                + "\nTravel cost deducted from money: " + travelCost * distance
                + "\nRemaining money: " + money);
    }

    /**
     * Calculates the Manhattan distance between two positions.
     *
     * @param position1 The position of the first point.
     * @param position2 The position of the second point.
     * @return The Manhattan distance.
     */
    private int calculateManhattanDistance(Position position1, Position position2) {
        return Math.abs(position1.getX() - position2.getX()) + Math.abs(position1.getY() - position2.getY());
    }

    /**
     * Checks if the person has enough money for an upcoming cost.
     *
     * @param upcomingCost The upcoming cost to be deducted.
     * @return True if the person has enough money, false otherwise.
     */
    private boolean hasMoney(double upcomingCost) {
        return (this.getMoney() - upcomingCost) > 0;
    }

    /**
     * Sets the person as inactive and logs the action.
     */
    private void setInactive() {
        Debug debug = new Debug();
        this.setMoney(0);
        this.setActive(false);
        debug.write("Person " + this.getName() + " has been set as inactive");
    }

    /**
     * Finds the closest shop from the list of shops.
     *
     * @param shops The list of shops.
     * @return The closest shop.
     */
    private Shop findClosestShop(ArrayList<Building> shops) {
        Building closestShop = null;
        int minDistance = Integer.MAX_VALUE;

        for (Building shop : shops) {
            int distance = calculateManhattanDistance(this.getCurrentPosition(), shop.getPosition());
            if (distance < minDistance) {
                minDistance = distance;
                closestShop = shop;
            }
        }

        return (Shop) closestShop;
    }

    // Getters and Setters
    public String getName() {
        return this.name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getTravelCost() {
        return travelCost;
    }

    public void setTravelCost(double travelCost) {
        this.travelCost = travelCost;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
