package synapse.server.protocol;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.ourgrid.yal.Logger;

import synapse.common.Consumer;
import synapse.common.Provider;

/**
 * This class acts as a <code>Provider</code>, however it works to get files in other servers.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class TerminalImpl extends UnicastRemoteObject implements Terminal {

    /**
     * This map stores the other known terminals.
     */
    private Set knownTerminals;

    /**
     * The provider given to another servers.
     */
    private Provider provider;

    /**
     * The logger.
     */
    private Logger logger;

    /**
     * Creates a new Terminal.
     * 
     * @param provider The provider given to another servers. It's useful
     * to allow another servers to access this one.
     * @throws RemoteException A remote exception.
     */
    public TerminalImpl(Provider provider) throws RemoteException {

        this.knownTerminals = new HashSet();
        this.provider = provider;
        this.logger = Logger.getInstance();

    }

    /**
     * Links this terminal with another one.
     * After this method call, the terminal will be connected with all terminals
     * previous connected to the terminal exported in <code>address</code>.
     * 
     * @param address The RMI address where the other <code>Terminal</code> is exported.
     * @throws MalformedURLException If the name is not an appropriately formatted URL.
     * @throws RemoteException If registry can't be contacted.
     * @throws NotBoundException If name is not currently bound.
     */
    public void connect(String address) throws MalformedURLException, RemoteException, NotBoundException {

        logger.info(getClass().getName() + ".connect()", "Connecting to the server <" + address + ">.");

        Collection localTerminals = new LinkedList();
        Collection remoteTerminals = new LinkedList();
        
        localTerminals.add(this);
        localTerminals.addAll(this.knownTerminals);
        Terminal term = (Terminal) Naming.lookup(address);
        // verifies if the looked up object isn't this one
        if (term.equals(this)) {
            throw new ConnectionException("You are trying to connect to yourself.");
        }
        // continue
        remoteTerminals.add(term);
        remoteTerminals.addAll(term.getKnownTerminals());

        this.linkTerminals(localTerminals, remoteTerminals);
        
        logger.info(getClass().getName() + ".connect()", "Connection established.");
    }

    /*
     * (non-Javadoc)
     * @see synapse.server.protocol.Terminal#getKnownTerminals()
     */
    public Collection getKnownTerminals() throws RemoteException {
        return new HashSet(this.knownTerminals);
    }

    /* (non-Javadoc)
     * @see synapse.server.protocol.Terminal#getServer()
     */
    public Provider getProvider() throws RemoteException {
        return this.provider;
    }

    /*
     * (non-Javadoc)
     * @see synapse.server.protocol.Terminal#linkTo(synapse.server.protocol.Terminal)
     */
    public void linkTo(Terminal terminal) throws RemoteException {
        if (terminal.equals(this)) {
            throw new ConnectionException("You are trying to connect to yourself.");
        }
        // continue
        synchronized (knownTerminals) {
        	this.knownTerminals.add(terminal);
		}
        
        logger.debug(getClass().getName() + ".linkTo()", "Linked to " + terminal + ". The total number of terminals is " + this.knownTerminals.size());
    }

    /* (non-Javadoc)
     * @see synapse.server.protocol.Terminal#searchFromCommunity(synapse.server.protocol.Terminal, long, synapse.common.Consumer, java.lang.String)
     */
    public void searchFromCommunity(Terminal terminal, long id, Consumer consumer, String fileName) throws RemoteException {
		try {
			if (this.knownTerminals.contains(terminal)) {
				this.provider.searchForCommunity(id, consumer, fileName);
			}
			else {
				logger.error(getClass().getName() + ".searchFromCommunity()", "A unknown Terminal performed a search but it was canceled.");
			}
		} catch (RemoteException e) {
			// do nothing
			logger.error(getClass().getName() + ".searchFile()", "Could not contact the local provider.");
		}
    }

    /**
     * This method takes two <code>Collection</code>s containing
     * <code>Terminal</code>s and links them.
     * 
     * @param set1 The first collection.
     * @param set2 The second collection.
     */
    private void linkTerminals(Collection set1, Collection set2) {
        
        Iterator it1 = set1.iterator();
        while ( it1.hasNext() ) {
            Terminal term1 = (Terminal) it1.next();
            
            Iterator it2 = set2.iterator();
            while ( it2.hasNext() ) {
                Terminal term2 = (Terminal) it2.next();
                try {
                    term1.linkTo(term2);
                    term2.linkTo(term1);
                } catch (RemoteException e) {
                    // this catch just skip the linking
                }
            }
        }
    }

    /**
     * Search the file in other serves.
     */
    public void searchForCommunity(long id, Consumer consumer, String fileName) {

    	Iterator it;
    	synchronized (knownTerminals) {
    		it = new HashSet(this.knownTerminals).iterator();
    	}
        while (it.hasNext()) {
            Terminal t = (Terminal) it.next();
            try {
                t.searchFromCommunity(this, id, consumer, fileName);
            } catch (RemoteException e) {
            	synchronized (knownTerminals) {
            		this.knownTerminals.remove(t);
            	}
                logger.info(getClass().getName() + ".searchFile()", "A terminal was removed. The total number is " + knownTerminals.size());
            }
        }
        
        logger.info(getClass().getName() + ".searchFile()", "The search was performed to " + this.knownTerminals.size() + " providers on Community.");
    }

    /* (non-Javadoc)
     * @see synapse.server.protocol.Terminal#getName()
     */
    public String getName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "(invalid hostname)";
        }
        
    }

}