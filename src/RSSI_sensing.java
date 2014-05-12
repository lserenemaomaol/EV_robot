import java.util.List;


public class RSSI_sensing implements Runnable {
	
	private List<Float> RSSI;
	private List<Long> RSSI_Time;
	
	
	
	public RSSI_sensing(List<Float> t1, List<Long> t2){
		RSSI      = t1;
		RSSI_Time = t2;
	}
	public void run() {
		// Sense and store RSSI Sensing result
		
//		RSSI.add(rssi);
//		RSSI_Time.add(ts);
	}

}
