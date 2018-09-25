
public class CPU {
	private Job job;	
	
	public CPU() {		
	}
	
	public Job getJob() {
		return job;
	}
	
	public void runJob() {
		
			
		
	}
	
	
	
	

	public Job changeJob(Job job) {
		Job aux = this.job;
		this.job = job;
		return aux;
	}	
	
}
