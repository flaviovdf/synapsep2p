package synapse.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import synapse.common.Config;

/**
 * The configuration used by the server.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ServerConfig extends Config {

    /**
     * The server properties file name.
     */
    private static final String PROPERTIES_FILENAME = getRootDir() + File.separator + "server.properties";

    /**
     * The property name used to define the port.
     */
    public static final String PORT = "port";

    /**
     * The property name used to define the hostname.
     */
    public static final String HOSTNAME = "hostname";

    /**
     * The property name used to define the maxretries.
     */
    public static final String MAX_RETRIES = "maxretries";

    /**
     * The property name used to define the logfile name.
     */
    public static final String LOGNAME = "logname";

    /**
     * The <code>Properties</code> object instance.
     */
    private static Properties properties;

    /**
     * Loads the properties from disk or the defaults if some error happens.
     */
    private static void loadProperties() {
        try {
            properties = new Properties();
            FileInputStream in = new FileInputStream(PROPERTIES_FILENAME);
            properties.load(in);
            in.close();
        } catch (Exception e) {
            loadDefaults();
        }
    }

    /**
     * Loads the default properties.
     */
    private static void loadDefaults() {
        properties.setProperty(PORT, "1551");
        properties.setProperty(HOSTNAME, "localhost");
        properties.setProperty(MAX_RETRIES, "0");
        properties.setProperty(LOGNAME, "server.log");
        
        FileOutputStream out;
        try {
            out = new FileOutputStream(PROPERTIES_FILENAME);
            properties.store(out, "Server Configuration");
        } catch (Exception e) {
            // do nothing
        }
    }

    /**
     * Sets a specified property.
     * 
     * @param key The property name.
     * @param value A value to be assigned.
     */
    public static void setProperty(String key, String value) {
        if (properties == null) {
            loadProperties();
        }
        properties.setProperty(key, value);
    }

    /**
     * Returns the port value.
     * 
     * @return The port value.
     */
    public static String getPort() {
        if (properties == null) {
            loadProperties();
        }
        return properties.getProperty(PORT);
    }

    /**
     * Returns the server hostname.
     * 
     * @return The hostname.
     */
    public static String getHostname() {
        if (properties == null) {
            loadProperties();
        }
        return properties.getProperty(HOSTNAME);
    }

    /**
     * Returns the max number of retries used to perform any search made by
     * the clients.
     * 
     * @return The number of retries.
     */
    public static String getMaxRetries() {
        if (properties == null) {
            loadProperties();
        }
        return properties.getProperty(MAX_RETRIES);
    }

    /**
     * Returns the logfile name.
     * 
     * @return The logfile name.
     */
    public static String getLogFilename() {
        if (properties == null) {
            loadProperties();
        }
        return getRootDir() + File.separator + "log" + File.separator + properties.getProperty(LOGNAME);
    }
}
