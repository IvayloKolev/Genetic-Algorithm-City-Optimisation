package Test;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
import Building.Building;
import Building.House;
import Building.Office;
import City.City;
import Person.Person;
import java.util.ArrayList;

import java.util.Random;

public class PersonTest {

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

        // Create an ArrayList to store people
        ArrayList<Person> peopleList = new ArrayList<>();

        // Assign each person to a separate house and one office
        for (int i = 0; i < city.getHouses().size(); i++) {
            House house = (House) city.getHouses().get(i);
            Office office = findAvailableOffice(city, i); // Use index to get different offices

            // Create a person and assign them to a house and one office
            Person person = new Person(startingMoney, travelCost, house, office, city);

            // Add the person to the list and the city
            peopleList.add(person);
            city.getPeople().add(person);
        }

        // Display the initial state of the city
        System.out.println("Initial City State:");
        System.out.println(city.toStringGridLayout());

        // Display the total money owned by all people at the start
        double totalMoneyStart = city.getTotalMoney();

        // Simulate activities for each person (going to work, shopping, going home)
        for (Person person : peopleList) {
            person.goToWork();
            person.goShopping();
            person.goHome();
        }

        // Display the final state of the city after all activities
        System.out.println("Final City State:");
        System.out.println(city.toStringGridLayout());

        // Display the money of each person at the end
        System.out.println("Money of each person at the end:");
        for (Person person : peopleList) {
            System.out.println("Person " + person.getName() + " Money: " + person.getMoney());
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

    // Helper method to find an available office for a person
    private static Office findAvailableOffice(City city, int index) {
        ArrayList<Building> offices = city.getOffices();

        if (!offices.isEmpty()) {
            if (index < offices.size()) {
                return (Office) offices.get(index);
            } else {
                // If there are more people than offices, assign to a random office
                Random random = new Random();
                int randomIndex = random.nextInt(offices.size());
                return (Office) offices.get(randomIndex);
            }
        } else {
            // If there are no offices, return null or handle accordingly based on your logic
            return null;
        }
    }
}
