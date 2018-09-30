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
										
					//Verifica se algum processo est� usando a CPU
					if(cpu.getStatus().equals("-"))
						//CPU livre. Tenta obter um novo processo pronto para executar
						pickNextJob(cpu);
						
					else {
						//CPU ocupada. 
						
						//Verifica se o processo que est� usando a CPU vai ser preemptado por outro de melhor prioridade
						int newPriority = checkPreemption(cpu.getJob());
						if(newPriority > 0) {
							//Caso haja preemp��o, muda o processo que est� usando a CPU	
							Job removedJob = removeJob(cpu, JobStatus.READY);
							receiveJob(removedJob);
							Job newJob = pickNextJob(newPriority);
							newJob.setStatus(JobStatus.RUNNING);
							cpu.receiveJob(newJob);							
						}
											
						//J� que n�o houve preemp��o, verifica se o processo atual no processador terminou sua fatia de tempo
						else {
							if(!checkHasTimeLeft(cpu)) {
						
								//Retira o processo da CPU caso a fatia de tempo dele tenha se esgotado
								Job removedJob = removeJob(cpu, JobStatus.READY);
								removedJob.checkDone();
								receiveJob(removedJob);
								pickNextJob(cpu);
							}
						
							//Processo n�o foi preemptado e ainda tem tem tempo na CPU. Verifica se ele vai fazer opera��o de IO
							else {
								if(cpu.getJob().checkIO(time)) {
							
									Job removedJob = removeJob(cpu, JobStatus.BLOCKED);
									receiveJob(removedJob);
									pickNextJob(cpu);						
								}
								//Processo "sobreviveu" na CPU (n�o foi preemptado, ainda tem tem tempo e n�o fez opera��o de IO).
								//Vai executar por mais uma unidade de tempo
								else {
									cpu.runJob();
									decrementTimeLeft();
									cpu.getJob().checkDone();
								}
							}
						}
					}
				
				//Ainda h� processos que n�o terminaram
				return true;
				}
				
				return false; ////Todos os processos j� terminaram
			}
			
			public void checkBlockedQueue() {
				if(!blocked.isEmpty()) {
					for(Job j : blocked)
						;
				}
			}
			
			public boolean checkHasTimeLeft(CPU cpu) {
				return timeLeft > 0;
			}

			private void checkJobsArrived(ArrayList<Job> jobs, int time) {
				
				//Envia para o escalonador os jobs que chegaram naquele instante de tempo
				for(Job job : jobs) {
					if(job.getArrivalTime() == time) 
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
			public void pickNextJob(CPU cpu) {
				Job nextJob = null;
				
				for(int i = 1; i < 10 && nextJob == null; i++)
					if(checkReadyQueue(i)){
						nextJob = ready.get(i).remove(0);
						nextJob.setStatus(JobStatus.RUNNING);
						cpu.receiveJob(nextJob);
						resetTimeLeft();
					}
			}
			
			public Job pickNextJob(int queue) {
				resetTimeLeft();
				return ready.get(queue).remove(0);
			}
			
			//Recebe um novo processo e o inclui na fila adequada conforme seu status
			public void receiveJob(Job job) {
				
				switch(job.getStatus()) {
				
					case READY:
						int priority = job.getPriority();
						ready.get(priority).add(job);											
						break;
					
					case BLOCKED:
						blocked.add(job);
						
						break;
						
					case DONE:
						done.add(job);
						break;
						
					default:
						break;
				}							
			}
			
			private Job removeJob(CPU cpu, JobStatus status) {
				Job jobRemoved = cpu.removeJob();
				jobRemoved.setStatus(status);
				jobRemoved.setReceivedTime(0);
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
