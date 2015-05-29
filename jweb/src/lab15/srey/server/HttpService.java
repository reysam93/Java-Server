package lab15.srey.server;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import lab15.srey.cache.Cache;

/**
 *	This service connect the server and the browser
 *	with basic HTTP non persistent connections
 */
public class HttpService implements Service{
	private String dir;
	private BufferedReader buffIn;
	private BufferedWriter buffOut;
	
	
	public HttpService(String dir){
		this.dir = dir;
	}

	
	public void serveClient(String tag, DataInputStream in, DataOutputStream out, Cache cache){
		buffIn = new BufferedReader(new InputStreamReader(in));
		buffOut = new BufferedWriter(new OutputStreamWriter(out));
		HttpReq req = HttpReq.readRqs(buffIn);
		System.out.print(tag + req.toString());
		try{
			if(!req.process(dir, cache)){
				System.err.println(tag + "null content");
			}
			req.respond(buffOut);
		}catch(Exception e){
			System.err.println(tag + "HTTPServing error " + e);
		}
	}
}