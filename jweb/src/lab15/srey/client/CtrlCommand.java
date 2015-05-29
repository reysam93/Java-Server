package lab15.srey.client;


/**
 * This is an interface for the different commands that can be used by
 * a control client and simplify its use in the main program.
 */
public interface CtrlCommand {
	public abstract void exec(CtrlClient cli, String[] args);
}
