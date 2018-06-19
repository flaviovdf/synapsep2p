package synapse.client;

import synapse.common.FileInfo;

/**
 * This is a fake object.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class FakeFileInfo extends FileInfo {

    /**
     * Creates a new fake object.
     * 
     * @param fileName The file name.
     * @param hash The file hash.
     * @param size The file size.
     */
    public FakeFileInfo(String fileName, String hash, long size) {
        super(fileName, hash, size);
    }

}
