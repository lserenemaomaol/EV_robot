import java.util.List;


public class Vision_tracking implements Runnable{
	
	private boolean E_Status;
	private List<Long> Time_Step;
	private long Step_Count;
	private List<Float> Step_Heading;
	private List<Float> Step_Length;
	private boolean V_Status;
	private List<Float> Px;
	private List<Float> Py;
	
	
	public Vision_tracking(boolean E_status, List<Long> t1, long t2, List<Float> t3, List<Float> t4, boolean V_status, List<Float> X, List<Float> Y) 
	{
		Time_Step       = t1;
		Step_Count      = t2;
		Step_Heading    = t3;
		Step_Length     = t4;
		E_Status = E_status;
		V_Status = V_status;
		
		Px = X;
		Py = Y;

		
		
	}

	public void run() {
		// based on t1~t4, perform constrained visual tracking
		
		if(E_Status){
			// then inertia can provide information for PF tracking
			
			//update tracking status
			
			// if succeed
			//status = true;
			//Px.add();
			//Py.add();
			
			//if lost
			V_Status = false;
		}

		if(!E_Status) {
			// then NO motion sensor information available
			
			// if succeed
			//status = true;
			//Px.add();
			//Py.add();
			
			
			// if lost
			//V_Status = false;
		}

//		System.out.println("E_status is: " + E_Status);
//		System.out.println("V_status is: " + V_Status);
		//Thread.yield();

	}

}
