package synapse.client.manager;

import java.io.File;
import java.rmi.RemoteException;

import junit.framework.TestCase;
import synapse.client.ClientConfig;
import synapse.client.TransferPipeImpl;
import synapse.common.FakeConsumer;
import synapse.common.FileInfo;

/**
 * Class that tests the <code>UploadManager</code>.
 * 
 * @author <p>João Arthur Brunet Monteiro, jarthur@dsc.ufcg.edu.br</p>
 */
public class UploadManagerTest extends TestCase {
    
    /**
     * The object to be tested.
     */
    private UploadManager uploadManager;

    private File file1, file2, file3, file4, file5, file6;
    private FileInfo resource1, resource2, resource3, resource4, resource5, resource6;
	private TransferPipeImpl pipe1, pipe2, pipe3, pipe4, pipe5, pipe6;
	private FakeConsumer fc1, fc2, fc3, fc4, fc5, fc6;
	
    /*
	 * @see TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        this.uploadManager = UploadManager.getInstance();
        fc1 = new FakeConsumer();
        fc2 = new FakeConsumer();
        fc3 = new FakeConsumer();
        fc4 = new FakeConsumer();
        fc5 = new FakeConsumer();
        fc6 = new FakeConsumer();
        
        file1 = new File(ClientConfig.getRootDir() + File.separator + "testFiles/test1.dat"); 
        file2 = new File(ClientConfig.getRootDir() + File.separator + "testFiles/test2.dat");
        file3 = new File(ClientConfig.getRootDir() + File.separator + "testFiles/test3.dat");
        file4 = new File(ClientConfig.getRootDir() + File.separator + "testFiles/test3_hashTest.dat");
        file5 = new File(ClientConfig.getRootDir() + File.separator + "testFiles/emptyFile.dat");
        file6 = new File(ClientConfig.getRootDir() + File.separator + "testFiles/this is a file that was made for testing.dat");
        
        resource1 = new FileInfo(file1);
    	resource2 = new FileInfo(file2);
    	resource3 = new FileInfo(file3);
    	resource4 = new FileInfo(file4);
    	resource5 = new FileInfo(file5);
    	resource6 = new FileInfo(file6);
    	
    	pipe1 = new TransferPipeImpl(resource1);
    	pipe2 = new TransferPipeImpl(resource2);
    	pipe3 = new TransferPipeImpl(resource3);
    	pipe4 = new TransferPipeImpl(resource4);
		pipe5 = new TransferPipeImpl(resource5);
		pipe6 = new TransferPipeImpl(resource6);

    }

    /*
	 * @see TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
    	super.tearDown();
    	
    	UploadManager.reset();
    }

    /**
     * Tests the <code>addUpload()</code> method.
     */
    public void testAddPipe() {
    	assertTrue(this.uploadManager.getAllUploads().isEmpty());
        
    	this.uploadManager.deliverFile(fc1, pipe1);
		this.uploadManager.deliverFile(fc2, pipe2);
		this.uploadManager.deliverFile(fc3, pipe3);
		this.uploadManager.deliverFile(fc4, pipe4);
		this.uploadManager.deliverFile(fc5, pipe5);
		this.uploadManager.deliverFile(fc6, pipe6);
		
		assertTrue(this.uploadManager.getRunningUploads().contains(new Upload(fc1, pipe1)));
		assertFalse(this.uploadManager.getWaitQueue().contains(new Upload(fc1, pipe1)));
		
		assertTrue(this.uploadManager.getRunningUploads().contains(new Upload(fc2, pipe2)));
		assertFalse(this.uploadManager.getWaitQueue().contains(new Upload(fc2, pipe2)));
		
		assertTrue(this.uploadManager.getRunningUploads().contains(new Upload(fc3, pipe3)));
		assertFalse(this.uploadManager.getWaitQueue().contains(new Upload(fc3, pipe3)));
		
		assertTrue(this.uploadManager.getRunningUploads().contains(new Upload(fc4, pipe4)));
		assertFalse(this.uploadManager.getWaitQueue().contains(new Upload(fc4, pipe4)));
		
		assertTrue(this.uploadManager.getRunningUploads().contains(new Upload(fc4, pipe4)));
		assertFalse(this.uploadManager.getWaitQueue().contains(new Upload(fc4, pipe4)));
		
		assertTrue(this.uploadManager.getRunningUploads().contains(new Upload(fc5, pipe5)));
		assertFalse(this.uploadManager.getWaitQueue().contains(new Upload(fc5, pipe5)));
		
		assertFalse(this.uploadManager.getRunningUploads().contains(new Upload(fc6, pipe6)));
		assertTrue(this.uploadManager.getWaitQueue().contains(new Upload(fc6, pipe6)));
				
		fc1.setExpectedHereIsFile(1);
		fc2.setExpectedHereIsFile(1);
		fc3.setExpectedHereIsFile(1);
		fc4.setExpectedHereIsFile(1);
		fc5.setExpectedHereIsFile(1);
		fc6.setExpectedHereIsFile(0);
		
		fc1.verify();
		fc2.verify();
		fc3.verify();
		fc4.verify();
		fc5.verify();
		fc6.verify();
		
		assertTrue(this.uploadManager.getAllUploads().contains(new Upload(fc1, pipe1)));
		assertTrue(this.uploadManager.getAllUploads().contains(new Upload(fc2, pipe2)));
		assertTrue(this.uploadManager.getAllUploads().contains(new Upload(fc3, pipe3)));
		assertTrue(this.uploadManager.getAllUploads().contains(new Upload(fc4, pipe4)));
		assertTrue(this.uploadManager.getAllUploads().contains(new Upload(fc5, pipe5)));
		assertTrue(this.uploadManager.getAllUploads().contains(new Upload(fc6, pipe6)));
    }

    public void testInvalidate() throws Exception {
         
    	this.uploadManager.deliverFile(fc1, pipe1);
		this.uploadManager.deliverFile(fc2, pipe2);
		this.uploadManager.deliverFile(fc3, pipe3);
		this.uploadManager.deliverFile(fc4, pipe4);
		this.uploadManager.deliverFile(fc5, pipe5);
		this.uploadManager.deliverFile(fc6, pipe6);
		
		fc6.setExpectedHereIsFile(0);
		fc6.verify();
		
		assertTrue(1 == this.uploadManager.getWaitQueue().size());
		
//		call the invalidate()
		this.uploadManager.invalidate(pipe1.getHash());
		
		this.uploadManager.invalidate(pipe2.getHash());
		assertTrue(0 == this.uploadManager.getWaitQueue().size());
		assertTrue(4 == this.uploadManager.getRunningUploads().size());
		this.uploadManager.invalidate(pipe3.getHash());
		assertTrue(3 == this.uploadManager.getRunningUploads().size());
		assertTrue(0 == this.uploadManager.getWaitQueue().size());
		
		
		fc6.setExpectedHereIsFile(1);
		fc6.verify();
}

    public void testInvalidateWithAInvalidHash() {
        try {
            uploadManager.invalidate("invalid hash");
            fail("A HashDoesNotExistException should be thrown");
        } catch (HashDoesNotExistException e) {
            // this line have to be executed
        }
    }

    public void testEquals() throws RemoteException {
    	assertFalse(pipe1.equals(pipe2));
    	assertFalse(pipe2.equals(pipe3));
	}
}