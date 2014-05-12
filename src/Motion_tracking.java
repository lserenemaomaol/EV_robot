import java.util.List;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



public class Motion_tracking implements Runnable {
	
	private boolean E_Status;
	private List<Long> Time_Step;
	private long Step_Count;
	private List<Float> Step_Heading;
	private List<Float> Step_Length;
	private int listn_port;
	private ServerSocket svrSkt;
	private Socket skt;

	
	
	public Motion_tracking(boolean E_status, List<Long> t1, long t2, List<Float> t3, List<Float> t4) {
		
		E_Status        = E_status;
		Time_Step       = t1;
		Step_Count      = t2;
		Step_Heading    = t3;
		Step_Length     = t4;
	}
	
	public void start() {
		listn_port = 10000;
		try{
			svrSkt = new ServerSocket(listn_port);
		}catch(IOException e){
			// motion tracking fails
			E_Status = false;
			System.out.println("Cannot open port "+ listn_port);
		}
		
		skt = null;
		System.out.println("Start to listening "+listn_port);
	}
	public void run() {
		try{
			skt = svrSkt.accept();
			
		}catch(IOException e){
			//motion tracking fails
			E_Status = false;
			System.out.println("Cannot read from "+skt.getInetAddress().getHostAddress());
			e.printStackTrace();
		}
		
		new Thread(new Pedometer(skt, Time_Step, Step_Count, Step_Heading, Step_Length,E_Status)).start();
		
	}
	
	
	
}