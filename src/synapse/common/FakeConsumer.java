package synapse.common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;




/**
 * A fake consumer used by tests.
 * 
 * @author <p>Joao Arthur Brunet Monteiro, joaoabm@lcc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
public class FakeConsumer extends UnicastRemoteObject implements Consumer {

	private int expectedFileWasFound;
	private int expectedHereIsFile;
	private int expectedPing;

	private int fileWasFoundCount;
	private int hereIsFileCount;
	private int pingCount;

	public FakeConsumer() throws RemoteException {
	    this.reset();
	}

	public void reset() {
	    this.expectedFileWasFound = 0;
	    this.expectedHereIsFile = 0;
	    this.expectedPing = 0;

	    this.fileWasFoundCount = 0;
	    this.hereIsFileCount = 0;
	    this.pingCount = 0;
	}

	public void verify () {

	    if (this.fileWasFoundCount != this.expectedFileWasFound) {
            throw new RuntimeException( "fileWasFound() was expected to be called " + this.expectedFileWasFound + " times but was " + this.fileWasFoundCount);
        }

	    if (this.hereIsFileCount != this.expectedHereIsFile) {
            throw new RuntimeException( "hereIsFile() was expected to be called " + this.expectedHereIsFile + " times but was " + this.hereIsFileCount);
        }

	    if (this.pingCount != this.expectedPing) {
            throw new RuntimeException( "ping() was expected to be called " + this.expectedPing + " times but was " + this.pingCount);
        }
	}

	public void setExpectedFileWasFound(int calls) {
		this.expectedFileWasFound = calls;
	}

	public void setExpectedHereIsFile(int calls) {
		this.expectedHereIsFile = calls;
	}

	public void setExpectedPing(int calls) {
		this.expectedPing = calls;
	}

    /* (non-Javadoc)
     * @see synapse.common.Consumer#fileWasFound(synapse.common.Provider, java.lang.String, java.lang.String)
     */
    public void fileWasFound(long id, Provider provider, FileInfo fileInfo) {
        this.fileWasFoundCount++;
    }

    /* (non-Javadoc)
     * @see synapse.common.Consumer#hereIsFile(synapse.common.TransferPipe)
     */
    public void hereIsFile(TransferPipe pipe) {
        this.hereIsFileCount++;   
    }

    /* (non-Javadoc)
     * @see synapse.common.Consumer#ping()
     */
    public void ping() throws RemoteException {
        this.pingCount++;
    }

}