/* 
 * Blitz
 * synapse.proxy/Facade.java
 * Created on: 17/09/2004
 * Author: Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br
 */

package synapse.common;

import org.ourgrid.yal.Logger;

import synapse.common.events.ShutdownRequestEvent;
import synapse.common.events.ShutdownResponseEvent;

/**
 * The facade class communicates with the <code>EventProcess</code> to enter de file of events
 * that are being requested to the proxy.
 * 
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
public abstract class Facade {

    //attributes
    protected EventProcessor eventProcessor;
    private boolean started = false;
    
    /**
     * Used only by tests.
     * 
     * @param eventProcessor
     */
    protected Facade(EventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    /**
     * Configures the facade.
     */
    public abstract void config();

    /**
     * Starts the thread.
     */
    public void startEventProcessor() {
		if (this.started == true) {
			throw new IllegalStateException();
		}
		eventProcessor.startProcessing();
		this.started = true;
		
		Logger.getInstance().info(getClass().getName() + ".startEventProcessor()", "The EventProcessor was successfully started.");
    }

    /**
     * Shuts down the facade.
     */
    public EventQueue shutdown() {
		EventQueue responseQueue = new EventQueue();
		ShutdownRequestEvent shutdownRequestEvent = new ShutdownRequestEvent(responseQueue, eventProcessor);
		if ( eventProcessor.isAlive() ) {
			eventProcessor.putEvent(shutdownRequestEvent);
		} else {
			responseQueue.put(new ShutdownResponseEvent());
		}
		return responseQueue;
    }

}
