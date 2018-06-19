package synapse.common.events;

import synapse.common.EventProcessor;
import synapse.common.EventQueue;

/**
 * An <code>ActionEvent</code> that represents the request for a shutdown.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ShutdownRequestEvent implements ActionEvent {

	/**
	 * The <code>EventProcessor</code> object.
	 */
	private EventProcessor eventProcessor;
	
	/**
	 * The response queue.
	 */
	private EventQueue responseQueue;
	
	/**
	 * Constructor.
	 * 
	 * @param responseQueue The response queue where the process result will be put.
	 * @param eventProcessor The <code>EventProcessor</code> object to be shutdown.
	 */
	public ShutdownRequestEvent( EventQueue responseQueue, EventProcessor eventProcessor ) {
		this.responseQueue = responseQueue;
		this.eventProcessor = eventProcessor;
	}

    /* (non-Javadoc)
     * @see synapse.common.ActionEvent#process()
     */
	public void process() {
		this.eventProcessor.shutdown( responseQueue );
	}	

}