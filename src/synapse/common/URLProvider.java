package synapse.common;

import synapse.server.ServerConfig;

/**
 * This class contains the RMI Addresses used to export the objects.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class URLProvider {

    public static String Proxy() {
        return "rmi://" + ServerConfig.getHostname() + ":" + ServerConfig.getPort() + "/Proxy";
    }

    public static String ServerManager() {
        return "rmi://" + ServerConfig.getHostname() + ":" + ServerConfig.getPort() + "/ServerManager";
    }

    public static String Terminal() {
        return "rmi://" + ServerConfig.getHostname() + ":" + ServerConfig.getPort() + "/Terminal";
    }

    public static String TerminalServices() {
        return "rmi://" + ServerConfig.getHostname() + ":" + ServerConfig.getPort() + "/TerminalServices";
    }

}
