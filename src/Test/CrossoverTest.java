package Test;

import City.City;
import City.Gene;
import GeneticAlgorithm.GeneticAlgorithm;
import java.util.ArrayList;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class CrossoverTest {

    public static void main(String[] args) {

        GeneticAlgorithm ga = new GeneticAlgorithm();

        int width = 25;
        int height = 25;
        int houses = 50;
        int shops = 30;
        int shopAverageSpend = 30;
        int offices = 30;
        int officeAverageSalary = 100;
        double variation = 0.1;

        int startingMoney = 100;
        double travelCost = 1.5;

        double centerBias = 2.0;

        City city1 = City.initializeRandomCity(width, height, houses, shops, offices, shopAverageSpend, officeAverageSalary, variation, centerBias);
        City city2 = City.initializeRandomCity(width, height, houses, shops, offices, shopAverageSpend, officeAverageSalary, variation, centerBias);

        city1.populate(startingMoney, travelCost);
        city2.populate(startingMoney, travelCost);

        Gene gene1 = Gene.encode(city1);
        Gene gene2 = Gene.encode(city2);

        System.out.println("Parent 1: \n" + city1.toStringGridLayout());
        System.out.println("\nParent 2: \n" + city2.toStringGridLayout());

        ArrayList<Gene> offspringGenes = GeneticAlgorithm.onePointCrossover(gene1, gene2);

        Gene offspring1 = offspringGenes.get(0);
        Gene offspring2 = offspringGenes.get(1);

        City decodedOffspring1 = Gene.decode(offspring1);
        City decodedOffspring2 = Gene.decode(offspring2);

        System.out.println("Offspring Gene1: \n" + decodedOffspring1.toStringGridLayout());
        System.out.println("\nOffspring Gene2: \n" + decodedOffspring2.toStringGridLayout());
    }
}
