package City;

import Debug.Debug;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Class responsible for visualizing the city. Loads images for different
 * building types. Displays the city layout.
 *
 * @author Ivaylo Kolev 2005549
 */
public class CityVisualization {

    private ArrayList<Image> buildingImages;
    private Debug debug = new Debug();

    public CityVisualization() throws IOException {
        this.buildingImages = new ArrayList<>();
        loadBuildingImages();
    }

    /**
     * Load building images from the 'img' folder.
     */
    private void loadBuildingImages() {
        File imgFolder = new File("src/img");

        if (imgFolder.exists() && imgFolder.isDirectory()) {
            File[] imgFiles = imgFolder.listFiles();

            if (imgFiles != null) {
                for (File file : imgFiles) {
                    if (file.isFile() && file.getName().endsWith(".png")) {
                        try {
                            Image image = ImageIO.read(file);
                            buildingImages.add(image);
                            debug.write("Loaded image: " + file.getName());
                        } catch (IOException e) {
                            debug.write("Error loading image: " + file.getName());
                        }
                    }
                }
            } else {
                debug.write("No image files found in the 'img' folder.");
            }
        } else {
            debug.write("The 'img' folder does not exist or is not a directory.");
        }
    }

    /**
     * Load an image from the 'img' folder.
     *
     * @param filename The filename of the image to be loaded.
     * @return The loaded Image object.
     */
    private Image loadImage(String filename) {
        try {
            File imgFile = new File("src/img/" + filename);
            return ImageIO.read(imgFile).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            debug.write("Error loading image: " + filename);
            return null;
        }
    }

    /**
     * Display building images in a JFrame.
     */
    public void displayBuildingImages() {
        if (buildingImages.isEmpty()) {
            debug.write("No building images to display.");
            return;
        }

        JFrame frame = new JFrame("City Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();

        for (Image image : buildingImages) {
            ImageIcon icon = new ImageIcon(image);
            JLabel label = new JLabel(icon);
            panel.add(label);
        }

        frame.add(panel);
        frame.setVisible(true);
        debug.write("Displayed building images in the JFrame.");
    }

    /**
     * Get the appropriate image for the building symbol or road section based
     * on neighboring roads.
     *
     * @param city The City object.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @return The Image for the building or road section, or null if not found.
     */
    private Image getBuildingImage(City city, int row, int col) {
        char symbol = city.getGridLayout()[row][col];

        // Check if the cell represents a building
        switch (symbol) {
            case 'H':
                return loadImage("house.png");
            case 'O':
                return loadImage("office.png");
            case 'S':
                return loadImage("shop.png");
            case '+':
                if (symbol == '+') {
                    int neighborCount = countRoadNeighbors(city, row, col);

                    // Determine the road section based on the number of neighbors
                    return switch (neighborCount) {
                        case 4 -> {
                            loadImage("roadCross.png");
                            break;
                        }
                        case 3 ->
                            getRoadTImage(city, row, col);
                        case 2 ->
                            getRoadCornerOrStraightImage(city, row, col);
                        default ->
                            null;
                    };
                }
            case ' ':
                // Randomly choose between empty1.png and empty2.png
                if (Math.random() < 0.5) {
                    return loadImage("empty1.png");
                } else {
                    return loadImage("empty2.png");
                }
            default:
                return null;
        }
        return null;
    }

    /**
     * Count the number of road neighbors for a given cell.
     *
     * @param city The City object.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @return The number of road neighbors.
     */
    private int countRoadNeighbors(City city, int row, int col) {
        int count = 0;
        char[][] gridLayout = city.getGridLayout();

        if (row > 0 && gridLayout[row - 1][col] == '+') {
            count++; // Neighbor to the top
        }
        if (row < gridLayout.length - 1 && gridLayout[row + 1][col] == '+') {
            count++; // Neighbor to the bottom
        }
        if (col > 0 && gridLayout[row][col - 1] == '+') {
            count++; // Neighbor to the left
        }
        if (col < gridLayout[0].length - 1 && gridLayout[row][col + 1] == '+') {
            count++; // Neighbor to the right
        }

        return count;
    }

    /**
     * Get the image for the roadT section based on neighboring roads.
     *
     * @param city The City object.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @return The Image for the roadT section, or null if not found.
     */
    private Image getRoadTImage(City city, int row, int col) {
        char[][] gridLayout = city.getGridLayout();

        boolean hasTopNeighbor = row > 0 && gridLayout[row - 1][col] == '+';
        boolean hasBottomNeighbor = row < gridLayout.length - 1 && gridLayout[row + 1][col] == '+';
        boolean hasLeftNeighbor = col > 0 && gridLayout[row][col - 1] == '+';
        boolean hasRightNeighbor = col < gridLayout[0].length - 1 && gridLayout[row][col + 1] == '+';

        // Check the orientation of the roadT section
        if (hasTopNeighbor && hasBottomNeighbor && hasLeftNeighbor) {
            return loadImage("roadT.png");
        } else if (hasTopNeighbor && hasBottomNeighbor && hasRightNeighbor) {
            return loadImage("roadT.png");
        } else if (hasTopNeighbor && hasLeftNeighbor && hasRightNeighbor) {
            return loadImage("roadT.png");
        } else if (hasBottomNeighbor && hasLeftNeighbor && hasRightNeighbor) {
            return loadImage("roadT.png");
        } else {
            return null;
        }
    }

    /**
     * Get the image for the road corner or straight section based on
     * neighboring roads.
     *
     * @param city The City object.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @return The Image for the road corner or straight section, or null if not
     * found.
     */
    private Image getRoadCornerOrStraightImage(City city, int row, int col) {
        char[][] gridLayout = city.getGridLayout();

        boolean hasTopNeighbor = row > 0 && gridLayout[row - 1][col] == '+';
        boolean hasBottomNeighbor = row < gridLayout.length - 1 && gridLayout[row + 1][col] == '+';
        boolean hasLeftNeighbor = col > 0 && gridLayout[row][col - 1] == '+';
        boolean hasRightNeighbor = col < gridLayout[0].length - 1 && gridLayout[row][col + 1] == '+';

        // Check if neighbors are on the same axis
        if ((hasTopNeighbor && hasBottomNeighbor) || (hasLeftNeighbor && hasRightNeighbor)) {
            return loadImage("roadStraight.png");
        } else {
            return loadImage("roadCorner.png");
        }
    }

    /**
     * Iterate through the gridLayout of the city, get images for the
     * appropriate buildings or road sections, and display the completed city.
     *
     * @param city The City object.
     */
    public void displayCity(City city) {
        char[][] gridLayout = city.getGridLayout();

        if (gridLayout.length == 0 || gridLayout[0].length == 0) {
            debug.write("Invalid city layout.");
            return;
        }

        JFrame frame = new JFrame("City Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(gridLayout.length, gridLayout[0].length));

        for (int row = 0; row < gridLayout.length; row++) {
            for (int col = 0; col < gridLayout[0].length; col++) {
                Image buildingImage = getBuildingImage(city, row, col);

                if (buildingImage != null) {
                    ImageIcon icon = new ImageIcon(buildingImage);
                    JLabel label = new JLabel(icon);
                    panel.add(label);
                }
            }
        }

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        debug.write("Displayed the completed city in the JFrame.");
    }

}
