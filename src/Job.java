import java.util.ArrayList;

public class Job {
	private static int jobCount  = 0;
	private int jobID;
	private int arrivalTime;
	private int runTime;
	private int priority;
	private ArrayList<Integer> IO;
	private JobStatus status;
	private int responseTime;
	private int waitingTime;
	private int receivedTime;
	
	
	public Job(int arrivalTime, int runTime, int priority, ArrayList<Integer> IO) {
		jobCount++;
		jobID = jobCount;
		this.arrivalTime = arrivalTime;
		this.runTime = runTime;
		this.priority = priority;
		this.IO = IO;
		status = JobStatus.READY;
		responseTime = 0;
		waitingTime = 0;
		receivedTime = 0;
	}
	
	//erase this later!
	public void includeIO(int n){
		IO.add(n);
	}
	
	public int getJobID() {
		return jobID;
	}
	
	public void setResponseTime(int initTime) {
		responseTime = initTime - arrivalTime;
	}
	
	public int getResponseTime() {
		return responseTime;
	}
	
	public void incrementWaitingTime() {
		waitingTime++;
	}
	
	public int getWaitingTime() {
		return waitingTime;
	}
	
	public void setStatus(JobStatus status) {
		this.status = status;
	}
	
	public JobStatus getStatus() {
		return status;		
	}
	
	private void checkDone() {
		if(receivedTime == runTime)
			status = JobStatus.DONE;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public int getReceivedTime() {
		return receivedTime;
	}
	
	public void incrementReceivedTime() {
		receivedTime++;
		checkDone();
	}
	
	public boolean checkIO() {
	
		if(IO.size()>0) {
			
			for(int time : IO) 
			time--;
	
			if(IO.get(0) == 0) {
				IO.remove(0);
				return true;
			}
		}
		
	return false;
	}
	
	@Override
	public String toString() {
		return "Job " + jobID 
					   + "\nStatus: " + status
					   + "\nPriority: " + priority;
	}
	
	
}
