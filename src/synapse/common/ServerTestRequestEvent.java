package synapse.common;

import synapse.common.events.ActionEvent;

/**
 * ServerTestRequestEvent This Event is used only for testing, it only puts a ServerTestResponseEvent
 * on its ResponseQueue in order to test if the <code>process</code> method was invoked.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>  
 */
public class ServerTestRequestEvent implements ActionEvent {

	/** An EventQueue */
	private EventQueue responseQueue;

	/**
	 * Constructs the Event.
	 * 
	 * @param responseQueue The EventQueue where the ServerTestResponseEvent will be put.
	 */
	ServerTestRequestEvent(EventQueue responseQueue){
		this.responseQueue = responseQueue;		
	}

	/**
	 * Puts a ServerTestResponseEvent on the responseQueue.
	 * 
	 * @see synapse.common.events.ActionEvent#process()
	 */
	public void process() {
		responseQueue.put(new ServerTestResponseEvent());
	}

}