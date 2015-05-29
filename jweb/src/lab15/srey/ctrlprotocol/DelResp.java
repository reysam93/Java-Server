package lab15.srey.ctrlprotocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;


/**
 * This message respond a delete message and
 * inform if the resource has been deleted or
 * if it hasn't been found.
 * Its components are: TAG(int)TYPE(byte)DELETED[boolean]
 */
public class DelResp extends Msgs{
	public boolean deleted;
	
	
	public DelResp(int tag, boolean del){
		super(tag, RDEL);
		deleted = del;
	}
	
	
	DelResp(int tag, DataInputStream in){
		super(tag, RDEL);
		try {
			deleted = in.readBoolean();
		} catch (Exception e) {
			throw new RuntimeException("READING ERROR: " + e);
		}
	}
	
	
	public void send(DataOutputStream out){
		synchronized(out){
			super.send(out);
			try {
				out.writeBoolean(deleted);
				out.flush();
			} catch (Exception e) {
				throw new RuntimeException("SENDING ERROR: " + e);
			}
		}
	}
	
	
	public String toString(){
		return tag + " RDEL";
	}
}
