package City;

import Debug.Debug;
import Person.Person;
import Building.BuildingType;
import Building.Office;
import Building.Building;
import Building.House;
import Building.Road;
import Building.Shop;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Represents a city with various buildings and people.
 *
 * @author Ivaylo Kolev 2005549
 */
public class City {

    private int width;
    private int height;
    private char[][] gridLayout;
    private Building[][] buildings;
    private ArrayList<Building> buildingsList;
    private ArrayList<Person> people;
    private double startingMoney;
    private double travelCost;
    private ArrayList<Building> houses;
    private ArrayList<Building> offices;
    private ArrayList<Building> shops;
    private Gene gene;
    private double fitness;

    private static final Random random = new Random();
    private static final Debug debug = new Debug();

    /**
     * Constructs a city with the specified width and height.
     *
     * @param width The width of the city.
     * @param height The height of the city.
     */
    public City(int width, int height) {
        this.width = width;
        this.height = height;
        this.gridLayout = new char[width][height];
        this.buildings = new Building[width][height];
        this.buildingsList = new ArrayList<>();
        this.people = new ArrayList<>();
        this.houses = new ArrayList<>();
        this.offices = new ArrayList<>();
        this.shops = new ArrayList<>();
        initializeRoadGrid();
    }

    /**
     * Empty Constructor.
     */
    public City() {
        this.width = 0;
        this.height = 0;
        initializeRoadGrid();
    }

    /**
     * Copy Constructor.
     *
     * @param toBeCopiedCity The City Object to be copied.
     */
    public City(City toBeCopiedCity) {
        this.width = toBeCopiedCity.width;
        this.height = toBeCopiedCity.height;
        this.startingMoney = toBeCopiedCity.startingMoney;
        this.travelCost = toBeCopiedCity.travelCost;
        this.gene = toBeCopiedCity.gene;
        initializeRoadGrid();

        // Copy gridLayout and buildings
        this.gridLayout = new char[this.width][this.height];
        this.buildings = new Building[this.width][this.height];
        for (int i = 0; i < this.width; i++) {
            // Deep copy the gridLayout
            this.gridLayout[i] = Arrays.copyOf(toBeCopiedCity.gridLayout[i], this.height);

            // Deep copy the buildings
            for (int j = 0; j < this.height; j++) {
                this.buildings[i][j] = new Building(toBeCopiedCity.buildings[i][j]);
            }
        }

        // Copy buildingsList
        this.buildingsList = new ArrayList<>(toBeCopiedCity.buildingsList.size());
        for (Building building : toBeCopiedCity.buildingsList) {
            if (building instanceof House) {
                this.buildingsList.add(new House((House) building));
            } else if (building instanceof Office) {
                this.buildingsList.add(new Office((Office) building));
            } else if (building instanceof Shop) {
                this.buildingsList.add(new Shop((Shop) building));
            }
        }

        this.people = new ArrayList<>(toBeCopiedCity.people.size());
        for (Person person : toBeCopiedCity.people) {
            // Make sure Person has a copy constructor as well
            this.people.add(new Person(person));
        }

    }

    /**
     * Initializes the grid layout with a pattern of roads and spaces for
     * buildings.
     */
    public final void initializeRoadGrid() {
        if (gridLayout == null) {
            gridLayout = new char[width][height];
        }

        if (buildings == null) {
            buildings = new Building[width][height];
        }

        for (int i = 0; i < gridLayout.length; i++) {
            for (int j = 0; j < gridLayout[0].length; j++) {
                // One row of roads followed by a row with alternating roads and spaces for buildings
                boolean isRoad = (i % 2 == 0) || (i % 2 == 1 && j % 2 == 0);
                gridLayout[i][j] = isRoad ? BuildingType.ROAD.getSymbol() : BuildingType.EMPTY.getSymbol();
                buildings[i][j] = isRoad ? new Road(i, j) : null;
            }
        }
    }

