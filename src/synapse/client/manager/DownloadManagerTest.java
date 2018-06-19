package synapse.client.manager;

import java.io.File;
import java.rmi.RemoteException;

import synapse.client.ClientConfig;
import synapse.client.TransferPipeImpl;
import synapse.util.*;
import junit.framework.TestCase;
import synapse.common.FileInfo;
import synapse.common.TransferPipe;

/**
 * This class Tests DownloadManager's methods.
 * @author <p>Thiago Emmanuel, thiago.manel@gmail.com</p>
 * @author <p>Vinicius Ferraz C. Florentino, vinicius.ferraz@gmail.com</p>
 */
public class DownloadManagerTest extends TestCase {

	private DownloadManager manager;

	private File file1, file2, file3, file4, file5;

	private FileInfo resource1, resource2, resource3, resource4, resource5;

	private TransferPipe transfer1, transfer2, transfer3, transfer4, transfer5,
			transfer6, transfer7;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		ClientConfig.setProperty(ClientConfig.SHARED_FOLDER, ClientConfig.getTestFolder());

		manager = DownloadManager.getInstance();

		file1 = new File(ClientConfig.getRootDir() + File.separator + "testFiles" + File.separator + "test1.dat");
		file2 = new File("testFiles" + File.separator + "emptyFile.dat");
		file3 = new File("testFiles" + File.separator + "test2.dat");
		file4 = new File("testFiles" + File.separator + "test1Copy.dat");
		file5 = new File("testFiles" + File.separator + "test2Copy.dat");

		resource1 = new FileInfo(file1);
		resource2 = new FileInfo(file2);
		resource3 = new FileInfo(file3);
		resource4 = new FileInfo(file4);
		resource5 = new FileInfo(file5);

		transfer1 = new TransferPipeImpl(resource1);
		transfer2 = new TransferPipeImpl(resource2);
		transfer5 = new TransferPipeImpl(resource3);
		transfer6 = new TransferPipeImpl(resource4);
		transfer7 = new TransferPipeImpl(resource5);

		//cria transfers de arquivos iguais para serem adicionados a
		// transferencia
		transfer3 = new TransferPipeImpl(resource1);
		transfer4 = new TransferPipeImpl(resource2);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

		file1 = null;
		file2 = null;
		file3 = null;
		file4 = null;
		file5 = null;

		resource1 = null;
		resource2 = null;
		resource3 = null;
		resource4 = null;
		resource5 = null;
		
		transfer1 = null;
		transfer2 = null;
		transfer5 = null;
		transfer6 = null;
		transfer7 = null;

		//cria transfers de arquivos iguais para serem adicionados a
		// transferencia
		transfer3 = null;
		transfer4 = null;
		
