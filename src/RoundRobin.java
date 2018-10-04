import java.util.ArrayList;
import java.util.HashMap;

public class RoundRobin {
	private HashMap<Integer,ArrayList<Job>> ready;			
	private ArrayList<Job> blocked;
	private ArrayList<Job> done;
	private int quantum;
	private int quantumLeft;
	private int numJobs;
						
	public RoundRobin(ArrayList<Job> jobs, int quantum) {
							
		numJobs = jobs.size();
			
		this.quantum = quantum;
		resetQuantumLeft();
				
		ready = new HashMap<Integer,ArrayList<Job>>();
				
		//Inserindo no Hash as filas de prioridade de processos prontos para executar
		for(int i = 1; i < 10; i++)
			ready.put(i,new ArrayList<Job>());
				
			//Processos bloqueados podem ser armazenados em filas simples, por ordem de chegada.
			//Considerando que o tempo de IO � constante, basta verificar apenas se o primeiro da fila est� novamente pronto para executar.
			//Se ele n�o estiver, aqueles que chegaram depois dele, logicamente, tamb�m n�o estar�o.
			blocked = new ArrayList<Job>();
				
			//N�o h� necessidade de ordenar os processos prontos. Utilizei um ArrayList por padroniza��o e pela facilidade de itera��o.
			done = new ArrayList<Job>();
	}
			
	public boolean run(CPU cpu, ArrayList<Job> jobs, int time) {
				
		//Executa apenas se o n�mero de processos na fila de conclu�dos for menor do que o de processos lidos do arquivo
		if(done.size()<numJobs) {
					
			//Verifica se algum processo chegou no tempo atual
			checkJobsArrived(jobs, time);
					
			//Verifica se h� processos bloqueados que conclu�ram sua opera��o de IO
			checkBlockedQueue(time);
										
			//Se houver processo na CPU, verifica se ele deve sair por fim de sua fatia de tempo ou conclus�o de sua execu��o
			if(cpu.getJob() != null) 
				freeCPU(cpu, time);
					
			//Se a CPU estiver liberada, tenta obter um novo processo pronto para executar
			if(cpu.getJob() == null) 
				cpu.receiveJob(pickNextJob(cpu));							
			}
					
			else {
			//Processo "sobreviveu" no processador - n�o terminou sua fatia de tempo, n�o concluiu sua execu��o, n�o fez IO e nem acabou de chegar. 
			//Verifica se ele vai ser preemptado por outro de melhor prioridade
				int newPriority = checkPreemption(cpu.getJob());
				//Caso haja preemp��o, muda o processo que est� usando a CPU	
				if(newPriority > 0) {
					Job removedJob = cpu.removeJob();
					removedJob.setStatus(JobStatus.READY);
					cpu.receiveJob(pickNextJob(cpu));
					receiveJob(removedJob);
				}
			}
					
			//CPU pronta para rodar
			cpu.run();
					
			//Se a CPU rodou um processo, sua fatia de tempo deve ser decrementada
			if(cpu.getJob() != null && cpu.getJob().getStatus() != JobStatus.CHANGING_CONTEXT)
				decrementQuantumLeft();					
					
			//Atualiza tempos de espera e de resposta de todos os processos
			updateJobsTimeStats();
					
			if(!JobsDone(numJobs)) 
				//Ainda h� processos que n�o terminaram. Segue a simula��o.
				return true;				
			//Todos os processos j� terminaram. Fim de simula��o.
			else 
				return false; 
			}		
			
	private void freeCPU(CPU cpu, int time) {
				
		Job job;
		boolean released;				
					
		released = false;
					
		if(cpu.getJob() != null) {
						
			//Retira o processo da CPU caso a fatia de tempo dele tenha se esgotado
			if(!checkHasQuantumLeft(cpu)) {
				released = true;
				job = cpu.removeJob();
				job.setStatus(JobStatus.READY);								
				receiveJob(job);								
			}
						
			//Verifica se o processo terminou de rodar
			else {
				if(cpu.getJob().checkDone()) {
					released = true;
					job = cpu.removeJob();
					job.setStatus(JobStatus.DONE);		
					receiveJob(job);
				}
						
				//Verifica se o processo far� IO
				else {
					if(cpu.getJob().checkIO(time)) {
						released = true;
						job = cpu.removeJob();
						job.setStatus(JobStatus.BLOCKED);		
						receiveJob(job);
					}
				}						
			}
					
			if(released)
				resetQuantumLeft();
		}
	}
			