    /**
     * Initializes a random city with the specified parameters.
     *
     * @param width The width of the city.
     * @param height The height of the city.
     * @param numHouses The number of houses in the city.
     * @param numShops The number of shops in the city.
     * @param numOffices The number of offices in the city.
     * @param shopAverageSpend The average spending in shops.
     * @param officeAverageSalary The average salary in offices.
     * @param variation The possible variation for the shop spending and
     * salaries.
     * @param centerBias Bias factor for clumping buildings in the center of the
     * city.
     * @return A randomly initialized city.
     */
    public static City initializeRandomCity(
            int width,
            int height,
            int numHouses,
            int numShops,
            int numOffices,
            double shopAverageSpend,
            double officeAverageSalary,
            double variation,
            double centerBias
    ) {

        City city = new City(width, height);

        int totalSpots = (width / 2) * (height / 2);

        if (numHouses + numShops + numOffices > totalSpots) {
            throw new IllegalArgumentException(
                    "Total number of buildings exceeds available spots:"
                    + "\nTotal number of buildings requested:" + (numHouses + numShops + numOffices)
                    + "\nTotal spots: " + totalSpots);

        }

        int housesPlaced = 0;
        int shopsPlaced = 0;
        int officesPlaced = 0;
        int emptySpacesPlaced = 0;
        double bias = centerBias;

        ArrayList<Building> buildingsList = new ArrayList<>();
        ArrayList<Building> houseList = new ArrayList<>();
        ArrayList<Building> shopList = new ArrayList<>();
        ArrayList<Building> officeList = new ArrayList<>();

        for (int i = 1; i < width - 1; i += 2) {
            for (int j = 1; j < height - 1; j += 2) {
                // Check if the maximum number of houses, shops, and offices have been placed
                if (housesPlaced >= numHouses && shopsPlaced >= numShops && officesPlaced >= numOffices) {
                    // If all limits are reached, break out of the loop
                    break;
                }

                double distanceToCenter = Math.sqrt(Math.pow(i - width / 2, 2) + Math.pow(j - height / 2, 2));
                double probability = Math.exp(-bias * distanceToCenter * distanceToCenter / (2 * Math.pow(width / 4.0, 2) + Math.pow(height / 4.0, 2)));

                int randomBuildingType;

                if (random.nextDouble() < probability && housesPlaced < numHouses) {
                    randomBuildingType = BuildingType.HOUSE.ordinal();
                    housesPlaced++;
                } else if (random.nextDouble() < probability && shopsPlaced < numShops) {
                    randomBuildingType = BuildingType.SHOP.ordinal();
                    shopsPlaced++;
                } else if (random.nextDouble() < probability && officesPlaced < numOffices) {
                    randomBuildingType = BuildingType.OFFICE.ordinal();
                    officesPlaced++;
                } else {
                    // Increase the probability of an empty space as distance from the center increases
                    double emptySpaceProbability = distanceToCenter / Math.max(width, height) / 2;

                    if (random.nextDouble() < emptySpaceProbability) {
                        randomBuildingType = BuildingType.EMPTY.ordinal();
                    } else {
                        randomBuildingType = BuildingType.EMPTY.ordinal(); // Fallback if empty space is not chosen, force empty space
                    }
                }

                // Place buildings based on the randomBuildingType
                switch (BuildingType.values()[randomBuildingType]) {
                    case HOUSE -> {
                        if (housesPlaced < numHouses) {
                            city.gridLayout[i][j] = BuildingType.HOUSE.getSymbol();
                            city.buildings[i][j] = new House(i, j);
                            housesPlaced++;
                            houseList.add(city.buildings[i][j]);
                            buildingsList.add(city.buildings[i][j]);
                            debug.write("Placed House at (" + i + ", " + j + ")");
                        }
                    }
                    case SHOP -> {
                        if (shopsPlaced < numShops) {
                            city.gridLayout[i][j] = BuildingType.SHOP.getSymbol();
                            city.buildings[i][j] = new Shop(i, j, shopAverageSpend, variation);
                            shopsPlaced++;
                            shopList.add(city.buildings[i][j]);
                            buildingsList.add(city.buildings[i][j]);
                            debug.write("Placed Shop at (" + i + ", " + j + ")");
                        }
                    }
                    case OFFICE -> {
                        if (officesPlaced < numOffices) {
                            city.gridLayout[i][j] = BuildingType.OFFICE.getSymbol();
                            city.buildings[i][j] = new Office(i, j, officeAverageSalary, variation);
                            officesPlaced++;
                            officeList.add(city.buildings[i][j]);
                            buildingsList.add(city.buildings[i][j]);
                            debug.write("Placed Office at (" + i + ", " + j + ")");
                        }
                    }
                    case EMPTY -> {
                        if (emptySpacesPlaced < (totalSpots - numHouses - numShops - numOffices)) {
                            city.gridLayout[i][j] = BuildingType.EMPTY.getSymbol();
                            city.buildings[i][j] = null;
                            emptySpacesPlaced++;
                            debug.write("Placed Empty Space at (" + i + ", " + j + ")");
                        }
                    }
                }
            }
        }

        city.setBuildingsList(buildingsList);
        city.setHouses(houseList);
        city.setShops(shopList);
        city.setOffices(officeList);

        debug.write("City initialized with random buildings.");
        debug.write("Open spots: " + totalSpots);
        debug.write("Total Houses (Placed/Requested): " + housesPlaced + "/" + numHouses);
        debug.write("Total Shops (Placed/Requested): " + shopsPlaced + "/" + numShops);
        debug.write("Total Offices (Placed/Requested): " + officesPlaced + "/" + numOffices);

        return city;
    }

