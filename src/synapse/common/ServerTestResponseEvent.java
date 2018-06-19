package synapse.common;

import synapse.common.events.ResponseEvent;

/**
 * ServerTestResponseEvent This Event is used only for testing. It is put on a EventQueue when the ServerTestRequestEvent's
 * <code>process</code>  method is invoked.
 * 
 * @author <p>Flavio Roberto, flaviors@dsc.ufcg.edu.br</p> 
 */
public class ServerTestResponseEvent implements ResponseEvent {

	/**
	 * Returns Boolean.True.
	 * 
	 * @return Boolean.True.
	 * @see synapse.common.events.ResponseEvent#getResponse()
	 */
	public Object getResponse() {		
		return Boolean.TRUE;
	}

}
