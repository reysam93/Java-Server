package lab15.srey.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lab15.srey.cache.Cache;
import lab15.srey.ctrlprotocol.Msgs;
import lab15.srey.ctrlprotocol.QuitMsg;
import lab15.srey.ctrlprotocol.QuitResp;


/**
 * This class serve a control client
 */
public class CtrlService implements Service{
	public void serveClient(String tag, DataInputStream in, DataOutputStream out, Cache cache){
		Msgs resp = null;
		try{
			Msgs msg = Msgs.readMsg(in);
			if (msg == null){
				System.out.println(tag + "eof received");
			}else{
				System.out.println(tag + "received " + msg.toString());
				if(!(msg instanceof QuitMsg)){
					resp = msg.process(cache);
					resp.send(out);
				}else{
					resp = new QuitResp(msg.tag);
					resp.send(out);
					Server.closeAll();
				}
			}
		}catch(Exception e){
			System.err.println(tag + "CtrlServing err: " + e);
		}
	}
}