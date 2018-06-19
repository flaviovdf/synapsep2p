package synapse.common;

import synapse.common.events.Event;

/**
 * This interface represents an object responsible for processing
 * events.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public interface EventProcessor extends Runnable {

	/**
	 * Includes an <code>Event</code> to be processed by this <code>EventProcessor</code>.
	 * @param event The <code>Event</code> to be included.
	 */
    public void putEvent(Event event);

	/**
	 * Starts processing its events.
	 */
    public void startProcessing();

	/**
	 * Marks this <code>Runnable</code> to be shutdown. It saves the parameter <code>EventQueue</code> to put a
	 * <code>ShutdownResponseEvent</code> when the shutdown is done.
	 * 
	 * @param responseQueue <code>EventQueue</code> that will contain the 
	 * <code>ShutdownResponseEvent</code> after the object had successfully shutdown.
	 */
	public void shutdown(EventQueue responseQueue); 
	
	/**
	 * Checks if the <code>EventProcessor</code> is alive.
	 * 
	 * @return True if it is alive, false otherwise
	 */
	public boolean isAlive();
}
