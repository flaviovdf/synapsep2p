package synapse.server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import junit.framework.TestCase;
import synapse.common.FakeConsumer;
import synapse.common.FakeEventProcessor;
import synapse.common.FakeProvider;
import synapse.common.Provider;
import synapse.common.URLProvider;

/**
 * Tests for Facade.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ServerFacadeTest extends TestCase {

    /**
     * The object which the method will be invoked.
     */
    private Provider provider;
    
    /**
     * Object to be tested.
     */
    private ServerFacade facade;
    
    /**
     * An auxiliar fake.
     */
    private FakeEventProcessor fakeEventProcessor;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        int registryPort = Integer.parseInt(ServerConfig.getPort());

        try {
            // starting registry
            LocateRegistry.createRegistry(registryPort);

        } catch (RemoteException e) {
            LocateRegistry.getRegistry(registryPort);
        }

        fakeEventProcessor = new FakeEventProcessor();
        facade = new ServerFacade(fakeEventProcessor);
        facade.config();

        provider = (Provider) Naming.lookup(URLProvider.Proxy());
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        
        facade.shutdown().blockingRemove();
    }

    /**
     * Verifies if the <code>Proxy</code> is working correctly
     * with the <code>Facade</code> when the method <code>identify()</code> is called.
     * 
     * @throws RemoteException A remote exception.
     */
    public void testIdentify() throws RemoteException {
        // set up the fake object
        this.fakeEventProcessor.setExpectedPutEvent(1);
        
        // call the method in proxy
        this.provider.identify(new FakeProvider());
        
        // verifies the fake object
        this.fakeEventProcessor.verify();
    }

    /**
     * Verifies if the <code>Proxy</code> is working correctly
     * with the <code>Facade</code> when the method <code>searchFile()</code> is called.
     * 
     * @throws RemoteException A remote exception.
     */
    public void testSearchFile() throws RemoteException {
        // set up the fake object
        this.fakeEventProcessor.setExpectedPutEvent(1);
        
        // call the method in proxy
        this.provider.searchFile(new FakeConsumer(), "bla");
        
        // verifies the fake object
        this.fakeEventProcessor.verify();
    }

}
