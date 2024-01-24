package Test;

import City.City;
import GeneticAlgorithm.GeneticAlgorithm;
import java.util.ArrayList;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class SelectionTestTournament {

    public static void main(String[] args) {

        int populationSize = 20;
        int width = 35;
        int height = 35;
        int houses = 60;
        int shops = 20;
        double shopAverageSpend = 70;
        int offices = 15;
        double officeAverageSalary = 100;
        double variation = 0.1;

        int startingMoney = 100;
        double travelCost = 4.0;

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
        ArrayList<City> sortedPopulation = GeneticAlgorithm.evaluateFitness(initialPopulation);

        for (int i = 0; i < sortedPopulation.size(); i++) {
            System.out.println("\nCity " + (i + 1) + "\n");
            System.out.println("Fitness: " + sortedPopulation.get(i).getFitness());
            System.out.println("Inactive People: " + sortedPopulation.get(i).countInactivePeople());
        }

        City selectedCity = GeneticAlgorithm.tournamentSelection(sortedPopulation, 10);

        // Display the selected city
        System.out.println("\nTournament Selection Test:");
        System.out.println("Selected City: " + selectedCity);
        System.out.println("City Fitness: " + selectedCity.getFitness());
    }

}
