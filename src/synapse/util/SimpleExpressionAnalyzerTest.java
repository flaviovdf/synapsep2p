package synapse.util;

import junit.framework.TestCase;

/**
 * Test for the <code>SimplesExpressionAnalyzer</code>.
 * 
 * @author <p>Thiago Emmanuel, thiago.manel@gmail.com</p>
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class SimpleExpressionAnalyzerTest extends TestCase {

    private SimpleExpressionAnalyzer analyzer;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        analyzer = new SimpleExpressionAnalyzer();
    }

    /**
     * Tests too many situations about the <code>accept()</code> condition.
     */
    public void testAccept() {
        //test invalids regex
        assertFalse(analyzer.accept("",""));
        assertFalse(analyzer.accept("!", "!"));
        assertFalse(analyzer.accept("+", "+"));
        
        assertTrue(analyzer.accept("infO.TxT", "Info.txt"));
        assertTrue(analyzer.accept("fiLe my", "My File.zip"));
        assertFalse(analyzer.accept("y.f", "My File.zip"));
        assertTrue(analyzer.accept(" file  ", "  My File"));

        assertTrue(analyzer.accept("    space   two ", " twO    SPACE  "));
        assertFalse(analyzer.accept("   ", "Some        Spaces!"));
        
        assertTrue (analyzer.accept(" turing machine  ", "church turing "));
        
        assertFalse (analyzer.accept("turing !machine ", "turing machine" ));
        assertFalse (analyzer.accept(" !machine turing ", "turing machine" ));
        assertFalse (analyzer.accept("!machine ", "turing machine" ));
        assertTrue (analyzer.accept("turing !machine ", "church - turing " ));
        assertTrue (analyzer.accept(" !machine turing", "church - turing " ));
        assertFalse (analyzer.accept("turing !thesis !church" , "church - turing " ));
        assertTrue (analyzer.accept("turing !thesis !church" , " turing " ));
        assertFalse (analyzer.accept("turing !thesis !church" , " turing church" ));
        assertFalse (analyzer.accept("turing !thesis !church" , " turing church thesis" ));
        assertFalse (analyzer.accept("turing !thesis church" , " turing church thesis" ));
        assertTrue (analyzer.accept("turing !thesis church" , " turing church" ));
        //this a important test. Even so the regex thesis doesn't exists in a phrase
        // "" turing church", the method accept return false, because was used only 
        // ! operator, otherwise a catastrophic situation may be occur. For example
        //the case    accept (regex-> !(improbable word), word->(any word)) always 
        //returns true , and will occur a overload in a system
        assertFalse (analyzer.accept(" !thesis " , " turing church " ));
        
        assertFalse (analyzer.accept(" turing+thesis "  , " turing church" ));
        assertTrue (analyzer.accept(" turing+thesis "  , " turing thesis " ));
        assertTrue (analyzer.accept(" turing+thesis "  , " turing thesis church" ));
        assertTrue (analyzer.accept(" turing+thesis foo"  , " turing thesis " ));
        assertTrue (analyzer.accept(" turing+thesis machine "  , " alonzo church machine" ));
        assertFalse (analyzer.accept(" turing+thesis !church"  , " turing thesis church" ));
        assertFalse (analyzer.accept(" turing+thesis church !halt"  , " turing thesis church halt" ));
        assertTrue (analyzer.accept(" turing+thesis church !halt"  , " bla   church" ));

    }

}