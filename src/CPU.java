
public class CPU {
	private Job job;	
	
	public CPU() {		
	}
	
	public Job getJob() {
		return job;
	}
	
	public void receiveJob(Job job) {
		this.job = job;
	}		

	public void setNewJob(Job job) {
		this.job = job;
	}

	public Job removeJob() {
		Job aux = this.job;
		this.job = null;
		return aux;		
	}
	
	public void runJob() {			
		job.incrementReceivedTime();
	}
	
}
