package City;

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
    private String gene;

    private Debug debug = new Debug();

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

    public void initializeRoadGrid() {
        if (gridLayout == null) {
            gridLayout = new char[width][height];
        }

        if (buildings == null) {
            buildings = new Building[width][height];
        }

        for (int i = 0; i < gridLayout.length; i++) {
            for (int j = 0; j < gridLayout[0].length; j++) {
                gridLayout[i][j] = BuildingType.ROAD.getSymbol();
                buildings[i][j] = new Road(i, j);
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
     * @return A randomly initialized city.
     */
    public static City initializeRandomCity(
            int width,
            int height,
            int numHouses,
            int numShops,
            int numOffices,
            int shopAverageSpend,
            int officeAverageSalary,
            double variation) {

        Debug debug = new Debug();
        Random random = new Random();
        City city = new City(width, height);

        int totalSpots = (width / 2) * (height / 2);

        if (numHouses + numShops + numOffices > totalSpots) {
            throw new IllegalArgumentException("Total number of buildings exceeds available spots.");
        }

        int housesPlaced = 0;
        int shopsPlaced = 0;
        int officesPlaced = 0;
        int emptySpacesPlaced = 0;

        ArrayList<Building> buildingsList = new ArrayList<>();
        ArrayList<Building> houseList = new ArrayList<>();
        ArrayList<Building> shopList = new ArrayList<>();
        ArrayList<Building> officeList = new ArrayList<>();

        for (int i = 1; i < width; i += 2) {
            for (int j = 1; j < height; j += 2) {

                // Using Gaussian distribution to bias building the placement towards the center
                double distanceToCenter = Math.sqrt(Math.pow(i - width / 2, 2) + Math.pow(j - height / 2, 2));
                double probability = Math.exp(-distanceToCenter * distanceToCenter / (2 * Math.pow(width / 4.0, 2) + Math.pow(height / 4.0, 2)));

                int randomBuildingType;

                // Adjust probabilities based on desired bias
                if (random.nextDouble() < probability / 2) {
                    randomBuildingType = 0; // House
                } else if (random.nextDouble() < probability / 1.5) {
                    randomBuildingType = 1; // Office
                } else if (random.nextDouble() < probability / 1.2) {
                    randomBuildingType = 2; // Shop
                } else {
                    // Increase the probability of an empty space as distance from the center increases
                    double emptySpaceProbability = distanceToCenter / Math.max(width, height) / 2;

                    if (random.nextDouble() < emptySpaceProbability) {
                        randomBuildingType = 3;  // Empty Space
                    } else {
                        randomBuildingType = 3;  //Fallback if empty space is not chosen, force empty space
                    }
                }

                if (randomBuildingType == 0 && housesPlaced < numHouses) {
                    city.gridLayout[i][j] = BuildingType.HOUSE.getSymbol();
                    city.buildings[i][j] = new House(i, j);
                    housesPlaced++;
                    houseList.add(city.buildings[i][j]);
                    buildingsList.add(city.buildings[i][j]);
                    debug.write("Placed House at (" + i + ", " + j + ")");
                } else if (randomBuildingType == 1 && shopsPlaced < numShops) {
                    city.gridLayout[i][j] = BuildingType.SHOP.getSymbol();
                    city.buildings[i][j] = new Shop(i, j, shopAverageSpend, variation);
                    shopsPlaced++;
                    shopList.add(city.buildings[i][j]);
                    buildingsList.add(city.buildings[i][j]);
                    debug.write("Placed Shop at (" + i + ", " + j + ")");
                } else if (randomBuildingType == 2 && officesPlaced < numOffices) {
                    city.gridLayout[i][j] = BuildingType.OFFICE.getSymbol();
                    city.buildings[i][j] = new Office(i, j, officeAverageSalary, variation);
                    officesPlaced++;
                    officeList.add(city.buildings[i][j]);
                    buildingsList.add(city.buildings[i][j]);
                    debug.write("Placed Office at (" + i + ", " + j + ")");
                } else {
                    city.gridLayout[i][j] = BuildingType.EMPTY.getSymbol();
                    city.buildings[i][j] = null;
                    emptySpacesPlaced++;
                    debug.write("Placed Empty Space at (" + i + ", " + j + ")");
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
        debug.write("Total Empty Spaces: " + emptySpacesPlaced);

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
        ArrayList<Person> peopleList = new ArrayList<>();
        this.setStartingMoney(startingMoney);
        this.setTravelCost(travelCost);
        for (int i = 0; i < houses.size(); i++) {
            House house = (House) houses.get(i);
            Office office = findAvailableOffice(this, i);

            Person person = new Person(startingMoney, travelCost, house, office, this);

            peopleList.add(person);
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
     * Encodes the city into a gene representation.
     *
     * @return A string representing the gene of the city.
     */
    public String encode() {
        StringBuilder encodedGene = new StringBuilder();

        // Append width and height
        encodedGene.append(gridLayout.length).append(" ").append(gridLayout[0].length).append(" ");

        // Append buildings
        for (Building building : buildingsList) {
            if (building instanceof House) {
                encodedGene.append("H ").append(building.getX()).append(" ").append(building.getY()).append(" ");
            } else if (building instanceof Office) {
                encodedGene.append("O ").append(building.getX()).append(" ").append(building.getY()).append(" ");
                encodedGene.append(((Office) building).getAverageSalary()).append(" ");
            } else if (building instanceof Shop) {
                encodedGene.append("S ").append(building.getX()).append(" ").append(building.getY()).append(" ");
                encodedGene.append(((Shop) building).getAverageSpend()).append(" ");
            }
        }

        // Append Starting money and Travel Cost for people
        encodedGene.append("SM ").append(this.getStartingMoney()).append(" ");
        encodedGene.append("TC ").append(this.getTravelCost()).append(" ");

        // Append people
        for (Person person : people) {
            encodedGene.append("P H ").append(person.getHouse().getX()).append(" ").append(person.getHouse().getY()).append(" ");
            encodedGene.append("O ").append(person.getOffice().getX()).append(" ").append(person.getOffice().getY()).append(" ");
        }

        this.gene = encodedGene.toString();
        return encodedGene.toString();
    }

    /**
     * Decodes the gene into a city.
     *
     * @param gene The gene representing the city.
     * @return The decoded city.
     */
    public static City decode(String gene) {
        String[] parts = gene.split(" ");
        int index = 0;

        // Parse width and height
        int width = Integer.parseInt(parts[index++]);
        int height = Integer.parseInt(parts[index++]);

        City city = new City(width, height);

        // Initialize gridLayout and buildings arrays
        city.gridLayout = new char[width][height];
        city.buildings = new Building[width][height];

        // Parse buildings
        while (index < parts.length) {
            // Check if it's a building
            if (parts[index].equals("H") || parts[index].equals("O") || parts[index].equals("S")) {
                Building building;
                int x = Integer.parseInt(parts[index + 1]);
                int y = Integer.parseInt(parts[index + 2]);

                // Check building type
                if (parts[index].equals("H")) {
                    building = new House(x, y);
                } else if (parts[index].equals("O")) {
                    // Parse salary (ensure it's not zero)
                    double salary = Math.max(Double.parseDouble(parts[index + 3]), 1.0);
                    building = new Office(x, y, salary, 0.0);
                    // Skip the salary part
                    index++;
                } else {
                    // Parse average spend (ensure it's not zero)
                    double averageSpend = Math.max(Double.parseDouble(parts[index + 3]), 1.0);
                    building = new Shop(x, y, averageSpend, 0.0);
                    // Skip the average spend part
                    index++;
                }

                // Add the building to the city
                city.addBuilding(building);
                city.gridLayout[x][y] = building.getType().getSymbol();
                city.buildings[x][y] = building;

                // Move to the next part
                index += 3;
            } else {
                break; // Break the loop if it's not a building
            }
        }

        // Parse Starting money and Travel Cost for people
        if (index + 2 < parts.length && parts[index].equals("SM") && parts[index + 2].equals("TC")) {
            // Parse starting money and travel cost (ensure they are not zero)
            double startingMoney = Math.max(Double.parseDouble(parts[index + 1]), 1.0);
            double travelCost = Math.max(Double.parseDouble(parts[index + 3]), 1.0);
            city.setStartingMoney(startingMoney);
            city.setTravelCost(travelCost);
            // Move to the next part
            index += 4;
        } else {
            return null; // Return null if the required parts are not found
        }

        // Parse people
        while (index < parts.length) {
            if (parts[index].equals("P")) {
                // Set the person's house and office using the getBuildingAt method
                int houseX = Integer.parseInt(parts[index + 2]);
                int houseY = Integer.parseInt(parts[index + 3]);
                int officeX = Integer.parseInt(parts[index + 5]);
                int officeY = Integer.parseInt(parts[index + 6]);

                Person person = new Person(
                        city.getStartingMoney(),
                        city.getTravelCost(),
                        (House) city.getBuildingAt(houseX, houseY),
                        (Office) city.getBuildingAt(officeX, officeY),
                        city);

                city.addPerson(person);
                // Move to the next part
                index += 7;
            } else {
                break; // Break the loop if it's not a person
            }
        }

        return city;
    }

    /**
     * Gets the building at the specified coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The building at the specified coordinates.
     */
    private Building getBuildingAt(int x, int y) {
        return this.buildings[x][y];
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

    // Helper method to calculate the total money owned by all people
    public double getTotalMoney() {
        double totalMoney = 0;
        ArrayList<Person> peopleInCity = this.getPeople();
        for (Person person : peopleInCity) {
            totalMoney += person.getMoney();
        }
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

        Debug debug = new Debug();

        if (!offices.isEmpty()) {
            if (index < offices.size()) {
                return (Office) offices.get(index);
            } else {
                // If there are more people than offices, assign to a random office
                Random random = new Random();
                int randomIndex = random.nextInt(offices.size());
                return (Office) offices.get(randomIndex);
            }
        } else {
            debug.write("ERROR: No offices available. Returning null.");
            return null;
        }
    }

    // Getters
    public void addPerson(Person person) {
        this.people.add(person);
    }

    public void addBuilding(Building building) {
        this.buildingsList.add(building);
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public void setPeople(ArrayList<Person> people) {
        this.people = people;
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

    public ArrayList<Building> getBuildingsList() {
        return buildingsList;
    }

    public void setBuildingsList(ArrayList<Building> buildingsList) {
        this.buildingsList = buildingsList;
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

}
