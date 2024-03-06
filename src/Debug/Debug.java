package Debug;

/**
 *
 * @author Ivaylo Kolev 2005549
 */
public class Debug {

    private Boolean debug = true;

    /**
     * Writes a message if debug mode is enabled.
     *
     * @param message The message to be displayed.
     */
    public void write(String message) {
        if (debug) {
            System.out.println("DEBUG: " + message);
        }
    }
}
