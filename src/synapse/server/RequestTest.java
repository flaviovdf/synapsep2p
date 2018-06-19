package synapse.server;

import junit.framework.TestCase;
import synapse.common.FakeConsumer;

/**
 * Test for Request.
 * 
 * @author <p>Joao Arthur Brunet Monteiro, joaoabm@lcc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
public class RequestTest extends TestCase {
	
	/**
	 * Objected that represent the tested class.
	 */
	private Request request;
	
	/**
	 * Fake consumer for some methods invocation;
	 */
	private FakeConsumer fakeConsumer;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		this.request = new Request(1234, this.fakeConsumer, "firstTest"); 
	}
	
	/**
	 * Test the gets methods.
	 */
	public void testRequest() {
		assertEquals(1234, this.request.getId());
		assertEquals("firstTest", this.request.getFileName());
		assertEquals(fakeConsumer, this.request.getConsumer());
	}
	
	/**
	 * Verifies if the <code>decreaseRetries()</code> is working correctly.
	 */
	public void testDecrease() {
	    int initialRetries = 50;
	    this.request.setRetries(initialRetries);

		assertEquals(initialRetries, this.request.getRetries());
		for (int i = 0; i < 10; i++) {
			this.request.decreaseRetries();
		}
		assertEquals(initialRetries - 10, this.request.getRetries());
	}

}
