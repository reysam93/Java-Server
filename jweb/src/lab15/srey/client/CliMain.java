package lab15.srey.client;

import java.util.HashMap;
import java.util.Map;

public class CliMain {
	
	
	private static void addCommands(Map<String, CtrlCommand> cmds){
		CtrlCommand cmd = null;
		
		cmd = new CtrlCommand(){
			public void exec(CtrlClient cli, String[] args){
				if(cli.flush()){
					System.out.println("ok");
				}else{
					System.out.println("couldn't make the flush");
				}
			}
		};
		cmds.put("flush",cmd);
		
		cmd = new CtrlCommand(){
			public void exec(CtrlClient cli, String[] args){
				if(args.length != 2){
					throw new RuntimeException("usage: del  resource");
				}
				if(cli.delete(args[1])){
					System.out.println("ok");
				}else{
					System.out.println("don't found");
				}
			}
		};
		cmds.put("del", cmd);
		
		cmd = new CtrlCommand(){
			public void exec(CtrlClient cli, String[] args){
				String[] pages = cli.list();
				if(pages == null){
					System.out.println("cache is empty");
				}else{
					String str = "";
					for(int i=0; i<pages.length; i++){
						str += pages[i];
					}
					System.out.println(str);
				}
			}
		};
		cmds.put("ls", cmd);
		
		cmd = new CtrlCommand(){
			public void exec(CtrlClient cli, String[] args){
				if(args.length != 2){
					throw new RuntimeException("usage: policy newPolicy");
				}
				String old =  cli.policy(args[1]);
				if(old != null){
					System.out.println("changed from " + old + " to " + args[1]);
				}else{
					System.out.println("wrong policy");
				}
			}
		};
		cmds.put("policy", cmd);
		
		cmd = new CtrlCommand(){
			public void exec(CtrlClient cli, String[] args){
				if(cli.quit()){
					System.out.println("ok");
				}else{
					System.out.println("couldn't close the server");
				}
				
			}
		};
		cmds.put("quit", cmd);
	}
	
	
	public static void main(String[] args){
		CtrlClient cli = new CtrlClient("localhost", 9090);
		Map<String, CtrlCommand> commands= new HashMap<String, CtrlCommand>();
		
		if(args.length < 1){
			throw new RuntimeException("usage: jclient  request [args]");
		}
		addCommands(commands);
		CtrlCommand cmd = commands.get(args[0]);
		if(cmd == null){
			System.err.println("wrong command");
		}else{
			cmd.exec(cli, args);
		}
		cli.close();
	}
}