package synapse.client.manager;

import java.io.File;

import junit.framework.TestCase;
import synapse.client.ClientConfig;
import synapse.client.TransferPipeImpl;
import synapse.common.FileInfo;
import synapse.util.HashUtil;

/**
 * 
 * Class that tests the <code>Download</code> class.
 * @author <p>Thiago Emmanuel, thiago.manel@gmail.com</p>
 * @author <p>Vinicius Ferraz C. Florentino, vinicius.ferraz@gmail.com</p>
 */

public class SimpleDownloadTest extends TestCase {

	private File file1,file2;
	
	private FileInfo resource1, resource2;
	
	private TransferPipeImpl transfer1, transfer2, transfer3, transfer4;
	
	private AbstractDownload down1, down2;
	
	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		ClientConfig.setProperty(ClientConfig.SHARED_FOLDER, ClientConfig.getTestFolder());
		
		file1 = new File(ClientConfig.getRootDir() + File.separator + "testFiles/test1.dat");			
		file2 = new File(ClientConfig.getRootDir() + File.separator + "testFiles/emptyFile.dat");
		
		resource1 = new FileInfo (file1);		
		resource2 = new FileInfo (file2);
		
		transfer1 = new TransferPipeImpl(resource1);
		transfer2 = new TransferPipeImpl(resource2);
		
		//cria transfers de arquivos iguais para serem adicionados a transferencia
		transfer3 = new TransferPipeImpl(resource1);
		transfer4 = new TransferPipeImpl(resource2);
		
		down1 = new SimpleDownload(transfer1);
		down2 = new SimpleDownload(transfer2);
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

		file1 = null;			
		file2 = null;
		
		resource1 = null;		
		resource2 = null;
		
		transfer1 = null;
		transfer2 = null;

		//deleta os arquivos q foram transferidos para a pasta testDestination
		down1.getFile().delete();
		down2.getFile().delete();
		
		down1 = null;
		down2 = null;
		
	}
	
	/**
	 * Tests the <code>contains</code> method.
	 * @throws Exception
	 */
	public void testContains() throws Exception {
		
		assertTrue(down1.contains(transfer1));
		assertFalse(down1.contains(transfer3));
		
		down1.addTransferPipe(transfer3);
		assertTrue(down1.contains(transfer3));
		
		down1.removeTransferPipe(transfer1);
		assertFalse(down1.contains(transfer1));
	}
	
	/**
	 * Tests the <code>getNumberTransfers</code> method.
	 * @throws Exception
	 */
	public void testGetNumberTransfers() throws Exception{
		assertEquals(1, down1.getNumberOfTransfers());
		
		down1.addTransferPipe(transfer3);
		assertEquals(2, down1.getNumberOfTransfers());
		
		down1.removeTransferPipe(transfer1);
		assertEquals(1, down1.getNumberOfTransfers());
	}
	
	/**
	 * Tests the <code>getSize</code> method.
	 */
	public void testGetSize(){
		assertEquals(file1.length(), down1.getSize());
		assertEquals(file2.length(), down2.getSize());
	}
	
	/**
	 * Tests the <code>getFileName</code> method.
	 */
	public void testGetFileName(){
		assertEquals(file1.getName(), down1.getFileName());
		assertEquals(file2.getName(), down2.getFileName());
	}
	
	/**
	 * Tests the <code>getHash</code> method.
	 */
	public void testGetHash(){
		assertEquals(resource1.getHash(), down1.getHash());
		assertEquals(resource2.getHash(), down2.getHash());
	}
	
	/**
	 * Tests the <code>getStatus()</code> method.
	 * @throws Exception
	 */
	public void testGetStatus() throws Exception {
		
		//testando se o status inicial eh PAUSED
		assertEquals(AbstractDownload.PAUSED, down1.getStatus());
		
		down1.cancel();
		assertEquals(AbstractDownload.CANCELED, down1.getStatus());
	
		//remove a unica fonte do download e verifica se o status alterou
		down1.removeTransferPipe(transfer1);
		assertEquals(AbstractDownload.NOTRANSFERS, down1.getStatus());
		
		//tenta resumir o download q esta sem fontes
		down1.resume();
		assertEquals(AbstractDownload.SEARCHING, down1.getStatus());

		//pause e verifica se qnd adicionar continua pausado
		down1.pause();
		assertEquals(AbstractDownload.PAUSED, down1.getStatus());
		
		down1.addTransferPipe(transfer3);
		assertEquals(AbstractDownload.PAUSED, down1.getStatus());
		
		down1.resume();
		while(AbstractDownload.DOWNLOADING == down1.getStatus()){
		}
		assertEquals(AbstractDownload.COMPLETED, down1.getStatus());

	}

	/**
	 * Tests the <code>numTransfers</code> method.
	 * @throws Exception
	 */
	public void testNumTransfers() throws Exception {
		assertTrue(down1.getNumberOfTransfers() == 1);
		assertTrue(down2.getNumberOfTransfers() == 1);
		
		//adiciona mais fontes
		down1.addTransferPipe(transfer3);
		down2.addTransferPipe(transfer4);
		
		//verifica se o numero de transfers aumentou
		assertTrue(down1.getNumberOfTransfers() == 2);
		assertTrue(down2.getNumberOfTransfers() == 2);
		
		//verifica se o mesmo transfer nao eh adicionado
		down1.addTransferPipe(transfer3);
		down2.addTransferPipe(transfer4);
		
		assertTrue(down1.getNumberOfTransfers() == 2);
		assertTrue(down2.getNumberOfTransfers() == 2);
		
		down1.removeTransferPipe(transfer1);
		down1.removeTransferPipe(transfer3);
		assertTrue(down1.getNumberOfTransfers() == 0);
	
	}
	
	/**
	 * Tests the <code>resume</code> method.
	 */
	public void testResume() throws Exception{

		down1.resume();
		while(down1.getStatus() == AbstractDownload.DOWNLOADING){
		}
		assertEquals(HashUtil.createHash(file1), HashUtil.createHash(down1.getFile()));

		down2.resume();
		while(down2.getStatus() == AbstractDownload.DOWNLOADING){
		}
		assertEquals(HashUtil.createHash(file2), HashUtil.createHash(down2.getFile()));
		
	}
	
	/**
	 * Tests the <code>removeTransferPipe</code> method.
	 * @throws Exception
	 */
	public void testRemoveTransferPipe() throws Exception {
		
		down1.addTransferPipe(transfer3);
		assertTrue(down1.contains(transfer3));
		
		down2.addTransferPipe(transfer4);
		assertTrue(down2.contains(transfer4));
		
		//remove 1 dos transfers dos downloads
		down1.removeTransferPipe(transfer1);
		assertFalse(down1.contains(transfer1));
		
		down2.removeTransferPipe(transfer4);
		assertFalse(down2.contains(transfer4));
		
		//remove os transfers dos downloads restantes
		down1.removeTransferPipe(transfer3);
		assertFalse(down1.contains(transfer3));
		
		down2.removeTransferPipe(transfer2);
		assertFalse(down2.contains(transfer2));
	}

}
