package synapse.common.events;

/**
 * These events are processed by the <code>EventProcessor</code>.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public interface ActionEvent extends Event {

    /**
     * The action performed when the event is executed.
     */
    public void process();

}
