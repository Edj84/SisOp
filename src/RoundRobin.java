import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class RoundRobin {
			private HashMap ready;
			private ArrayList<Job> blocked;
			private ArrayList<Job> done;
			private int timeSlice;
			
						
			public RoundRobin(int timeSlice) {
				this.timeSlice = timeSlice;
				ready = new HashMap<Integer,ArrayList<Job>>();
					
				for(int i = 1; i < 10; i++)
					ready.put(i,new ArrayList<Job>());
				
				blocked = new ArrayList<Job>();
				done = new ArrayList<Job>();
			}
			
			public void receiveJob(Job job) {
				ArrayList<Job> aux = null;
				
				switch(job.getStatus()) {
				
					case READY:
						aux = (ArrayList<Job>) ready.get(job.getPriority());											
						break;
					
					case BLOCKED:
						aux = blocked;						
						break;
						
					case DONE:
						aux = done;
						break;					
				}
				
				aux.add(job);				
			}
			
			public void selectJob() {
				
				Job nextJob = null;
				ArrayList<Job> queue;
				
				for(int i = 1; nextJob==null && i<10; i++) {
					queue = (ArrayList<Job>) ready.get(i);
					
				}
				
			}
			
			
			
}
