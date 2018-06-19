package synapse.client.ui;

/**
 * This exception is thrown when the client is not connected to the server.
 * 
 * @author flaviors@dsc.ufcg.edu.br
 */
public class ClientNotConnectedException extends Exception {

	public ClientNotConnectedException() {
		super("The client is not connected to the server");
	}

	public ClientNotConnectedException(String msg) {
		super(msg);
	}

}
