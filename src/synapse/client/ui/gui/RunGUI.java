package synapse.client.ui.gui;

/**
 * Main class that runs synapse gui.
 */
public class RunGUI {

	/**
	 * Main
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			if (args[0].equals("configure")) {
				new ConfigDialog();
			}
			else {
				System.out.println("Did you mean: \"RunGUI configure\"?");
			}
		}
		else {
			new SynapseGUI();
		}
	}
}
