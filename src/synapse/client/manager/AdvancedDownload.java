package synapse.client.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ourgrid.yal.Logger;

import synapse.common.TransferPipe;

/**
 * This is a download heuristic that transfer a file using too many <code>TransferPipe</code>s.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 * @author <p>Thiago Emmanuel, thiago.manel@gmail.com</p>
 * @author <p>Vinicius Ferraz C. Florentino, vinicius.ferraz@gmail.com</p>
 */
public class AdvancedDownload extends AbstractDownload implements Runnable, Serializable {

    private final int PARTS = 4;

    private transient Set allocatedPipes;
    
    private transient Set deallocatedPipes;
    
    private DownloadProcessor downloadProcessor;
    
    private transient RandomAccessFile output;
    
    private transient Logger logger;

    private List downloadActions;
    
    private int downloaded;

    public AdvancedDownload(TransferPipe pipe) throws RemoteException {
        super(pipe);

        downloaded = 0;

        this.allocatedPipes = new HashSet();
        this.deallocatedPipes = new HashSet();

        this.deallocatedPipes.add(pipe);

        this.downloadActions = new LinkedList();
        this.mountDownloadActions();

        //cria um arquivo de acesso aleatorio a partir do arquivo anteriormente criado
        try {
            output = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            cancel();
        }

        new Thread(this).start();
    }

    protected void prepareInstance() {
        super.prepareInstance();
        this.downloadProcessor = new DownloadProcessor();
       
        this.logger = Logger.getInstance();
    }

