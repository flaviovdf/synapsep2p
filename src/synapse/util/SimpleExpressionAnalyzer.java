package synapse.util;

import java.util.StringTokenizer;

import synapse.client.ExpressionAnalyzer;

/**
 * A simple <code>ExpressionAnalyzer</code> implementation.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 * @author <p>Thiago Emmanuel, thiago.manel@gmail.com</p>
 */
public class SimpleExpressionAnalyzer implements ExpressionAnalyzer {

    /* (non-Javadoc)
     * @see synapse.client.ExpressionAnalyzer#accept(java.lang.String, java.lang.String)
     */
    public boolean accept(String regex, String word) {
        
        if (! isValid(regex) ) return false;
        boolean returnCase = false;
		StringTokenizer tks = new StringTokenizer( format(regex) );

		while (tks.hasMoreTokens()) {
		    String temp = tks.nextToken();		
		    		    
		    //if exists a ! operator 
		    if (! ( temp.indexOf("!") == -1)){
		        temp = temp.substring(1);
		        //if a word preceded for ! exists in a String 
		        //return false
		        if (!(word.toUpperCase().trim().indexOf(temp) == -1)) {
					return false;
				}
		    }//end of ! case
		    
		    //if exists a + operator 
		    else if (! ( temp.indexOf("+") == -1)){
		    		String arg1,arg2;
		    		int i = temp.indexOf("+");
		    		arg1 = temp.substring(0,i);
		    		arg2 = temp.substring(i+1);
		    	
		    		//if the word doesn't contain both the keys  
		    		if (!(accept(arg1,word) && accept(arg2,word)))
		    		    returnCase =  false;	
		    		else returnCase = true;
		    }
		    		    		    
		    else {
				if (!(word.toUpperCase().trim().indexOf(temp) == -1)) {
				    returnCase = true;
				}
		    }
		}
		return returnCase;
     
    }

    /**
     * Puts the tokens with, and only with, one blank space
     * between them.
     * 
     * @param regex The expression to be formatted.
     * @return A new string without white spaces in excess.
     */
    private String format(String regex) {
        String result = "";
        StringTokenizer tks = new StringTokenizer(regex);
        
        while (tks.hasMoreTokens()) {
            result += tks.nextToken();
            
            if (tks.hasMoreTokens()) {
                result += " ";
            }
        }

        return result.toUpperCase();
    }

    /**
     * Check if a given expression is in a valid form.
     * 
     * @param regex The expression to be verified.
     * @return true if it's in a valid form.
     */
    private boolean isValid(String regex) {
        return  (!regex.trim().equals("") && !regex.trim().equals("-") &&  !regex.trim().equals("+"));
    }

}
