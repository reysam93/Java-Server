package lab15.srey.cache;

import java.util.Iterator;
import java.util.Map;

import lab15.srey.cache.Cache.Page;

/**
 * This class delete the first element introduced in the cache.
 * Delete the oldest element.
 */
public class FifoPolicy extends DeletePolicy{
		

	public void delete(Map<String, Page> cache) {
		Iterator<Page> it = cache.values().iterator();
		long min = 0;
		String first = "";
		while(it.hasNext()){
			Page page = it.next();
			if(page.stored < min | min == 0){
				min = page.stored;
				first = page.name;
			}
		}
		cache.remove(first);
		System.out.print(" removed " + first + " ");
	}
	
	
	public String toString(){
		return "fifo";
	}
}