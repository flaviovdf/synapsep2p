package synapse.client.manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import synapse.common.FileInfo;
import synapse.common.Provider;


/**
 * This class manages the search results.
 * 
 * @author <p>Felipe Ribeiro, felipern@lcc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius, flaviov@lcc.ufcg.edu.br</p>
 */
public class ReplyManager {
	
	/**
	 * Map of resultByID, contains de ID as Key and a SearchResult as Value.
	 */
	private Map resultByID;
	
	/**
	 * ReplyManager used for Singleton Design Pattern.
	 */
	private static ReplyManager uniqueInstance;

	/**
	 * Creates a new <code>ReplyManager</code>.
	 */
	private ReplyManager() {
		resultByID = new HashMap();
	}
	
	/**
	 * Gets the unique instance of the object.
	 * 
	 * @return The <code>ReplyManager</code> instance.
	 */
	public static ReplyManager getInstance() {
		
        if (uniqueInstance == null) {
            uniqueInstance = new ReplyManager();
        }
        return uniqueInstance;

	}

	public static void reset() {
	    uniqueInstance = null;
	}

	public synchronized void addNewSearch(long id) {
		this.resultByID.put(new Long(id), null);
	}

	/**
	 * Adds an obtained result.
	 * 
	 * @param id Search ID
	 * @param provider Provider who has the file
	 * @param fileInfo A file information.
	 */
	public synchronized void addReply(long id, Provider provider, FileInfo fileInfo) {
		if(resultByID.get(new Long(id)) != null) { //If the search has already returned any result
			Map result = (Map) resultByID.get(new Long(id));
			if(result.containsKey(fileInfo.getHash())) { //If the hash of the file has been already found in another provider
				Reply reply = (Reply) result.get(fileInfo.getHash());
				reply.providers.add(provider);
			}
			else {
				Reply newReply = new Reply(fileInfo, provider);
				result.put(fileInfo.getHash(), newReply);
			}
		}
		else{
			Map newResult = new HashMap();
			Reply newReply = new Reply(fileInfo, provider);
			newResult.put(fileInfo.getHash(), newReply);
			resultByID.put(new Long(id), newResult);
		}
	}

	/**
	 * Removes the results from the manager.
	 * 
	 * @param id Id of the search.
	 * @throws RequestIDDoesNotExistException Thrown in case the ID doesnt exist.
	 */
	public synchronized void removeResult(long id) throws RequestIDDoesNotExistException {
		if (!this.containsId(id)) {
			throw new RequestIDDoesNotExistException(id);
		}
		resultByID.remove(new Long(id));
	}

	/**
	 * Returns a <code>Map</code> that represents a search result according to the id.
	 * 
	 * @return The result map.
	 */
	public synchronized Map getSearchResult() {
		return new HashMap(resultByID);
	}

	/**
	 * Return the Replies of a search, used in tests.
	 * 
	 * @param id Search ID
	 * @param hash File's hash code.
	 * @return The reply set.
	 * @throws HashDoesNotExistException
	 * @throws RequestIDDoesNotExistException
	 */
	private Reply getReply(long id, String hash) throws RequestIDDoesNotExistException, HashDoesNotExistException {
		if (!this.containsHash(id, hash)) {
			throw new HashDoesNotExistException(hash);
		}
		HashMap resultMap = (HashMap)resultByID.get(new Long (id));
		Reply replySet = (Reply)resultMap.get(hash);
		return replySet;
	}

	/**
	 * Returns the file info object containing the file information.
	 * 
	 * @param id Search ID
	 * @param hash File's hash code.
	 * @return The file information object.
	 * @throws HashDoesNotExistException
	 * @throws RequestIDDoesNotExistException
	 */
	public synchronized FileInfo getFileInfo(long id, String hash) throws RequestIDDoesNotExistException, HashDoesNotExistException {
		Reply reply = this.getReply(id, hash);

		return reply.fileInfo;
	}

	/**
	 * Gets <code>Providers</code> for a certain search.
	 * 
	 * @param id Search ID
	 * @param hash File's hash code.
	 * @return The providers list.
	 * @throws HashDoesNotExistException If the hash does not exist.
	 * @throws RequestIDDoesNotExistException If the request ID does not exist.
	 */
	public synchronized List getProviders (long id, String hash) throws RequestIDDoesNotExistException, HashDoesNotExistException {
		Reply reply = this.getReply(id, hash);

		return new LinkedList(reply.providers);
	}

	/**
	 * Checks if the Search ID exists, used in tests.
	 * 
	 * @param id ID to be verified.
	 * @return True if exists.
	 */
	public synchronized boolean containsId (long id) {
		return resultByID.containsKey(new Long (id));
	}
	
	/**
	 * Checks if a Hash exists in the result HashMap.
	 * 
	 * @param id The ID of the search.
	 * @param hash The hash that was found.
	 * @return True if exists.
	 * @throws RequestIDDoesNotExistException
	 */
	public synchronized boolean containsHash (long id, String hash) throws RequestIDDoesNotExistException {
		if (!resultByID.containsKey(new Long(id))) {
			 throw new RequestIDDoesNotExistException (id);
		}
		HashMap resultMap = (HashMap)resultByID.get(new Long (id));
		if (resultMap == null) {
			 return false;
		}
		return resultMap.containsKey(hash);
	}

	/**
	 * Represents a reply obtained when a search is executed.
	 * 
	 * @author <p>Felipe Ribeiro, felipern@lcc.ufcg.edu.br</p>
	 * @author <p>Flavio Vin?cius, flaviov@lcc.ufcg.edu.br</p>
	 */
	private class Reply {
		
		/**
		 * The file info object.
		 */
		public FileInfo fileInfo;
		
		/**
		 * Providers list who has the file.
		 */
		public List providers;

		/**
		 * Creates a new Reply
		 * @param fileInfo
		 * @param provider
		 */
		public Reply(FileInfo fileInfo, Provider provider) {
			this.providers = new LinkedList();
			
			this.fileInfo = fileInfo;
			this.providers.add(provider);
		}

		public String toString() {
			return "(Seeds: " + providers.size() + ") " + fileInfo;
		}
	}
}