package Test;

import City.City;
import GeneticAlgorithm.GeneticAlgorithm;
import java.util.ArrayList;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class GeneTest {

    public static void main(String[] args) {

        GeneticAlgorithm ga = new GeneticAlgorithm();

        int width = 9;
        int height = 9;
        int houses = 2;
        int shops = 2;
        int shopAverageSpend = 30;
        int offices = 2;
        int officeAverageSalary = 100;
        double variation = 0.1;

        int startingMoney = 100;
        double travelCost = 1.5;

        double centerBias = 2.0;

        City city = City.initializeRandomCity(width, height, houses, shops, offices, shopAverageSpend, officeAverageSalary, variation, centerBias);

        city.populate(startingMoney, travelCost);

        System.out.println(city.toStringGridLayout());

        String gene = city.encode();
        System.out.println("Gene: \n" + gene);

        ArrayList<String> parts = GeneticAlgorithm.splitGene(gene);

        System.out.println("\nGene Parts:");
        for (int i = 0; i < parts.size(); i++) {
            System.out.println(parts.get(i));
        }

    }

}
