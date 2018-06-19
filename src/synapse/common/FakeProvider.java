package synapse.common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;



/**
 * A fake provider used by tests.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class FakeProvider extends UnicastRemoteObject implements Provider {

    private int expectedIdentify;
    private int expectedSearchFileLong;
	private int expectedSearchFileVoid;
	private int expectedSearchFileHash;
	private int expectedSearchForCommunity;
	private int expectedGetFile;

	private int identifyCount;
    private int searchFileLongCount;
	private int searchFileVoidCount;
	private int searchFileHashCount;
	private int searchForCommunityCount;
	private int getFileCount;

	public FakeProvider() throws RemoteException {
	    this.reset();
	}

	public void reset() {
	    this.expectedIdentify = 0;
	    this.expectedSearchFileLong = 0;
	    this.expectedSearchFileVoid = 0;
	    this.expectedSearchFileHash = 0;
	    this.expectedSearchForCommunity = 0;
	    this.expectedGetFile = 0;

	    this.identifyCount = 0;
	    this.searchFileLongCount = 0;
	    this.searchFileVoidCount = 0;
	    this.searchFileHashCount = 0;
	    this.searchForCommunityCount = 0;
	    this.getFileCount = 0;
	}

	public void verify () {

	    if (this.identifyCount != this.expectedIdentify) {
            throw new RuntimeException( "identify() was expected to be called " + expectedIdentify + " times but was " + identifyCount);
        }

	    if (this.searchFileLongCount != this.expectedSearchFileLong) {
            throw new RuntimeException( "searchFile() was expected to be called " + expectedSearchFileLong + " times but was " + searchFileLongCount);
        }

	    if (this.searchFileVoidCount != this.expectedSearchFileVoid) {
            throw new RuntimeException( "searchFile() was expected to be called " + expectedSearchFileVoid + " times but was " + searchFileVoidCount);
        }

	    if (this.searchFileHashCount != this.expectedSearchFileHash) {
            throw new RuntimeException( "searchFile() was expected to be called " + expectedSearchFileHash + " times but was " + searchFileHashCount);
        }

	    if (this.searchForCommunityCount != this.expectedSearchForCommunity) {
            throw new RuntimeException( "searchForCommunity() was expected to be called " + expectedSearchForCommunity + " times but was " + searchForCommunityCount);
        }

	    if (this.getFileCount != this.expectedGetFile) {
            throw new RuntimeException( "getFile() was expected to be called " + expectedGetFile + " times but was " + getFileCount);
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
    public void searchFile(String hash, Consumer consumer) {
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

}
