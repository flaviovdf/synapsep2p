package synapse.server;

/**
 * Exception case a non-identified peer tries to send a request to the server.
 * 
 * @author <p>Joao Arthur Brunet Monteiro, jarthur@dsc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
public class ClientNotIdentifiedException extends Exception {
	
	/**
	 * Creates a new ClientNotIdentifiedException.
	 */
	public ClientNotIdentifiedException () {
		super ("You are not identified, please indentify yourself.");
	}
}
