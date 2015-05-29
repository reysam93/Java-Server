package lab15.srey.server;


public class SrvMain {
	public static void main(String[] args){
		if(args.length != 1){
			throw new RuntimeException("usage: jweb  serverdir");
		}
		
		
		Server httpSrv = new Server(8181, new HttpService(args[0]));
		Server ctrlSrv = new Server(9090, new CtrlService());
		httpSrv.start();
		ctrlSrv.start();
	}
}