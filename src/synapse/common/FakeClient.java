package synapse.common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 * A fake client object.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class FakeClient extends UnicastRemoteObject implements Provider, Consumer {

    private int expectedIdentify;
    private int expectedSearchFileLong;
	private int expectedSearchFileVoid;
	private int expectedSearchFileHash;
	private int expectedSearchForCommunity;
	private int expectedGetFile;
	private int expectedFileWasFound;
	private int expectedHereIsFile;
	private int expectedPing;

	private int identifyCount;
	private int searchFileLongCount;
	private int searchFileVoidCount;
	private int searchFileHashCount;
	private int searchForCommunityCount;
	private int getFileCount;
	private int fileWasFoundCount;
	private int hereIsFileCount;
	private int pingCount;

	public FakeClient() throws RemoteException {
	    this.reset();
	}

	public void reset() {
	    this.expectedIdentify = 0;
	    this.expectedSearchFileLong = 0;
	    this.expectedSearchFileVoid = 0;
	    this.expectedSearchFileHash = 0;
	    this.expectedSearchForCommunity = 0;
	    this.expectedGetFile = 0;
	    this.expectedFileWasFound = 0;
	    this.expectedHereIsFile = 0;
	    this.expectedPing = 0;

	    this.identifyCount = 0;
	    this.searchFileLongCount = 0;
	    this.searchFileVoidCount = 0;
	    this.searchFileHashCount = 0;
	    this.searchForCommunityCount = 0;
	    this.getFileCount = 0;
	    this.fileWasFoundCount = 0;
	    this.hereIsFileCount = 0;
	    this.pingCount = 0;
	}

	public void verify () {

	    if (this.identifyCount != this.expectedIdentify) {
            throw new RuntimeException( "identify() was expected to be called " + this.expectedIdentify + " times but was " + this.identifyCount);
        }

	    if (this.searchFileLongCount != this.expectedSearchFileLong) {
            throw new RuntimeException( "searchFile() was expected to be called " + this.expectedSearchFileLong + " times but was " + this.searchFileLongCount);
        }

	    if (this.searchFileVoidCount != this.expectedSearchFileVoid) {
            throw new RuntimeException( "searchFile() was expected to be called " + this.expectedSearchFileVoid + " times but was " + this.searchFileVoidCount);
        }

	    if (this.searchFileHashCount != this.expectedSearchFileHash) {
            throw new RuntimeException( "searchFile() was expected to be called " + this.expectedSearchFileHash + " times but was " + this.searchFileHashCount);
        }

	    if (this.searchForCommunityCount != this.expectedSearchForCommunity) {
            throw new RuntimeException( "searchForCommunity() was expected to be called " + this.expectedSearchForCommunity + " times but was " + this.searchForCommunityCount);
        }

	    if (this.getFileCount != this.expectedGetFile) {
            throw new RuntimeException( "getFile() was expected to be called " + this.expectedGetFile + " times but was " + this.getFileCount);
        }

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


	public void setExpectedIdentify(int calls) {
		this.expectedIdentify = calls;
	}

	public void setExpectedSearchFileLong(int calls) {
		this.expectedSearchFileLong = calls;
	}

	public void setExpectedSearchFileVoid(int calls) {
		this.expectedSearchFileVoid = calls;
	}

	public void setExpectedSearchFileHash(int calls) {
		this.expectedSearchFileHash = calls;
	}

	public void setExpectedSearchForCommunity(int calls) {
		this.expectedSearchForCommunity = calls;
	}

	public void setExpectedGetFile(int calls) {
		this.expectedGetFile = calls;
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
     * @see synapse.common.Provider#identify(synapse.common.Provider)
     */
    public void identify(Provider provider) {
        this.identifyCount++;
    }

    /* (non-Javadoc)
     * @see synapse.common.Provider#searchFile(synapse.common.Consumer, java.lang.String)
     */
    public long searchFile(Consumer consumer, String fileName) {
        this.searchFileLongCount++;
        
        return 0;
    }

    /* (non-Javadoc)
     * @see synapse.common.Provider#searchFile(long, synapse.common.Consumer, java.lang.String)
     */
    public void searchFile(long id, Consumer consumer, String fileName) {
        this.searchFileVoidCount++;
    }

    /* (non-Javadoc)
     * @see synapse.common.Provider#searchFile(java.lang.String, synapse.common.Consumer)
     */
    public void searchFile(String hash, Consumer consumer) throws RemoteException {
        this.searchFileHashCount++;
    }

	/* (non-Javadoc)
	 * @see synapse.common.Provider#searchForCommunity(long, synapse.common.Consumer, java.lang.String)
	 */
	public void searchForCommunity(long id, Consumer consumer, String fileName) throws RemoteException {
		this.searchForCommunityCount++;
	}

    /* (non-Javadoc)
     * @see synapse.common.Provider#getFile(java.lang.String)
     */
    public void getFile(String hash, Consumer consumer) {
        this.getFileCount++;
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