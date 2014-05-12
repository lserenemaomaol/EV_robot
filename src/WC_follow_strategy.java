import java.util.List;


public class WC_follow_strategy implements Runnable {
	

	private boolean E_Status;
	private boolean V_Status;
	private List<Long> Step_Time;
	private long Step_Count;
	private List<Float> Step_Heading;
	private List<Float> Step_Length;
	private List<Long> RSSI_Time;
	private List<Float> RSSI;
	private List<Float> Px;
	private List<Float> Py;
	private List<Float> V_L;
	private List<Float> V_R;	
	private boolean WC_alive;
	
	public WC_follow_strategy(boolean wc,boolean E_track, boolean V_track, List<Long> t1, long t2, List<Float> t3, List<Float> t4, List<Float> t5, List<Long> t6, List<Float> f1, List<Float> f2, List<Float> f3, List<Float> f4)  {
		
		WC_alive = wc;
		E_Status = E_track;
		V_Status = V_track;
		
		Step_Time       = t1;
		Step_Count      = t2;
		Step_Heading    = t3;
		Step_Length     = t4;
		
		RSSI            = t5;
		RSSI_Time       = t6;
		
		Px  = f1;
		Py  = f2;
        V_L = f3;
        V_R = f4;
		
	}
	
	
	public void run()  {
		if(V_Status)  {
			// based on Px and Py from Vision tracking, how to make the wheelchair move
			
			//
//			V_L.add(e);
//			V_R.add(e);
		}
		
		if(!V_Status) {
			// In this case, vision fails
			if(RSSI.get(RSSI.size()-1)>2) {
				WC_alive = false; // wheelchair will stop 
			}
			// the moving strategies following Inertial sensing only
			//
//			V_L.add(e);
//			V_R.add(e);
		}
		
		
	}

}
