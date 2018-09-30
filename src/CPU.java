
public class CPU {
	private Job job;
	private String status;
	
	public CPU() {		
		status = "-";
	}
	
	public Job getJob() {
		return job;
	}
	
	public void receiveJob(Job job) {
		this.job = job;
		setStatus("C");
	}		
	
	public Job removeJob() {
		Job aux = this.job;
		this.job = null;
		setStatus("C");
		return aux;		
	}
	
	public void runJob() {			
		job.incrementReceivedTime();
		setStatus(String.valueOf(job.getID()));
	}
	
	public String getStatus() {
		return status;
	}
	
	public String toString() {
		return "CPU tem Job " + job.getID() + " - Status " + job.getStatus();
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
