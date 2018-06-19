package synapse.common;

/**
 * This exception is used to flag when
 * something wrog happens in the UserInterface.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class UIException extends Exception {

    /**
     * Crates a new exception.
     * 
     * @param message The error message.
     */
    public UIException(String message) {
        super(message);
    }

}
