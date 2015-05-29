package lab15.srey.ctrlprotocol;

import java.io.DataOutputStream;


/**
 * This message inform to the client that the server
 * has received the QuitMsg and it is closing.
 * Its components are: TAG(int)TYPE(byte)
 */
public class QuitResp extends Msgs{
	public QuitResp(int tag){
		super(tag, RQUIT);
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
		return tag + " RQUIT";
	}
}
