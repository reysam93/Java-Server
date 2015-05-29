package lab15.srey.ctrlprotocol;

import java.io.DataOutputStream;



/**
 * This message close the server.
 * It only have the common components: TAG(int)TYPE(byte)
 */
public class QuitMsg extends Msgs{
	
	
	public QuitMsg(){
		super(nextTag(), QUIT);
	}
	
	
	QuitMsg(int tag){
		super(tag, QUIT);
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
		return tag + " QUIT";
	}
}
