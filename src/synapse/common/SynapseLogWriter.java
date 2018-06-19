package synapse.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.ourgrid.yal.LogEvent;
import org.ourgrid.yal.eventbus.Event;
import org.ourgrid.yal.eventbus.EventConsumer;

/**
 * 
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class SynapseLogWriter implements EventConsumer, Runnable {


    /**
     * LOG Timeout
     */
    public static final int TIMEOUT = 10000;

    /**
     * HashMap containing writers that had been instantiated
     */
    private static HashMap writers = new HashMap();

    /**
     * Disk log file pre-defined by the user.
     */
    private File logFile;

    /**
     * Used to write to the log file.
     */
    private BufferedWriter bw;

    /**
     * Stores the events.
     */
    private Vector logInfo = new Vector();

    /**
     * The constructor.
     */
    private SynapseLogWriter(String file, boolean newFile) {

        try {
            logFile = new File(file);
            if (newFile) {
                logFile.delete();
                logFile.createNewFile();
            }

            bw = new BufferedWriter(new FileWriter(logFile, true));
            bw.close();
        } catch (IOException ioe) {
            System.err.println("Cannot create log file !!!");
        }

        Thread writerThread = new Thread(this, "Log Writer Thread");
        writerThread.setDaemon(true);
        writerThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread("SynapseLogWriter Shutdown Hook") {

            public void run() {

                writeLogInfo();
            }

        });

    }

    /**
     * Writes log info to disk.
     */
    private void writeLogInfo() {

        synchronized (logInfo) {

            try {

                bw = new BufferedWriter(new FileWriter(logFile, true));

                Iterator it = logInfo.iterator();
                while (it.hasNext()) {

                    bw.write(getLogEntry((LogEvent) ((Event) it.next()).getInfo()));

                }

            } catch (IOException ioe) {

                System.out.println("Cannot write to log file !!!");

            } finally {

                try {

                    bw.close();

                } catch (IOException e) {

                    System.out.println("Cannot close to log file !!!");

                }

                logInfo.removeAllElements();

            }

        }
    }

    /**
     * This method returns the single instance of SynapseLogWriter with an specified log file.
     * (Implementation of the Singleton design pattern)
     * 
     * @param file Path to a file where the information will be written.
     * @param newFile Indicates if a new file has to be created (<code>true</code> = new file is
     * created; <code>false</code> = it appends the information to an existent file).
     * @return A single instance of the class.
     */
    public synchronized static SynapseLogWriter getLogWriter(String file, boolean newFile) {

        File myFile = new File(file);
        SynapseLogWriter logWriter = (SynapseLogWriter) writers.get(myFile.getAbsolutePath());
        if (logWriter == null) {
            logWriter = new SynapseLogWriter(file, newFile);
            writers.put(myFile.getAbsolutePath(), logWriter);
        }
        return logWriter;
    }

    /**
     * This method formats the LogEvent to a MyGrid specific format to be
     * written to disk.
     * 
     * @param le LogEvent received from the EventBus containing the information
     * to be written to disk.
     * @return A log entry in a Synapse specific format.
     */
    protected String getLogEntry(LogEvent le) {

        StringBuffer formattedString = new StringBuffer();
        if ((le != null) && (!(le.getTypeStr().equals("")) && !(le.getReference().equals("")))) {
            String strAux = "[" + le.getTimeAsString() + "]";

            formattedString.append(strAux + " ");

            strAux = le.getTypeStr();
            formattedString.append(strAux + " ");

            strAux = le.getReference();
            if (strAux != null) {
                if (!strAux.equals("")) {
                    formattedString.append(strAux);
                    formattedString.append(" ===> ");
                }
            }

            formattedString.append(le.getMessage() + '\n');

        }

        return formattedString.toString();
    }

    /**
     * Consumes an event from the EventBus and writes its information to disk in
     * a Synapse specific format.
     * 
     * @param event Event sent by the EventBus.
     * 
     * @see org.ourgrid.yal.eventbus.EventConsumer#consume(org.ourgrid.yal.eventbus.Event)
     */
    public void consume(Event event) {

        synchronized (logInfo) {
            logInfo.add(event);
            logInfo.notify();
        }

    }

    /**
     * Periodically writes log info to disk.
     */
    public void run() {

        while (true) {

            try {

                Thread.sleep(TIMEOUT);

            } catch (InterruptedException e) {

            }

            writeLogInfo();

            synchronized (logInfo) {
                try {
                    logInfo.wait();
                } catch (InterruptedException e1) {

                }

            }

        }

    }

    /**
     * Returns the absolute log file path used by this writer.
     * 
     * @return Log file path
     */
    public String getLogFile() {

        return logFile.getAbsolutePath();
    }

    /**
     * Compares this object with another based on the log file absolute path.
     * 
     * @param obj Object to be compared with.
     */
    public boolean equals(Object obj) {

        if (obj instanceof SynapseLogWriter) {
            if (this.getLogFile().equals(((SynapseLogWriter) obj).getLogFile())) {
                return true;
            }
        }

        return false;
    }
}
