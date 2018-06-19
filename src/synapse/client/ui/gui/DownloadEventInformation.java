package synapse.client.ui.gui;

/**
 * Class that stores the download information
 * 
 * @author Felipe Ribeiro N. Barbosa
 */
class DownloadEventInformation{
	/**
	 * Hash of the file.
	 */
	private String hash;
	
	/**
	 * Id of the search.
	 */
	private Long id;
	
	/**
	 * Name of the file.
	 */
	private String fileName;
	
	/**
	 * Creates a new DownloadEventInformation.
	 * 
	 * @param hash
	 * @param id
	 * @param fileName
	 */
	public DownloadEventInformation(String hash, Long id,String fileName){
		this.hash = hash;
		this.id=id;
		this.fileName=fileName;
	}

	/**
	 * @return Returns the fileName.
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @return Returns the hash.
	 */
	public String getHash() {
		return hash;
	}
	/**
	 * @return Returns the id.
	 */
	public Long getId() {
		return id;
	}
}
