package lab15.srey.serialize;

public class LittleEndian {
	final private static int mask = 0xFF;
	final private static long longmask = 0xFF;


	public static byte[] serialize(long n){
		byte[] b = new byte[8];
		int poss = 0;
		
		for(int i=0; i < b.length; i++){
			b[i] = (byte) ((n >> (poss)) & longmask);
			poss += 8;
		}
		return b;
	}


	public static byte[] serialize(int n){
		byte[] b = new byte[4]; 
		int poss = 0;
		
		for(int i=0; i < b.length; i++){
			b[i] = (byte) ((n >> poss) & mask);
			poss += 8;
		}
		return b;
	}


	public static int toInt(byte[] b){
		int n = 0;
		int poss = 0;
		
		for(int i=0; i <b.length; i++){
			n = n | ((b[i] & mask) << poss);
			poss += 8;
		}
		return n;
	}


	public static long toLong(byte[] b){
		long n = 0L;
		int poss = 0;
		for(int i=0; i <b.length; i++){
			n = n | ((b[i] & longmask) << poss);
			poss += 8;
		}
		return n;
	}
}