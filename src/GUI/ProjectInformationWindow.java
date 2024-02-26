package GUI;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class ProjectInformationWindow extends JDialog {

    private static final long serialVersionUID = 1L;

    public ProjectInformationWindow() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Project Information");

        JTextArea infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        infoTextArea.setLineWrap(true);
        infoTextArea.setWrapStyleWord(true);
        infoTextArea.setText(getProjectInformation());

        // Add padding to the text area
        int padding = 10;
        infoTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        ));

        JScrollPane scrollPane = new JScrollPane(infoTextArea);
        getContentPane().add(scrollPane);

        infoTextArea.setCaretPosition(0);

        pack();
        setSize(900, 700);
        setLocationRelativeTo(null); // Center the window on the screen
    }

    private String getProjectInformation() {
        return "Project Name: Grid-Based City Simulation and Optimization with Genetic Algorithms\n"
                + "Author: Ivaylo Kolev\n"
                + "Student ID: 2005549\n\n"
                + "Description:\n"
                + "The goal of this program is to create and optimize cities using a Genetic Algorithm. To set up a city, you need to configure various parameters that the algorithm utilizes. These parameters include:\n\n"
                + "Generations - Integer - The number of generations the genetic algorithm will run before stopping.\n"
                + "Simulation Days - Integer - The duration each city will be simulated. A day comprises each person going to work, visiting a shop, and returning home.\n\n"
                + "Selection Method - Select - The method for choosing parent cities for the next generation. Some methods require an additional Selection Parameter:\n"
                + "- Fitness Proportional - No parameter - Selects parents based on their fitness relative to the total population fitness.\n"
                + "- Linear Ranking - Floating Point - Assigns a linear rank to each city and selects parents based on their rank, with the parameter influencing selection pressure.\n"
                + "- Tournament - Integer - Randomly selects a subset of cities, choosing the one with the highest fitness as a parent. The parameter determines the tournament size.\n"
                + "- Boltzmann - Floating Point - Uses a Boltzmann distribution to probabilistically select parents based on fitness. The parameter influences the distribution temperature.\n\n"
                + "Crossover Method - Select - The method for mixing genes to produce offspring:\n"
                + "- One Point - Randomly selects one split point along the genes of both parents, swapping resulting halves to produce offspring.\n"
                + "- Two Point - Randomly selects two split points along the genes, mixing resulting subgenes to produce offspring.\n"
                + "- Uniform - Iterates through both genes simultaneously, with a 50% chance for buildings to be swapped, producing new offspring.\n\n"
                + "Mutation Chance - Floating Point - The chance for a gene to mutate randomly. Mutations include changing buildings, moving buildings, removing buildings, changing shop prices, altering office salaries, changing people's starting money, and adjusting travel costs. Best kept low, up to 0.05 (5%), for more consistent results.\n\n"
                + "Population Size - The number of cities in each generation.\n"
                + "City Width - Integer - The width of each city.\n"
                + "City Height - Integer - The height of each city.\n"
                + "Number of Houses - Integer - The number of houses in each city's first generation, with one person per house.\n"
                + "Number of Shops - Integer - The number of shops in each city's first generation.\n"
                + "Number of Offices - Integer - The number of offices in each city's first generation.\n"
                + "Shop Average Spend - Floating Point - The average amount a person spends in a shop.\n"
                + "Office Salary - Floating Point - The average amount a person gains after working.\n"
                + "Variation - Floating Point - The range for spending in shops and salaries in offices. Example: Office Salary = 100, Variation = 10 => Salary = [90~110].\n\n"
                + "Building Center Bias - Floating Point - A parameter for the Gaussian Distribution used in city generation. Higher values cause buildings to clump in the center. If too high (over 4.5), cities may become tightly packed in the center with few buildings around the corners.\n"
                + "Person Starting Money - Floating Point - The amount of money each person has at the start of the simulation.\n"
                + "Travel Cost - Floating Point - The money required to travel one tile in the city.\n";
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new ProjectInformationWindow().setVisible(true);
        });
    }
}
