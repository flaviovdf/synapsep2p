package synapse.util;

import java.io.File;

import junit.framework.TestCase;

/**
 * Tests the HashUtil class.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class HashUtilTest extends TestCase {

    private File file1;
    private File file2;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        file1 = new File("testFiles" + File.separator + "test3.dat");
        file2 = new File("testFiles" + File.separator + "test3_hashTest.dat");
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Tests the hash creation.
     */
    public void testCreateHash() {
        
        String hash1 = HashUtil.createHash(file1);
        String hash2 = HashUtil.createHash(file2);

        assertFalse(hash1.equals(hash2));
        
    }

}
