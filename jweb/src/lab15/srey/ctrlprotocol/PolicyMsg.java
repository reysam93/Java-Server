package lab15.srey.ctrlprotocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lab15.srey.cache.Cache;


/**
 * This message type allows to change the replace the delete
 * policy of the cache.
 * The String passed as argument to the constructor must be
 * "RND", "FIFO" or "LRU". In oder case it will return an error
 * message. 
 */
public class PolicyMsg extends Msgs{
	private String policy;
	
	
	public PolicyMsg(String newPol){
		super(nextTag(), POLICY);
		policy = newPol;
	}
	
	
	PolicyMsg(int tag, DataInputStream in){
		super(tag, POLICY);
		try {
			policy = in.readUTF();
		} catch (Exception e) {
			throw new RuntimeException("READING ERROR: " + e);
		}
	}
	
	
	public Msgs process(Cache cache){
		String old = cache.getDelPolicy();
		if(!cache.newPolicy(policy)){
			String error = "ERROR: couldn't change policy to " + policy;
			return new RError(tag, error);
		}
		return new PolicyResp(tag, old);
	}
	
	
	public void send(DataOutputStream out){
		synchronized(out){
			super.send(out);
			try {
				out.writeUTF(policy);
				out.flush();
			} catch (Exception e) {
				throw new RuntimeException("SENDING ERROR: " + e);
			}
		}
	}
	
	
	public String toString(){
		return tag + " POLICY";
	}
}