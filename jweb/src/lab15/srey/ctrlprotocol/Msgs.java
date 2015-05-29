package lab15.srey.ctrlprotocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;

import lab15.srey.cache.Cache;
import lab15.srey.serialize.LittleEndian;


/**
 * This class defines the messages protocol used between the server
 * and the control client.
 * 
 * All messages start with a TAG(int) and TYPE(byte)
 * 
 * Messages types:
 * 		Flush
 * 		FlushRespond
 * 		Delete
 * 		DeleteRespond
 * 		List
 * 		ListRespond
 * 		Policy
 * 		PolicyRespond
 * 		Quit
 * 		QuitRespond
 * 		RError
 * 
 * Each type protocol is defined in its own class
 */
public class Msgs {
	public int tag;
	protected byte type;
	protected String resp;
	private static int taggen;
	
	public static final byte NONE = 0;
	public static final byte RERROR = 66;
	public static final byte FLUSH = 67;
	public static final byte DEL = 68;
	public static final byte LIST = 69;
	public static final byte RLIST = 70;
	public static final byte POLICY = 71;
	public static final byte QUIT = 72;
	public static final byte RFLUSH = 73;
	public static final byte RDEL = 74;
	public static final byte RPOLICY = 75;
	public static final byte RQUIT = 76;
	
	protected Msgs(int tag, byte type){
		this.tag = tag;
		this.type = type;
	}
	
	
	protected static synchronized int nextTag(){
		taggen++;
		return taggen;
	}
	
	
	public static Msgs readMsg(DataInputStream in){
		byte[] tagBytes = new byte[4];
		synchronized(in){
			try {
				in.readFully(tagBytes);
				int tag = LittleEndian.toInt(tagBytes);
				byte type = in.readByte();
				switch(type){
				case NONE: throw new RuntimeException("NONE received");
				case RERROR: return new RError(tag, in);
				case FLUSH: return new FlushMsg(tag);
				case RFLUSH: return new FlushResp(tag);
				case DEL:return new DeleteMsg(tag, in);
				case RDEL: return new DelResp(tag, in);
				case LIST: return new ListMsg(tag);
				case RLIST: return new ListResp(tag, in);
				case POLICY: return new PolicyMsg(tag, in);
				case RPOLICY: return new  PolicyResp(tag, in);
				case QUIT: return new QuitMsg(tag);
				case RQUIT: return new QuitResp(tag);
				default: throw new RuntimeException("msg type not recognized");
				}
			}catch (EOFException e) {
				return null;
			}catch(Exception e){
				throw new RuntimeException("READING ERR: " + e);
			}
		}
	}
	
	
	/**
	 * This method only write in a DataOutputStream the common
	 * fields of the protocol (Tag and Type), but does not flush.
	 * You should use synchronized and flush when use it.
	 */
	public void send(DataOutputStream out){
		byte[] tagBytes = LittleEndian.serialize(tag);
		
		try {
			out.write(tagBytes);
			out.writeByte(type);
		} catch (Exception e) {
			throw new RuntimeException("SENDING ERROR: " + e);
		}
	}
	
	
	/**
	 * This default method does nothing. Have to be redefined by
	 * the classes  that inherit from Msgs and need it.
	 */
	public Msgs process(Cache cache){
		System.err.println("Default process method.");
		return null;
	}
}