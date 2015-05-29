package lab15.srey.ctrlprotocol;

import java.io.DataOutputStream;


/**
 * A simple respond message for the flush message.
 * It doens't have any content, only confirm the cache 
 * flush went well when its received
 * It only has the default components: TAG(int)TYPE(BYTE)
 */
public class FlushResp extends Msgs{
	public FlushResp(int tag){
		super(tag, RFLUSH);
	}
	
	
	public void send(DataOutputStream out){
		synchronized(out){
			super.send(out);
			try {
				out.flush();
			} catch (Exception e) {
				throw new RuntimeException("SENDING ERROR: " + e);
			}
		}
	}
	
	
	public String toString(){
		return tag + " RFLUSH";
	}
}