    /**
     * Populates the city with people, assigning each person to a separate house
     * and one office.
     *
     * @param startingMoney The initial amount of money for each person.
     * @param travelCost The cost of travel for each person.
     */
    public void populate(double startingMoney, double travelCost) {
        this.people.clear();
        this.setStartingMoney(startingMoney);
        this.setTravelCost(travelCost);
        for (int i = 0; i < houses.size(); i++) {
            House house = (House) houses.get(i);
            Office office = findAvailableOffice(this, i);

            Person person = new Person(startingMoney, travelCost, house, office, this);

            people.add(person);
        }
    }

    /**
     * Simulates activities for each person in the city (going to work,
     * shopping, going home).
     */
    public void simulate() {
        ArrayList<Person> peopleList = this.getPeople();
        for (Person person : peopleList) {
            person.goToWork();
            person.goShopping();
            person.goHome();
        }
    }

    /**
     * Gets a string representation of the city grid layout.
     *
     * @return A string representing the city grid layout.
     */
    public String toStringGridLayout() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < gridLayout.length; i++) {
            for (int j = 0; j < gridLayout[0].length; j++) {
                result.append(gridLayout[i][j]).append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * Gets all the sum of money from all the people in the city.
     *
     * @return The sum of money.
     */
    public double getTotalMoney() {
        double totalMoney = 0;
        ArrayList<Person> peopleInCity = this.getPeople();
        for (Person person : peopleInCity) {
            totalMoney += person.getMoney();
        }
        debug.write("Total money in this City: " + totalMoney);
        return totalMoney;
    }

    // Helper method to count inactive people
    public int countInactivePeople() {
        int count = 0;
        ArrayList<Person> peopleInCity = this.getPeople();
        for (Person person : peopleInCity) {
            if (!person.getActive()) {
                count++;
            }
        }
        debug.write("Total inactive people in this City: " + count);
        return count;
    }

    // Helper method to count active people
    public int countActivePeople() {
        int count = 0;
        ArrayList<Person> peopleInCity = this.getPeople();
        for (Person person : peopleInCity) {
            if (person.getActive()) {
                count++;
            }
        }
        debug.write("Total inactive people in this City: " + count);
        return count;
    }

    /**
     * Helper method to find an available office for a person.
     *
     * @param index The index used to get different offices.
     * @return An available office.
     */
    private static Office findAvailableOffice(City city, int index) {
        ArrayList<Building> offices = city.getOffices();

        if (!offices.isEmpty()) {
            if (index < offices.size()) {
                return (Office) offices.get(index);
            } else {
                // If there are more people than offices, assign to a random office
                int randomIndex = random.nextInt(offices.size());
                return (Office) offices.get(randomIndex);
            }
        } else {
            debug.write("ERROR: No offices available. Returning null.");
            return null;
        }
    }

    // Getters and Setters
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

    public char[][] getGridLayout() {
        return gridLayout;
    }

    public void setGridLayout(char[][] gridLayout) {
        this.gridLayout = gridLayout;
    }

    public Building[][] getBuildings() {
        return buildings;
    }

    public void setBuildings(Building[][] buildings) {
        this.buildings = buildings;
    }

    public ArrayList<Building> getBuildingsList() {
        return buildingsList;
    }

    public void setBuildingsList(ArrayList<Building> buildingsList) {
        this.buildingsList = buildingsList;
    }

    public void addBuilding(Building building) {
        this.buildingsList.add(building);

        switch (building.getType()) {
            case HOUSE -> {
                this.houses.add(building);
            }
            case OFFICE -> {
                this.offices.add(building);
            }
            case SHOP -> {
                this.shops.add(building);
            }
        }
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public void setPeople(ArrayList<Person> people) {
        this.people = people;
    }

    public void addPerson(Person person) {
        this.people.add(person);
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

    public ArrayList<Building> getHouses() {
        return houses;
    }

    public void setHouses(ArrayList<Building> houses) {
        this.houses = houses;
    }

    public ArrayList<Building> getOffices() {
        return offices;
    }

    public void setOffices(ArrayList<Building> offices) {
        this.offices = offices;
    }

    public ArrayList<Building> getShops() {
        return shops;
    }

    public void setShops(ArrayList<Building> shops) {
        this.shops = shops;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        return "City{" + "width=" + width + ", height=" + height + ", gridLayout=" + gridLayout + ", buildings=" + buildings + ", buildingsList=" + buildingsList + ", people=" + people + ", startingMoney=" + startingMoney + ", travelCost=" + travelCost + ", houses=" + houses + ", offices=" + offices + ", shops=" + shops + ", gene=" + gene + ", fitness=" + fitness + '}';
    }

}
