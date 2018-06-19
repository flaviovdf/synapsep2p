package synapse.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface should be implemented by the classes that gives access
 * to a <code>FileInfo</code>. It abstracts how the user reads a byte
 * from the file to be transfered.
 * 
 * @author <p>Vinicius Ferraz C. Florentino, vinicius.ferraz@gmail.com</p>
 * @author <p>Thiago Emmanoel, thiago.manel@gmail.com</p>
 */

public interface TransferPipe extends Remote {

	/**
	 * Returns the file size to be transfered.
	 * @return File size to be transfered.
	 * @throws RemoteException Thrown in case a remote problem occurs.
	 */
	public long getSize() throws RemoteException;
	
	/**
	 * Reads up to len bytes of data from this file
	 * into an array of bytes. This method blocks until
	 * at least one byte of input is available.
	 * 
	 * @param i The start offset of the data.
	 * @param len The maximum number of bytes read.
	 * @return The buffer into which the data is read.
	 * @throws RemoteException Thrown in case a remote problem occurs.
	 */
	public byte[] getFile(long i, long len) throws RemoteException;
	
	/**
	 * Gets the name of the file to be transfered.
	 * @return Name of the file.
	 * @throws RemoteException Thrown in case a remote problem occurs.
	 */
	public String getFileName() throws RemoteException;
	
	/**
	 * Gets the hash of the file to be transfered.
	 * @return Hash code of the file.
	 * @throws RemoteException Thrown in case a remote problem occurs.
	 */
	public String getHash() throws RemoteException;

}

