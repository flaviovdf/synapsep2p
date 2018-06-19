package synapse.common;

/**
 * Creates a new RequestIDAlreadyExistsException.
 *
 * @author <p>Joao Arthur Brunet Monteiro, joaoabm@lcc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
public class RequestIDAlreadyExistsException extends Exception {

	/**
	 * Creates a new <code>RequestIDAlreadyExistsException</code>.
	 * @param requestID The colliding id.
	 */
	public RequestIDAlreadyExistsException(long requestID) {
		super("Request ID <" + requestID + "> already exists.");
	}
}