	private void updateJobsTimeStats() {
				
		ArrayList<Job> queue;
				
		for(int i = 1; i < 10; i++) {
			queue = ready.get(i);
					
			for(Job job : queue) 
				job.updateTimeStats();
		}				
	}

	public void checkBlockedQueue(int time) {
		if(!blocked.isEmpty()) {
			Job jobBlocked;
			for(int i = 0; i < blocked.size(); i++) {
				jobBlocked = blocked.get(i);
				if(jobBlocked.getIOEndTime() == time) {
					jobBlocked.setStatus(JobStatus.READY);
					blocked.remove(i);
					receiveJob(jobBlocked);						
				}							
			}
		}
	}
						
	public boolean checkHasQuantumLeft(CPU cpu) {
		return quantumLeft > 0;
	}
			
	private void checkJobsArrived(ArrayList<Job> jobs, int time) {
				
	//Envia para o escalonador os jobs que chegaram no instante anterior de tempo
		for(Job job : jobs) {
			if(job.getArrivalTime() == time-1) 
				receiveJob(job);
		}	
	}

	private int checkPreemption(Job currentJob) {
		int currentPriority = currentJob.getPriority();
		int newPriority = 0;
							
		//Verifica se as filas de prioridades menores do que a do processo atual cont�m um job pronto para ser executado
		for(int i = 1; i < currentPriority && newPriority == 0; i++) {
			if(checkReadyQueue(i))
				newPriority = i; 
		}
					
		return newPriority;	
	}
			
	//Verifica se a fila com a prioridade i tem processo pronto para executar
	public boolean checkReadyQueue(int priority) {
				
		ArrayList<Job> aux = ready.get(priority);
				
		return !aux.isEmpty();
	}
						
	public void decrementQuantumLeft() {
		quantumLeft--;
	}
			
	public void resetQuantumLeft() {
		quantumLeft = quantum;
	}
			
	//Verifica se ainda h� processos a executar
	public boolean JobsDone(int numJobs) {
		return done.size() == numJobs;
	}
						
	//Escolhe o pr�ximo processo a ser executado
	public Job pickNextJob(CPU cpu) {
		Job nextJob = null;
				
		for(int i = 1; i < 10 && nextJob == null; i++)
			if(checkReadyQueue(i)){
				nextJob = ready.get(i).remove(0);
				nextJob.setStatus(JobStatus.CHANGING_CONTEXT);
				resetQuantumLeft();
		}
		return nextJob;
	}
			
	public Job pickNextJob(int queue) {
		Job nextJob = ready.get(queue).remove(0);
		nextJob.setStatus(JobStatus.CHANGING_CONTEXT);
		resetQuantumLeft();
		return nextJob; 
	}
			
	//Recebe um novo processo e o inclui na fila adequada conforme seu status
	public void receiveJob(Job job) {
				
		int priority = job.getPriority();
				
		switch(job.getStatus()) {
				
			case READY:						
				ready.get(priority).add(job);											
				break;
					
			case BLOCKED:
				blocked.add(job);
				break;
						
			case DONE:
				done.add(job);
				break;
						
			case CHANGING_CONTEXT: case CREATED:
				ready.get(priority).add(job);
				job.setStatus(JobStatus.READY);
				break;
						
			default:
				break;
		}							
	}
			
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Escalonador Round Robin \n"); 
				
		for(int i = 1; i < 10; i++) {
			str.append("Fila READY " + i + " com " + ready.get(i).size() + " processos\n");
		}
				
	str.append("Fila BLOCKED com " + blocked.size() + " processos\n");
	str.append("Fila DONE com " + done.size() + " processos");
				
	return str.toString();
							 
	}

	public String calculate() {
		float burstSum = 0;
		float waitingSum = 0;
				
		for(Job job : done) {
			burstSum += job.getBurstTime();
			waitingSum += job.getWaitingTime();			
		}
				
		float burstMean = burstSum/numJobs;
		float waitingMean = waitingSum/numJobs;
				
		return "M�dia do tempo de resposta: " + burstMean 
				+ "\nM�dia do tempo de espera: " + waitingMean;				
	}
			
	public ArrayList<Job> getReadyJobs(int priority){
		return ready.get(priority);
	}
			
}
