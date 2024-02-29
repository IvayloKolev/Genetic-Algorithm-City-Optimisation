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
            int width = 13;
            int height = 13;
            int numHouses = 18;
            int numShops = 9;
            int numOffices = 9;
            double shopAverageSpend = 30;
            double officeAverageSalary = 130;
            double variation = 25;
            double centerBias = 2.0;

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
