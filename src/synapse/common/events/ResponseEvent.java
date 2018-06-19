package synapse.common.events;

/**
 * An event that stores a response.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public interface ResponseEvent extends Event {
	
	/**
	 * Gets the response of an <code>Event</code>.
	 * 
	 * @return an Object that corresponds to the response of an Event.
	 */
	public Object getResponse();

}
