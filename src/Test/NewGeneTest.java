package Test;

import City.City;
import City.Gene;
import GeneticAlgorithm.GeneticAlgorithm;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class NewGeneTest {
    
    public static void main(String[] args) {
        
        GeneticAlgorithm ga = new GeneticAlgorithm();
        
        int width = 69;
        int height = 69;
        int houses = 300;
        int shops = 250;
        int shopAverageSpend = 30;
        int offices = 150;
        int officeAverageSalary = 1000;
        double variation = 0.1;
        
        int startingMoney = 100;
        double travelCost = 1.5;
        
        double centerBias = 2.0;
        
        City city = City.initializeRandomCity(width, height, houses, shops, offices, shopAverageSpend, officeAverageSalary, variation, centerBias);
        
        city.populate(startingMoney, travelCost);
        
        Gene gene = Gene.encode(city);
        city.simulate();
        ga.evaluateSingleCityFitness(city);
        
        City decodedCity = Gene.decode(gene);
        
        System.out.println("New Gene Test");
        System.out.println("Old City: \n");
        System.out.println(city.toStringGridLayout());
        System.out.println("\nNew City: \n");
        System.out.println(decodedCity.toStringGridLayout());
        System.out.println(decodedCity.toString());
        
    }
    
}
