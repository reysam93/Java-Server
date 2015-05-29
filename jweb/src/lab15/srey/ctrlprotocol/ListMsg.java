package lab15.srey.ctrlprotocol;

import java.io.DataOutputStream;

import lab15.srey.cache.Cache;


/**
 * This message request for all the pages in the cache.
 * It has an specific response type.
 * It's structure is: TAG(int)TYPE(BYTE)
 */
public class ListMsg extends Msgs{
	
	
	public ListMsg(){
		super(nextTag(), LIST);
	}
	
	
	ListMsg(int tag){
		super(tag, LIST);
	}
	
	
	public Msgs process(Cache cache){
		return new ListResp(tag, cache.getPages());
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
		return tag + " LIST";
	}
}