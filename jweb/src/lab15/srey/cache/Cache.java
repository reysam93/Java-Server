package lab15.srey.cache;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * This class is a cache that stores as maximum 5 websites
 * It has 3 different replaces policies:
 * 	First In First Out
 * 	Last Recently Used
 * 	Random
 * 
 * The default policy is random.
 */
public class Cache {
	private Map<String,Page> cache;
	private static final int maxPages = 5;
	private DeletePolicy mode;
	
	
	public Cache(DeletePolicy mode){
		cache = new HashMap<String,Page>();
		this.mode = mode;
	}
	
	
	/**
	 * Use a RndPolicy as default method for deleting 
	 * elements from the cache.
	 */
	public Cache(){
		this(new RndPolicy());
	}
	
	
	/**
	 * This class represents the different pages stored
	 * stored in the cache and all the necessary information
	 * about them. 
	 */
	public static class Page{
		public String name;
		public String content;
		public long size;
		public long stored;
		public long lastUsed;
		
		
		private Page(String name, String cont, long size){
			this.name = name;
			content = cont;
			this.size = size;
			stored = Calendar.getInstance().getTimeInMillis();
			lastUsed = stored;
		}
		
		
		public Page(){
			name = null;
			content = null;
			size = 0;
			stored = 0;
			lastUsed = 0;
		}
		
		
		public String toString(){
			String str = name + "\t" + size;
			Date date = new Date(stored);
			str += " bytes " + date.toString() + "\n";
			return str;
		}
	}
	
	
	public synchronized String getDelPolicy(){
		return mode.toString();
	}
	
	
	/**
	 * Delete all the contents from the cache.
	 */
	public void flush(){
		synchronized(cache){
			cache.clear();
		}
	}
	
	
	public Page delete(String name){
		synchronized(cache){
			return cache.remove(name);
		}
	}
	
	
	public List<Page> getPages(){
		synchronized(cache){
			List<Page> pages = new ArrayList<Page>();
			Iterator<Page> it = cache.values().iterator();
			while(it.hasNext()){
				pages.add(it.next());
			}
			return pages;
		}
	}
	
	
	public synchronized boolean newPolicy(String newPol){
		DeletePolicy newMode = DeletePolicy.getPolicy(newPol);
		if(newMode == null){
			return false;
		}else{
			mode = newMode;
			return true;
		}
	}
	
	
	/**
	 * Put a page in the cache
	 */
	private void putPage(String name, String cont, long size){
		synchronized(cache){
			if(cache.size() >= maxPages){
				mode.delete(cache);
			}
			Page page = new Page(name, cont, size);
			cache.put(name, page);
			System.out.print(" stored " + name + " ");
			System.out.println(new Date(page.stored));
		}
	}
	
	
	/**
	 * Updates the content of the page stored in the cache,
	 * its date of last used and stored 
	 */
	private void update(Page page, String cont, long size){
		page.content = cont;
		page.stored = Calendar.getInstance().getTimeInMillis();
		page.lastUsed = page.stored;
		page.size = size;
		synchronized(cache){
			cache.put(page.name, page);
		}
	}
	
	
	private static void closeFile(DataInputStream fileOut){
		try {
			fileOut.close();
		} catch (Exception e) {
			System.err.println("closing error: " + e);
		}
	}
	
	
	private static String readDir(File dir){
		String[] files = dir.list();
		String content = "<html><body><h1>" + dir + "</h1>";
		for(String file: files){
			content += file + "<br/>";
		}
		content += "</body></html>";
		return content;
	}
	
	
	private static String readFile(File file) throws FileNotFoundException{
		String content = "";
		byte[] contBytes = new byte[32*1024];
		DataInputStream fileIn = new DataInputStream(new FileInputStream(file));
		
		synchronized(fileIn){
			for(;;){
				try{
					fileIn.readFully(contBytes);
					content += new String(contBytes);
				}catch(EOFException e){
					content += new String(contBytes);
					break;
				}catch(Exception e){
					System.err.println("READING FILE ERROR: " + e);
					content = null;
				}
			}
			closeFile(fileIn);
			return content;
		}
	}
	
	
	private static CacheResp readContent(String name, File file) throws FileNotFoundException{
		String content = null;
		String code = null;
		
		if(file.isDirectory()){
			content = readDir(file);
			code = "200 OK";
		}else if(file.isFile()){
			content = readFile(file);
			if(content == null){
				code = "500 Intern Error";
				content = "<html>INTERN ERROR</html>";
			}else{
				code = "200 OK";
			}
		}else{
			code = "404 NOT FOUND";
			content = "<html>" + name + " not found</html>";
		}
		return new CacheResp(code, content);
	}
	
	
	/**
	 * Look for the page in the cache. If it's not there or if the copy is not updated,
	 * then it will read it from the file, saved it in the cache and return its content
	 */
	public CacheResp getPage(String name, File file) throws FileNotFoundException{
		CacheResp resp = null;
		Page page = null;
		
		synchronized(cache){
			page = cache.get(name);
		}
		if(page == null){
			resp = readContent(name, file);
			if("200 OK".equals(resp.code)){
				putPage(name, resp.content, file.length());
			}
		}else if(file.lastModified() >= page.stored){
			resp = readContent(name, file);
			if("200 OK".equals(resp.code)){
				update(page, resp.content, file.length());
			}
			System.out.print(" updated file " + page.name + " ");
			System.out.println(new Date(page.stored));
		}else{
			page.lastUsed = Calendar.getInstance().getTimeInMillis();
			resp = new CacheResp("200 OK", page.content);
			System.out.print(" served " + page.name);
			System.out.println(new Date(page.lastUsed));
		}
		return resp;
	}
}