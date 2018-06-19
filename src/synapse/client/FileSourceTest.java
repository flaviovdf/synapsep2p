
package synapse.client;

import java.io.File;

import junit.framework.TestCase;
import synapse.common.FileInfo;

/**
 * Class that tests the <code>FileSource</code> class.
 * @author <p>João Arthur Brunet Monteiro, jarthur@dsc.ufcg.edu.br</p>
 */
public class FileSourceTest extends TestCase {
	
	/**
	 * Object to be tested.
	 */
	private FileSource fileSource;
	private File a,b,c;
	private FileInfo one,two,three,four;
	
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		this.fileSource = new FileSource();
		a = new File("testFiles/test1.dat");
		b = new File("testFiles/test2.dat");
		c = new File("testFiles/test3.dat");
		one = new FileInfo(a);
		two = new FileInfo(b);
		three = new FileInfo(c);
	}
	
	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Tests the method puFile() of the <code>FileSource</code> class.
	 */
	public void testPutFile() {
		
		this.fileSource.putFile(a);
		this.fileSource.putFile(b);
		this.fileSource.putFile(c);
		
		FileInfo expected = (FileInfo) this.fileSource.getFileInfo(one.getHash());
		assertEquals(a, expected.getFile());
		assertFalse(b.equals(expected.getFile()));
		assertFalse(c.equals(expected.getFile()));
				
		expected = (FileInfo) this.fileSource.getFileInfo(two.getHash());
		assertEquals(b, expected.getFile());
		assertFalse(a.equals(expected.getFile()));
		assertFalse(c.equals(expected.getFile()));
		
		
		expected = (FileInfo) this.fileSource.getFileInfo(three.getHash());
		assertEquals(c, expected.getFile());
		assertFalse(b.equals(expected.getFile()));
		assertFalse(a.equals(expected.getFile()));
		
		
		assertTrue(c.equals(expected.getFile()));
		assertFalse(b.equals(expected.getFile()));
		assertFalse(a.equals(expected.getFile()));
			
	}
	/**
	 * Tests the method putResources of the <code>FileSource</code> class.
	 */
	public void testPutResource() {

		this.fileSource.putFile(a);
		this.fileSource.putFile(b);
		this.fileSource.putFile(c);
		
		assertEquals(one.getFileName(), "test1.dat");
		assertEquals(two.getFileName(), "test2.dat");
		assertEquals(three.getFileName(), "test3.dat");
	}
	
	/**
	 * Tests the method searchForResources of the <code>FileSource</code> class.
	 */
	public void testSearchForResources() {
		
		one = new FileInfo(a);
		two = new FileInfo(b);
		
		assertTrue(this.fileSource.putFile(a));
		assertTrue(this.fileSource.putFile(b));
		assertFalse(this.fileSource.putFile(a));
		assertFalse(this.fileSource.putFile(b));
		
		assertEquals(2, this.fileSource.getAllResources().size());
		
		assertEquals(1, this.fileSource.searchForResources(one.getFileName()).size());
		assertEquals(1, this.fileSource.searchForResources(two.getFileName()).size());
		
		assertEquals(2, this.fileSource.searchForResources("t").size());
		
	}

}