import java.util.ArrayList;

public class Job {
	private static int jobCount = 0;
	private int jobID;
	private int arrivalTime;
	private int runTime;
	private int priority;
	private ArrayList<Integer> IO;
	private int IOEndTime;
	private JobStatus status;
	private int responseTime;
	private int waitingTime;
	private int receivedTime;
	private boolean answered;
	
	
	public Job() {
		jobCount++;
		jobID = jobCount;
	}
	
	public Job(int arrivalTime, int runTime, int priority, ArrayList<Integer> IO) {
		jobCount++;
		jobID = jobCount;
		this.arrivalTime = arrivalTime;
		this.runTime = runTime;
		this.priority = priority;
		this.IO = IO;
		IOEndTime = -1;
		status = JobStatus.CREATED;
		responseTime = 0;
		waitingTime = 0;
		receivedTime = 0;
		answered = false;
	}
	
	public int getID() {
		return jobID;
	}
	
	public void setResponseTime(int initTime) {
		responseTime = initTime - arrivalTime;
	}
	
	public boolean getAnswered() {
		return answered;
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
	
	public boolean checkDone() {
		if(receivedTime == runTime) {
			status = JobStatus.DONE;
			return true;
		}
		
		else return false;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public int getRunTime() {
		return runTime;
	}
	
	public int getArrivalTime() {
		return arrivalTime;
	}
	
	public void setIOEndTime(int IOEndTime) {
		this.IOEndTime = IOEndTime;
	}

	public int getReceivedTime() {
		return receivedTime;
	}
	
	public void incrementReceivedTime() {
		receivedTime++;		
	}
	
	public boolean checkIO(int time) {
		
		if(IO.size()>0) {
			
			if(IO.get(0) == receivedTime) {
				int IOEndTime = time +4;
				IO.remove(0);
				setIOEndTime(IOEndTime);
				return true;
			}
		}
		
	return false;
	}
	
	public int getIOEndTime(){
		return IOEndTime;
	}
	
	@Override
	public String toString() {
		return "Job " + jobID 
					   + "\nStatus: " + status
					   + "\nPriority: " + priority
					   + "\narrivalTime: " + arrivalTime
					   + "\nrunTime: " + runTime
					   + "\nresponseTime: " + responseTime
					   + "\nwaitingTime: " + waitingTime;
	}

	
	
	
}
