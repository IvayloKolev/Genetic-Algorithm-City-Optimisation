package Test;

import City.City;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class GeneTest {

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

    }

}
