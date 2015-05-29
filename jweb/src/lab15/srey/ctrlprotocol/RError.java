package lab15.srey.ctrlprotocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * This message notify if there has been some error
 * Its components are: TAG(int)TYPE(BYTE)ERROR(UTF8-String)
 */
public class RError extends Msgs{
	private String error;
	
	
	public RError(int tag, String resp){
		super(tag, RERROR);
		error = resp;
	}
	
	
	RError(int tag, DataInputStream in){
		super(tag, RERROR);
		try {
			error = in.readUTF();
		} catch (Exception e) {
			throw new RuntimeException("READING ERROR: " + e);
		}
	}
	
	
	public void send(DataOutputStream out){
		synchronized(out){
			super.send(out);
			try {
				out.writeUTF(error);
				out.flush();
			} catch (Exception e) {
				throw new RuntimeException("SENDING ERROR: " + e);
			}
		}
	}
	
	
	public String toString(){
		return error;
	}
}