package synapse.client.manager;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import synapse.client.FakeFileInfo;
import synapse.common.FakeProvider;

/**
 * Test for the the <code>ReplyManager</code>.
 * 
 * @author <p>Felipe Ribeiro, felipern@lcc.ufcg.edu.br</p>
 * @author <p>Flavio Vinícius, flaviov@lcc.ufcg.edu.br</p>
 */
public class ReplyManagerTest extends TestCase {

	ReplyManager replyManager;
	FakeProvider fakeProvider;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		replyManager = ReplyManager.getInstance();
		fakeProvider = new FakeProvider ();
	}
	
	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		
		ReplyManager.reset();
	}
	
	/**
	 * Tests if a new search is being added.
	 * @throws RequestIDDoesNotExistException
	 * @throws HashDoesNotExistException
	 */
	public void testAddReply() throws RequestIDDoesNotExistException, HashDoesNotExistException {
		replyManager.addReply(1234, fakeProvider, new FakeFileInfo("testMe", "hash", 0));
		assertTrue(replyManager.containsId(1234));
		assertTrue(replyManager.containsHash(1234, "hash"));
		
		// Tests if the search already exists and a new reply is being added.
		replyManager.addReply(1234, fakeProvider, new FakeFileInfo("imNotTheSame", "hash", 0));
		List providers = replyManager.getProviders(1234, "hash");
		assertEquals(2, providers.size());

		Map resultByID = replyManager.getSearchResult();
		Map result = (Map) resultByID.get(new Long(1234));
		assertNotNull(result);
		assertEquals(1, result.keySet().size());
		assertTrue(result.containsKey("hash"));
		
		replyManager.addReply(1234, fakeProvider, new FakeFileInfo("blah", "hash2", 0));
		
		resultByID = replyManager.getSearchResult();
		result = (Map) resultByID.get(new Long(1234));
		assertNotNull(result);
		assertEquals(2, result.keySet().size());
		assertTrue(result.containsKey("hash"));
		assertTrue(result.containsKey("hash2"));
	}
	
	/**
	 * Tests if the RequestIDDoesntExistException is being thrown.
	 */
	public void testRequestIDDoesntExistException () {

		//Tests in the removeReply method
		try {
			replyManager.removeResult(2345);
			fail();
		}
		catch (RequestIDDoesNotExistException e) {
		}
	}
	
	/**
	 * Tests if a search is being removed.
	 * 
	 * @throws RequestIDDoesNotExistException Thrown in case the ID doesnt exist.@throws RequestIDDoesntExistException
	 */
	public void testRemoveReply() throws RequestIDDoesNotExistException {
		replyManager.addReply(1234, fakeProvider, new FakeFileInfo("testMe", "hash", 0));
		replyManager.removeResult(1234);
		assertFalse(replyManager.containsId(1234));
	}
	
	/**
	 * Tests the <code>getProviders</code> method.
	 * 
	 * @throws RequestIDDoesNotExistException
	 * @throws HashDoesNotExistException
	 */
	public void testGetProviders () throws RequestIDDoesNotExistException, HashDoesNotExistException {
		replyManager.addReply(1234, fakeProvider, new FakeFileInfo("testMe", "hash", 0));
		List list = this.replyManager.getProviders(1234, "hash");
		assertEquals(1, list.size());
	}
}