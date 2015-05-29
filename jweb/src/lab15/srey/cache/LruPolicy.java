package lab15.srey.cache;

import java.util.Iterator;
import java.util.Map;

import lab15.srey.cache.Cache.Page;

/**
 * This class delete the element that has been unused for more time.
 */
public class LruPolicy extends DeletePolicy{

	
	public void delete(Map<String, Page> cache) {
		long min = 0;
		String lastUsed = "";
		Iterator<Page> it = cache.values().iterator();
		while(it.hasNext()){
			Page page = it.next();
			if(page.lastUsed < min | min == 0){
				min = page.lastUsed;
				lastUsed = page.name;
			}
		}
		cache.remove(lastUsed);
		System.out.print(" removed " + lastUsed + " ");
	}
	
	
	public String toString(){
		return "lru";
	}
}