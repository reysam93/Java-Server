package lab15.srey.ctrlprotocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lab15.srey.cache.Cache;


/**
 * This class represent a message for delete one page from
 * the cache.
 * Its components are: TAG(int)TYPE(byte)NAME(utf8-String)
 */
public class DeleteMsg extends Msgs{
	private String name;
	
	
	public DeleteMsg(String name){
		super(nextTag(), DEL);
		this.name = name;
	}
	
	
	DeleteMsg(int tag, DataInputStream in){
		super(tag, DEL);
		try {
			name = in.readUTF();
		} catch (IOException e) {
			throw new RuntimeException("READING ERROR: " + e);
		}
	}
	
	
	public Msgs process(Cache cache){
		if(!name.startsWith("/")){
			name = "/" + name;
		}
		if(cache.delete(name) == null){
			return new DelResp(tag, false);
		}else{
			return new DelResp(tag, true);
		}
	}
	
	
	public void send(DataOutputStream out){
		synchronized(out){
			super.send(out);
			try{
				out.writeUTF(name);
				out.flush();
			}catch(Exception e){
				throw new RuntimeException("SENDING ERROR: " + e);
			}
		}
	}
	
	public String toString(){
		return tag + " DEL " + name;
	}
}