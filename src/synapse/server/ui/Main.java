package synapse.server.ui;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.ourgrid.yal.Logger;
import org.ourgrid.yal.LoggerCreator;

import synapse.common.SynapseLogWriter;
import synapse.common.URLProvider;
import synapse.server.ServerConfig;
import synapse.server.ServerFacade;

/**
 * The Main class used to run the server.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class Main {

    public static void main(String[] args) {

        Logger logger = LoggerCreator.config(SynapseLogWriter.getLogWriter(ServerConfig.getLogFilename(), true));

        /*
         * Creates the registry
         */
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(Integer.parseInt(ServerConfig.getPort()));

        } catch (RemoteException e) {
            System.err.println("Could not create a registry!");;
            System.err.println("Error cause: " + e.getMessage());
            logger.exception(Main.class.getName() + ".main()", e);
            System.exit(1);
        }

        System.out.println("Starting Synapse Server...");

        ServerFacade facade = new ServerFacade();

        try {
            Naming.rebind(URLProvider.ServerManager(), new ServerManagerImpl(facade));

        } catch (Exception e) {
            System.err.println("Could not bind ServerManager!");
            System.err.println("Error couse: " + e.getMessage());
            logger.exception(Main.class.getName() + ".main()", e);
            System.exit(2);
        }

        facade.config();
        
        facade.startEventProcessor();
        
        System.out.println("Server is Up and Running!");

    }
}
