package synapse.client.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import org.ourgrid.yal.Logger;
import org.ourgrid.yal.LoggerCreator;

import synapse.client.ClientCommunicator;
import synapse.client.ClientConfig;
import synapse.client.ClientFacade;
import synapse.client.manager.HashDoesNotExistException;
import synapse.client.manager.RequestIDDoesNotExistException;
import synapse.common.Provider;
import synapse.common.SynapseLogWriter;

/**
 * The main class used to run de client.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class Main {

    private static final int RESUME_INT = 0;

    private static final int PAUSE_INT = 1;

    private static final int PAUSEALL_INT = 2;

    private static final int CANCEL_INT = 3;
    
    private static final String SEARCH = "search";
    
    private static final String SEARCH_SYNTAX = SEARCH + " <argument>";

    private static final String MORE = "more";

    private static final String MORE_SYNTAX = MORE + " <hash>";

    private static final String LIST = "list";
    
    private static final String RESULTS = "results";
    
    private static final String UPLOADS = "uploads";
    
    private static final String DOWNLOADS = "downloads";
    
    private static final String LIST_SYNTAX = LIST + " <" + RESULTS + " | " + UPLOADS + " | " + DOWNLOADS + ">";

    private static final String GET = "get";
    
    private static final String GET_SYNTAX = GET + " <id> <hash>";

    private static final String RESUME = "resume";
    
    private static final String RESUME_SYNTAX = RESUME + " <hash>";
    
    private static final String PAUSE = "pause";
    
    private static final String PAUSE_SYNTAX = PAUSE + " <hash>";
    
    private static final String PAUSEALL = "pauseAll";
    
    private static final String CANCEL = "cancel";
    
    private static final String CANCEL_SYNTAX = CANCEL + " <hash>";

    private static final String INVALIDATE = "invalidate";

    private static final String INVALIDATE_SYNTAX = INVALIDATE + " <hash>"; 

    private static ClientCommunicator communicator;

    private static void printUsage() {
        System.out.println("Usage: java -classpath YOUR_CLASS_PATH Main <rmi_remote_address>");
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            printUsage();
            System.exit(1);
        }
        else {
	        Logger logger = LoggerCreator.config(SynapseLogWriter.getLogWriter(ClientConfig.getLogFilename(), true));
	        
	        Provider provider = null;
	        
	        System.out.println("Connecting to Synapse server...");
	        
	        try {
	            
	            provider = (Provider) Naming.lookup(args[0]);
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.exit(2);
	        }
	        
	        System.out.println("Connection established!");
	        
	        System.out.println();
	        System.out.println("Loading the shared folder... (this operation can take serveral minutes)");
	        
	        ClientFacade facade = new ClientFacade();
	        
	        facade.config();
	        
	        System.out.println("Files loaded!");
	        
	        facade.startEventProcessor();
	
	        try {
	            provider.identify(facade.getClient());
	            logger.info(Main.class.getName(), "Identify command was sent.");
	        } catch (RemoteException e) {
	            logger.info(Main.class.getName(), "Identify command could not be sent.");
	            logger.exception(Main.class.getName(), e);
	            System.exit(3);
	        }

	        communicator = new ClientCommunicator(provider, facade.getClient());

	        System.out.println();
	        System.out.println("Reading commands...");
	        
	        String line;
	        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	        
	        try {
	            System.out.print("> ");
	            line = in.readLine();
	            
		        while ( line != null && !line.equals("exit") ) {
		        	
		            StringTokenizer tks = null;
		            String command = "";
		            
		            if (! line.equals("") ) {
		                tks = new StringTokenizer(line);
		                command = tks.nextToken();
		            }

		        	if (command.equals(SEARCH)) {
		        	    try {
			            	String searchArgument;
			            	if ((searchArgument = tks.nextToken("\"")).trim().equals("")) {
			            		searchArgument = tks.nextToken("\"");
			            	}
		        	        communicator.search(searchArgument);
		        	    } catch (NoSuchElementException e) {
		        	        System.out.println("Usage: " + SEARCH_SYNTAX);
		        	        System.out.println("<argument> is \"something like this\" or a simple text without quotes");
		        	    }
		        	}
		        	else if (command.equals(MORE)) {
		        	    try {
			        	    communicator.searchMore(tks.nextToken());
		        	    } catch (NoSuchElementException e) {
		        	        System.out.println("Usage: " + MORE_SYNTAX);
		        	    }
		        	}
		        	else if (command.equals(LIST)) {
		        	    try {
			        	    String command2 = tks.nextToken();
			        	    
			        	    if (command2.equals(RESULTS)) {
				        	    printResults(communicator.listResults());
			        	    }
			        	    else if (command2.equals(UPLOADS)) {
			        	        Collection running = communicator.listRunningUploads();
			        	        if (running.size() == 0) {
			        	        	System.out.println("There are no uploads running. ");
			        	        }
			        	        else {
			        	        	System.out.println("Uploads running: ");
			        	        	printUsingIterator(running.iterator());
			        	        }
			        	        Collection waiting = communicator.listWaitQueueUploads();
			        	        if (waiting.size() == 0) {
			        	        	System.out.println("There are no uploads waiting. ");
			        	        }
			        	        else{
			        	        	System.out.println("Uploads waiting: ");
			        	        	printUsingIterator(waiting.iterator());
			        	        }
			        	    }
			        	    else if (command2.equals(DOWNLOADS)) {
			        	        List list = communicator.listDownloads();
			        	        
			        	        System.out.println("There are " + list.size() + " downloads:");
			        	        
			        	        printUsingIterator(list.iterator());
			        	    }
		        	    } catch (NoSuchElementException e) {
		        	        System.out.println("Usage: " + LIST_SYNTAX);
		        	    }
		        	}
		        	else if (command.equals(GET)) {
		        	    try {
			        	    long id = Long.parseLong(tks.nextToken());
		        	        String hash = tks.nextToken();
			        	    try {
			        	        communicator.sendGetToProviders(id, hash);
			        	    } catch (RequestIDDoesNotExistException e) {
			        	        System.out.println("The id <" + id + "> doesn't exist.");
			        	    } catch (HashDoesNotExistException e) {
			        	        System.out.println("The hash <" + hash + "> doesn't exist.");
	                        }
		        	    } catch (NoSuchElementException e) {
		        	        System.out.println("Usage: " + GET_SYNTAX);
		        	    }
		        	}
		        	else if (command.equals(RESUME)) {
		        	    try {
			        	    String hash = tks.nextToken();
			        	    manipulateDownload(RESUME_INT, hash);
		        	    } catch (NoSuchElementException e) {
		        	        System.out.println("Usage: " + RESUME_SYNTAX);
		        	    }
		        	}
		        	else if (command.equals(PAUSE)) {
		        	    try {
			        	    String hash = tks.nextToken();
			        	    manipulateDownload(PAUSE_INT, hash);
		        	    } catch (NoSuchElementException e) {
		        	        System.out.println("Usage: " + PAUSE_SYNTAX);
		        	    }
		        	}
		        	else if (command.equals(PAUSEALL)) {
		        	    manipulateDownload(PAUSE_INT, "");
		        	}
		        	else if (command.equals(CANCEL)) {
		        	    try {
			        	    String hash = tks.nextToken();
			        	    manipulateDownload(CANCEL_INT, hash);
		        	    } catch (NoSuchElementException e) {
		        	        System.out.println("Usage: " + CANCEL_SYNTAX);
		        	    }
		        	}
		        	else if (command.equals(INVALIDATE)) {
		        	    try {
		        	        String hash = tks.nextToken(); 
                            try {
                                communicator.invalidatePipe(hash);
                            } catch (HashDoesNotExistException e1) {
                                System.out.println("The hash <" + hash + "> doesn't exist.");
                            }
		        	    } catch (NoSuchElementException e) {
		        	        System.out.println("Usage: " + INVALIDATE_SYNTAX);
		        	    }
		        	}
		            else if (command.equals("help")) {
		                System.out.println("The commands are:");
		                
		                System.out.println("\t" + SEARCH_SYNTAX + " - searches a filename with the specified argument.");
		                System.out.println("\t" + MORE_SYNTAX + " - searches a filename with the specified hash.");
		                System.out.println("\t" + LIST_SYNTAX + " - shows the search results or uploads and downloads status.");
		                System.out.println("\t" + GET_SYNTAX + " - sends the 'get' command to providers.");
		                System.out.println("\t" + PAUSE_SYNTAX + " - pause a current download.");
		                System.out.println("\t" + PAUSEALL + " - pause all downloads.");
		                System.out.println("\t" + CANCEL_SYNTAX + " - cancels a specified download.");
		                System.out.println("\t" + INVALIDATE_SYNTAX + " - cancels a specified upload.");
		                
		                System.out.println("\texit - close the program");
		                System.out.println("\n\thelp - this help");
		            }
		            else {
		            	System.out.println("Unknow command: '" + command + "'");
		            }
		            
		            System.out.print("> ");
		            line = in.readLine();
		        }
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.exit(4);
	        } catch (ClientNotConnectedException e) {
				System.err.println(e.getMessage());
				System.exit(5);
			}
	
	        // shutdowns the facade
	        facade.shutdown().blockingRemove();
	        System.out.println("Bye!");

	        System.exit(0);
        }
    }

    private static void skipLines(int number) {
        for (int i = 0; i < number; i++) {
            System.out.println();
        }
    }

    private static void printResults(Map resultByID) {
        Set keys = resultByID.keySet();

        if (keys.isEmpty()) {
            System.out.println("There are no searches.");
        }
        else {
        	System.out.println("Results:");
		    Iterator it = keys.iterator();
		    while (it.hasNext()) {
		        Long id = (Long) it.next();
		        System.out.println("  ID: " + id);
		        
		        Map results = (Map) resultByID.get(id);
		        if (results != null) {
	    	        Iterator it2 = results.keySet().iterator();
	    	        while (it2.hasNext()) {
	    	            String hash = (String) it2.next();
	    	            System.out.println("\t" + results.get(hash));
	    	        }
		        }
		        else {
		            System.out.println("\t(no results)");
		        }
		        
		        System.out.println();
		    }
        }
    }

    private static void printUsingIterator(Iterator it) {
        while (it.hasNext()) {
            System.out.println("\t" + it.next());
        }
    }

    private static void manipulateDownload(int command, String param) {
	    try {
	        switch (command) {
	        	case RESUME_INT:
	        	    communicator.resume(param);
	        	    break;
	        	case PAUSE_INT:
	        	    communicator.pause(param);
	        	    break;
	        	case PAUSEALL_INT:
	        	    communicator.pauseAll();
	        	    break;
	        	case CANCEL_INT:
	        	    communicator.cancel(param);
	        	    break;
	        }
	    } catch (HashDoesNotExistException e) {
	        System.out.println("The hash " + param + " doesn't exist.");
	    }
    }
}