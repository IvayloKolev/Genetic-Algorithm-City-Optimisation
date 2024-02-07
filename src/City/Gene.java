package City;

import Building.Building;
import Building.House;
import Building.Office;
import Building.Road;
import Building.Shop;
import Debug.Debug;
import java.util.ArrayList;

/**
 * Gene class represents the genetic information of a city for the genetic
 * algorithm.
 *
 * @author Ivaylo Kolev (2005549)
 */
public class Gene {

    private static final Debug debug = new Debug();

    private int width;
    private int height;
    private double startingMoney;
    private double travelCost;
    private ArrayList<Building> buildingsList;

    /**
     * Empty Constructor. Initializes variables to be set later.
     */
    public Gene() {
        this.width = 0;
        this.height = 0;
        this.startingMoney = 0.0;
        this.travelCost = 0.0;
        this.buildingsList = new ArrayList<>();
    }

    /**
     * Encodes a City object into a Gene.
     *
     * @param city The City object to be encoded.
     * @return The encoded Gene representing the genetic information of the
     * city.
     */
    public static Gene encode(City city) {
        Gene gene = new Gene();

        gene.setWidth(city.getWidth());
        gene.setHeight(city.getHeight());
        gene.setStartingMoney(city.getStartingMoney());
        gene.setTravelCost(city.getTravelCost());
        gene.setBuildingsList(city.getBuildingsList());

        city.setGene(gene);

        return gene;
    }

    /**
     * Decodes a Gene object into a City.
     *
     * @param gene The Gene object to be decoded.
     * @return The decoded City object representing the reconstructed city
     * structure, or null if decoding fails.
     */
    public static City decode(Gene gene) {
        City city = new City(gene.getWidth(), gene.getHeight());

        city.setGridLayout(new char[gene.getWidth()][gene.getHeight()]);
        city.setBuildings(new Building[gene.getWidth()][gene.getHeight()]);
        city.initializeRoadGrid();

        ArrayList<Building> buildings = gene.getBuildingsList();

        boolean hasOffice = false;
        boolean hasShop = false;

        for (Building building : buildings) {
            int x = building.getX();
            int y = building.getY();

            // Handle different building types
            switch (building.getType()) {
                case HOUSE ->
                    city.addBuilding(new House(x, y));
                case OFFICE -> {
                    double salary = ((Office) building).getSalary();
                    city.addBuilding(new Office(x, y, salary, 0.0));
                    hasOffice = true;
                }
                case SHOP -> {
                    double averageSpend = ((Shop) building).getAverageSpend();
                    city.addBuilding(new Shop(x, y, averageSpend, 0.0));
                    hasShop = true;
                }
                default ->
                    city.addBuilding(new Road(x, y));
            }

            city.getGridLayout()[x][y] = building.getType().getSymbol();
            city.getBuildings()[x][y] = building;
        }

        // Set Starting money and Travel Cost
        city.setStartingMoney(gene.getStartingMoney());
        city.setTravelCost(gene.getTravelCost());

        // Check if at least one office and one shop are present
        if (!hasOffice || !hasShop) {
            // Discard the city if the condition is not met
            if (!hasOffice) {
                debug.write("Discarded a gene with no offices.");
            } else if (!hasShop) {
                debug.write("Descarded a gene with no shops");
            } else if (!hasShop && !hasOffice) {
                debug.write("Discarded a gene with no shops or offices, one in a million!");
            }
            return null;
        }

        // Populate the city with people
        city.populate(city.getStartingMoney(), city.getTravelCost());

        city.setGene(gene);
        return city;
    }

    // Getters and Setters
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getStartingMoney() {
        return startingMoney;
    }

    public void setStartingMoney(double startingMoney) {
        this.startingMoney = startingMoney;
    }

    public double getTravelCost() {
        return travelCost;
    }

    public void setTravelCost(double travelCost) {
        this.travelCost = travelCost;
    }

    public ArrayList<Building> getBuildingsList() {
        return buildingsList;
    }

    public void setBuildingsList(ArrayList<Building> buildingsList) {
        this.buildingsList = buildingsList;
    }

}
