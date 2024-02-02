package City;

import Debug.Debug;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
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
public class CityVisualisation {

    private ArrayList<Image> buildingImages;
    private static final Debug debug = new Debug();

    /**
     * Constructor for CityVisualization. Initializes the list for building
     * images and loads the building images from the 'img' folder.
     *
     * @throws IOException If there is an error loading building images.
     */
    public CityVisualisation() throws IOException {
        this.buildingImages = new ArrayList<>();
        loadBuildingImages();
    }

    /**
     * Iterate through the gridLayout of the city, get images for the
     * appropriate buildings or road sections, and display the completed city.
     *
     * @param city The City object.
     * @param displayPanel The JPanel to display the city image.
     * @return The modified displayPanel with the final image inside.
     */
    public static JPanel displayCity(City city, JPanel displayPanel) {
        char[][] gridLayout = city.getGridLayout();

        if (gridLayout.length == 0 || gridLayout[0].length == 0) {
            debug.write("Invalid city layout.");
            return displayPanel;
        }

        int rows = gridLayout.length;
        int cols = gridLayout[0].length;

        // Set layout manager to FlowLayout
        displayPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        int panelWidth = displayPanel.getWidth();
        int panelHeight = displayPanel.getHeight();

        int preferredImageWith = 0;
        int preferredImageHeight = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Image buildingImage = getResizedBuildingImage(city, row, col, panelWidth, panelHeight);

                ImageIcon icon = new ImageIcon(buildingImage);
                JLabel label = new JLabel(icon);

                // Calculate the preferred size to maintain the aspect ratio
                preferredImageWith = panelWidth / cols;
                preferredImageHeight = panelHeight / rows;

                // Set the preferred size of the label
                label.setPreferredSize(new Dimension(preferredImageWith, preferredImageHeight));

                displayPanel.add(label);
            }
        }

        // Check if the city is not square and adjust panel size accordingly
        if (rows != cols) {
            int newPanelWidth = preferredImageWith * rows;
            int newPanelHeight = preferredImageHeight * cols;

            displayPanel.setPreferredSize(new Dimension(newPanelWidth, newPanelHeight));
        }

        debug.write("Displayed the completed city in the JPanel.");

        return displayPanel;
    }

    /**
     * Load building images from the 'img' folder.
     */
    private void loadBuildingImages() {
        File imgFolder = new File("src/img");

        File[] imgFiles = imgFolder.listFiles();

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
    }

    /**
     * Load an image from the 'img' folder.
     *
     * @param filename The filename of the image to be loaded.
     * @return The loaded Image object.
     */
    private static Image loadImage(String filename) {
        try {
            File imgFile = new File("src/img/" + filename);
            return ImageIO.read(imgFile).getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            debug.write("Error loading image: " + filename);
            return null;
        }
    }

    /**
     * Get the appropriate resized image for the building symbol or road section
     * based on neighboring roads.
     *
     * @param city The City object.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @param panelWidth The width of the panel.
     * @param panelHeight The height of the panel.
     * @return The resized Image for the building or road section, or null if
     * not found.
     */
    private static Image getResizedBuildingImage(City city, int row, int col, int panelWidth, int panelHeight) {
        Image originalImage = getBuildingImage(city, row, col);

        int imageWidth = originalImage.getWidth(null);
        int imageHeight = originalImage.getHeight(null);

        // Ensure that the target width and height are non-zero
        int targetWidth = Math.max(1, panelWidth / city.getGridLayout()[0].length);
        int targetHeight = Math.max(1, panelHeight / city.getGridLayout().length);

        // Choose the smaller scaling factor to maintain the original aspect ratio
        double widthScale = (double) targetWidth / imageWidth;
        double heightScale = (double) targetHeight / imageHeight;
        double scale = Math.min(widthScale, heightScale);

        // Resize the image
        int scaledWidth = (int) (imageWidth * scale);
        int scaledHeight = (int) (imageHeight * scale);

        return originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
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
    private static Image getBuildingImage(City city, int row, int col) {
        char symbol = city.getGridLayout()[row][col];

        // Check if the cell represents a building
        switch (symbol) {
            case 'H' -> {
                return loadImage("house.png");
            }
            case 'O' -> {
                return loadImage("office.png");
            }
            case 'S' -> {
                return loadImage("shop.png");
            }
            case '+' -> {
                int neighborCount = countRoadNeighbors(city, row, col);

                // Determine the road section based on the number of neighbors
                switch (neighborCount) {
                    case 4 -> {
                        return loadImage("roadCross.png");
                    }
                    case 3 -> {
                        return getRoadTImage(city, row, col);
                    }
                    case 2 -> {
                        // Determine whether to use road corner or straight based on neighbors
                        boolean hasTopNeighbor = row > 0 && city.getGridLayout()[row - 1][col] == '+';
                        boolean hasBottomNeighbor = row < city.getGridLayout().length - 1 && city.getGridLayout()[row + 1][col] == '+';
                        boolean hasLeftNeighbor = col > 0 && city.getGridLayout()[row][col - 1] == '+';
                        boolean hasRightNeighbor = col < city.getGridLayout()[0].length - 1 && city.getGridLayout()[row][col + 1] == '+';

                        if ((hasTopNeighbor && hasBottomNeighbor) || (hasLeftNeighbor && hasRightNeighbor)) {
                            return getRoadStraightImage(city, row, col);
                        } else {
                            return getRoadCornerImage(city, row, col);
                        }
                    }
                    default -> {
                        return null;
                    }
                }
            }

            case ' ' -> {
                // Randomly choose between empty1.png and empty2.png
                if (Math.random() < 0.9) {
                    return loadImage("empty1.png");
                } else {
                    return loadImage("empty2.png");
                }
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Count the number of road neighbors for a given cell.
     *
     * @param city The City object.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @return The number of road neighbors.
     */
    private static int countRoadNeighbors(City city, int row, int col) {
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
     * Get the image for the road straight section based on neighboring roads.
     *
     * @param city The City object.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @return The Image for the road straight section, or null if not found.
     */
    private static Image getRoadStraightImage(City city, int row, int col) {
        char[][] gridLayout = city.getGridLayout();

        boolean hasLeftNeighbor = col > 0 && gridLayout[row][col - 1] == '+';
        boolean hasRightNeighbor = col < gridLayout[0].length - 1 && gridLayout[row][col + 1] == '+';

        // Check if neighbors are roads and are on the left or right
        if (hasLeftNeighbor || hasRightNeighbor) {
            // Rotate 90 degrees for left-right orientation
            return rotateImage(loadImage("roadStraight.png"), 90);
        } else {
            // If no neighbors on the left or right, return the original image
            return loadImage("roadStraight.png");
        }
    }

    /**
     * Get the image for the road corner section based on neighboring roads.
     *
     * @param city The City object.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @return The Image for the road corner section, or null if not found.
     */
    private static Image getRoadCornerImage(City city, int row, int col) {
        char[][] gridLayout = city.getGridLayout();

        boolean hasTopNeighbor = row > 0 && gridLayout[row - 1][col] == '+';
        boolean hasBottomNeighbor = row < gridLayout.length - 1 && gridLayout[row + 1][col] == '+';
        boolean hasLeftNeighbor = col > 0 && gridLayout[row][col - 1] == '+';
        boolean hasRightNeighbor = col < gridLayout[0].length - 1 && gridLayout[row][col + 1] == '+';

        // Check the orientation of the road corner section
        if (hasBottomNeighbor && hasRightNeighbor) {
            return loadImage("roadCorner.png");
        } else if (hasRightNeighbor && hasTopNeighbor) {
            return rotateImage(loadImage("roadCorner.png"), -90); // Rotate 90 degrees counter-clockwise
        } else if (hasTopNeighbor && hasLeftNeighbor) {
            return rotateImage(loadImage("roadCorner.png"), 180); // Rotate 180 degrees
        } else if (hasLeftNeighbor && hasBottomNeighbor) {
            return rotateImage(loadImage("roadCorner.png"), 90); // Rotate 90 degrees clockwise
        } else {
            return null;
        }
    }

    /**
     * Get the image for the road T section based on neighboring roads.
     *
     * @param city The City object.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @return The Image for the road T section, or null if not found.
     */
    private static Image getRoadTImage(City city, int row, int col) {
        char[][] gridLayout = city.getGridLayout();

        boolean hasTopNeighbor = row > 0 && gridLayout[row - 1][col] == '+';
        boolean hasBottomNeighbor = row < gridLayout.length - 1 && gridLayout[row + 1][col] == '+';
        boolean hasLeftNeighbor = col > 0 && gridLayout[row][col - 1] == '+';
        boolean hasRightNeighbor = col < gridLayout[0].length - 1 && gridLayout[row][col + 1] == '+';

        // Check the orientation of the road T section
        if (!hasTopNeighbor) {
            return loadImage("roadT.png");
        } else if (!hasLeftNeighbor) {
            return rotateImage(loadImage("roadT.png"), -90); // Rotate 90 degrees counter-clockwise
        } else if (!hasBottomNeighbor) {
            return rotateImage(loadImage("roadT.png"), 180); // Rotate 180 degrees
        } else if (!hasRightNeighbor) {
            return rotateImage(loadImage("roadT.png"), 90); // Rotate 90 degrees clockwise
        } else {
            return null;
        }
    }

    /**
     * Rotate the given image by the specified angle.
     *
     * @param image The Image to be rotated.
     * @param angle The rotation angle in degrees.
     * @return The rotated Image.
     */
    private static Image rotateImage(Image image, int angle) {
        // Convert Image to BufferedImage for rotation
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // Rotate the BufferedImage
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(angle), bufferedImage.getWidth() / 2.0, bufferedImage.getHeight() / 2.0);

        BufferedImage rotatedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        Graphics2D g = rotatedImage.createGraphics();
        g.setTransform(transform);
        g.drawImage(bufferedImage, 0, 0, null);
        g.dispose();

        return rotatedImage;
    }
}
