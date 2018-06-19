package synapse.client;

/**
 * It's the interface that have to be implemented by to classes which
 * knows when the search argument matches with the model name.
 * If it satisfies the method <code>accept()</code> have to return <code>true</code>,
 * <code>false</code> otherwise.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 * @author <p>Thiago Emmanuel, thiago.manel@gmail.com</p>
 */
public interface ExpressionAnalyzer {

    /**
     * This method is responsible to do the match between and serach argument
     * (regex) and the <code>word</code>.
     * 
     * @param regex The search argument.
     * @param word The model name.
     * @return true if the parameters matches, false otherwise.
     */
    public boolean accept (String regex, String word);

}
