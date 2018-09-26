import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class RoundRobin {
			private HashMap ready;
			private ArrayList<Job> blocked;
			private ArrayList<Job> done;
			private int timeSlice;
			private int timeLeft;
						
			public RoundRobin(int timeSlice) {
				this.timeSlice = timeSlice;
				timeLeft = timeSlice;
				
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
			}
			
			//recebe um novo processo e o inclui na fila adequada conforme seu status
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
				
				return aux.isEmpty();
			}
			
			//Escolhe o próximo processo a ser executado
			
			public Job selectNextJob() {
				
				Job nextJob;
				
				return null;
			}
			
			public void decrementTimeLeft() {
				timeLeft--;
			}
			
			public boolean checkHasTimeLeft() {
				return timeLeft > 0;
			}
			
			//Verifica se ainda há processos a executar
			public boolean JobsDone(int numJobs) {
				
				return done.size() == numJobs;
			}

			
			
			
			
}
