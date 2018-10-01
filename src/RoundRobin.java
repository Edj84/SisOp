import java.util.ArrayList;
import java.util.HashMap;

public class RoundRobin {
			private HashMap<Integer,ArrayList<Job>> ready;			
			private ArrayList<Job> blocked;
			private ArrayList<Job> done;
			private int timeSlice;
			private int timeLeft;
			private int numJobs;
						
			public RoundRobin(ArrayList<Job> jobs, int timeSlice) {
							
				numJobs = jobs.size();
				
				this.timeSlice = timeSlice;
				resetTimeLeft();
				
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
										
					//Verifica se algum processo est� usando a CPU
					if(cpu.getJob() == null) {
						//CPU livre. Tenta obter um novo processo pronto para executar
						cpu.receiveJob(pickNextJob(cpu));
					}
					
					//CPU ocupada.
					else {
						//Verifica se o processo que est� na CPU conclu�u sua execu��o
						if(cpu.getJob().checkDone()) {
							Job jobRemoved = cpu.removeJob();
							receiveJob(jobRemoved);
						}
						//Verifica se o processo atual no processador terminou sua fatia de tempo
						else {
							
							if(!checkHasTimeLeft(cpu)) {
					
								//Retira o processo da CPU caso a fatia de tempo dele tenha se esgotado
								Job removedJob = cpu.removeJob();
								removedJob.setStatus(JobStatus.READY);								
								receiveJob(removedJob);								
							}
						
							else {
						
								//Verifica se o processo que est� usando a CPU vai ser preemptado por outro de melhor prioridade
								int newPriority = checkPreemption(cpu.getJob());
								if(newPriority > 0) {
									//Caso haja preemp��o, muda o processo que est� usando a CPU	
									Job removedJob = cpu.removeJob();
									removedJob.setStatus(JobStatus.READY);
									receiveJob(removedJob);																	
								}
							
								//Processo n�o foi preemptado e ainda tem tem tempo na CPU. Verifica se ele vai fazer opera��o de IO
								else {
									if(cpu.getJob().checkIO(time)) {
										Job removedJob = cpu.removeJob();
										removedJob.setStatus(JobStatus.BLOCKED);
										removedJob.checkDone();
										receiveJob(removedJob);							
									}
								
									//Processo "sobreviveu" na CPU (n�o terminou, ainda tem tem tempo, n�o foi preemptado e n�o fez opera��o de IO).
									//Vai executar por uma unidade de tempo
									else {
										cpu.runJob();
										decrementTimeLeft();										
									}
								}
							}
						}
						if(cpu.getJob() == null)
							cpu.receiveJob(pickNextJob(cpu));	
					}
				
				
				//Ainda h� processos que n�o terminaram
				return true;
				}
				
				else return false; ////Todos os processos j� terminaram
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
			
			
			public boolean checkHasTimeLeft(CPU cpu) {
				return timeLeft > 0;
			}
			

			private void checkJobsArrived(ArrayList<Job> jobs, int time) {
				
				//Envia para o escalonador os jobs que chegaram no instante anterior de tempo
				for(Job job : jobs) {
					if(job.getArrivalTime() == time-1) 
						receiveJob(job);
				}
				
									
					/*
					for(Job j : jobs) {
						if(j.getRunTime() == time)	
							receiveJob(j);
							jobs.remove(j);
					}
					
					Gerou 
					Exception in thread "main" java.util.ConcurrentModificationException					
					*/
					
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
						
			public void decrementTimeLeft() {
				timeLeft--;
			}
			
			public void resetTimeLeft() {
				timeLeft = timeSlice;
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
						resetTimeLeft();
					}
				return nextJob;
			}
			
			public Job pickNextJob(int queue) {
				Job nextJob = ready.get(queue).remove(0);
				nextJob.setStatus(JobStatus.CHANGING_CONTEXT);
				resetTimeLeft();
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
			
			private Job removeJobFromCPU(CPU cpu, JobStatus status) {
				Job jobRemoved = cpu.removeJob();
				jobRemoved.setStatus(status);
				return jobRemoved;		
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

			public ArrayList<Job> getReadyJobs(int priority){
				return ready.get(priority);
			}
			
}
