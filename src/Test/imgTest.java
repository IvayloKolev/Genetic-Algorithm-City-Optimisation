package Test;

import City.City;
import City.CityVisualization;
import java.io.IOException;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class imgTest {

    public static void main(String[] args) throws IOException {

        CityVisualization cv = new CityVisualization();

        int width = 11;
        int height = 11;
        int houses = 5;
        int shops = 5;
        double shopAverageSpend = 30;
        int offices = 5;
        double officeAverageSalary = 100;
        double variation = 0.1;

        int startingMoney = 100;
        double travelCost = 1.5;

        double centerBias = 3.0;

        City city = City.initializeRandomCity(
                width,
                height,
                houses,
                shops,
                offices,
                shopAverageSpend,
                officeAverageSalary,
                variation,
                centerBias);

        city.populate(startingMoney, travelCost);

        System.out.println(city.toStringGridLayout());
        
        cv.displayCity(city);

    }

}
