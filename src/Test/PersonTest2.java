package Test;

import City.City;
import Person.Person;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class PersonTest2 {

    public static void main(String[] args) {

        int width = 69;
        int height = 69;
        int houses = 100;
        int shops = 1;
        int shopAverageSpend = 30;
        int offices = 1;
        int officeAverageSalary = 100;
        double variation = 0.1;

        int startingMoney = 100;
        double travelCost = 1.5;

        double centerBias = 3.0;

        City city = City.initializeRandomCity(width, height, houses, shops, offices, shopAverageSpend, officeAverageSalary, variation, centerBias);

        city.populate(startingMoney, travelCost);

        // Display the initial state of the city
        System.out.println("Initial City State:");
        System.out.println(city.toStringGridLayout());
        // Display the total money owned by all people at the start
        double totalMoneyStart = city.getTotalMoney();

        city.simulate();

        // Display the final state of the city after all activities
        System.out.println("Final City State:");
        System.out.println(city.toStringGridLayout());

        // Display the money of each person at the end
        System.out.println("Money of each person at the end:");
        for (Person person : city.getPeople()) {
            System.out.println("Person Money: " + person.getMoney());
        }

        // Display the total money owned by all people at the end
        double totalMoneyEnd = city.getTotalMoney();

        System.out.println("Total Money at the Start: " + totalMoneyStart);
        System.out.println("Total Money at the End: " + totalMoneyEnd);

        int peopleCount = city.getPeople().size();
        System.out.println("Number of people: " + peopleCount);
        // Display the count of inactive people
        int inactiveCount = city.countInactivePeople();
        System.out.println("Inactive People Count: " + inactiveCount);

    }

}
