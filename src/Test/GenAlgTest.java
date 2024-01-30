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

        int generations = 1000;
        int simulationDays = 100;
        SelectionMethod selectionMethod = SelectionMethod.LINEAR_RANKING;
        double selectionPressure = 1;
        CrossoverMethod crossoverMethod = CrossoverMethod.UNIFORM;
        double mutationChance = 0.05;
        int populationSize = 100;

        int width = 69;
        int height = 69;
        int houses = 300;
        int shops = 250;
        int offices = 150;
        double shopAverageSpend = 40;
        double officeAverageSalary = 100;
        double variation = 0.1;
        double centerBias = 2.5;
        int startingMoney = 100;
        double travelCost = 5.5;

        GeneticAlgorithm ga = new GeneticAlgorithm();

        ga.runGeneticAlgorithm(
                generations,
                simulationDays,
                selectionMethod,
                selectionPressure,
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
