import java.util.ArrayList;
import java.util.HashMap;

public class RoundRobin {
			private HashMap<Integer,ArrayList<Job>> ready;			
			private ArrayList<Job> blocked;
			private ArrayList<Job> done;
			private int timeSlice;
			ArrayList<CPU> cpus;
			private HashMap<CPU, Integer> timeLeft;
			ArrayList<Job> jobs;
			private int numJobs;
						
			public RoundRobin(ArrayList<Job> jobs, int timeSlice, ArrayList<CPU> cpus) {
							
				ready = new HashMap<Integer,ArrayList<Job>>();
				
				//Inserindo no Hash as filas de prioridade de processos prontos para executar
				for(int i = 1; i < 10; i++)
					ready.put(i,new ArrayList<Job>());
				
				//Processos bloqueados podem ser armazenados em filas simples, por ordem de chegada.
				//Considerando que o tempo de IO é constante, basta verificar apenas se o primeiro da fila está novamente pronto para executar.
				//Se ele não estiver, aqueles que chegaram depois dele, logicamente, também não estarão.
				blocked = new ArrayList<Job>();
				
				//Não há necessidade de ordenar os processos prontos. Utilizei um ArrayList por padronização e pela facilidade de iteração.
				done = new ArrayList<Job>();
								
				this.timeSlice = timeSlice;
				
				this.cpus = cpus;
				
				timeLeft = new HashMap<CPU, Integer>();
				for(CPU cpu : cpus) 
					timeLeft.put(cpu, timeSlice);
				
				this.jobs = jobs;
				numJobs = jobs.size();
			}
			
			public boolean run(int time) {
				
				//Executa apenas se o número de processos na fila de concluídos for menor do que o de processos lidos do arquivo
				if(!JobsDone()) {
					
					//Verifica se algum processo chegou no tempo atual
					checkJobsArrived(jobs, time);
					
					//Verifica se há processos bloqueados que concluíram sua operação de IO
					checkBlockedQueue(time);
										
					freeCPUs(cpus, time);
					
					
					
					//Para todas as CPUs livres, tenta obter um novo processo pronto para executar
					for(CPU cpu : cpus) {
						if(cpu.getJob() == null) 
							cpu.receiveJob(pickNextJob(cpu));							
					}
					
					for(CPU cpu : cpus) {
						Job job = cpu.getJob();
						
						//Para todas as CPUs ocupadas 
						if(job != null) {
							
							//...e cujos processos não estejam recém mudando o contexto
							if(job.getStatus() != JobStatus.CHANGING_CONTEXT) {
							
								//Verifica se o processo atual vai ser preemptado por outro de melhor prioridade
								int newPriority = checkPreemption(cpu.getJob());
								
								//Caso haja preempção, muda o processo que está usando a CPU	
								if(newPriority > 0) {
									
									Job removedJob = cpu.removeJob();
									removedJob.setStatus(JobStatus.READY);
									cpu.receiveJob(pickNextJob(cpu));
									receiveJob(removedJob);
								}
							}
						//Agora todas as cpus estão no estado correto e devem rodar
						cpu.runJob();
						decrementTimeLeft(cpu);								
						}
					}
				
					//Atualiza o log de todas as CPUs
					for(CPU cpu : cpus)
						cpu.setLog();
				
					//Atualiza os dados de tempo de resposta e de tempo de espera dos processo na fila de prontos		
					updateJobsTimeStats();				
				}
				
				if(!JobsDone()) 
					//Ainda há processos que não terminaram
					return true;				
				
				else return false; ////Todos os processos já terminaram
			}

			private void freeCPUs(ArrayList<CPU> cpus, int time) {
				
				Job job;
				boolean released; 
				
				for(CPU cpu : cpus) {
					
					released = false;
					
					if(cpu.getJob() != null) {
						
						//Retira o processo da CPU caso a fatia de tempo dele tenha se esgotado
						if(!checkHasTimeLeft(cpu)) {
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
						
							else {
								if(cpu.getJob().checkIO(time)) {
									released = true;
									job = cpu.removeJob();
									job.setStatus(JobStatus.BLOCKED);		
									receiveJob(job);
								}
							}						
						}
					}
					if(released)
						resetTimeLeft(cpu);
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
						
			public boolean checkHasTimeLeft(CPU cpu) {
				return timeLeft.get(cpu) > 0;
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
							
				//Verifica se as filas de prioridades menores do que a do processo atual contém um job pronto para ser executado
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
						
			public void decrementTimeLeft(CPU cpu) {
				timeLeft.compute(cpu, (k,v) -> v = v-1);
			}
			
			public void resetTimeLeft(CPU cpu) {
				timeLeft.put(cpu, timeSlice);
			}
			
			//Verifica se ainda há processos a executar
			public boolean JobsDone() {				
				return done.size() == numJobs;
			}
						
			//Escolhe o próximo processo a ser executado
			public Job pickNextJob(CPU cpu) {
				Job nextJob = null;
				
				for(int i = 1; i < 10 && nextJob == null; i++)
					if(checkReadyQueue(i)){
						nextJob = ready.get(i).remove(0);
						nextJob.setStatus(JobStatus.CHANGING_CONTEXT);
						resetTimeLeft(cpu);
					}
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
				int responseMean = 0;
				int waitingMean = 0;
				
				for(Job job : done) {
					responseMean += job.getResponseTime();
					waitingMean += job.getWaitingTime();			
				}
				
				return "Média do tempo de resposta: " + String.valueOf(responseMean) 
						+ "\nMédia do tempo de espera: " + String.valueOf(waitingMean);				
			}
			
			public ArrayList<Job> getReadyJobs(int priority){
				return ready.get(priority);
			}

			
			
}
