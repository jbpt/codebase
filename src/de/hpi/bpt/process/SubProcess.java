package de.hpi.bpt.process;


/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class SubProcess extends Activity {

	public SubProcess() {
		super();
	}

	public SubProcess(String name, String desc) {
		super(name, desc);
	}

	public SubProcess(String name) {
		super(name);
	}

	private Process process = null;

	public Process getProcess() {
		return this.process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}
}
