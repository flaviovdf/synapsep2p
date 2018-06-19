package synapse.client.manager;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Observable;

import synapse.client.ClientConfig;
import synapse.common.TransferPipe;

/**
 * This is the basic model to implement a download object.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public abstract class AbstractDownload extends Observable implements Serializable {

    public static final int DOWNLOADING = 0,
						    PAUSED = 1,
						    COMPLETED = 2,
						    ERROR = 3,
						    CANCELED = 4,
						    NOTRANSFERS = 5,
						    SEARCHING = 6;

    protected File file;

    protected long size;

    protected String hash;

    private int status;

    protected transient LinkedList transfersList;

    /**
     * Constructs a <code>AbstractDownload</code> object
     * @param transfer
     * @throws RemoteException
     */
    protected AbstractDownload(TransferPipe transfer) throws RemoteException {

        prepareInstance();

        size = transfer.getSize();
        
        hash = transfer.getHash();

        setStatus(PAUSED);

        transfersList.add(transfer);

        //Cria o arquivo de acordo com o nome do arquivo origem
        file = new File(ClientConfig.getSharedFolder() + File.separator + transfer.getFileName());
        
        //Cria um novo arquivo se ele nao existir
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e3) {
                cancel();
            }
        }

        //verifica se eh possivel escrever e ler o arquivo
        if(!file.canWrite() || !file.canRead()) {
            cancel();
        }
    }

    /**
     * Return the current download status value.
     *  
     * DOWNLOADING = 0,
     * PAUSED = 1,
     * COMPLETED = 2,
     * ERROR = 3,
     * CANCELED = 4,
     * NOTRANSFERS = 5;
     * SEARCHING = 6;
     * @return The Download's status
     */
    public synchronized int getStatus() {
        return this.status;
    }

    /**
     * Returns the file size.
     * @return The file size.
     */
    public long getSize() {
        return this.size;
    }

    /**
     * Returns the File's name.
     * @return The File's name.
     */
    public String getFileName() {
        return this.file.getName();
    }

    /**
     * Sets the download status value.
     * 
     * @param value The new code value.
     */
    protected synchronized void setStatus(int value) {
        this.status = value;
    }

    /**
     * This class represents a Download
     * Adds a new <code>TranferPipe</code> to this Download.
     * @param transfer The pipe to be added.
     */
    public boolean addTransferPipe(TransferPipe transfer) {
        if (!this.contains(transfer)) {
            transfersList.add(transfer);

            if(getStatus() == NOTRANSFERS || getStatus() == SEARCHING){
                setStatus(DOWNLOADING);
                setChanged();
                resume();
            }
            return true;
        }
        return false;
    }

    /**
     * Removes the TransferPipe in this Download.
     * @param transfer to be removed.
     */
    protected void removeTransferPipe(TransferPipe transfer){ 
        this.transfersList.remove(transfer);
        
        //verifica se a lista de Fontes do arquivo esta vazia
        if(getNumberOfTransfers() == 0){
            setStatus(NOTRANSFERS);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Returns the number of TransferPipe in this Download.
     * @return The number of TransferPipe in this Download.
     */
    public int getNumberOfTransfers(){
        return this.transfersList.size();
    }

    /**
     * Returns if this Download contains the TransferPipe.
     * @param transfer The transfer to be checked.
     * @return True if this Download contains the TransferPipe.
     */
    public boolean contains(TransferPipe transfer){
        return this.transfersList.contains(transfer);
    }

    /**
     * Returns download's Hash.
     * @return Hash.
     */
    public String getHash() {
        return this.hash;
    }

    /**
     * Returns the Download's File.
     * @return The Download's File.
     */
    protected File getFile() {
        return this.file;
    }

    /**
     * Returns the percentage of bytes downloades so far.
     * @return The percentage of bytes downloades so far. 
     */
    public abstract float getDownloadProgress();

    /**
     * Returns the current download speed.
     * 
     * @return The current download speed.
     */
    public abstract long getSpeed();

    /**
     * Changes the Download status to DOWNLOADING and call the 
     * method run
     */
    public void resume() {
    	//if not exist transfers the data send is enable,then the <code>AbstractDownload</code>
    	//status changes to SEARCHING, it's a way of inform to managers for search new fonts
    	//to <code>AbstractDownload</code> for example.
        if(transfersList.size() == 0){
            setStatus(SEARCHING);
            setChanged();
            notifyObservers(hash);
        }
        else{
            setStatus(DOWNLOADING);
            setChanged();
            notifyObservers();	
        }
    }

    /**
     * Changes the Download status to CANCELED.
     */
    public void cancel() {
        setStatus(CANCELED);
        getFile().delete();
        setChanged();
        notifyObservers();
    }

    /**
     * Changes the Download status to PAUSED.
     */
    public void pause(){
        setStatus(PAUSED);

        setChanged();
        notifyObservers();
    }

    /**
     * Createss the collection to save the transfers to this Download
     */
    protected void prepareInstance() {
        if (transfersList == null){
            transfersList = new LinkedList();
            setStatus(NOTRANSFERS);
            setChanged();
            notifyObservers();
        }
    }

}