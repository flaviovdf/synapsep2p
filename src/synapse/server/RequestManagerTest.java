package synapse.server;

import junit.framework.TestCase;
import synapse.common.FakeConsumer;
import synapse.common.FakeProvider;
import synapse.common.RequestIDAlreadyExistsException;

/**
 * Tests for the RequestManager class.
 *
 * @author <p>Joao Arthur Brunet Monteiro, joaoabm@lcc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
public class RequestManagerTest extends TestCase {
	
	/**
	 * Object that will represent the tested class.
	 */
	private RequestManager reqManager;
	
	/**
	 * Fake Provider for method invocation.
	 */
	private FakeProvider fakeProvider;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		reqManager = new RequestManager ();
	    fakeProvider = new FakeProvider();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Tests the create and contains request method.
	 * @throws RequestIDAlreadyExistsException It is thrown if the id already exists.
	 */
	public void testCreateRequest () throws Exception {
		reqManager.createRequest(1234, new FakeConsumer(), "testMe");
		assertTrue (reqManager.containsRequest(1234));
	}
	
	/**
	 * Tests if the RequestIDAlreadyExistsException is being thrown.
	 */
	public void testRequestIDAlreadyExistsException () {
		
		try {
			reqManager.createRequest(1234, null, "testMe");
			reqManager.createRequest(1234, null, "iWillFail");
			fail ("A RequestIDAlreadyExistsException should be thrown.");
		} catch (RequestIDAlreadyExistsException e) {
			// this line has to be executed.
		}
	}
	
	/**
	 * Tests if the requests are being added to <code>RequestManager</code> list.
	 * @throws RequestIDAlreadyExistsException It is thrown if the id already exists.
	 */
	public void testGetAllRequests() throws RequestIDAlreadyExistsException {
		
		assertEquals(0, reqManager.getAllRequests().size());
		
		reqManager.createRequest(1234, null, "testMe");
		reqManager.createRequest(9999, null, "testMeToo");
		reqManager.createRequest(4440, null, "meToo");
		
		assertEquals(3, reqManager.getAllRequests().size());
		
	}
}