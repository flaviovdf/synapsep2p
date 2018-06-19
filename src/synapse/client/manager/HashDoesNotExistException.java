package synapse.client.manager;

/**
 * This exception is used to flag when a specified hash does
 * not exist.
 * 
 * @author <p>Thiago Emmanuel, thiago.manel@gmail.com</p>
 */
public class HashDoesNotExistException extends Exception {

	/**
	 * Creates a new HashDoesNotExists
	 */
	public HashDoesNotExistException (String hash) {
		super ("The hash " + hash + " doesn't exist");
	}

}