		DownloadManager.reset();
	}

	/**
	 * Tests a testAddTransferPipe method
	 * 
	 * @throws RemoteException
	 * @throws Exception
	 */
	public void testAddTransferPipe() throws RemoteException, Exception {

		//adicionando Transfers sem fazer a solicitacao
		manager.addTransferPipe(transfer1);
		manager.addTransferPipe(transfer6);
		manager.addTransferPipe(transfer5);
		manager.addTransferPipe(transfer2);

		assertFalse(manager.contains(transfer1.getHash()));
		assertFalse(manager.contains(transfer6.getHash()));
		assertFalse(manager.contains(transfer5.getHash()));
		assertFalse(manager.contains(transfer2.getHash()));

		//adicionando as solicitacoes e testando se foram adicionadas
		manager.addSolicitation(transfer1.getHash());
		manager.addSolicitation(transfer6.getHash());
		manager.addSolicitation(transfer5.getHash());
		manager.addSolicitation(transfer2.getHash());
		
		manager.addTransferPipe(transfer1);
		manager.addTransferPipe(transfer6);
		manager.addTransferPipe(transfer5);
		manager.addTransferPipe(transfer2);
		
		assertTrue(manager.contains(transfer1.getHash()));
		assertTrue(manager.contains(transfer6.getHash()));
		assertTrue(manager.contains(transfer5.getHash()));
		assertTrue(manager.contains(transfer2.getHash()));
		
		//testa se apos cancelar ele eh retirado da Lista de Solicitacao
		manager.getSelectedDownload(transfer6.getHash()).cancel();
		assertFalse(manager.contains(transfer6.getHash()));
	}

	/**
	 * Test a cancelDownload method
	 * @throws RemoteException
	 * @throws HashDoesNotExistException
	 */

	public void testCancelDownload() throws Exception {
		
		manager.addSolicitation(transfer1.getHash());
		manager.addTransferPipe(transfer1);
		assertTrue(manager.contains(transfer1.getHash()));

		manager.cancelDownload(transfer1.getHash());

		assertFalse(manager.contains(transfer1.getHash()));
	}

	/**
	 * Test a resumeDownload method
	 * 
	 * @throws RemoteException
	 * @throws HashDoesNotExistException
	 */
	public void testResumeDownload() throws Exception {
		
		manager.addSolicitation(transfer2.getHash());
		manager.addTransferPipe(transfer2);
		AbstractDownload auxDown = manager.getSelectedDownload(transfer2.getHash());
		//cria um arquivo para ter acesso aos dados depois de serem baixados
		File temp = new File(auxDown.getFile().getAbsolutePath());
		manager.resumeDownload(transfer2.getHash());
		while (auxDown.getStatus() != AbstractDownload.COMPLETED) {
		   // System.out.println(auxDown);
		}
		//verifico se o hash dos arquivos sao iguais
		assertEquals(HashUtil.createHash(temp), transfer2.getHash());

		//testa o caso em que uma chamada resume() eh feita em downloadManager,
		//onde o download especificado naum tem nenhum transferPipe. Apos esta
		// chamada
		//o status do download deve ser setado como SEARCHING
		manager.addSolicitation(transfer1.getHash());
		manager.addTransferPipe(transfer1);
		manager.getSelectedDownload(transfer1.getHash()).removeTransferPipe(
				transfer1);
		manager.getSelectedDownload(transfer1.getHash()).resume();
		assertTrue(manager.getSelectedDownload(transfer1.getHash())
				.getStatus() == AbstractDownload.SEARCHING);

	}

	/**
	 * Tests a pauseAll method
	 * 
	 * @throws Exception
	 */
	public void testPauseAll() throws Exception {
		
		manager.addSolicitation(transfer1.getHash());
		manager.addSolicitation(transfer2.getHash());
		manager.addSolicitation(transfer5.getHash());
		
		manager.addTransferPipe(transfer1);
		manager.addTransferPipe(transfer2);
		manager.addTransferPipe(transfer5);

		//faz o status dos downloads mudar para NOTRANSFERS
		manager.getSelectedDownload(transfer1.getHash()).removeTransferPipe(
				transfer1);
		manager.getSelectedDownload(transfer2.getHash()).removeTransferPipe(
				transfer2);
		manager.getSelectedDownload(transfer5.getHash()).removeTransferPipe(
				transfer5);

		manager.pauseAll();

		assertEquals(manager.getSelectedDownload(transfer1.getHash())
				.getStatus(), AbstractDownload.PAUSED);
		assertEquals(manager.getSelectedDownload(transfer2.getHash())
				.getStatus(), AbstractDownload.PAUSED);
		assertEquals(manager.getSelectedDownload(transfer5.getHash())
				.getStatus(), AbstractDownload.PAUSED);

	}

	/**
	 * Test a close method
	 * 
	 * @throws Exception
	 */
	public void testClose() throws Exception {
		
		manager.addSolicitation(transfer1.getHash());
		manager.addSolicitation(transfer2.getHash());
		manager.addSolicitation(transfer3.getHash());
		manager.addSolicitation(transfer4.getHash());
		manager.addSolicitation(transfer5.getHash());
		
		//adiciona varias fontes
		manager.addTransferPipe(transfer1);
		manager.addTransferPipe(transfer2);
		manager.addTransferPipe(transfer3);
		manager.addTransferPipe(transfer4);
		manager.addTransferPipe(transfer5);

		manager.close();

		DownloadManager.reset();
		manager = DownloadManager.getInstance();

		//verifica se os downloads foram adicionados do arquivo ao manager
		assertNotNull(manager.getSelectedDownload(transfer1.getHash()));
		assertNotNull(manager.getSelectedDownload(transfer2.getHash()));
		assertNotNull(manager.getSelectedDownload(transfer5.getHash()));

		//verifica se os downloads estao sem fontes
		assertTrue(manager.getSelectedDownload(transfer1.getHash()).getNumberOfTransfers() == 0);
		assertTrue(manager.getSelectedDownload(transfer2.getHash()).getNumberOfTransfers() == 0);
		assertTrue(manager.getSelectedDownload(transfer5.getHash()).getNumberOfTransfers() == 0);

		//altero o status para paused do download com mesmo hash do transfer5
		manager.getSelectedDownload(transfer5.getHash()).pause();
		assertEquals(manager.getSelectedDownload(transfer5.getHash())
				.getStatus(), AbstractDownload.PAUSED);
		//adiciona o transfer5
		manager.addSolicitation(transfer5.getHash());
		manager.addTransferPipe(transfer5);
		assertEquals(manager.getSelectedDownload(transfer5.getHash())
				.getStatus(), AbstractDownload.PAUSED);

		//fazendo com quem seja feita uma transferencia de uma parte do arquivo
		//manager.getSelectionedDownload(transfer5.getHash()).resume();
		manager.getSelectedDownload(transfer5.getHash()).pause();

		//salvo em disco a parte baixada
		manager.close();

		DownloadManager.reset();
		manager = DownloadManager.getInstance();

		assertEquals(manager.getSelectedDownload(transfer5.getHash())
				.getStatus(), AbstractDownload.NOTRANSFERS);
		
		manager.addSolicitation(transfer5.getHash());
		assertTrue(manager.contains(transfer5.getHash()));
		AbstractDownload auxDown = manager.getSelectedDownload(transfer5.getHash());
		File temp = new File(manager
				.getSelectedDownload(transfer5.getHash()).getFile()
				.getAbsolutePath());
		manager.addTransferPipe(transfer5);
		while (auxDown.getStatus() == AbstractDownload.DOWNLOADING) {

		}

		//verifico se o hash dos arquivos sao iguais
		assertEquals(HashUtil.createHash(temp), transfer5.getHash());
		temp.delete();

		//deleta os arquivos q foram transferidos
		(manager.getSelectedDownload(transfer1.getHash())).getFile()
				.delete();
		(manager.getSelectedDownload(transfer2.getHash())).getFile()
				.delete();
	}

	/**
	 * Test a update method
	 * 
	 * @throws RemoteException
	 * @throws HashDoesNotExistException
	 * @throws InterruptedException
	 *  
	 */
	public void testUpdate() throws Exception {

		//testa a acao promovida por update, apos ter sido terminado
		//a transferencia em um download, o metodo update deve retirar
		//este download da lista dos que ainda nao estao completos
		manager.addSolicitation(transfer2.getHash());
		manager.addTransferPipe(transfer2);
		//quantidade inicial de downloads em downloadManager
		int numDown = manager.getNumberOfDownloads();

		AbstractDownload auxDown = manager.getSelectedDownload(transfer2.getHash());
		manager.resumeDownload(transfer2.getHash());
		while (auxDown.getStatus() != AbstractDownload.COMPLETED) {
		}

		//testa a acao do update, apos uma chamada para o metodo cancel
		//em download, logica semelhante ao caso de teste anterior
		manager.addSolicitation(transfer2.getHash());
		manager.addTransferPipe(transfer2);
		//quantidade inicial de downloads em downloadManager
		numDown = manager.getNumberOfDownloads();
		auxDown = manager.getSelectedDownload(transfer2.getHash());
		manager.cancelDownload(transfer2.getHash());
		//deve existir um download a menos que numDown, pois com certeza
		//um download foi cancelado
		assertTrue(manager.getNumberOfDownloads() == numDown - 1);

	}

}
