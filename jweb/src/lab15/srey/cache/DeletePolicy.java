package lab15.srey.cache;

import java.util.HashMap;
import java.util.Map;

import lab15.srey.cache.Cache.Page;


/**
 * This class is the interface of the different method used
 * by the cache when its full.  
 */
public abstract class DeletePolicy {
	
	private static Map<String, DelPolGen> generators;
	
	static{
		addGenerators();
	}
	
	
	private static void addGenerators(){
		generators = new HashMap<String, DelPolGen>();
		DelPolGen gen = new DelPolGen(){
			public DeletePolicy generate(){
				return new RndPolicy();
			}
		};
		generators.put("rnd", gen);
		
		gen = new DelPolGen(){
			public DeletePolicy generate(){
				return new FifoPolicy();
			}
		};
		generators.put("fifo", gen);
		
		gen = new DelPolGen(){
			public DeletePolicy generate(){
				return new LruPolicy();
			}
		};
		generators.put("lru", gen);
	}
	
	
	public static DeletePolicy getPolicy(String policy){
		DelPolGen gen = generators.get(policy);
		if(gen != null){
			return gen.generate();
		}else{
			return null;
		}
	}
	
	
	public abstract void delete(Map<String, Page> cache);
}