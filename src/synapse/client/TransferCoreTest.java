package synapse.client;

import java.io.File;
import java.rmi.RemoteException;

import junit.framework.TestCase;
import synapse.client.manager.DownloadManager;
import synapse.client.manager.HashDoesNotExistException;
import synapse.client.manager.ReplyManager;
import synapse.client.manager.UploadManager;
import synapse.common.FakeConsumer;
import synapse.common.FakeProvider;
import synapse.common.FileInfo;
import synapse.common.TransferPipe;
import synapse.util.HashUtil;


/**
 * Tests for <code>TransferCore</code>.
 * 
 * @author <p>Felipe Ribeiro, felipern@lcc.ufcg.edu.br</p>
 * @author <p>Flavio Vin?cius, flaviov@lcc.ufcg.edu.br</p>
 */
public class TransferCoreTest extends TestCase {

	private TransferCore transferCore;
	private FileSource fileSrc;
	private ReplyManager repManager;
	private UploadManager upManager;
	private DownloadManager downManager;
	private FakeProvider fakeProvider;
	private FakeConsumer fakeConsumer;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		this.repManager = ReplyManager.getInstance();
		this.upManager = UploadManager.getInstance();
		this.downManager = DownloadManager.getInstance();
		this.fileSrc = new FileSource ();
		this.transferCore = new TransferCore(repManager, upManager, downManager, fileSrc);
		this.fakeProvider = new FakeProvider ();
		this.fakeConsumer = new FakeConsumer ();		
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		this.repManager = null;
		this.upManager = null;
		this.fileSrc = null;
		this.transferCore = null;
		this.fakeProvider = null;
		this.fakeConsumer = null;
		
		UploadManager.reset();
		ReplyManager.reset();
		DownloadManager.reset();
	}

	/**
	 * Tests local file search.
	 * 
	 * @throws RemoteException thrown in case a remote exception occurs.
	 */
	public void testSearchFilelongConsumerString() throws RemoteException {
		this.fileSrc.putFile(new File(ClientConfig.getRootDir() + File.separator + "testFiles" + File.separator + "test1.dat"));
		this.fileSrc.putFile(new File(ClientConfig.getRootDir() + File.separator + "testFiles" + File.separator + "test2.dat"));
		this.fakeConsumer.setExpectedFileWasFound(2);
		this.transferCore.searchFile(1234, this.fakeConsumer, "test");
		this.fakeConsumer.verify();
		
		//tests for only one file to be found
		this.fakeConsumer.reset();
		this.fakeConsumer.setExpectedFileWasFound(1);
		this.transferCore.searchFile(1234, this.fakeConsumer, "1");
		this.fakeConsumer.verify();
		
		//tests for a file that will not be found
		this.fakeConsumer.reset();
		this.fakeConsumer.setExpectedFileWasFound(0);
		this.transferCore.searchFile(1234, this.fakeConsumer, "lala");
		this.fakeConsumer.verify();
	}

	/**
	 * Tests the serach method using the hash file.
	 */
	public void testSerachForHash() {
	    File file1 = new File(ClientConfig.getRootDir() + File.separator + "testFiles" + File.separator + "test2.dat");
	    this.fileSrc.putFile(file1);

	    this.fakeConsumer.setExpectedHereIsFile(0);
	    this.transferCore.searchFile("invalid hash", this.fakeConsumer);
	    this.fakeConsumer.verify();
	    
	    this.fakeConsumer.setExpectedHereIsFile(1);
	    this.transferCore.searchFile(HashUtil.createHash(file1), this.fakeConsumer);
	    this.fakeConsumer.verify();
	}

	/**
	 * Tests the <code>fileWasFound</code> method.
	 * 
	 * @throws RemoteException thrown in case a remote exception occurs.
	 */
	public void testFileWasFound () throws RemoteException {
		this.transferCore.fileWasFound(1234, fakeProvider, new FakeFileInfo("none", "bah", 0));
		assertTrue (this.repManager.containsId(1234));
	}
	
	/**
	 * Tests the <code>getFile</code> method.
	 * 
	 * @throws RemoteException thrown in case a remote exception occurs.
	 */
	public void testGetFile() throws RemoteException {
		File file = new File (ClientConfig.getRootDir() + File.separator + "testFiles" + File.separator + "test1.dat");
		this.fileSrc.putFile(file);
		this.fakeConsumer.setExpectedHereIsFile(1);
		this.transferCore.getFile(HashUtil.createHash(file), this.fakeConsumer);
		assertEquals(1, this.upManager.getAllUploads().size());
		this.fakeConsumer.verify();

	    this.fakeConsumer.reset();
	    this.transferCore.getFile("inexistentHash", this.fakeConsumer);
	    this.fakeConsumer.verify();
	}
	
	/**
	 * Tests the <code>hereIsFile</code> method.
	 */
	public void testHereIsFile() throws RemoteException, HashDoesNotExistException {
		TransferPipe pipe = new TransferPipeImpl (new FileInfo(new File("testFiles/test1.dat")));
		this.downManager.addSolicitation(pipe.getHash());
		this.transferCore.hereIsFile(pipe);
		assertEquals(1, this.downManager.getNumberOfDownloads());
		downManager.cancelDownload(pipe.getHash());
	}
}