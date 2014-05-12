import java.util.ArrayList;
import java.util.List;


public class EV_robot_main {
	
	public static void main(String [] args)
	{
		// all the information from inertial sensor
		boolean E_track = true;
		List<Long> TimeStep = new ArrayList<Long>();
		long StepCount = 0;
		List<Float> StepHeading = new ArrayList<Float>();
		List<Float> StepLength = new ArrayList<Float>();
		
		// all the information from vision tracking
		boolean V_track = true;   // denote vision tracking status
		List<Float> TargetX = new ArrayList<Float>();
		List<Float> TargetY = new ArrayList<Float>();
		
		// all the information from RSSI detection
		List<Float> RSSI_D = new ArrayList<Float>();
		List<Long> RSSI_Time = new ArrayList<Long>();
		
		// all the information for WC control
		boolean WC_alive = true;                   // stop the WC or not
		List<Float> V_L = new ArrayList<Float>();  // left wheel voltage
		List<Float> V_R = new ArrayList<Float>();  // right wheel voltage

		
		while(true) {

			// RSSI sensing thread
			new Thread(new RSSI_sensing(RSSI_D,RSSI_Time)).start();			
			
			// Motion tracking thread
			new Thread(new Motion_tracking(E_track,TimeStep,StepCount,StepHeading,StepLength)).start();
			
			// Vision tracking thread 
			new Thread(new Vision_tracking(E_track,TimeStep,StepCount,StepHeading,StepLength,V_track,TargetX,TargetY)).start();
			
			// Wheelchair motion control thread
			new Thread(new WC_follow_strategy(WC_alive,E_track,V_track,TimeStep,StepCount,StepHeading,StepLength,RSSI_D,RSSI_Time,TargetX,TargetY,V_L,V_R)).start();
			
			// Execution of WC commands
			new Thread(new WC_control(WC_alive,V_L,V_R)).start();
		}
	}

}
