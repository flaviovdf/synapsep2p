package synapse.common.events;

import synapse.common.Provider;
import synapse.server.ConnectionManager;

/**
 * This event is used to identify a consumer in the <code>ConnectionManager</code>.
 * 
 * @author <p>Joao Arthur Brunet Monteiro, jarthur@dsc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
public class IdentifyEvent implements ActionEvent {
	
	/**
	 * The source where the method will be invoked.
	 */
	private ConnectionManager connectionManager;
	
	/**
	 * The provider to be identified.
	 */
	private Provider provider;
	
	/**
	 * Creates a new IdentifyEvent.
	 * 
	 * @param connectionManager The source where the method will be invoked.
	 * @param provider The provider to be identified.
	 */
	public IdentifyEvent(ConnectionManager connectionManager, Provider provider) {
		this.connectionManager = connectionManager;
		this.provider = provider;
	}

	/* (non-Javadoc)
	 * @see synapse.common.events.ActionEvent#process()
	 */
	public void process() {
		this.connectionManager.identify(this.provider);
	}

}
