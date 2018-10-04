
public class CPU {
	private static int CPUCount = 0;
	private int ID;
	private Job job;
	private String status;
	private StringBuilder log;
	
	public CPU() {		
		CPUCount++;
		ID = CPUCount;
		setStatus();
		log = new StringBuilder();
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
	
	public void runJob() {			
		job.setStatus(JobStatus.RUNNING);
		job.setAnswered();
		job.updateReceivedTime();
		setStatus();		
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
	
	public String getLog() {
		return log.toString();
	}
	
	public void setLog() {
		log.append(status);		
	}	
	
	public String toString() {
		if(job != null)
			return "CPU " + ID + " tem Job " + job.getID() + " - Status " + job.getStatus() + " receivedTime " + job.getReceivedTime();
		else
			return "CPU " + ID + "não tem job no momento";
	}

	
}
