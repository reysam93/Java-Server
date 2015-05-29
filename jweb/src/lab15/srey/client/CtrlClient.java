package lab15.srey.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import lab15.srey.ctrlprotocol.DelResp;
import lab15.srey.ctrlprotocol.DeleteMsg;
import lab15.srey.ctrlprotocol.FlushMsg;
import lab15.srey.ctrlprotocol.FlushResp;
import lab15.srey.ctrlprotocol.ListMsg;
import lab15.srey.ctrlprotocol.ListResp;
import lab15.srey.ctrlprotocol.Msgs;
import lab15.srey.ctrlprotocol.PolicyMsg;
import lab15.srey.ctrlprotocol.PolicyResp;
import lab15.srey.ctrlprotocol.QuitMsg;
import lab15.srey.ctrlprotocol.QuitResp;
import lab15.srey.ctrlprotocol.RError;


/**
 * This class has the necessary methods for makes 
 * different control requests to the server and check
 * for the state of the cache
 */
public class CtrlClient {
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private Msgs msg;
	private Msgs resp;
	
	
	public CtrlClient(String name, int port){
		try {
			socket = new Socket(name, port);
			in = new DataInputStream(
					new BufferedInputStream(socket.getInputStream()));
			out = new DataOutputStream(
					new BufferedOutputStream(socket.getOutputStream()));
		} catch (Exception e) {
			throw new RuntimeException("CLI ERROR: " + e);
		}
	}
	
	
	private void checkTags(){
		if(msg.tag != resp.tag){
			throw new RuntimeException("ERROR: tags don't match");
		}
	}
	
	
	/**
	 * This method flushes the cache and returns true when
	 * its done. If there are some error it will raise a RuntimeException.
	 */
	public boolean flush(){
		msg = new FlushMsg();
		
		msg.send(out);
		resp = Msgs.readMsg(in);
		checkTags();
		if(resp instanceof FlushResp){
			return true;
		}else{
			throw new RuntimeException("ERROR: wrong message received");
		}
	}
	
	
	/**
	 * This method delete the element name from the cache.
	 * Returns true if the element has been delete and false 
	 * if it wasn't in the cache. If there are some error it
	 * will raise a RuntimeException.
	 */
	public boolean delete(String name){
		msg = new DeleteMsg(name);
		
		msg.send(out);
		resp = Msgs.readMsg(in);
		checkTags();
		if(resp instanceof DelResp){
			return ((DelResp)resp).deleted;
		}else{
			throw new RuntimeException("ERROR: wrong message received");
		}
	}
	
	
	/**
	 * This method returns a string array with the string representation
	 * of the elements in the cache or null if its empty. If there are any
	 * error it will raise a RuntimeException.  
	 */
	public String[] list(){
		msg = new ListMsg();
		msg.send(out);
		resp = Msgs.readMsg(in);
		checkTags();
		if(resp instanceof ListResp){
			return ((ListResp)resp).getPages();
		}else{
			throw new RuntimeException("ERROR: wrong message received");
		}
	}
	
	
	/**
	 * This method change the delete policy used in the cache and
	 * returns the string that represents the old policy or null if
	 * the new policy wasn't a valid one. It will raise a RuntimeException
	 * if there are some error. 
	 */
	public String policy(String newPol){
		msg = new PolicyMsg(newPol);
		msg.send(out);
		resp = Msgs.readMsg(in);
		checkTags();
		if(resp instanceof PolicyResp){
			return ((PolicyResp)resp).oldPolicy;
		}else if(resp instanceof RError){
			return null;
		}else{
			throw new RuntimeException("ERROR: wrong message received");
		}
	}
	
	
	/**
	 * This method close the server. Its HTTP and control ports.
	 * If there are some error it will raise a RuntimeException, or 
	 * returns true if everything goes fine. 
	 */
	public boolean quit(){
		msg = new QuitMsg();
		msg.send(out);
		resp = Msgs.readMsg(in);
		checkTags();
		if(resp instanceof QuitResp){
			return true;
		}else{
			throw new RuntimeException("ERROR: wrong message received");
		}
	}
	
	
	/**
	 * This method closes the control client.
	 */
	public void close(){
		try{
			if(in != null){
				in.close();
			}
			if(out != null){
				out.close();
			}
			if(socket != null){
				socket.close();
			}
		}catch(Exception e){
			throw new RuntimeException("CLOSING ERROR: " + e);
		}
	}
}