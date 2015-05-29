package lab15.srey.server;


import java.io.DataInputStream;
import java.io.DataOutputStream;

import lab15.srey.cache.Cache;


public interface Service {
	public void serveClient(String tag, DataInputStream in, DataOutputStream out, Cache cache);
}
