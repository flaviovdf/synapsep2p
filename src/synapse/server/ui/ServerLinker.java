package synapse.server.ui;

import synapse.common.UIException;
import synapse.server.ServerNotRunningException;

/**
 * This class allows the user to link this server to another one.
 * After that, the searches in this server will be spread to other servers
 * linked to this one.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ServerLinker {

    public static void main(String[] args) {
        
        if (args.length != 1) {
            printUsage();
            System.exit(1);
        }
        else {
            try {
		        System.out.println("Connecting to " + args[0]);
		        ServerConcreteUIServices.getInstance().connect(args[0]);
		        System.out.println("The servers are linked!");
		        System.exit(0);
		    } catch (UIException e) {
		        System.err.println("The specified server could not be contacted!");
		        System.err.println("Error cause: " + e.getMessage());
		        System.exit(2);
		    } catch (ServerNotRunningException e) {
		        System.err.println("Error: Synapse Server is not running.");
		        System.exit(3);
		    }
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java -classpath YOUR_CLASS_PATH ServerLinker <rmi_remote_address>");
    }
}
