package synapse.common;

import synapse.common.events.ActionEvent;
import synapse.common.events.ShutdownRequestEvent;
import synapse.common.events.ShutdownResponseEvent;
import junit.framework.TestCase;


/**
 * Tests the <code>SimpleEventProcessor</code> functions.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class SimpleEventProcessorTest extends TestCase {

    /**
     * The object to be tested.
     */
	private SimpleEventProcessor eventProcessor;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		eventProcessor = new SimpleEventProcessor();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Tests if the events are been received by the <code>SimpleEventProcessor</code>.
	 */
	public void testAll() {
		EventQueue eventQueue = new EventQueue();
		ActionEvent requestEvent = new ServerTestRequestEvent(eventQueue);

		// Put Event, StartProcessing and isAlive
		eventProcessor.putEvent(requestEvent);

		Object response = eventQueue.unblockingRemove();
		assertNull(response);
		assertFalse(eventProcessor.isAlive());

		eventProcessor.startProcessing();
		assertTrue(eventProcessor.isAlive());
		response = eventQueue.blockingRemove();
		assertNotNull(response);
		assertTrue(response instanceof ServerTestResponseEvent);

		int numberOfEventQueues = 1000;
		EventQueue[] eventQueues = new EventQueue[numberOfEventQueues];
		for (int k = 0; k < eventQueues.length; k++) {
			eventQueues[k] = new EventQueue();
			ActionEvent event = new ServerTestRequestEvent(eventQueues[k]);
			eventProcessor.putEvent(event);
		}

		for (int k = 0; k < eventQueues.length; k++) {
			Object responseEvent = eventQueues[k].blockingRemove();
			assertNotNull(responseEvent);
			assertTrue(responseEvent instanceof ServerTestResponseEvent);
		}

		// Shutdown, isAlive
		assertTrue(eventProcessor.isAlive());
		eventProcessor.putEvent(new ShutdownRequestEvent(eventQueue, eventProcessor));
		response = eventQueue.blockingRemove();
		assertFalse(eventProcessor.isAlive());
		assertTrue(response instanceof ShutdownResponseEvent);
	}

}
