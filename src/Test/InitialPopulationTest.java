package Test;

import City.City;
import GeneticAlgorithm.GeneticAlgorithm;
import java.util.ArrayList;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class InitialPopulationTest {

    public static void main(String[] args) {

        int populationSize = 10;
        int width = 15;
        int height = 15;
        int houses = 20;
        int shops = 10;
        double shopAverageSpend = 30;
        int offices = 5;
        double officeAverageSalary = 100;
        double variation = 0.1;

        int startingMoney = 100;
        double travelCost = 1.5;

        double centerBias = 3.0;

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

        ArrayList<City> initialPopulation = geneticAlgorithm.initialPopulation(
                populationSize,
                width,
                height,
                houses,
                shops,
                offices,
                shopAverageSpend,
                officeAverageSalary,
                variation,
                centerBias,
                startingMoney,
                travelCost);

        for (City city : initialPopulation) {
            city.simulate();
        }

        GeneticAlgorithm.evaluateFitness(initialPopulation);

        for (int i = 0; i < initialPopulation.size(); i++) {
            System.out.println("\nCity " + (i + 1) + "\n");
            System.out.println("Fitness: " + initialPopulation.get(i).getFitness());
            System.out.println("Inactive People: " + initialPopulation.get(i).countInactivePeople());
            System.out.println(initialPopulation.get(i).toStringGridLayout());
        }
    }

}
