import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 Defini��o:
Ler de um arquivo as seguintes informa��es, nesta ordem: 
n�mero de processos, DONE
tamanho de fatia de tempo, DONE 
para cada processo:, DONE
	tempo de chegada, 
	tempo de execu��o, 
	prioridade (1 at� 9 - prioridade 1 � a melhor) e 
	tempos de acesso a opera��es de E/S (tempo correspondente a sua execu��o).

Imprimir os tempos m�dios de resposta e espera para o algoritmo Round Robin com prioridade.

Al�m disto imprimir um gr�fico (texto) mostrando como os processo foram executados.
 
Considerar uma unidade de tempo para troca de contexto (representado abaixo como C). 
Tempo come�a em 1. - DONE
Processos iniciam com 1. 
Processo chega no tempo x e pode come�ar a executar (respeitando o algoritmo de escalonamento) no tempo x+2 (1 unidade para troca de contexto).
Tempo que leva para fazer uma opera��o de entrada e sa�da: use valor constante igual a 4. 

Exemplo de arquivo de entrada:

5
3
3 10 2
5 12 1
9 15 2
11 15 1
12 8 5 2

Exemplo de gr�fico a ser exibido para o exemplo acima:
1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18
- - - C 1 C 2 2 2  C  2  2  2  C  4  4  4  C 2 2 2 C 4 4 4 C 2 2 2 C 4 4 4 C 4 4 4 C 4 4 4 C 1 1 C 3 3 3 C111C333C111C333C1C333C333C55C---C5C555C55

Entregar o c�digo fonte e um arquivo com a descri��o do trabalho em no m�ximo 4 p�ginas. Utilizar um formato de artigo cient�fico, da ACM, IEEE ou SBC.
 */


public class app {
	
	private static RoundRobin rr;
	private static ArrayList<Job> jobs;
	private static CPU cpu;
	private static int numJobs;
	private static int time;
				
	public static void main(String[ ] args) {
		
		//Leitura da time slice e dos jobs a partir do arquivo
		read();		
		System.out.println("Li " + jobs.size() + " jobs");		
		
		//inicia a simula��o
		simulate();
	
	}
		
	private static void simulate() {
		
		time = 1;
		
		cpu = new CPU();
		
		//Executa enquanto o n�mero de processos na fila de conclu�dos for menor do que o de processos lidos do arquivo
		
		while(!rr.JobsDone(numJobs)) {
			
			//Verifica se algum processo chegou no tempo atual
			checkJobsArrived();
			
			//Verifica se algum processo est� usando a CPU
			if(!getCpuStatus()) {							
				
				//CPU ocupada. Verifica se o processo que est� usando a CPU vai ser preemptado por outro de melhor prioridade
				if(checkPreemption()) {
					//Caso haja preemp��o, muda o processo que est� usando a CPU	
					removeJob(JobStatus.READY);
					getNewJob();
				}
				
				//J� que n�o houve preemp��o, verifica se o processo atual no processador terminou sua fatia de tempo
				else if(!rr.checkHasTimeLeft()) {
						//Retira o processo da CPU caso a fatia de tempo tenha se esgotado
						removeJob(JobStatus.READY);
						getNewJob();
				}
				
					//Processo n�o foi preemptado e ainda tem tem tempo na CPU. Verifica se ele vai fazer opera��o de IO
					else if(cpu.getJob().checkIO(time)) {
							removeJob(JobStatus.BLOCKED);
							getNewJob();						
					}
						//Processo "sobreviveu" na CPU (n�o foi preemptado, ainda tem tem tempo e n�o fez opera��o de IO).
						//Vai executar por mais uma unidade de tempo
						else {
							runJob();
							checkJobDone();
						}
			}
			
			//CPU livre. Tenta obter um novo processo pronto para executar
			else getNewJob();
			
			time++;
		}
		
	}

	private static void checkJobDone() {
		Job currentJob = cpu.getJob();
		if(currentJob.getReceivedTime() == currentJob.getRunTime())
			removeJob(JobStatus.DONE);		
	}
	
	private static void removeJob(JobStatus status) {
		Job jobRemoved = cpu.removeJob();
		jobRemoved.setStatus(status);
		rr.receiveJob(jobRemoved);		
	}

	private static void checkJobsArrived() {
			
		for(int i = 0; i < jobs.size(); i++) {
		
			if(jobs.get(i).getRunTime() == time)	
				//Retira o job a executar da lista de jobs lidos do arquivo e o envia para o escalonador
				rr.receiveJob(jobs.remove(i));
		}		
	}	
	
	private static boolean getCpuStatus() {
		if(cpu.getJob() == null) {
			//CPU sem processo
			System.out.println(" - ");
			return false;
		}
		return true;
	}

	private static boolean checkPreemption() {
		Job currentJob = cpu.getJob();
		int currentPriority = currentJob.getPriority();
		boolean preempt = false;
		
		//Processo com prioridade 1 n�o pode ser preemptado
		if(currentPriority == 1)
			return false;
		
		//Verifica se as filas de prioridades menores do que a do processo atual cont�m um job pronto para ser executado
		for(int i = 1; i < currentPriority && !preempt; i++)
			preempt = rr.checkReadyQueue(i);
			
		return preempt;	
	}
		
	private static void getNewJob() {
		cpu.setNewJob(rr.pickNextJob());		
	}
	
	private static void runJob() {
		cpu.runJob();
		rr.decrementTimeLeft();		
	}
		
	private static void read() {
		
		ArrayList<Job> jobs = new ArrayList<Job>();		
		File file = new File("jobs.txt");
		
		try {
			Scanner scan = new Scanner(file);
			
			ArrayList<String> jobsRead = new ArrayList<String>();
			
			numJobs = scan.nextInt();
			
			//Instancia Round Robin, informando o valor da fatia de tempo
			rr = new RoundRobin(scan.nextInt());
			
			scan.nextLine();
			
			//L� os dados dos processos
			for(int i = 0; i < numJobs; i++) {
				String line = scan.nextLine();				
				jobsRead.add(line);
				System.out.println("Leitura: " + line );
			}			
			
			scan.close();
			
			//Aciona o m�todo est�tico que vai instanciar os jobs e popular o ArrayList que os armazena
			populate(jobsRead);			
			
		}		
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		
	}
	
	//Instancia os jobs conforme os dados lidos do arquivo
	private static void populate(ArrayList<String> jobsRead) {
		
		Scanner scan;
		Job job;
		int arrivalTime, runTime, priority;
		ArrayList<Integer> IO;
		jobs = new ArrayList<Job>();
		
		for(String j : jobsRead) {
			scan = new Scanner(j);
			arrivalTime = scan.nextInt(); 
			runTime = scan.nextInt();
			priority = scan.nextInt();
			IO = new ArrayList<Integer>();
			
			while(scan.hasNextInt()) {
				IO.add(scan.nextInt());
			}
			
			jobs.add(new Job(arrivalTime, runTime, priority, IO));
		}
		
	}	
}