package synapse.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import synapse.common.Consumer;
import synapse.common.RequestIDAlreadyExistsException;

/**
 * Class that manages all the requests sent to the server.
 * 
 * @author <p>Joao Arthur Brunet Monteiro, jarthur@dsc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
public class RequestManager {

	/**
	 * Represents the requests that are waiting for answer.
	 */
	private HashMap requests;
	
	/**
	 * Creates a new RequestManager.
	 */
	public RequestManager () {
		this.requests = new HashMap ();
	}

	/**
	 * Adds a new request to the requests list.
	 * @param id Request id.
	 * @param fileName Requested file name.
	 * @param consumer The consumer that requested the search.
	 * 
	 * @throws RequestIDAlreadyExistsException It is thrown if the id already exists.
	 */
	public void createRequest (long id, Consumer consumer, String fileName) throws RequestIDAlreadyExistsException {
		if (requests.containsKey(new Long (id))) {
			throw new RequestIDAlreadyExistsException (id);
		}
		requests.put (new Long (id), new Request (id, consumer, fileName));
	}
	
	/**
	 * Method that returns all the requests in this manager.
	 * 
	 * @return A collection containing all requests.
	 */
	public Collection getAllRequests() {
		return new LinkedList(this.requests.values());
	}
	
	/**
	 * Sees if the request still exists in the set.
	 * 
	 * @param id The requests id.
	 * @return True if it still exists.
	 */
	public boolean containsRequest (long id) {
		return requests.containsKey(new Long(id));
	}
	
	/**
	 * Returns a request according to its id.
	 * 
	 * @param id The request id.
	 * @return The request.
	 */
	protected Request getRequest (long id) {
	    return (Request)requests.get(new Long(id));
	}

    /**
     * Removes a request according to its id.
     * 
     * @param id The request id.
     */
    public void remove(long id) {
       requests.remove(new Long(id)); 
    }
}