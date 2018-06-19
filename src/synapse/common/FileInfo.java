package synapse.common;

import java.io.File;
import java.io.Serializable;

import synapse.util.HashUtil;

/**
 * Encapsulates the basic information about a file. These object instances
 * will be transfered between the peers.
 * 
 * @author <p>Joao Arthur Brunet Monteiro, jarthur@dsc.ufcg.edu.br</p>
 */
public class FileInfo implements Serializable {
	
    /**
     * The file name.
     */
    private String fileName;

    /**
     * The file hash.
     */
    private String hash;

    /**
     * The file size.
     */
    private long size;

    /**
     * The resource.
     */
	private transient File file;
	
	/**
	 * Constructs a new resource.
	 */
	public FileInfo(File file) {
		this.file = file;
		this.fileName = file.getName();
		/*
		 * This hash creation sometimes takes several minutes
		 * to be concluded.
		 */
		this.hash = HashUtil.createHash(file);
		this.size = file.length();
	}

	/**
	 * Used only by tests.
	 * 
	 * @param fileName The file name.
	 * @param hash The file hash.
	 * @param size The file size.
	 */
	protected FileInfo(String fileName, String hash, long size) {
	    this.fileName = fileName;
	    this.hash = hash;
	    this.size = size;
	}

	/**
	 * Method that returns the fileName of this resource.
	 * @return the fileName of this resource.
	 */
	public String getFileName() {
		return this.fileName;
	}
	
	/**
	 * Method that resturns the hash of the file that is in this resource.
	 * @return the hash of the file that is in this resource.
	 */
	public String getHash() {
		return this.hash;
	}

    /**
     * Returns the file size.
     * 
     * @return The file name.
     */
    public long getSize() {
        return this.size;
    }

	/**
	 * Method that returns the File that is in resource.
	 * @return the File that is in resource.
	 */
	public File getFile() {
		return this.file;
	}

	 /**
     * Compares the specified object with this <code>FileInfo</code> for equality.
     * Returns true if the specified object is also a <code>FileInfo</code>, 
     * the two <code>FileInfo</code>s have the same hash.
     * @param other to be compared for equality with this <code>FileInfo</code>.
     * @return true if the specified Object is equal to this <code>FileInfo</code>.
     */
	public boolean equals(Object other) {
		if (!(other instanceof FileInfo)) {
			return false;
		}
		FileInfo aux = (FileInfo) other;
		return this.getHash().equals(aux.getHash());
	}
	
	/**
     * Method that returns a string representation of the <code>FileInfo</code>.
     * @return A string representation of the this FileInfo.
     */
	public String toString() {
		return "FileName: " + this.getFileName() + " \tSize: " + this.getSize() + " " + " Hash: "+ this.getHash();
	}
}