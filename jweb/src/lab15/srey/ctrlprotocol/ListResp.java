package lab15.srey.ctrlprotocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lab15.srey.cache.Cache.Page;
import lab15.srey.serialize.LittleEndian;


/**
 * This class is the response for the list message.
 * It's structure is:
 * TAG(int)TYPE(byte)NPAGES(int)[PAGE1: NAME(utf8-String)SIZE(long)CONTENT(utf8-String)DATE(long).....]
 */
public class ListResp extends Msgs{
	private List<Page> pages;

	
	public ListResp(int tag, List<Page> pages){
		super(tag, RLIST);
		this.pages = pages;
	}
	
	
	private void readPages(int n, DataInputStream in) throws IOException{
		int i;
		byte[] dateBytes = new byte[8];
		byte[] sizeBytes = new byte[8];
		
		for(i = 0; i < n; i++){
			Page page = new Page();
			page.name = in.readUTF();
			in.readFully(sizeBytes);
			page.size = LittleEndian.toLong(sizeBytes);
			page.content = in.readUTF();
			in.readFully(dateBytes);
			page.stored = LittleEndian.toLong(dateBytes);
			pages.add(page);
		}
	}
	
	
	public ListResp(int tag, DataInputStream in){
		super(tag,RLIST);
		byte[] nPagBytes = new byte[4];
		pages = new ArrayList<Page>();
		try {
			in.readFully(nPagBytes);
			int nPags = LittleEndian.toInt(nPagBytes);
			if(nPags > 0){
				readPages(nPags,in);
			}
		} catch (Exception e) {
			throw new RuntimeException("READING ERR: " + e);
		}
	}
	
	
	private void writePages(DataOutputStream out) throws IOException{
		for(int i=0; i < pages.size(); i++){
			Page page = pages.get(i);
			out.writeUTF(page.name);
			out.write(LittleEndian.serialize(page.size));
			out.writeUTF(page.content);
			out.write(LittleEndian.serialize(page.stored));
		}
	}
	
	
	public void send(DataOutputStream out){
		synchronized(out){
			super.send(out);
			try {
				out.write(LittleEndian.serialize(pages.size()));
				if(pages.size() > 0){
					writePages(out);
				}
				out.flush();
			} catch (Exception e) {
				throw new RuntimeException("SENDING ERROR: " + e);
			}
		}
	}
	
	
	public String toString(){
		return tag + " RLIST";
	}

	
	public String[] getPages(){
		String[] strs = new String[pages.size()];
		int i;
		if(pages.size() > 0){
			for(i = 0; i < pages.size(); i++){
				Page page = pages.get(i);
				strs[i] = page.toString();
			}
			return strs;
		}else{
			return null;
		}
	}
}