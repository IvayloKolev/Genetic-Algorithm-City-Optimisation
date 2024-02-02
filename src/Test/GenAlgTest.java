package Test;

import GeneticAlgorithm.CrossoverMethod;
import GeneticAlgorithm.GeneticAlgorithm;
import GeneticAlgorithm.SelectionMethod;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class GenAlgTest {

    public static void main(String[] args) {

        int generations = 100;
        int simulationDays = 100;
        SelectionMethod selectionMethod = SelectionMethod.BOLTZMANN;
        double selectionParameter = 2.5;
        CrossoverMethod crossoverMethod = CrossoverMethod.TWO_POINT;
        double mutationChance = 0.05;
        int populationSize = 100;

        int width = 69;
        int height = 69;
        int houses = 350;
        int shops = 250;
        int offices = 50;
        double shopAverageSpend = 40;
        double officeAverageSalary = 1000;
        double variation = 0.1;
        double centerBias = 2.5;
        int startingMoney = 100;
        double travelCost = 5.5;

        GeneticAlgorithm ga = new GeneticAlgorithm();

        ga.runGeneticAlgorithm(generations,
                simulationDays,
                selectionMethod,
                selectionParameter,
                crossoverMethod,
                mutationChance,
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

    }

}
