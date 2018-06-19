package synapse.server.ui;

import synapse.common.UIException;
import synapse.server.ServerNotRunningException;

/**
 * This application is used to perform the "stop action" in the server.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ServerStopper {

    public static void main(String[] args) {

        try {
            System.out.println("Stopping Synapse Server ...");
            ServerConcreteUIServices.getInstance().stopServer();
            System.out.println("Synapse Server successfully stopped.");
            System.exit(0);
        } catch (UIException e) {
            System.err.println("Synapse Server could not be stopped.");
            System.err.println("Error cause: " + e.getMessage());
            System.exit(1);
        } catch (ServerNotRunningException e) {
	        System.err.println("Error: Synapse Server is not running.");
	        System.exit(2);
	    }
    }
}
