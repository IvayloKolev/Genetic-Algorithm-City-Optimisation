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
            int width = 15;
            int height = 21;
            int numHouses = 10;
            int numShops = 6;
            int numOffices = 5;
            double shopAverageSpend = 30;
            double officeAverageSalary = 130;
            double variation = 25;
            double centerBias = 2.5;

            CityVisualisation visualisation = new CityVisualisation();

            for (int i = 0; i < 10; i++) {
                String fileName = "stitchedImage" + (i + 1) + ".png";
                City city = City.initializeRandomCity(width, height, numHouses, numShops, numOffices, shopAverageSpend, officeAverageSalary, variation, centerBias);

                visualisation.exportStitchedImage(city, fileName, "png");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
