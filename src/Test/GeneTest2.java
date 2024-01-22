package Test;

import city.optimisaiton.genetic.algorithm.City.City;
import static city.optimisaiton.genetic.algorithm.City.City.decode;
import city.optimisaiton.genetic.algorithm.City.Person;
import java.util.ArrayList;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class GeneTest2 {

    public static void main(String[] args) {

        int width = 15;
        int height = 15;
        int houses = 10;
        int shops = 3;
        int shopAverageSpend = 30;
        int offices = 3;
        int officeAverageSalary = 100;
        double variation = 0.1;

        int startingMoney = 100;
        double travelCost = 1.5;

        City city = City.initializeRandomCity(width, height, houses, shops, offices, shopAverageSpend, officeAverageSalary, variation);

        city.populate(startingMoney, travelCost);

        System.out.println(city.toStringGridLayout());

        String gene = city.encode();
        System.out.println("Gene: \n" + gene);

        City decodedCity = new City(decode(gene));
        
        System.out.println("Decoded City");
        System.out.println(decodedCity.toStringGridLayout());
        System.out.println("\n Builsings List");
        System.out.println(decodedCity.getBuildingsList());
        System.out.println("\n People List");
        ArrayList<Person> peopleList = decodedCity.getPeople();
        System.out.println(peopleList.get(0).getName());

    }

}
