package synapse.server.ui;

import java.rmi.RemoteException;
import java.util.Iterator;

import synapse.common.UIException;
import synapse.server.ServerNotRunningException;
import synapse.server.protocol.Terminal;

/**
 * This class allows the users to get information about this server.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ServerStatus {

    public static void main(String[] args) {
        
        try {
            Iterator it = ServerConcreteUIServices.getInstance().getKnownTerminals().iterator();

            System.out.println("Servers linked:");

            if (!it.hasNext()) {
            	System.out.println("\t(none)");
            }

            while (it.hasNext()) {
                Terminal term = (Terminal) it.next();

                try {
					System.out.println("\t" + term.getName());
				} catch (RemoteException e1) {
					System.out.println("\t(unreached server)");
				}
            }
            
            System.exit(0);

        } catch (UIException e) {
            System.err.println("The services could not be contacted!");
            System.err.println("Error cause: " + e.getMessage());
            System.exit(1);
        } catch (ServerNotRunningException e) {
	        System.err.println("Error: Synapse Server is not running.");
	        System.exit(2);
	    }
        
    }
}