    private void mountDownloadActions() {
        long partSize = this.getSize() / 4;
        long lastSize = this.getSize() - 3 * partSize;
        
        this.downloadActions.add(new DownloadAction(partSize, 0));
        this.downloadActions.add(new DownloadAction(partSize, partSize));
        this.downloadActions.add(new DownloadAction(partSize, 2 * partSize));
        this.downloadActions.add(new DownloadAction(lastSize, 3 * partSize));
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {

        while (true) {
            downloadProcessor.waitConditions();

            if (getStatus() == COMPLETED) break;

            DownloadAction da = this.getFirstDeallocatedDownloadAction();
            if (da == null) continue;
            TransferPipe pipe = (TransferPipe) deallocatedPipes.iterator().next();
            this.allocatePipe(pipe);
            da.setTransferPipe(pipe);
            da.start();

        }
//System.err.println("(Thread Principal acabou)");
    }

    private void allocatePipe(TransferPipe pipe) {
        if (this.deallocatedPipes.remove(pipe)) {
            this.allocatedPipes.add(pipe);
        }
    }

    private void deallocatePipe(TransferPipe pipe) {
        if (this.allocatedPipes.remove(pipe)) {
            this.deallocatedPipes.add(pipe);
        }
    }

    private DownloadAction getFirstDeallocatedDownloadAction() {
        if (downloadActions != null) {
	        Iterator it = this.downloadActions.iterator();
	        
	        while (it.hasNext()) {
	            DownloadAction da = (DownloadAction) it.next();
	            if (! da.isBusy() ) {
	                return da; 
	            }
	        }
        }
        return null;
    }

    private void download () {
        Iterator it = downloadActions.iterator();

        while (it.hasNext()) {
            DownloadAction da = (DownloadAction)it.next();
            if (da.isBusy()) {
                da.start();
            }
        }
    }

    /* (non-Javadoc)
     * @see synapse.client.manager.AbstractDownload#pause()
     */
    public void pause() {
        super.pause();

        logger.info(getClass().getName() + ".pause()", "The Download's status was changed to PAUSED.");
    }

    /* (non-Javadoc)
     * @see synapse.client.manager.AbstractDownload#resume()
     */
    public void resume (){
        super.resume();

        logger.info(getClass().getName() + ".resume()", "The Download has "+ transfersList.size() +" transfers.");

        if (getStatus() == DOWNLOADING) {
            download();
            this.downloadProcessor.notice();
        }
    }

    /* (non-Javadoc)
     * @see synapse.client.manager.AbstractDownload#cancel()
     */
    public void cancel (){
        super.cancel();

        logger.info(getClass().getName() + ".cancel()", "The Download's status was changed to CANCELED.");
    }

    /* (non-Javadoc)
     * @see synapse.client.manager.AbstractDownload#getDownloadProgress()
     */
    public float getDownloadProgress() {
        return (downloaded * 100) / getSize();
    }


    /* (non-Javadoc)
     * @see synapse.client.manager.AbstractDownload#getSpeed()
     */
    public long getSpeed() {
        return 0;
    }

    /* (non-Javadoc)
     * @see synapse.client.manager.AbstractDownload#addTransferPipe(synapse.common.TransferPipe)
     */
    public boolean addTransferPipe(TransferPipe pipe) {
        if (super.addTransferPipe(pipe)) {
            this.deallocatedPipes.add(pipe);
            
            if (this.allocatedPipes.size() < PARTS) {
                this.downloadProcessor.notice();
            }
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see synapse.client.manager.AbstractDownload#removeTransferPipe(synapse.common.TransferPipe)
     */
    protected void removeTransferPipe(TransferPipe pipe) {
        super.removeTransferPipe(pipe);
        
        this.allocatedPipes.remove(pipe);
        this.deallocatedPipes.remove(pipe);
    }

    /**
     * Returns the number of allocated <code>TransferPipe</code>s.
     * 
     * @return The number of allocated pipes.
     */
    public int getNumberOfAllocatedPipes() {
        return this.allocatedPipes.size();
    }

    /**
     * Returns the number of deallocated <code>TransferPipe</code>s.
     * 
     * @return The number of deallocated pipes.
     */
    public int getNumberOfDeallocatedPipes() {
        return this.deallocatedPipes.size();
    }

    /**
     * Writes an array of bytes on disk mounting an structure like a complete file.
     * 
     * @param array The byte array to be written.
     * @param position Start offset to write the array.
     */
    private synchronized boolean writeOnDisk(byte[] array, long position) {
        try {
            output = new RandomAccessFile(getFile(), "rw");
            output.seek(position);
	        output.write(array);
	        output.close();
	    } catch (IOException e) {
	        cancel();
	        return false;
        }
	    this.downloaded += array.length;
	    if (downloaded == size) {
	        setStatus(COMPLETED);
	        logger.info(getClass().getName() + ".writeOnDisk()", "The download of " + getFileName() + " finished.");
	    }
	    return true;
    }

    private class DownloadProcessor {

        public synchronized void waitConditions() {
            try {
	            while (getStatus() != DOWNLOADING || (allocatedPipes.size() == PARTS || allocatedPipes.size() == transfersList.size())) {
//System.err.println("(Main Thread esperando)");
	                wait();
//System.err.println("(Main Thread acordou)");
	            }
            } catch (InterruptedException e) {
                System.err.println("There is something wrong!");
            }
        }

        public synchronized void notice() {
            notify();
        }
    }

    /**
     * This class is responsible to download a part of a previous file.
     * 
     * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
     * @author <p>Thiago Emmanuel, thiago.manel@gmail.com</p>
     * @author <p>Vinicius Ferraz C. Florentino, vinicius.ferraz@gmail.com</p>
     */
    private class DownloadAction extends Thread implements Serializable {
        
        private byte[] array;
        
        private transient TransferPipe pipe;
        
        private int downloaded;

        private long size;
        
        private long offset;
        
        private transient boolean busy;

        private final int BUFFER_SIZE = 32 * 1024;
        
        private boolean actionTransferCondition;
        
        /**
         * Creates a new DownloadAction.
         * 
         * @param size The file size that have to be downloaded by this <code>DownloadAction</code>.
         * @param offset The offset to continue downloading.
         */
        public DownloadAction(long size, long offset) {
            this.downloaded = 0;

            this.size = size;
            this.offset = offset;
            this.array = new byte[ BUFFER_SIZE ];
            actionTransferCondition = true;
        }

     
        
        /**
         * Informs if this action is busy.
         * 
         * @return true if the action is busy, false otherwise.
         */
        public boolean isBusy() {
            return this.busy;
        }

        /**
         * Configures the current transfer pipe.
         * 
         * @param pipe The <code>TransferPipe</code> used to download the bytes.
         * @return true is there wasn't any associated pipe, false otherwise.
         */
        public boolean setTransferPipe(TransferPipe pipe) {
            if (this.pipe == null) {
                this.pipe = pipe;
                this.busy = true;
                return true;
            }
            return false;
        }

        /**
         * Sets the <code>pipe</code> to <code>null</code>.
         */
        public void removeTransferPipe() {
            this.pipe = null;
            this.busy = false;
        }

        /**
         * The thread action.
         */
        public void run() {

            while(busy && getStatus() == DOWNLOADING && actionTransferCondition){

	            if (BUFFER_SIZE > this.size - this.downloaded) {
	                array = new byte[(int)(this.size - this.downloaded)];
	            }
	            try{

	                //recebe um array de bytes do transferPipe q esta na primeira posicao
	                array = pipe.getFile(offset + this.downloaded, array.length);

		            if(writeOnDisk(array, this.offset + this.downloaded)) {
		                //incrementa o numero de bytes transferidos
		                this.downloaded += array.length;
		                
		                if (this.downloaded == this.size) {
		                    this.actionTransferCondition = false;
		                    busy = true;
		                }
		            }
	            }
	            catch (RemoteException e) {
	                logger.error(getClass().getName() + ".run()", "A TransferPipe with " + getFileName() + " is invalid and will be removed.");
	                this.removeTransferPipe();
	                AdvancedDownload.this.removeTransferPipe(pipe);
	                logger.error(getClass().getName() + ".run()", "The total number of transfers is " + getNumberOfTransfers());
	            }
            }
			deallocatePipe(this.pipe);
			downloadProcessor.notice();
        }
    }


    /**
     * Returns the String representation of this Download.
     * @return The String representation of this Download.
     */
    public String toString(){
        String temp;
        temp = "[AdvancedDownload] " + file.getName() + " " + downloaded + "/"+ size + " " + hash + "  ";
        switch (getStatus()) {
        case DOWNLOADING:
            return temp + "Downloading.";
        case PAUSED:
            return temp + "Paused.";
        case CANCELED:
            return temp + "Canceled.";
        case COMPLETED:
            return temp + "Completed.";
        case ERROR: 
            return temp + "Error.";
        case NOTRANSFERS:
            return temp + "No Transfers.";
        default:
            return temp + "Searching.";
        }
    }

}
