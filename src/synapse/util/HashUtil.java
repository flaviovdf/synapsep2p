package synapse.util;

import java.io.File;
import java.io.FileInputStream;
import com.twmacinta.util.MD5;

/**
 * Class that creats a hashCode for a file. 
 * @author <p>Joao arthur Brunet Monteiro, jarthur@dsc.ufcg.edu.br </p>.
 */
public class HashUtil {

    private static final int BUFFER = 20971520 /* 20MB */;

	/**
	 * Creats a hash for the file.
	 * @param file The file to create hash.
	 * @return The hash of the file.
	 */
	public static String createHash(File file) {

	    long total = file.length();

	    long partSize = BUFFER;
	    long lastSize = total % partSize;

	    long parts = total / partSize;
	    if ((total % BUFFER) != 0) parts += 1;

	    MD5 md5 = new MD5();

	    byte[] buffer = new byte[ (int) partSize ];

	    try {
	        FileInputStream in = new FileInputStream(file);
	        // the first parts
	        if (lastSize == 0) parts += 1; 
	        for (int i = 0; i < parts - 1; i++) {
	            in.read(buffer, 0, (int) partSize);
	            md5.Update(buffer);

	        }
	        // the last part
	        if (lastSize != 0) {
		        buffer = new byte[ (int) lastSize ];
		        in.read(buffer, 0, (int) lastSize);
	            md5.Update(buffer);
	        }
	        // process
	        md5.Final();
	        return md5.asHex();
	    } catch (Exception e) {
	        // do nothing
	        e.printStackTrace();
	    }
	    return null;
	}

}