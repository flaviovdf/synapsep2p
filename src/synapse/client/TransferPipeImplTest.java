package synapse.client;

import java.io.File;
import java.io.RandomAccessFile;
import java.rmi.RemoteException;
import java.util.Arrays;

import synapse.common.FileInfo;
import synapse.common.OperationNotSupportedException;

import junit.framework.TestCase;

/**
 * Tests for TransferPipeImpl.
 * 
 * @author
 * <p>
 * Vinicius Ferraz Campos Florentino, vinicius.ferraz@gmail.com
 * </p>
 */
public class TransferPipeImplTest extends TestCase {

	File file1;

	File file2;

	FileInfo resource1;

	FileInfo resource2;

	TransferPipeImpl transfer1;

	TransferPipeImpl transfer2;

	protected void setUp() throws Exception {
		super.setUp();
		file1 = new File("testFiles" + File.separator + "test1.dat");
		file2 = new File("testFiles" + File.separator + "test2.dat");

		resource1 = new FileInfo(file1);
		resource2 = new FileInfo(file2);

		transfer1 = new TransferPipeImpl(resource1);
		transfer2 = new TransferPipeImpl(resource2);

	}

	protected void tearDown() throws Exception {
		super.tearDown();

		file1 = null;
		file2 = null;

		resource1 = null;
		resource2 = null;

		transfer1 = null;
		transfer2 = null;
	}

	public void testInvalidate() throws Exception {
	    byte[] array = new byte[ 10 ];
	    array = transfer1.getFile(0, 0);
	    assertNotNull(array);
	    
	    transfer1.invalidate();
	    
	    try {
	        array = transfer1.getFile(0, 1);
	        fail("An OperationNotSupportedException should be thrown here!");
	    } catch (OperationNotSupportedException e) {
	        // this line have to be executed
	    }
	}

	public void testGetName() throws RemoteException {

		//verifica se ele verifica se os nomes estao iguais
		assertEquals(transfer1.getFileName(), file1.getName());

		assertEquals(transfer2.getFileName(), file2.getName());

		//verifica para nomes diferentes
		assertFalse(transfer2.getFileName().equals(file1.getName()));

		assertFalse(transfer1.getFileName().equals(file2.getName()));

	}

	public void testGetSize() throws RemoteException {

		//verifica se ele verifica se os tamanhos estao iguais
		assertEquals(transfer1.getSize(), file1.length());

		assertEquals(transfer2.getSize(), file2.length());

		//verifica se os tamanhos dao diferentes
		assertFalse(transfer2.getSize() == file1.length());

		assertFalse(transfer1.getSize() == file2.length());
	}

	public void testGetFile() throws Exception {
		byte[] array1 = new byte[17];
		byte[] array2 = new byte[17];
		byte[] array3, array4;

		RandomAccessFile dataInput1 = new RandomAccessFile(file1, "rw");

		dataInput1.seek(0);
		dataInput1.read(array1, 0, array1.length);
		array2 = transfer1.getFile(0, array2.length);

		assertTrue(Arrays.equals(array1, array2));

		dataInput1.seek(20);
		dataInput1.read(array1, 0, array1.length);
		array2 = transfer1.getFile(20, array2.length);

		assertTrue(Arrays.equals(array1, array2));

		RandomAccessFile dataInput2 = new RandomAccessFile(file2, "rw");

		array3 = new byte[17];
		array4 = new byte[17];

		dataInput2.seek(0);
		dataInput2.read(array3, 0, array3.length);

		array4 = transfer2.getFile(0, array4.length);

		assertTrue(Arrays.equals(array3, array4));

	}

    public void testEquals() throws RemoteException {
    	assertFalse(transfer1.equals(transfer2));
    	
    	assertFalse(transfer2.equals(new TransferPipeImpl(resource2)));
    	
    	assertEquals(transfer2, transfer2);
	}
}
