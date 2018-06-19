package synapse.server.protocol;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import junit.framework.TestCase;
import synapse.common.FakeConsumer;
import synapse.common.FakeProvider;
import synapse.server.ServerConfig;

/**
 * Test for <code>TerminalImpl</code>.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class TerminalImplTest extends TestCase {

	private FakeProvider fakeProvider1;

	private FakeProvider fakeProvider2;

    private TerminalImpl terminal1;
    
    private TerminalImpl terminal2;
    
    private TerminalImpl terminal3;
    
    private TerminalImpl terminal4;
    
    private String address1;
    
    private String address2;
    
    private String address3;
    
    private String address4;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.fakeProvider1 = new FakeProvider();
        this.fakeProvider2 = new FakeProvider();

        this.terminal1 = new TerminalImpl( this.fakeProvider1 );
        this.terminal2 = new TerminalImpl( this.fakeProvider2 );
        this.terminal3 = new TerminalImpl( this.fakeProvider1 );
        this.terminal4 = new TerminalImpl( this.fakeProvider2 );
        
        this.address1 = "rmi://" + ServerConfig.getHostname() + ":" + ServerConfig.getPort() + "/Terminal1";
        this.address2 = "rmi://" + ServerConfig.getHostname() + ":" + ServerConfig.getPort() + "/Terminal2";
        this.address3 = "rmi://" + ServerConfig.getHostname() + ":" + ServerConfig.getPort() + "/Terminal3";
        this.address4 = "rmi://" + ServerConfig.getHostname() + ":" + ServerConfig.getPort() + "/Terminal4";

        try {
            // starting registry
            LocateRegistry.createRegistry(Integer.parseInt(ServerConfig.getPort()));

        } catch (RemoteException e) {
            LocateRegistry.getRegistry(ServerConfig.getPort());
        }

        Naming.rebind(this.address1, this.terminal1);
        Naming.rebind(this.address2, this.terminal2);
        Naming.rebind(this.address3, this.terminal3);
        Naming.rebind(this.address4, this.terminal4);
    }

    /**
     * Tests the <code>linkTo()</code>.
     * 
     * @throws RemoteException A remote exception.
     */
    public void testLinkTo() throws RemoteException {
        this.terminal1.linkTo(this.terminal2);
        this.terminal3.linkTo(this.terminal4);
        assertTrue(this.terminal1.getKnownTerminals().contains(this.terminal2));
        assertTrue(this.terminal3.getKnownTerminals().contains(this.terminal4));
    }

    /**
     * A simple stress test.
     * 
     * @throws Exception A possible exception.
     */
    public void testStressConnect() throws Exception {
        
        final int NUMBER_OF_TERMINALS = 10;
        String[] addresses = new String[ NUMBER_OF_TERMINALS ];
        TerminalImpl[] terminals = new TerminalImpl[ NUMBER_OF_TERMINALS ];
        
        // creates the objects
        for (int i = 0; i < NUMBER_OF_TERMINALS; i++) {
            
            terminals[i] = new TerminalImpl( null );
            addresses[i] = "rmi://localhost:1551/Term" + i;
            
            Naming.rebind(addresses[i], terminals[i]);

        }
        
        for (int i = 1; i < NUMBER_OF_TERMINALS; i++) {
            
            // connect terminal "i" to terminal "i - 1" using RMI
            terminals[i].connect(addresses[i - 1]);
        }
        
        // check the known servers
        for (int i = 0; i < NUMBER_OF_TERMINALS; i++) {
            for (int j = 0; j < NUMBER_OF_TERMINALS; j++) {
                
                if (i != j) {
                    assertTrue(terminals[i].getKnownTerminals().contains(terminals[j]));
                }
                
            }
        }
    }

    /**
     * Verifies if the exception is been thrown correctly.
     * 
     * @throws Exception A possible exception.
     */
    public void testConnectingToMyself() throws Exception {
        try {
            this.terminal1.connect(this.address1);
            fail("A ConnectionException should be thrown.");
        } catch (ConnectionException e) {
            // this line has to be executed
        }

        try {
            this.terminal1.linkTo(this.terminal1);
            fail("A ConnectionException should be thrown.");
        } catch (ConnectionException e) {
            // this line has to be executed
        }
    }

    /**
     * Verifies if the <code>connect()</code> method works on "one to one" connection.
     * 
     * @throws RemoteException A remote exception.
     */
    public void testConnectOneToOne() throws Exception {
        // connect terminal1 to terminal2 using RMI
        this.terminal1.connect(this.address2);
        // check the known servers
        assertTrue(this.terminal1.getKnownTerminals().contains(this.terminal2));
        assertTrue(this.terminal2.getKnownTerminals().contains(this.terminal1));
    }
    
    /**
     * Verifies if the <code>connect()</code> method works on "two to one" connection.
     * 
     * @throws Exception A possible exception.
     */
    public void testConnectTwoToOne() throws Exception {
        // connect terminal1 to terminal2 and
        // terminal2 to linker 3 using RMI
        this.terminal1.connect(this.address2);
        this.terminal2.connect(this.address3);
        // check the known servers
        assertTrue(this.terminal1.getKnownTerminals().contains(this.terminal2));
        assertTrue(this.terminal2.getKnownTerminals().contains(this.terminal1));
        assertTrue(this.terminal1.getKnownTerminals().contains(this.terminal3));
        assertTrue(this.terminal3.getKnownTerminals().contains(this.terminal1));
        assertTrue(this.terminal2.getKnownTerminals().contains(this.terminal3));
        assertTrue(this.terminal3.getKnownTerminals().contains(this.terminal2));
    }

    /**
     * Verifies if the <code>connect()</code> method works on "two to two" connection.
     * 
     * @throws Exception A possible exception.
     */
    public void testConnectTwoToTwo() throws Exception {
        // connect terminal1 to terminal2 and
        // terminal3 to terminal4 using RMI
        this.terminal1.connect(this.address2);
        this.terminal3.connect(this.address4);
        // check the known servers
        assertTrue(this.terminal1.getKnownTerminals().contains(this.terminal2));
        assertTrue(this.terminal2.getKnownTerminals().contains(this.terminal1));
        assertTrue(this.terminal3.getKnownTerminals().contains(this.terminal4));
        assertTrue(this.terminal4.getKnownTerminals().contains(this.terminal3));
        
        // connect terminal
        this.terminal3.connect(this.address2);
        // check the known servers
        assertTrue(this.terminal1.getKnownTerminals().contains(this.terminal2));
        assertTrue(this.terminal2.getKnownTerminals().contains(this.terminal1));
        assertTrue(this.terminal1.getKnownTerminals().contains(this.terminal3));
        assertTrue(this.terminal3.getKnownTerminals().contains(this.terminal1));
        assertTrue(this.terminal1.getKnownTerminals().contains(this.terminal4));
        assertTrue(this.terminal4.getKnownTerminals().contains(this.terminal1));
        assertTrue(this.terminal2.getKnownTerminals().contains(this.terminal3));
        assertTrue(this.terminal3.getKnownTerminals().contains(this.terminal2));
        assertTrue(this.terminal2.getKnownTerminals().contains(this.terminal4));
        assertTrue(this.terminal4.getKnownTerminals().contains(this.terminal2));
        assertTrue(this.terminal3.getKnownTerminals().contains(this.terminal4));
        assertTrue(this.terminal4.getKnownTerminals().contains(this.terminal3));
    }

    /**
     * Tests if the <code>searchFile()</code> method is been invoked
     * in all of known providers.
     * 
     * @throws Exception If something wrong happens.
     */
    public void testSearch() throws Exception {
        // connect terminal1 to terminal2 using RMI
        this.terminal1.connect(this.address2);

        // prepares the fakes
        this.fakeProvider1.setExpectedSearchForCommunity(1);
        this.fakeProvider2.setExpectedSearchForCommunity(0);
        // performs the search
        this.terminal2.searchForCommunity(1, new FakeConsumer(), "file1");
        // verifies
        this.fakeProvider1.verify();
        this.fakeProvider2.verify();
        
        // reset
        this.fakeProvider1.reset();
        this.fakeProvider2.reset();

        // prepares the fakes
        this.fakeProvider1.setExpectedSearchForCommunity(0);
        this.fakeProvider2.setExpectedSearchForCommunity(1);
        // performs the search
        this.terminal1.searchForCommunity(1, new FakeConsumer(), "file2");
        // verifies
        this.fakeProvider1.verify();
        this.fakeProvider2.verify();
        
        // finishing
        assertEquals(1, this.terminal1.getKnownTerminals().size());
        assertEquals(1, this.terminal2.getKnownTerminals().size());
    }

}
