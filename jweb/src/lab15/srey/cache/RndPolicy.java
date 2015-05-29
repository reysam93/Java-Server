package lab15.srey.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import lab15.srey.cache.Cache.Page;


/**
 * This class choose randomly an element of the cache for delete. 
 */
public class RndPolicy extends DeletePolicy{
	

	public void delete(Map<String, Page> cache) {
		String name = null;
		
		Iterator<String> it = cache.keySet().iterator();
		int index = new Random().nextInt(cache.size());
		for(int i=0; i<index; i++){
			name = it.next();
		}
		cache.remove(name);
		System.out.print(" removed " + name + " ");
	}
	
	
	public String toString(){
		return "rnd";
	}
}