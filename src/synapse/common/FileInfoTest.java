package synapse.common;

import java.io.File;

import junit.framework.TestCase;
import synapse.util.HashUtil;

/**
 * Class that tests the <code>FileInfo</code> class.
 * @author <p>João Arthur Brunet Monteiro, jarthur@dsc.ufcg.edu.br</p>
 */ 
public class FileInfoTest extends TestCase {
	
	private File a,b,c,d,e,f,g,h;
	private FileInfo one,two,three,four,five,six;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() {
		a = new File("testFiles/test1.dat");
		b = new File("testFiles/test2.dat");
		c = new File("testFiles/test3.dat");
		d = new File("testFiles/test3.dat");
		e = new File("testFiles/test1Copy.dat");
		f = new File("testFiles/this is a file that was made for testing.dat");
		g = new File("testFiles/emptyFile.dat");
		h = new File("testFiles/this is a file that was made for testing.dat");
			
		one = new FileInfo(a);
		two = new FileInfo(b);
		three = new FileInfo(c);
		four = new FileInfo(d);
		five = new FileInfo(e);
		six = new FileInfo(f);
		
	}
	
	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test the method equals of <code>FileInfo</code>. In this tests is necessary to use
	 * the method <code>getHash()</code>. So, with this only test
	 * we can make sure the all methods of <code>FileInfo</code> are corrects.
	 * */

	public void testEquals() {
		assertTrue(one.equals(five));
		assertEquals(one.getHash(), five.getHash());
		assertEquals (three, four);
		assertFalse(one.equals(two));
		assertFalse(three.getHash().equals(one.getHash()));
		assertEquals(three.getFileName(), four.getFileName());
		assertEquals(four.getFile(), three.getFile());
		assertEquals(HashUtil.createHash(g), HashUtil.createHash(h));
	}

}
