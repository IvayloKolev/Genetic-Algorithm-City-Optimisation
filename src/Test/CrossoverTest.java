package Test;

import City.City;
import GeneticAlgorithm.GeneticAlgorithm;
import java.util.ArrayList;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class CrossoverTest {

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

        City city1 = City.initializeRandomCity(width, height, houses, shops, offices, shopAverageSpend, officeAverageSalary, variation, centerBias);
        City city2 = City.initializeRandomCity(width, height, houses, shops, offices, shopAverageSpend, officeAverageSalary, variation, centerBias);

        city1.populate(startingMoney, travelCost);
        city2.populate(startingMoney, travelCost);

        System.out.println(city1.toStringGridLayout());

        String gene1 = city1.encode();
        String gene2 = city2.encode();
        System.out.println("Gene1 : \n" + gene1);
        System.out.println("Gene2 : \n" + gene2);

        ArrayList<String> parts = GeneticAlgorithm.splitGene(gene1);

        System.out.println("\nGene Parts:");
        for (int i = 0; i < parts.size(); i++) {
            System.out.println(parts.get(i));
        }

        ArrayList<String> newGenes = ga.singlePointCrossover(gene1, gene2);
        System.out.println("New Gene1 : \n" + newGenes.get(0));
        System.out.println("");
        System.out.println("New Gene2 : \n" + newGenes.get(1));

    }

}
