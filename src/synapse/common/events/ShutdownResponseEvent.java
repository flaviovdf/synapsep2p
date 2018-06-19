package synapse.common.events;


/**
 * A <code>ResponseEvent</code> that can block waiting
 * for its response, that corresponds to a shutdown.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ShutdownResponseEvent implements ResponseEvent {

	/**
	 * Gets the response of this event when its ready.
	 * 
	 * @return an Object returned when a shutdown has terminated. 
	 */
	public Object getResponse() {
		return null;
	}

}
