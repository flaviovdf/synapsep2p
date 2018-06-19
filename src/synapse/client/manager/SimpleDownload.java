package synapse.client.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.rmi.RemoteException;

import org.ourgrid.yal.Logger;

import synapse.common.TransferPipe;

/**
 * This class represents a Download. An object <code>Download</code> know as transfer data
 * of the provider to him. <code>Download</code> implements <code>Serializable</code> because 
 * the user may stop the transference process. An aplication that uses <code>Download</code>s 
 * objects must read the <code>Downloads</code>s in disk and reinicialize them in a consistent
 * form, methods for this are implemented in this class.
 * <code>Download</code> comes methods to get transference process's status: getSpeed, 
 * getDownloadProgress, getDownloaded.   
 * 
 * @author <p>Vinicius Ferraz C. Florentino, vinicius.ferraz@gmail.com</p>
 * @author <p>Thiago Emmanuel, thiago.manel@gmail.com</p>
 */

public class SimpleDownload extends AbstractDownload implements Runnable, Serializable {

	/**
	 * The largest downloaded block size.
	 */
    private final int BUFFER_SIZE = 32 * 1024;

    /**
     * The downloaded size.
     */
    private long downloaded;

    /**
     * Initial time.
     */
    private long time1;

    /**
     * Final time.
     */
    private long time2;

    private byte[] buffer;

    private transient RandomAccessFile output;

    private transient Logger logger;

    /**
     * Constructs a new Download
     * @param transfer
     * @throws RemoteException
     */
    public SimpleDownload(TransferPipe transfer) throws RemoteException {
        super(transfer);

        this.setDownloaded(file.length());

        this.time1 = 0;
        this.time2 = 0;
        
        //cria um arquivo de acesso aleatorio a partir do arquivo anteriormente criado
        try {
            output = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            cancel();
        }
    }

    /**
     * Put a <code>SimpleDownload</code> in a consistent form
     */
    protected void prepareInstance() {
        super.prepareInstance();
        
        this.logger = Logger.getInstance();
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

        //chamada para retomar o download
        if (getStatus() == DOWNLOADING) {
            download();
        }
    }

    /* (non-Javadoc)
     * @see synapse.client.manager.AbstractDownload#cancel()
     */
    public void cancel (){
        super.cancel();

        logger.info(getClass().getName() + ".cancel()", "The Download's status was changed to CANCELED.");
    }
    
    /**
     * Returns the number of bytes transfered.
     * @return The number of bytes transfered.
     */
    public synchronized long getDownloaded(){
        return downloaded;
    }

    /**
     * Sets the downloaded size.
     * @param value The value.
     */
    private synchronized void setDownloaded(long value) {
    	this.downloaded = value;
    }

    /**
     * Marks the initial time used to calculates the download speed.
     */
    private synchronized void markBefore() {
        time1 = System.currentTimeMillis();
    }

    /**
     * Marks the final time used to calculates the download speed.
     */
    private synchronized void markAfter() {
        time2 = System.currentTimeMillis();
    }

    /* (non-Javadoc)
     * @see synapse.client.manager.AbstractDownload#getDownloadProgress()
     */
    public synchronized float getDownloadProgress() {
        return (downloaded * 100) / getSize();
    }

    /* (non-Javadoc)
     * @see synapse.client.manager.AbstractDownload#getSpeed()
     */
    public synchronized long getSpeed() {
        if (time1 == time2) {
            return 0;
        }
        return BUFFER_SIZE / ((time2 - time1) * 1000);
    }
    
    /**
     * Start  transference of data
     */
    private void download () {
        Thread thread = new Thread (this);
        logger.info(getClass().getName() + ".download()", "Starting the transfer.");
        thread.start();
    }
    
    /**
     * Utilitary method. Close a file used in a tranference
     */
    private void closeFile(){
        try {
            output.close();
        } catch (IOException e1) {
            logger.exception(getClass().getName() + ".closeFile()", e1);
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run(){
        int read = 0;
        
        while(getStatus() == DOWNLOADING && transfersList.size() > 0){
            //confere se a parte do arquivo restante eh maior do que o buffer
            if(size - downloaded > BUFFER_SIZE){
                buffer = new byte[BUFFER_SIZE];
            }
            else{
                buffer = new byte[(int)(size - downloaded)];
            }
            
            try{
                
                //recebe um array de bytes do transferPipe q esta na primeira posicao
            	markBefore();
                buffer = ((TransferPipe)transfersList.getFirst()).getFile(downloaded, buffer.length);
                markAfter();
                //cria o arquivo que recebera a transferencia dos bytes
                output = new RandomAccessFile(file, "rw");                
                //coloca o ponteiro no final do arquivo
                output.seek(downloaded);                
                //escreve o buffer que foi transferido no arquivo
                output.write(buffer);                
                //incrementa o numero de bytes transferidos
                setDownloaded(downloaded + buffer.length);                
                //Se completou, muda o status e notifica aos Observadores
                if(downloaded == size){
                    setStatus(COMPLETED);
                }
            }
            catch(RemoteException e){               
                 //manda remover o transfer q lan?ou a remote exception q aqui sempre sera
                 // q aqui sempre sera o primeiro da lista                       	
                removeTransferPipe((TransferPipe)transfersList.getFirst());
                logger.exception(getClass().getName() + ".run()", e);
                logger.info(getClass().getName() + ".run()", "The TransferPipe was removed. This Download, now, has "+transfersList.size()+" Transfers.");
            }
            catch (IOException e) {               
                logger.exception(getClass().getName() + ".run()", e);
            }
            
            //Fecha o arquivo
            finally{
                closeFile();
                if(getStatus() == COMPLETED) {
                    logger.info(getClass().getName() + ".run()", "The Download is completed.");
                    setChanged();
                    notifyObservers();
                }
            }           
        }        
    }

    /**
     * Returns the String representation of this Download.
     * @return The String representation of this Download.
     */
    public synchronized String toString(){
        String temp;
        temp = "[SimpleDownload] Seeds: " + getNumberOfTransfers() + " " + file.getName() + " " + downloaded + "/" + size + " [" + this.getDownloadProgress() + "%] " + " (" + getSpeed() + " KB/s) " + hash + "  ";
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