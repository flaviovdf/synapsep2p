package synapse.client.manager;

import java.io.File;

import junit.framework.TestCase;
import synapse.client.ClientConfig;
import synapse.client.TransferPipeImpl;
import synapse.common.FileInfo;
import synapse.common.TransferPipe;

/**
 * Test for AdvancedDownload
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class AdvancedDownloadTest extends TestCase {

    /**
     * Object to be tested.
     */
    private AdvancedDownload download;

    private File file1;

    private File file2;

    private TransferPipe pipe1;

    private TransferPipe pipe2;

    private TransferPipe pipe2Copy1;

    private TransferPipe pipe2Copy2;
    
    private TransferPipe pipe2Copy3;
    
    private TransferPipe pipe2Copy4;
    
    private TransferPipe pipe2Copy5;
    
    private TransferPipe pipe2Copy6;
    
    private TransferPipe pipe2Copy7;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        ClientConfig.setProperty(ClientConfig.SHARED_FOLDER, ClientConfig.getTestFolder());

        this.file1 = new File(ClientConfig.getRootDir() + File.separator + "testFiles" + File.separator + "test1.dat");

        this.file2 = new File(ClientConfig.getRootDir() + File.separator + "testFiles" + File.separator + "test2.dat");
        //this.file2 = new File("D:/Videos/Filmes/Shrek/Shrek (FR).avi");
        //this.file2 = new File("D:/Videos/Bandas/The Calling - Adrienne.mpg");

        this.pipe1 = new TransferPipeImpl(new FileInfo(this.file1));

        FileInfo fi = new FileInfo(this.file2);

        this.pipe2 = new TransferPipeImpl(fi);
        this.pipe2Copy1 = new TransferPipeImpl(fi);
        this.pipe2Copy2 = new TransferPipeImpl(fi);
        this.pipe2Copy3 = new TransferPipeImpl(fi);
        this.pipe2Copy4 = new TransferPipeImpl(fi);
        this.pipe2Copy5 = new TransferPipeImpl(fi);
        this.pipe2Copy6 = new TransferPipeImpl(fi);
        this.pipe2Copy7 = new TransferPipeImpl(fi);

        this.download = new AdvancedDownload(this.pipe2);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Verities the download initial conditions.
     */
    public void testInitialConditions() {
        assertEquals(1, this.download.getNumberOfTransfers());
        assertEquals(0, this.download.getNumberOfAllocatedPipes());
        assertEquals(1, this.download.getNumberOfDeallocatedPipes());
    }

	/**
	 * Tests the <code>contains</code> method.
	 * @throws Exception
	 */
	public void testContains() throws Exception{
		assertTrue(download.contains(pipe2));
		assertFalse(download.contains(pipe1));
		
		download.addTransferPipe(pipe2Copy1);
		assertTrue(download.contains(pipe2Copy1));
		assertFalse(download.contains(pipe2Copy2));
        assertEquals(0, this.download.getNumberOfAllocatedPipes());
        assertEquals(2, this.download.getNumberOfDeallocatedPipes());
		
		download.removeTransferPipe(pipe2);
		assertFalse(download.contains(pipe2));
        assertEquals(0, this.download.getNumberOfAllocatedPipes());
        assertEquals(1, this.download.getNumberOfDeallocatedPipes());
		
		assertTrue(download.contains(pipe2Copy1));
		assertFalse(download.contains(pipe2Copy2));
	}

    public void testGetNumberOfTransfers() throws Exception {
        assertEquals(1, download.getNumberOfTransfers());

        download.addTransferPipe(pipe2);
        assertEquals(1, download.getNumberOfTransfers());
        
        download.addTransferPipe(pipe2Copy1);
        assertEquals(2, download.getNumberOfTransfers());

        download.addTransferPipe(pipe2Copy2);
        assertEquals(3, download.getNumberOfTransfers());
    }

	/**
	 * Tests the <code>getSize</code> method.
	 */
	public void testGetSize(){
		assertEquals(file2.length(), download.getSize());
	}

	/**
	 * Tests the <code>getStatus()</code> method.
	 * @throws Exception
	 */
	public void testGetStatus() throws Exception {
		
		//testando se o status inicial eh PAUSED
		assertEquals(AbstractDownload.PAUSED, this.download.getStatus());
		
		this.download.cancel();
		assertEquals(AbstractDownload.CANCELED, this.download.getStatus());
	
		//remove a unica fonte do download e verifica se o status alterou
		this.download.removeTransferPipe(this.pipe2);
		assertEquals(AbstractDownload.NOTRANSFERS, this.download.getStatus());
		
		//tenta resumir o download q esta sem fontes
		this.download.resume();
		assertEquals(AbstractDownload.SEARCHING, this.download.getStatus());

		//pause e verifica se qnd adicionar continua pausado
		this.download.pause();
		assertEquals(AbstractDownload.PAUSED, this.download.getStatus());
		
		this.download.addTransferPipe(this.pipe2Copy1);
		this.download.addTransferPipe(this.pipe2Copy2);
		this.download.addTransferPipe(this.pipe2Copy3);
		this.download.addTransferPipe(this.pipe2Copy4);
		this.download.addTransferPipe(this.pipe2Copy5);
		this.download.addTransferPipe(this.pipe2Copy6);
		this.download.addTransferPipe(this.pipe2Copy7);
		assertEquals(AbstractDownload.PAUSED, this.download.getStatus());

		this.download.resume();
		while(AbstractDownload.DOWNLOADING == this.download.getStatus()){
		}

		assertEquals(AbstractDownload.COMPLETED, this.download.getStatus());

	}
}
