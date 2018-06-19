package synapse.server;

import junit.framework.TestCase;
import synapse.common.FakeClient;
import synapse.common.FakeProvider;
import synapse.common.RequestIDAlreadyExistsException;

/**
 * Tests for the ConnectionManager class.
 *
 * @author <p>Joao Arthur Brunet Monteiro, joaoabm@lcc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
public class ConnectionManagerTest extends TestCase {
	
	/**
	 * This is the tested object.
	 */
	private ConnectionManager connectionManager;
	
	/**
	 * RequestManager that is the one wich exists inside the conection manager.
	 */
	private RequestManager reqManager;
	
	/**
	 * A fake to test method invocation.
	 */
	private FakeClient fakeClient;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		this.reqManager = new RequestManager();
		this.connectionManager = new ConnectionManager(this.reqManager);
		this.connectionManager.config(new FakeProvider());
		this.fakeClient = new FakeClient();
		
		ServerConfig.setProperty(ServerConfig.MAX_RETRIES, "0");
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		
		this.fakeClient.reset();
	}

	/**
	 * Tests identifing a new peer.
	 */
	public void testIdentify() {
		this.connectionManager.identify(this.fakeClient);
		assertTrue(this.connectionManager.containsClient(this.fakeClient));
	}

	/**
	 * Tests the searching files.
	 * 
	 * @throws ClientNotIdentifiedException Thrown if the client is not identified
	 */
	public void testSearchFile() throws Exception {
	    //Tests if the request is being entered in the request manager
	    this.connectionManager.identify(this.fakeClient);
	    this.connectionManager.searchFile(1234, this.fakeClient, "testMe");
		assertTrue(this.reqManager.containsRequest(1234));
		this.fakeClient.verify();
		
		//Tests if the client isn't identified can create a request
		connectionManager.searchFile(1111, new FakeClient(), "testMe");
		assertFalse(this.reqManager.containsRequest(1111));
		this.fakeClient.verify();
		
		//Tests if the searchFile are being invoked
		FakeClient newFakeClient = new FakeClient();
		this.fakeClient.setExpectedSearchFileVoid(1);
		connectionManager.identify(newFakeClient);
		connectionManager.searchFile(2222, newFakeClient, "testMe");

		this.fakeClient.verify();
		newFakeClient.verify();
	}

	/**
	 * Tests if the SearchOldRequests is being correctly used.
	 * 
	 * @throws RequestIDAlreadyExistsException If any problem exists.
	 */
	public void testAnswerOldRequests () throws RequestIDAlreadyExistsException {

	    //Tests if the maxRetries is being decreased.
		this.reqManager.createRequest(1234, null, "testMe");
		Request req = this.reqManager.getRequest(1234);
		int initialRetries = 5;
		req.setRetries(initialRetries);
		this.connectionManager.searchOldRequest(this.fakeClient);
	    assertEquals(initialRetries - 1, req.getRetries());
	    
	    //Tests if the request is being removed.
	    for (int i = 0; i < initialRetries; i++) {
	        this.connectionManager.searchOldRequest(this.fakeClient);
	    }
	    
	    assertFalse(this.reqManager.containsRequest(1234));
	}

	/**
	 * Tests if the SearchOldRequests is being correctly used.
	 * 
	 * @throws Exception If any problem exists.
	 */
	public void testAnswerOldRequests2() throws Exception {
	    this.fakeClient.setExpectedSearchFileVoid(0);

	    this.reqManager.createRequest(1234, this.fakeClient, "testeMe");
	    assertEquals(0, this.reqManager.getRequest(1234).getRetries());
	    this.connectionManager.searchOldRequest(new FakeProvider());
	    assertFalse(this.reqManager.containsRequest(1234));
	    this.fakeClient.verify();
	}
}