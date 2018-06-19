package synapse.common;

import synapse.common.events.ActionEvent;
import synapse.common.events.Event;
import synapse.common.events.ShutdownResponseEvent;

/**
 * This class is responsible for receiving events that come probably from a <code>Facade</code> and process them.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 * 
 * @see Facade
 */
public class SimpleEventProcessor implements EventProcessor {

	/**
	 * A <code>EventQueue</code> that is filled by <code>putEvent()</code> method and provides the events to
	 * <code>run()</code> method consuming.
	 */
	protected EventQueue eventQueue;

	/**
	 * Indicates <code>run()</code> method must shutdown.
	 */
	private boolean mustShutdown;

	/**
	 * When the processing (event consuming) shutdowns, this queue receives a <code>ShutdownNotifyEvent</code>. Such event
	 * indicates that shutdown is done.
	 */
	private EventQueue shutdownEventQueue;

	/**
	 * Thread that performs <code>Event</code> consuming.
	 */
	private Thread myThread;

	/**
	 * Indicates if this <code>EventProcessor</code> is alive.
	 */
	private boolean isAlive;

	/**
	 * Creates a new <code>SimpleEventProcessor</code>.
	 */
	public SimpleEventProcessor() {
		this.eventQueue = new EventQueue();
		this.isAlive = false;
		this.myThread = new Thread(this);
	}

	/**
	 * Begin consuming of events. Consuming means remove events from the queue and process (call the <code>process()</code>
	 * method) them.
	 */
	public void startProcessing() {
	    this.mustShutdown = false;
	    this.isAlive = true;
		this.myThread.start();
	}

	/**
	 * Inserts an <code>Event</code> - to be processed - into a internal <code>EventQueue</code>.
	 * 
	 * @param event An <code>Event</code> to be processed.
	 */
	public void putEvent(Event event) {
		this.eventQueue.put(event);
	}

	/**
	 * Consumes events from internal <code>EventQueue</code>. In other words, it removes events from the queue and process
	 * (call the <code>process()</code> method) them.
	 */
	public void run() {

		while (!mustShutdown) {
			ActionEvent actionEvent = (ActionEvent) eventQueue.blockingRemove();
			actionEvent.process();
		}

		this.isAlive = false;
		this.shutdownEventQueue.put(new ShutdownResponseEvent());
	}

	/**
	 * @see EventProcessor#shutdown(EventQueue)
	 */
	public void shutdown(EventQueue eq) {
		this.shutdownEventQueue = eq;
		this.mustShutdown = true;
	}

	/**
	 * @see EventProcessor#isAlive()
	 */
	public boolean isAlive() {
		return this.isAlive;
	}
}
