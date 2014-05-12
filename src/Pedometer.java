import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Pedometer implements Runnable  {
	
	private Socket skt = null;
	private List<Long> Step_Time;
	private long Step_Count;
	private List<Float> Step_Heading;
	private List<Float> Step_Length;
	private boolean E_flag;
	
	private List<Long> tss1;
	private List<Long> tss2;
	private List<Float> a_g1;
	private List<Float> an_g1;
	private List<Float> a_g2;
	private List<Float> an_g2;
	
	
	public Pedometer(Socket socket,List<Long> t1, long t2, List<Float> t3, List<Float> t4, boolean E_Status)  {
		
		skt          = socket;
		Step_Time    = t1;
		Step_Count   = t2;
		Step_Heading = t3;
		Step_Length  = t4;
		E_flag = E_Status;
		
	}
	
	public static List<Float> FIR_Filter(List<Float> raw_data)  {
				float filter[] = {-0.0029f,-0.0007f, 0.0055f, 0.0166f, 0.0327f,
		                           0.0526f, 0.0740f, 0.0942f, 0.1098f, 0.1184f,
		                           0.1184f, 0.1098f, 0.0942f, 0.0740f, 0.0526f,
		                           0.0327f, 0.0166f, 0.0055f,-0.0007f,-0.0029f};
				List<Float> a_raw = raw_data;
				List<Float> a_filtered = new ArrayList<Float>();
				float sum = 0f;
				for(int i=20; i< a_raw.size(); i++)  {
					for(int j=0; j<20; j++)  {
						sum = sum + filter[j]*a_raw.get(i-j);
					}
					a_filtered.add(sum);
					sum = 0;
				}
				
				return a_filtered;
	}
	

	
	public void run() {
		
		int N=1;
		
		while(N<30) {
			try{
				BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
				String raw_data = in.readLine();
				skt.close();
				String []contents = raw_data.split(" ");
				String mac = contents[0];
				
				if (contents[2].equals("empty"))
					return;
				
				long ts;
				float acc_g;
				float ang_g;
				int i=2;
				
				// receive new raw data
				while (i < contents.length) {
					ts = Long.parseLong(contents[i]);
					i += 3;
					
					acc_g = Float.parseFloat(contents[i]);
					i += 3;
					
					ang_g = Float.parseFloat(contents[i]);
					
					if (acc_g<=0.1 && acc_g>=-0.1)
						continue;

					tss2.add(ts);
					a_g2.add(acc_g);
					an_g2.add(ang_g);
				}			
				
				// Moving combining the raw data
				if(a_g1.size()<1)  {                     // 1st time listening
					tss1.addAll(tss2);
					a_g1.addAll(a_g2);
					an_g1.addAll(an_g2);
					
					tss2.clear();
					a_g1.clear();
					an_g1.clear();
					
					E_flag = false;
				}
				else {                                  // combine two data segments and count steps
					List<Long> tss = new ArrayList<Long>(tss1);
					List<Float> a_g = new ArrayList<Float>(a_g1);
					List<Float> an_g = new ArrayList<Float>(an_g1);
					
					// the lists to be processed for step detection
					tss.addAll(tss2);
					a_g.addAll(a_g2);
					an_g.addAll(an_g2);
					

					
					// now filter the raw data in a_g and an_g
					List<Float> a_gs =  FIR_Filter(a_g);
			        // taking 1/30 of total data for calculating static G		
					float sum = 0;
					float avg_g = 0;
					int j = 1;
					for( j=1; j<a_gs.size()/30; j=j+1) {
						sum = sum+a_gs.get(j);
					}
					avg_g = sum/(j-1);
					// calculating g_max g_min
					float g_max = a_gs.get(1);
					float g_min = a_gs.get(1);
					for( i=1; i<a_gs.size()-1; i=i+1) {
						if(a_gs.get(i)>g_max)
							g_max = a_gs.get(i);
						if(a_gs.get(i)<g_min)
							g_min = a_gs.get(i);
					}
					// for storing step related value
					List<Integer> p_index = new ArrayList<Integer>();
					List<Float> p_a_gs = new ArrayList<Float>();
					List<Long> p_ts  = new ArrayList<Long>();

					List<Integer> b_index = new ArrayList<Integer>();
					List<Float> b_a_gs = new ArrayList<Float>();
					List<Float> step_esti = new ArrayList<Float>();
					List<Float> step_angle_esti = new ArrayList<Float>();
					
					// peak detection threshold
					float pek_thre = 10.50f;
					float btm_thre = (float)(avg_g-0.20);
					
					for( i=1; i<a_gs.size()-2; i=i+1) {
						if ( (a_gs.get(i)>a_gs.get(i-1)) && (a_gs.get(i)>a_gs.get(i+1)) ) // detect peaks
						{
							if(a_gs.get(i) > pek_thre){
								if(Step_Count>0) {       // this is not the first time detection
									if(i>a_g1.size()-1) {
										p_index.add(i);
										p_a_gs.add(a_gs.get(i));
										p_ts.add(tss.get(i));
									}
								} else {
									p_index.add(i);
									p_a_gs.add(a_gs.get(i));
									p_ts.add(tss.get(i));
								}
								
							}	
						}
						if ( (a_gs.get(i)<a_gs.get(i-1)) && (a_gs.get(i)<a_gs.get(i+1)) )  // detect bottoms
						{
							if(a_gs.get(i) < btm_thre) {
								b_index.add(i);
							    b_a_gs.add(a_gs.get(i));
							}	
						}			
					}
					
					E_flag = false;
					// if there is step event
					if(p_index.size()>0) {
						// estimate the step lengths
						for(i=0; i<p_index.size(); i=i+1) {
							for( j=0; j<b_index.size(); j=j+1) {
								if(b_index.get(j)>p_index.get(i)) {
									// calculate step length 
									float temp = (float)(Math.pow(p_a_gs.get(i)-b_a_gs.get(j), 0.25));
									step_esti.add(temp);

									// calculate the step angle 
									float angle=0f;
									
									for(int m=p_index.get(i); m<b_index.get(j);m++) {
									angle =(float)(angle + 0.02*an_g.get(m)); 
								}
			                        angle = (float)(180*angle/Math.PI);  // convert into degrees
									step_angle_esti.add(angle);
									break;
								}
								else
									continue;						
							}
							
						}
						// update the detection result
						Step_Count = Step_Count + p_index.size();
						Step_Time.addAll(p_ts);
						Step_Length.addAll(step_esti);
						Step_Heading.addAll(step_angle_esti);
						E_flag = true;
						
					}
					
					// buffer in latest data
					tss1.clear();
					tss1.addAll(tss2);
					tss2.clear();
					
					a_g1.clear();
					a_g1.addAll(a_g2);
					a_g2.clear();
					
					an_g1.clear();
					an_g1.addAll(an_g2);
					an_g2.clear();
					
				}
	  		
			}catch(IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Cannot read from "+skt.getInetAddress().getHostAddress());
				e.printStackTrace();
				
			} finally {
			}	
		}

	}

}
