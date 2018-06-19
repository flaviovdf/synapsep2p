package synapse.common;

/**
 * It's the configuration superclass.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class Config {

    public static final String SYNAPSE_DIR = "SYNAPSE_DIR";

    public static String getRootDir() {
        String prop = System.getProperty(SYNAPSE_DIR); 

        return (prop == null ? "." : prop);
    }

}
