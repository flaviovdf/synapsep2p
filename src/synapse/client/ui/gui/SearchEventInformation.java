package synapse.client.ui.gui;

/**
 * Search event used to send a search from <code>SearchPanel</code> to <code>SynapseGUI</code>.
 * 
 * @author Flavio Vinicius Diniz de Figueiredo
 */
public class SearchEventInformation {
	
	/**
	 * Key used to search.
	 */
	private String searchKey;
	
	/**
	 * Identifies what must be done.
	 */
	private int whatToDo;
	
	/**
	 * Make a new search.
	 */
	public static final int NEW_SEARCH = 1;
	
	/**
	 * Find more sources.
	 */
	public static final int MORE_SOURCES= 2;
	
	/**
	 * Creates a new SearchEvent.
	 * 
	 * @param searchKey Key that was asked to be searched in the Synapse Network.
	 */
	public SearchEventInformation (String searchKey, int whatToDo) {
		this.searchKey = searchKey;
		this.whatToDo = whatToDo;
	}
	
	/**
	 * @return Search key.
	 */
	public String searchKey () {
		return this.searchKey;
	}
	
	/**
	 * @return What is to be done.
	 */
	public int getWhatToDo() {
		return this.whatToDo;
	}
}
