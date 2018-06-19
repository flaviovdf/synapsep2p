package synapse.client;

import synapse.common.FileInfo;
import synapse.common.OperationNotSupportedException;
import synapse.common.TransferPipe;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.ourgrid.yal.Logger;

/**
 * This class abstracts the way used to transfer the file.
 * 
 * @author <p>Vinicius Ferraz C. Florentino, vinicius.ferraz@gmail.com</p>
 * @author <p>Thiago Emmanuel, thiago.manel@gmail.com</p>
 */
public class TransferPipeImpl extends UnicastRemoteObject implements TransferPipe {

    /**
     * When it's true, the <code>getFile()</code> method
     * throws an <code>OperationNotSupportedException</code>.
     */
	private boolean invalidateMark;

	/**
	 * The informations about the file.
	 */
	private FileInfo fileInfo;

	/**
	 * Stream used to read the file bytes.
	 */
	private RandomAccessFile input;
	
	private Logger logger;
	
	/**
	 * Creates a new TransferPipeImpl.
	 * @param fileInfo The object that contains the informations about the file.
	 */
	public TransferPipeImpl(FileInfo fileInfo) throws RemoteException {
		
	    this.fileInfo = fileInfo;
		invalidateMark = false;
		this.logger = Logger.getInstance();

		try {
			input = new RandomAccessFile(this.fileInfo.getFile(), "rw");
			//
			
		} catch (FileNotFoundException e) {
			
			logger.error(this.getClass().getName()+".TransferPipeImpl(FileInfo fileInfo)",
					"FileNotFoundException occur, pipe will be invalidated" );
			this.invalidate();
		}
	}

	/* (non-Javadoc)
     * @see synapse.common.TransferPipe#getName()
     */
	public String getFileName() throws RemoteException{
		return this.fileInfo.getFileName();
	}

	/* (non-Javadoc)
     * @see synapse.common.TransferPipe#getSize()
     */
	public long getSize() throws RemoteException{
		return this.fileInfo.getSize();
	}

	/**
	 * This method turns the TransferPipe to a invalide form. If a TransferPipe
	 * is a invalide form, the consumer can't get information.	 
	 */
	public void  invalidate(){	
		//naum foram feitos testes deste metodo e das acoes que ele ocasiona, 
		//porque logo serao feitas mudancas, por exemplo o metodo getFile
		//podera lancar uma Exception se o invalidateMark estiver setado como 
		//true
		this.invalidateMark = true;
	}
	
	/**
	 * This method returns a flag, to indicate the TransferPipe's status.
	 * If the  invalidateMark is true, this TransferPipe can't be transfer data 
	 * @return The invalidateMark
	 */
	public boolean isInvalidated(){		
		return this.invalidateMark;
	}
	
	/* (non-Javadoc)
     * @see synapse.common.TransferPipe#getHash()
     */
	public String getHash() throws RemoteException{
		return this.fileInfo.getHash();
	}
	
	/* (non-Javadoc)
     * @see synapse.common.TransferPipe#getFile(int i, int len)
     */
	public byte[] getFile(long i, long len) throws RemoteException {
	    if (this.isInvalidated()) {
	        throw new OperationNotSupportedException("The TransferPipe was invalidated");
	    }

	    byte[] array = new byte[ (int) len ];

		try {
			input.seek(i);
			input.read(array, 0, (int) len);
		} catch (IOException e) {
			throw new RemoteException("Could not read the remote file");
		}

		return array;
	}

    /**
     * Method that returns a string representation of the <code>TransferPipe</code>.
     * @return A string representation of this pipe.
     */
    public String toString() {
        try {
            return "File Name: " + this.getFileName() + " Size: " + this.getSize()+ " Hash: " + this.getHash();
        } catch (RemoteException e) {
            return "(invalid TransferPipe)";
        }
    }

}

