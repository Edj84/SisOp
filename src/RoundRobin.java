import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class RoundRobin {
			private ArrayList<Job> jobs;
			private HashMap<Integer,ArrayList<Job>> ready;
			private ArrayList<Job> blocked;
			private ArrayList<Job> done;
			private int timeSlice;
			private int timeLeft;
			private int numJobs;
						
			public RoundRobin(ArrayList<Job> jobs, int timeSlice) {
				//Agrupa todos os processos lidos do arquivos e que ser�o escalonados conforme forem chegando para executar
				this.jobs = jobs;
				
				numJobs = jobs.size();
				
				this.timeSlice = timeSlice;
				timeLeft = timeSlice;
				
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
			
			public boolean run(CPU cpu, int time) {
				
				//Executa enquanto o n�mero de processos na fila de conclu�dos for menor do que o de processos lidos do arquivo
				if(done.size()<numJobs) {
					
					//Verifica se algum processo chegou no tempo atual
					checkJobsArrived(time);
					
					//Verifica se algum processo est� usando a CPU
					if(cpu.getJob() != null) {							
						
						//CPU ocupada. Verifica se o processo que est� usando a CPU vai ser preemptado por outro de melhor prioridade
						int preempt = checkPreemption(cpu.getJob());
						if(preempt > 0) {
							//Caso haja preemp��o, muda o processo que est� usando a CPU	
							Job removedJob = removeJob(cpu, JobStatus.READY);
							receiveJob(removedJob);
							cpu.setNewJob(pickNextJob(preempt));							
						}
						
						//J� que n�o houve preemp��o, verifica se o processo atual no processador terminou sua fatia de tempo
						else if(checkHasTimeLeft(cpu)) {
								//Retira o processo da CPU caso a fatia de tempo tenha se esgotado
								removeJob(cpu, JobStatus.READY);
								getNewJob(cpu);
						}
						
							//Processo n�o foi preemptado e ainda tem tem tempo na CPU. Verifica se ele vai fazer opera��o de IO
							else if(cpu.getJob().checkIO(time)) {
									removeJob(cpu, JobStatus.BLOCKED);
									getNewJob(cpu);						
							}
								//Processo "sobreviveu" na CPU (n�o foi preemptado, ainda tem tem tempo e n�o fez opera��o de IO).
								//Vai executar por mais uma unidade de tempo
								else {
									cpu.runJob();
									checkJobDone(cpu.getJob());
								}
					}
					
					//CPU livre. Tenta obter um novo processo pronto para executar
					else
						getNewJob(cpu);					
					
				return true; //Ainda h� processos que n�o terminaram
				}
				
				return false; ////Todos os processos j� terminaram
			}
			
			private void checkJobsArrived(int time) {
				
				if(!jobs.isEmpty()) {
					for(Job j : jobs) {
						if(j.getRunTime() == time)	
							//Retira o job a executar da lista de jobs lidos do arquivo e o envia para o escalonador
							receiveJob(j);
							jobs.remove(j);
					}
				}		
			}	
			
			private int checkPreemption(Job currentJob) {
				int currentPriority = currentJob.getPriority();
				int preempt = 0;
				
				//Processo com prioridade 1 n�o pode ser preemptado
				if(currentPriority == 1)
					return preempt;
				
				//Verifica se as filas de prioridades menores do que a do processo atual cont�m um job pronto para ser executado
				for(int i = 1; i < currentPriority && !checkReadyQueue(i); i++)
					preempt++; 
					
				return preempt;	
			}

			private Job removeJob(CPU cpu, JobStatus status) {
				Job jobRemoved = cpu.removeJob();
				jobRemoved.setStatus(status);
				return jobRemoved;		
			}
			
			private Job getNewJob(CPU cpu) {
				return pickNextJob();		
			}
			
			private boolean checkJobDone(Job job) {
				if(job.getReceivedTime() == job.getRunTime()) {
					job.setStatus(JobStatus.DONE);
					return true;
				}
				
				return false;
			}
						
			//Recebe um novo processo e o inclui na fila adequada conforme seu status
			public void receiveJob(Job job) {
				
				ArrayList<Job> aux = null;
				
				switch(job.getStatus()) {
				
					case READY:
						int priority = job.getPriority();
						aux = (ArrayList<Job>) ready.get(priority);											
						break;
					
					case BLOCKED:
						aux = blocked;						
						break;
						
					case DONE:
						aux = done;
						break;					
				}
				
				aux.add(job);				
			}
			
			//Verifica se a fila com a prioridade i tem processo pronto para executar
			public boolean checkReadyQueue(int priority) {
				
				ArrayList<Job> aux = (ArrayList<Job>) ready.get(priority);
				
				return !aux.isEmpty();
			}
			
			public void checkBlockedQueue() {
				if(!blocked.isEmpty()) {
					for(Job j : blocked)
						;
				}
			}
			//Escolhe o pr�ximo processo a ser executado
			
			public Job pickNextJob() {
				Job nextJob = null;
				
				for(int i = 1; i < 10 && nextJob == null; i++)
					if(!ready.get(i).isEmpty())
						nextJob = ready.get(i).remove(0);
				
				return nextJob;
			}
			
			public Job pickNextJob(int queue) {
				return ready.get(queue).remove(0);
			}
			
			public void decrementTimeLeft() {
				timeLeft--;
			}
			
			public boolean checkHasTimeLeft(CPU cpu) {
				return cpu.getJob().getReceivedTime() < 3;
			}
			
			//Verifica se ainda h� processos a executar
			public boolean JobsDone(int numJobs) {
				
				return done.size() == numJobs;
			}

			
			
			
			
}
