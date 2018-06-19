package synapse.client;

/**
 * It's thrown when a pipe communicator is invalidated.
 * 
 * @author <p>Thiago Emmanuel, thiago.manel@gmail.com</p>
 */
public class InvalidTransferPipeException extends Exception {
	
	public InvalidTransferPipeException(){
		super ("Invalid TransferPipe");
	}

}

