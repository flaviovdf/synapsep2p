package synapse.common;

import synapse.common.events.Event;

/**
 * A fake object.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class FakeEventProcessor implements EventProcessor {

    private int putEventCount;
    private int startProcessingCount;
    private int expectedPutEvent;
    private int expectedStartProcessing;

    public FakeEventProcessor() {
        this.putEventCount = 0;
        this.startProcessingCount = 0;

        this.expectedPutEvent = 0;
        this.expectedStartProcessing = 0;
    }

    public void setExpectedPutEvent(int value) {
        this.expectedPutEvent = value;
    }

    public void setExpectedStartProcessing(int value) {
        this.expectedStartProcessing = value;
    }

    public void verify() {

        if (this.putEventCount != this.expectedPutEvent) {
            throw new RuntimeException( "putEvent() was expected to be called " + expectedPutEvent + " times but was " + putEventCount);
        }

        if (this.startProcessingCount != this.expectedStartProcessing) {
            throw new RuntimeException( "startProcessing() was expected to be called " + expectedStartProcessing+ " times but was " + startProcessingCount);
        }
    }

    /* (non-Javadoc)
     * @see synapse.common.EventProcessor#putEvent(synapse.common.Event)
     */
    public void putEvent(Event event) {
        this.putEventCount++;
    }

    /* (non-Javadoc)
     * @see synapse.common.EventProcessor#startProcessing()
     */
    public void startProcessing() {
        this.startProcessingCount++;
    }

    /* (non-Javadoc)
     * @see synapse.common.EventProcessor#shutdown(synapse.common.EventQueue)
     */
    public void shutdown(EventQueue responseQueue) {

    }

    /* (non-Javadoc)
     * @see synapse.common.EventProcessor#isAlive()
     */
    public boolean isAlive() {
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {

    }

}
