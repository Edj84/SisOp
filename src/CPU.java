
public class CPU {
	private Job job;
	private String status;
	
	public CPU() {		
		setStatus();
	}
	
	public Job getJob() {
		return job;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void receiveJob(Job job) {
		this.job = job;
		setStatus();
	}		
	
	public Job removeJob() {
		Job aux = this.job;
		this.job = null;
		setStatus();
		return aux;		
	}
	
	public void run() {			
		setStatus();
		if(job != null) {
			job.setStatus(JobStatus.RUNNING);
			job.setAnswered();
			job.updateReceivedTime();
		}		
	}
	
	public void setStatus() {
		
		String status;
		
		if(job == null)
			status = "-";
		
		else {
			switch (job.getStatus()) {
			case RUNNING:
				status = String.valueOf(job.getID());
				break;
			
			case CHANGING_CONTEXT:
				status = "C";
				break;
				
			default:
				status = "";
				break;		
			}
		}
		
		this.status = status;
	}
	
	public String toString() {
		if(job != null)
			return "CPU tem Job " + job.getID() + " - Status " + job.getStatus() + " receivedTime " + job.getReceivedTime();
		else
			return "CPU não tem job no momento";
	}	
}
