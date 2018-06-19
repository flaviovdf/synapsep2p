package synapse.server;

import synapse.common.Consumer;

/**
 * Class that represents a request made by a consumer.
 * 
 * @author <p>Joao Arthur Brunet Monteiro, joaoabm@lcc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
class Request {
	
	/**
	 * ID of the search.
	 */
	private long id;
	
	/**
	 * Person who made the search.
	 */
	private Consumer consumer;
	
	/**
	 * Name of file that is being searched.
	 */
	private String fileName;

    /**
	 * Represents the number of retries that the requests
	 * will be submited when a new <code>Provider</code> identifies.
	 */
	private int retries;
	
	/**
	 * Creates a new request.
	 */
	public Request (long id, Consumer consumer, String fileName) {
		this.id = id;
		this.consumer = consumer;
		this.fileName = fileName;
		this.setRetries(Integer.parseInt(ServerConfig.getMaxRetries()));
	}

	/**
	 * Sets the max retries number. It's used to perform the search
	 * about <code>retries</code> more times.
	 * 
	 * @param value The value.
	 */
	public void setRetries(int value) {
	    this.retries = value;
	}

	/**
	 * Returns the max retries value.
	 * 
	 * @return The max retries value.
	 */
	public int getRetries() {
	    return this.retries;
	}

	/**
	 * @return The request id.
	 */
	public long getId () {
		return this.id;
	}
	
	/**
	 * @return The file name of the request.
	 */
	public String getFileName () {
		return this.fileName;
	}
	
	/**
	 * @return The consumer that requested the file.
	 */
	public Consumer getConsumer () {
		return this.consumer;
	}

	/**
	 * Decreases the number of max retries.
	 */
	public void decreaseRetries () {
		this.retries--;
	}
}