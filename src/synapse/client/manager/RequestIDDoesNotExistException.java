package synapse.client.manager;

/**
 * Exception thrown if the client tries to download form a Search ID that does not exist.
 * 
 * @author <p>Felipe Ribeiro, felipern@lcc.ufcg.edu.br</p>
 * @author <p>Flavio Vinícius, flaviov@lcc.ufcg.edu.br</p>
 */
public class RequestIDDoesNotExistException extends Exception {
	
	/**
	 * Creates a new RequestIDDoesntExistException.
	 */
	public RequestIDDoesNotExistException(long id) {
		super("The ID <" + id + "> given doesn't exist");
	}
}
