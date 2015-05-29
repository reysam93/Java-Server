package lab15.srey.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import lab15.srey.cache.Cache;
import lab15.srey.cache.CacheResp;


public abstract class HttpReq{
	protected static String method;
	protected String resource;
	protected String response;
	
	
	protected HttpReq(String meth, String resource){
		method = meth;
		this.resource = resource;
	}
	
	
	public static HttpReq readRqs(BufferedReader in){
		String request = null;
		synchronized(in){
			try {
				request = in.readLine();
			} catch (Exception e) {
				return new BadReq("Received EOF");
			}
			String method = request.split(" ")[0];
			if ("GET".equals(method)){
				return new GetReq(request); 
			}else{
				return new BadReq(request);
			}
		}
	}
	
	
	public String toString(){
		return method + " " + resource;
	}
	
	
	/**
	 * This will process the received request and will look for the
	 * requested resource if it is necessary and will prepare a 
	 * respond, but doesn't send it. 
	 */
	public abstract boolean process(String dir, Cache cache) throws IOException;
	
	
	/**
	 * This will send the respond generated with the process method
	 */
	public void respond(BufferedWriter out) throws IOException{
		synchronized(out){
			out.write(response);
			out.flush();
		}
	}
	
	
	private static String setContentHeaders(String code, String resource){
		String headers = "Content-Type: ";
		
		if("200 OK".equals(code)){
			if(resource.endsWith(".txt")){
				headers += "text/plain\r\n";
			}else{
				headers += "text/html\r\n";
			}
		}else{
			headers += "text/html\r\n";
		}
		return headers;
	}
	
	
	/**
	 * This class represent a Get request received by the server. 
	 */
	private static class GetReq extends HttpReq{	
		private GetReq(String request){
			super("GET", request.split(" ")[1]);
		}

		
		public boolean process(String dir, Cache cache) throws IOException{			
			File file = new File(dir + resource);
			CacheResp resp = cache.getPage(resource, file);
			
			response = "HTTP/1.1 " + resp.code + "\r\n" + setContentHeaders(resp.code, resource);
			response += "\r\n" + resp.content;
			if("500 Intern Error".equals(resp.code)){
				return false;
			}else{
				return true;
			}
		}
	}
	
	
	/**
	 * This class represent a request that is not supported 
	 * by this server.
	 */
	public static class BadReq extends HttpReq{		
		public BadReq(String request){
			super(request.split(" ")[0], request.split(" ")[1]);
		}
		
		
		public boolean process(String dir, Cache cache) throws IOException{
			response = "HTTP/1.1 400 BAD REQUEST\r\n\r\n";
			response += "<html><body>BAD REQUEST</body></html>";
			return true;
		}
		
		
		public String toString(){
			return "BAD REQUEST: " + super.toString();
		}
	}
}