package lab15.srey.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import lab15.srey.cache.Cache;

/**
 * A generic concurrent server class that offer the service 
 * passed as the parameter service in the constructor
 */
public class Server {
	private int port;
	private ServerSocket socket;
	private Service service;
	private List<SrvClient> clients;
	private boolean exit;
	private Thread srvproc;
	private static Cache cache;
	private static List<Server> servers;

	
	/**
	 * Initialize the server and create a Socket in the 
	 * port received as argument but doesn't start listening
	 */
	public Server(int port, Service srv){
		if(srv == null){
			throw new RuntimeException("Null service");
		}else{
			service = srv;
		}
		this.port = port;
		if(servers == null){
			servers = new ArrayList<Server>();
		}
		clients = new ArrayList<SrvClient>();
		if (cache == null){
			cache = new Cache();
		}
		try {
			socket = new ServerSocket(port);
		} catch (Exception e) {
			throw new RuntimeException (this + e.toString());
		}
	}
	
	
	/**
	 * The server starts accepting and serving clients
	 */
	public void start(){
		exit = false;
		synchronized(servers){
			servers.add(this);
		}
		srvproc = new Thread(){
			public void run(){
				runServer();
			}
		};
		srvproc.start();
	}
	
	
	public String toString(){
		return "Srv[" + port + "]: ";
	}
	
	
	/**
	 * An internal class that is used to store information about
	 * the clients in a list of clients
	 */
	private class SrvClient{
		private final Socket sd;
		private final DataInputStream in;
		private final DataOutputStream out;
		
		
		private SrvClient(Socket s){
			sd = s;
			try {
				in = new DataInputStream(
						new BufferedInputStream(sd.getInputStream()));
				out = new DataOutputStream(
						new BufferedOutputStream(sd.getOutputStream()));
			} catch (Exception e) {
				throw new RuntimeException("Couldn't create Streams: " + e);
			}
			
		}
		
		
		/**
		 * Close InputStream and OutputStream and the socket sd, 
		 * but doesn't close the ServerSocket
		 * After call this method it's necessary to remove the SrvClient from
		 * the clients list
		 */
		private void close(){
			try{
				if (out != null){
					out.close();
				}
				if (in != null){
					in.close();
				}
				if (sd != null){
					sd.close();
				}
			}catch (Exception e) {
					throw new RuntimeException("closing SrvClient: " + e);
			}
		}
		
		
		/**
		 * Add the client to the list of clients
		 */
		private void add(){
			synchronized (clients){
				clients.add(this);
			}
		}
		
		
		/**
		 * Remove the client from the list of clients
		 */
		private boolean remove(){
			synchronized(clients){
				return clients.remove(this);
			}
		}
	}
	
	
	private void runServer(){
		while(!exit && !socket.isClosed()){
			System.out.println(this + "waiting for clients...");
			try {
				final Socket sd = socket.accept();
				System.out.println(this + "client received");
				final String tag = this.toString();
				final SrvClient client = new SrvClient(sd);
				Thread cliProc = new Thread(){
					public void run(){
						service.serveClient(tag, client.in, client.out, cache);
						client.close();
						if(!client.remove() & !exit){
							System.err.println(this + "couldn't remove client from the list");
						}
					}
				};
				client.add();
				cliProc.start();
			} catch (Exception e) {
				if(!"java.net.SocketException: Socket closed".equals(e.toString())){
					System.err.println(this + " accepting error: " + e);
				}
			}
		}
	}
	
	
	/**
	 * Close the server and the sockets of its clients
	 */
	public void close(){
		System.out.println(this + "closing server.");
		exit = true;
		synchronized(clients){
			for(SrvClient c: clients){
				c.close();
			}
			clients.removeAll(clients);
		}
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("Closing srv: " + e);
		}
		
	}

	
	/**
	 * Closes all servers that has been started
	 */
	public static void closeAll(){
		synchronized(servers){
			for(Server srv: servers){
				srv.close();
			}
			servers.removeAll(servers);
		}
	}
}