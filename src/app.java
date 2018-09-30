import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 Definição:
Ler de um arquivo as seguintes informações, nesta ordem: 
número de processos, DONE
tamanho de fatia de tempo, DONE 
para cada processo:, DONE
	tempo de chegada, 
	tempo de execução, 
	prioridade (1 até 9 - prioridade 1 é a melhor) e 
	tempos de acesso a operações de E/S (tempo correspondente a sua execução).

Imprimir os tempos médios de resposta e espera para o algoritmo Round Robin com prioridade.

Além disto imprimir um gráfico (texto) mostrando como os processo foram executados.
 
Considerar uma unidade de tempo para troca de contexto (representado abaixo como C). 
Tempo começa em 1. - DONE
Processos iniciam com 1. 
Processo chega no tempo x e pode começar a executar (respeitando o algoritmo de escalonamento) no tempo x+2 (1 unidade para troca de contexto).
Tempo que leva para fazer uma operação de entrada e saída: use valor constante igual a 4. 

Exemplo de arquivo de entrada:

5
3
3 10 2
5 12 1
9 15 2
11 15 1
12 8 5 2

Exemplo de gráfico a ser exibido para o exemplo acima:
1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18
- - - C 1 C 2 2 2  C  2  2  2  C  4  4  4  C 2 2 2 C 4 4 4 C 2 2 2 C 4 4 4 C 4 4 4 C 4 4 4 C 1 1 C 3 3 3 C111C333C111C333C1C333C333C55C---C5C555C55

Entregar o código fonte e um arquivo com a descrição do trabalho em no máximo 4 páginas. Utilizar um formato de artigo científico, da ACM, IEEE ou SBC.
 */


public class app {
	
	private static CPU cpu;
	private static RoundRobin rr;
	private static ArrayList<Job> jobs;
	private static int numJobs;
	private static int timeSlice;	
	private static int time;
	private static StringBuilder timeLine;
	private static StringBuilder CPULog;
	private static boolean running;
				
	public static void main(String[ ] args) {
		
		//Leitura do arquivo
		read();		
		
		//Gerencia a simulação
		simulate();
	
	}
		
	private static void simulate() {
		
		cpu = new CPU();
		rr = new RoundRobin(jobs, timeSlice);
		time = 1;
		timeLine = new StringBuilder();
		CPULog = new StringBuilder();
		running = true;
		
		while(running) {
			running = rr.run(cpu, jobs, time);
			print();
			time++;
		}
		
		//calculate();
		
	}

	private static void print() {
		timeLine.append(time);
		CPULog.append(cpu.getStatus());
		System.out.println(timeLine);
		System.out.println(CPULog);
		
		/*System.out.println(rr);
		for(int i = 1; i< 10; i++) {
			ArrayList<Job> aux = rr.getReadyJobs(i);
			for(Job j : aux)
				System.out.println(j);				
		}
		*/
	}

	private static void read() {
		
		File file = new File("jobs.txt");
		
		try {
			Scanner scan = new Scanner(file);
			
			numJobs = scan.nextInt();			
			timeSlice = scan.nextInt();
			
			scan.nextLine();
			
			
			//Lê os dados dos processos
			ArrayList<String> jobsRead = new ArrayList<String>();
			
			for(int i = 0; i < numJobs; i++) {
				String line = scan.nextLine();				
				jobsRead.add(line);
				System.out.println("Leitura: " + line );
			}			
			
			scan.close();
			
			//Aciona o método estático que vai instanciar os jobs e popular o ArrayList que os armazena
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
			
			job = new Job(arrivalTime, runTime, priority, IO);
			jobs.add(job);
		}
		
	}	
}