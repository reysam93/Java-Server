package lab15.srey.ctrlprotocol;

import java.io.DataOutputStream;
import java.io.IOException;

import lab15.srey.cache.Cache;


/**
 * This message empty the cache. It only has
 * the default components: TAG(int)TYPE(BYTE)
 */
public class FlushMsg extends Msgs{
	
	
	public FlushMsg(){
		super(nextTag(), FLUSH);
	}


	FlushMsg(int tag){
		super(tag, FLUSH);
	}
	
	
	public Msgs process(Cache cache) {
		cache.flush();
		return new FlushResp(tag);
	}
	
	
	public void send(DataOutputStream out){
		synchronized(out){
			super.send(out);
			try {
				out.flush();
			} catch (IOException e) {
				throw new RuntimeException("SENDING ERROR: " + e);
			}
		}
	}
	
	
	public String toString(){
		return tag + " FLUSH";
	}
}