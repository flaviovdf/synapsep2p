package synapse.client;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import synapse.common.FileInfo;
import synapse.util.SimpleExpressionAnalyzer;

/**
 * @author <p>Joao Arthur Brunet Monteiro, jarthur@dsc.ufcg.edu.br</p>
*/
public class FileSource{

    /**
     * A map:
     * Hash x FileInfo
     */
	private HashMap resources;
	
	/**
	 * The matcher.
	 */
	private ExpressionAnalyzer analyzer;
	
	/**
	 * Constructs a new FileSource.
	 */
	public FileSource (){
		this.resources = new HashMap();
		this.analyzer = new SimpleExpressionAnalyzer();
	}

	/**
	 * Creats a FileInfo with the file and put this FileInfo in the HashMap 
	 * that is in this FileSource.
	 * @param file The file to be added.
	 * @return true if the file was added, false otherwise.
	 */
	public boolean putFile (File file) {
		FileInfo aux = new FileInfo(file);
		return this.resources.put(aux.getHash(), aux) == null;
	}

	/**
	 * Returns the FileInfo to which the specified hash is mapped in this FileSource. 
	 * @param hash the hash whose associated FileInfo is to be returned.
	 * @return the value to which this FileSource maps the specified hash, 
	 * or null if the FileSource contains no mapping for this hash.
	 */	
	public FileInfo getFileInfo(String hash) {
		return (FileInfo)resources.get(hash);
	}
	
	/**
	 * This method search for <code>FileInfo</code>s that contains in their file names
	 * this key.
	 * @param key the key to be looked for in the <code>FileInfo</code>s that is in this <code>FileSource</code>.
	 * @return a <code>Collection</code> containing the <code>FileInfo</code>s that have this key
	 * at the name.
	 */
	public Collection searchForResources (String key) {
		
		List feedBack = new LinkedList();
		Iterator it = this.getAllResources().iterator();
		
		while (it.hasNext()) {
			FileInfo temp = (FileInfo)it.next();

			if (this.analyzer.accept(key, temp.getFileName())) {
				feedBack.add(temp);				
			}
		}
		
		return feedBack;
	}
	
	/**
	 * Returns A <code>Collection</code> that contains all the <code>FileInfo</code>s
	 * in this <code>FileSource</code>.
	 * @return A <code>Collection</code> that contains all the <code>FileInfo</code>s
	 * in this <code>FileSource</code>.
	 */
	public Collection getAllResources() {
		return this.resources.values();
	}

	/**
	 * Fills this <code>FileSource</code> with the <code>sharedFolder</code> contents (files).
	 * 
	 * @param sharedFolder The source folder.
	 * @return The number of files loaded.
	 */
    public int loadFiles(String sharedFolder){
        
        File folder = new File(sharedFolder);
        
        if(!folder.exists()){
            folder.mkdir();
        }
        
        int count = 0;
        File[]  files;
        LinkedList directories = new LinkedList();
        
		//adiciona primeiramente a pasta principal a ser pesquisada na lista
		directories.add(folder.getAbsolutePath());
		
		//ciclo para efetuar a pesquisa em todas as subpastas da pasta principal
		while(directories.size() > 0){
		    files = (new File(directories.getFirst().toString())).listFiles();
			for(int i = 0; i <  files.length; i++) {
				if(files[i].canRead() && !files[i].isHidden()){
					if(files[i].isDirectory()){
						//adiciona a subpasta no linkedlista para ter a busca efetuada nela posteriormente
						directories.add(files[i].getAbsolutePath());
					}
					else if(files[i].isFile()){
					    if (this.putFile(files[i])) {
					        count++;
					    }
					}
				}
			}
			//remove a pasta em que a pesquisa terminou
			directories.removeFirst();
		}
		
		return count;
    }
}
