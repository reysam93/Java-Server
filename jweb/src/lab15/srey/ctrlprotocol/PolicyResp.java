package lab15.srey.ctrlprotocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;


/**
 * This message is a respond for a Policy message and
 * notify the old policy.
 * Its components are: TAG(int)TYPE(BYTE)OLDPOLICY(UTF8-String) 
 */
public class PolicyResp extends Msgs{
	public String oldPolicy;
	
	
	public PolicyResp(int tag, String policy){
		super(tag, RPOLICY);
		oldPolicy = policy;
	}
	
	
	PolicyResp(int tag, DataInputStream in){
		super(tag, RPOLICY);
		try {
			oldPolicy = in.readUTF();
		} catch (Exception e) {
			throw new RuntimeException("READING ERROR: " + e);
		}
	}
	
	
	public void send(DataOutputStream out){
		synchronized(out){
			super.send(out);
			try {
				out.writeUTF(oldPolicy);
				out.flush();
			} catch (Exception e) {
				throw new RuntimeException("SENDING ERROR: " + e);
			}
		}
	}
	
	
	public String toString(){
		return tag + " RPOLICY";
	}
}