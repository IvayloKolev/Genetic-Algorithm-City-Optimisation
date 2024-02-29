package City;

import Debug.Debug;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
     * appropriate buildings or road sections, and display the stitched city
     * image.
     *
     * @param city The City object.
     * @param displayPanel The JPanel to display the city image.
     * @return The modified displayPanel with the stitched city image inside.
     * @throws java.io.IOException
     */
    public static JPanel displayCity(City city, JPanel displayPanel) throws IOException {
        Image stitchedImage = new CityVisualisation().createStitchedImage(city, displayPanel);

        if (stitchedImage == null) {
            debug.write("Failed to create stitched image.");
            return displayPanel;
        }

        ImageIcon icon = new ImageIcon(stitchedImage);
        JLabel label = new JLabel(icon);

        displayPanel.removeAll();  // Clear existing components
        displayPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        displayPanel.add(label);   // Add the label with the stitched image

        debug.write("Displayed the stitched city image in the JPanel.");

        return displayPanel;
    }

    /**
     * Load building images from the 'img' folder.
     */
    private void loadBuildingImages() {
        File imgFolder = new File("src/img");

        if (!imgFolder.exists() || !imgFolder.isDirectory()) {
            debug.write("Error: 'img' folder not found.");
            return;
        }

        File[] imgFiles = imgFolder.listFiles();

        if (imgFiles == null) {
            debug.write("Error: No files found in 'img' folder.");
            return;
        }

        for (File file : imgFiles) {
            if (file.isFile() && file.getName().endsWith(".png")) {
                try {
                    Image image = ImageIO.read(file);
                    buildingImages.add(image);
                    debug.write("Loaded image: " + file.getName());
                } catch (IOException e) {
                    debug.write("Error loading image: " + file.getName() + " - " + e.getMessage());
                }
            }
        }
    }

    /**
     * Get the appropriately resized image for building or road.
     *
     * @param city The City object.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @param targetWidth The desired width of the resized image.
     * @param targetHeight The desired height of the resized image.
     * @return The resized Image for the building or road section, or null if
     * not found.
     */
    private static Image getResizedBuildingImage(City city, int row, int col, int targetWidth, int targetHeight) {
        Image originalImage = getBuildingImage(city, row, col);

        if (originalImage == null) {
            return null;
        }

        int imageWidth = originalImage.getWidth(null);
        int imageHeight = originalImage.getHeight(null);

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
     * Load an image from the 'img' folder.
     *
     * @param filename The filename of the image to be loaded.
     * @return The loaded Image object.
     */
    private static Image loadImage(String filename) {
        try {
            ClassLoader classLoader = CityVisualisation.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("img/" + filename);

            if (inputStream != null) {
                return ImageIO.read(inputStream);
            } else {
                debug.write("Error loading image: " + filename);
                return null;
            }
        } catch (IOException e) {
            debug.write("Error loading image: " + filename);
            return null;
        }
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

    /**
     * Create a stitched image of the city.
     *
     * @param city The City object.
     * @param displayPanel The JPanel to display the city image.
     * @return The stitched image of the city.
     */
    public Image createStitchedImage(City city, JPanel displayPanel) {
        char[][] gridLayout = city.getGridLayout();
        int rows = gridLayout.length;
        int cols = gridLayout[0].length;

        int panelWidth = displayPanel.getWidth();
        int panelHeight = displayPanel.getHeight();

        // Check if the panel dimensions are valid
        if (panelWidth <= 0 || panelHeight <= 0) {
            debug.write("Invalid panel dimensions.");
            return null;
        }

        // Calculate the preferred image size based on the minimum of panel width and height
        int preferredImageSize = Math.min(panelWidth / cols, panelHeight / rows);

        // Create a BufferedImage for the stitched image
        BufferedImage stitchedImage = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = stitchedImage.createGraphics();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Image buildingImage = getResizedBuildingImage(city, row, col, preferredImageSize, preferredImageSize);

                if (buildingImage != null) {
                    g2d.drawImage(buildingImage, col * preferredImageSize, row * preferredImageSize, null);
                }
            }
        }

        g2d.dispose();

        return stitchedImage;
    }

    /**
     * Create a stitched image of the city.
     *
     * @param city The City object.
     * @return The stitched image of the city.
     */
    public Image createStitchedImage(City city) {
        char[][] gridLayout = city.getGridLayout();
        int rows = gridLayout.length;
        int cols = gridLayout[0].length;

        // Calculate the dimensions of the stitched image
        int preferredImageWidth = 80;
        int preferredImageHeight = 80;

        int totalWidth = preferredImageWidth * cols;
        int totalHeight = preferredImageHeight * rows;

        // Create a BufferedImage for the stitched image
        BufferedImage stitchedImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = stitchedImage.createGraphics();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Image buildingImage = getResizedBuildingImage(city, row, col, preferredImageWidth, preferredImageHeight);

                if (buildingImage != null) {
                    g2d.drawImage(buildingImage, col * preferredImageWidth, row * preferredImageHeight, null);
                }
            }
        }

        g2d.dispose();

        return stitchedImage;
    }

    /**
     * Exports the stitched image of a City to a specified file.
     *
     * This method creates a BufferedImage for the stitched image of the given
     * City and saves it to the specified directory with the provided image name
     * and type.
     *
     * @param city The City object to generate the stitched image from.
     * @param imageName The name to be given to the exported image file.
     * @param imageType The type of the image file (e.g., "png", "jpg").
     * @throws IllegalArgumentException If the provided image type is not
     * supported.
     */
    public void exportStitchedImage(City city, String imageName, String imageType) {
        // Create a BufferedImage for the stitched image
        BufferedImage stitchedImage = (BufferedImage) createStitchedImage(city);

        // Save the stitched image to the specified directory
        String path = "src/img/ExportedImages";
        File directory = new File(path);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                System.out.println("Error creating directory: " + path);
                return;
            }
        }

        // Construct the file path
        String filePath = path + File.separator + imageName;

        try {
            File outputImage = new File(filePath);
            ImageIO.write(stitchedImage, imageType, outputImage);
            System.out.println("Stitched image saved to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving stitched image: " + e.getMessage());
        }

    }

}
