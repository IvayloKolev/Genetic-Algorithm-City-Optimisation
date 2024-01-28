package Test;

import City.City;
import static City.City.decode;
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

        String gene1 = city1.encode();
        String gene2 = city2.encode();

        System.out.println(city1.toStringGridLayout());

        ArrayList<String> parts1 = GeneticAlgorithm.splitGene(gene1);

        System.out.println("\nGene Parts:");
        for (int i = 0; i < parts1.size(); i++) {
            System.out.println(parts1.get(i));
        }

        System.out.println(city2.toStringGridLayout());

        ArrayList<String> parts2 = GeneticAlgorithm.splitGene(gene2);

        System.out.println("\nGene Parts:");
        for (int i = 0; i < parts2.size(); i++) {
            System.out.println(parts2.get(i));
        }

        ArrayList<String> newGenes = ga.singlePointCrossover(gene1, gene2);
        System.out.println("New Gene1 : \n" + newGenes.get(0));
        System.out.println("");
        System.out.println("New Gene2 : \n" + newGenes.get(1));

        City decodedCity1 = new City(decode(newGenes.get(0)));
        System.out.println(decodedCity1.toStringGridLayout());

        ArrayList<String> parts3 = GeneticAlgorithm.splitGene(decodedCity1.getGene());
        System.out.println("\nGene Parts:");
        for (int i = 0; i < parts3.size(); i++) {
            System.out.println(parts3.get(i));
        }
        City decodedCity2 = new City(decode(newGenes.get(1)));
        System.out.println(decodedCity2.toStringGridLayout());

    }

}
