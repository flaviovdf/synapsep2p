package synapse.common;

import java.util.LinkedList;

import org.ourgrid.yal.Logger;

import synapse.common.events.Event;

/**
 * This class is desired to queue events in a synchronized way.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p> 
 */
public class EventQueue {
	
	/**
	 * The queued events.
	 */
	private LinkedList eventQueue;
	
	/**
	 * Object responsible for logging events.
	 */
	private Logger logger;

	/**
	 * The constructor.
	 */
	public EventQueue() {
		eventQueue = new LinkedList();
		logger = Logger.getInstance();
	}

	/**
	 * Adds an event to the end of the queue.
	 * 
	 * @param event The event to be queued.
	 */
	public synchronized void put(Event event) {
		eventQueue.addLast( event );
		notify();
	}

	/**
	 * Removes the first event from the queue.
	 * 
	 * @return The first event if exists, otherwise returns <code>null</code>.
	 */
	public synchronized Event unblockingRemove() {
		if ( eventQueue.size() > 0 ) {
			return (Event) eventQueue.removeFirst();	
		} else {
			return null;
		}		
	}
	
	/**
	 * Removes the first event of the queue. If there is no event in the queue,
	 * this method blocks until an event is queued.
	 * 
	 * @return The first event.
	 */
	public synchronized Event blockingRemove() {
		try {
			while (eventQueue.size() == 0) {
				wait();
			}
		} catch (InterruptedException e) {
			logger.exception(getClass().getName() + ".blockingRemove()", e);
		}
		return (Event) eventQueue.removeFirst();
	}
	
	/**
	 * The queue size.
	 * 
	 * @return The size.
	 */
	public synchronized int size() {
		return eventQueue.size();
	}

}