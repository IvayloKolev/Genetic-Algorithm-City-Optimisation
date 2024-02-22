package City;

import java.io.IOException;

/**
 * CityImageExport class to demonstrate exporting the stitched image of the
 * city.
 *
 * @author Ivaylo Kolev 2005549
 */
public class CityImageExport {

    public static void main(String[] args) {
        try {
            int width = 41;
            int height = 41;
            int numHouses = 150;
            int numShops = 100;
            int numOffices = 150;
            double shopAverageSpend = 30;
            double officeAverageSalary = 130;
            double variation = 25;
            double centerBias = 2.5;

            CityVisualisation visualisation = new CityVisualisation();

            for (int i = 0; i < 100; i++) {
                String fileName = "stitchedImage" + (i + 1) + ".png";
                City city = City.initializeRandomCity(width, height, numHouses, numShops, numOffices, shopAverageSpend, officeAverageSalary, variation, centerBias);

                visualisation.exportStitchedImage(city, fileName, "png");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
