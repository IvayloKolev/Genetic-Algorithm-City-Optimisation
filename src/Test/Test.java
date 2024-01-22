package Test;

import City.City;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class Test {

    public static void main(String[] args) {

        City city = new City(15, 15);
        
        System.out.println(city.toStringGridLayout());

    }

}
