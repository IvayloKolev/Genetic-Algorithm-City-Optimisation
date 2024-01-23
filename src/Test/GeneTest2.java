package Test;

import City.City;
import static City.City.decode;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class GeneTest2 {

    public static void main(String[] args) {

        int width = 35;
        int height = 35;
        int houses = 60;
        int shops = 40;
        int shopAverageSpend = 30;
        int offices = 40;
        int officeAverageSalary = 100;
        double variation = 0.1;

        int startingMoney = 100;
        double travelCost = 1.5;

        double centerBias = 3.0;

        City city = City.initializeRandomCity(width, height, houses, shops, offices, shopAverageSpend, officeAverageSalary, variation, centerBias);

        city.populate(startingMoney, travelCost);

        System.out.println(city.toStringGridLayout());

        String gene = city.encode();
        System.out.println("Gene: \n" + gene);

        City decodedCity = new City(decode(gene));
        System.out.println(decodedCity.toStringGridLayout());

    }

}
