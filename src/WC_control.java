import java.util.List;


public class WC_control implements Runnable {
	
	private boolean WC_alive;
	private List<Float> V_L;
	private List<Float> V_R;	
	// TODO: add serial port variables
	
	public WC_control(boolean wc,List<Float> f1,List<Float> f2){
		
		WC_alive = wc;
		V_L = f1;
		V_R = f2;
		
		// open serial port
		
	}
	
	
	public void run(){
		if(WC_alive){
			//write commands to serial port
			
		}		
	}

}
