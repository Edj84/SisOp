import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 Defini��o:
Ler de um arquivo as seguintes informa��es, nesta ordem: 
n�mero de processos, 
tamanho de fatia de tempo, 
para cada processo:, 
	tempo de chegada, 
	tempo de execu��o, 
	prioridade (1 at� 9 - prioridade 1 � a melhor) e 
	tempos de acesso a opera��es de E/S (tempo correspondente a sua execu��o).

Imprimir os tempos m�dios de resposta e espera para o algoritmo Round Robin com prioridade.

Al�m disto imprimir um gr�fico (texto) mostrando como os processo foram executados. 
Considerar uma unidade de tempo para troca de contexto (representado abaixo como C). 
Tempo come�a em 1. 
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
1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 
-  -  -  C 1C 2 2 2 C   2    2    2   C  4  44C222C444C222C444C444C444C11C333C111C333C111C333C1C333C333C55C---C5C555C55

Entregar o c�digo fonte e um arquivo com a descri��o do trabalho em no m�ximo 4 p�ginas. Utilizar um formato de artigo cient�fico, da ACM, IEEE ou SBC.
 */


public class app {
	
	
			
	public static void main(String[ ] args) {
		
		ArrayList<Job> jobs = read();
		
		System.out.println(jobs.size());
		
		for(Job j : jobs)
			System.out.println(j);
		
		
		
	}
				
	/* Ordem da opera��es
	 
	 - verificar se CPU tem processo (m�todo: CPU.getJob() );
	  				!null -> 
	  							CPU: verificar job status 
	  										RUNNING -> verificar se job far� IO (m�todo: CPU.job.checkIO() )
	  											t -> mudar job status p/ BLOCKED (m�todo: CPU.changeStatus(BLOCKED) )
	  											f -> verificar job.receivedTime (m�todo: CPU.job.getReceivedTime () )
	  						 							maior do que timeSlice -> mudar jobStatus (m�todo: CPU.changeStatus(DONE) )
	  						 		 					menor do que timeSlice -> job.receivedTime++ (m�todo: CPU.job.incrementReceivedTime() )
	  						 		 		BLOCKED -> adicionar processo em rr.blocked				  						
	  				null ->  selecionar novo processo at� que done.size() == numJobs
	
	*/
	
	private static ArrayList<Job> read() {
		
		ArrayList<Job> jobs = new ArrayList<Job>();		
		File file = new File("jobs.txt");
		
		try {
			Scanner scan = new Scanner(file);
			
			ArrayList<String> jobsRead = new ArrayList<String>();
			
			int numJobs = scan.nextInt();
			System.out.println(numJobs + " processos");
			int timeSlice = scan.nextInt();
			System.out.println("timeSlice " + timeSlice);
			scan.nextLine();
			
			for(int i = 0; i < numJobs; i++) {
				String line = scan.nextLine();				
				jobsRead.add(line);
				System.out.println("Leitura: " + line );
			}			
			
			scan.close();
			
			jobs = populate(jobsRead);			
			
		}		
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return jobs;		
		
	}

	private static ArrayList<Job> populate(ArrayList<String> jobsRead) {
		
		ArrayList<Job> jobs = new ArrayList<Job>();
		
		Scanner scan;
		Job job;
		int arrivalTime, runTime, priority;
		ArrayList<Integer> IO;
		
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
		
		return jobs;		
	}	
}