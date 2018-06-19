package synapse.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.ourgrid.yal.Logger;

import synapse.common.Config;

/**
 * The configuration used by the client.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ClientConfig extends Config {

	/**
	 * The client property full path.
	 */
    private static final String PROPERTIES_FILENAME = getRootDir() + File.separator + "client.properties";

    /**
     * Port field.
     */
    public static final String PORT = "port";

    /**
     * Log name field.
     */
    public static final String LOGNAME = "logname";

    /**
     * Shared folder field.
     */
    public static final String SHARED_FOLDER = "sharedfolder";

    /**
     * Test folder full path.
     */
    private static final String TEST_FOLDER = getRootDir() + File.separator + "testDestination";

    /**
     * Client current properties.
     */
    private static Properties properties;

    /**
     * Loads the properties on file <code>PROPERTIES_FILENAME</code>.
     */
    private static void loadProperties() {
    	FileInputStream in = null;
        try {
            properties = new Properties();
            in = new FileInputStream(PROPERTIES_FILENAME);
            properties.load(in);
        } catch (IOException e) {
            loadDefaults();
        } finally {
    		if (in != null) {
    			try {
					in.close();
				} catch (IOException e1) {
					Logger.getInstance().error(ClientConfig.class.getName() + ".saveProperties()", "Could not close the file.");
				}
    		}
        }
    }

    /**
     * Saves the properties to disk.
     */
    public static void saveProperties() {
    	FileOutputStream out = null;
    	try {
	    	out = new FileOutputStream(PROPERTIES_FILENAME);
	        properties.store(out, "Client Configuration");
    	} catch (IOException e) {
    		Logger.getInstance().error(ClientConfig.class.getName() + ".saveProperties()", "Could not save the file " + PROPERTIES_FILENAME + ".");
    	} finally {
    		if (out != null) {
    			try {
					out.close();
				} catch (IOException e1) {
					Logger.getInstance().error(ClientConfig.class.getName() + ".saveProperties()", "Could not close the file.");
				}
    		}
    	}
    }

    /**
     * Loads the default properties.
     */
    private static void loadDefaults() {
        properties.setProperty(PORT, "1551");
        properties.setProperty(LOGNAME, "client.log");
        properties.setProperty(SHARED_FOLDER, "sharedFolder");

        saveProperties();
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

    /**
     * Returns the shared folder name.
     * 
     * @return The shared folder.
     */
    public static String getSharedFolder() {
        if (properties == null) {
            loadProperties();
        }
        return properties.getProperty(SHARED_FOLDER);
    }

    /**
     * Returns the folder name used on tests.
     * 
     * @return The folder name. 
     */
    public static String getTestFolder() {
        if (properties == null) {
            loadProperties();
        }
        return TEST_FOLDER;
    }

}