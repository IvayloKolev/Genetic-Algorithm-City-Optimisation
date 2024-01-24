package Test;

import City.City;
import GeneticAlgorithm.GeneticAlgorithm;
import GeneticAlgorithm.SelectionMethod;
import java.util.ArrayList;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class SelectParentsTest {

    public static void main(String[] args) {

        int populationSize = 100;
        int width = 35;
        int height = 35;
        int houses = 60;
        int shops = 20;
        double shopAverageSpend = 50;
        int offices = 15;
        double officeAverageSalary = 100;
        double variation = 0.1;

        int startingMoney = 100;
        double travelCost = 3.0;

        double centerBias = 3.0;

        GeneticAlgorithm ga = new GeneticAlgorithm();

        ArrayList<City> initialPopulation = ga.initialPopulation(
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

        // Evaluate fitness
        ArrayList<City> sortedPopulation = ga.evaluateFitness(initialPopulation);

        for (int i = 0; i < sortedPopulation.size(); i++) {
            System.out.println("\nCity " + (i + 1) + "\n");
            System.out.println("Fitness: " + sortedPopulation.get(i).getFitness());
            System.out.println("Inactive People: " + sortedPopulation.get(i).countInactivePeople());
        }

        ArrayList<City> parents = ga.selectParents(sortedPopulation, SelectionMethod.TOURNAMENT, 10);

        for (int i = 0; i < parents.size(); i++) {
            System.out.println("\nSelected Parent City " + (i + 1) + "\n");
            System.out.println("Fitness: " + parents.get(i).getFitness());
            System.out.println("Inactive People: " + parents.get(i).countInactivePeople());
        }
    }

}
