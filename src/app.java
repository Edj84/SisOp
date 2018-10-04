import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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
---C1C222C222C444C222C444C222C444C444C444C11C333C111C333C111C333C1C333C333C55C---C5C555C55
Entregar o código fonte e um arquivo com a descrição do trabalho em no máximo 4 páginas. Utilizar um formato de artigo científico, da ACM, IEEE ou SBC.
 */


public class app {
	
	private static ArrayList<CPU> cpus;
	private static RoundRobin rr;
	private static ArrayList<Job> jobs;
	private static int numJobs;
	private static int timeSlice;	
	private static int time;
	private static StringBuilder timeLine;	
	private static boolean running;
				
	public static void main(String[ ] args) {
		
		//Configurar path na hora de apresentar!
		File file = new File ("C:/Users/Maica/git/SisOp/testFiles/trab-so1-teste2 SR.txt");
		
		//Leitura do arquivo
		read(file);
		
		//Gerencia a simulação
		int numCPUs = getNumCPUs(); 
		simulate(numCPUs);
		System.out.println(rr.calculate());			
	}
	
	//Pede ao usuário para informar o número de CPUs da simulação
	private static int getNumCPUs() {
		int numCPUs = 1;
		Scanner input = new Scanner(System.in);
		System.out.println("Selecione a quantidade de de processadores: ");
		System.out.println("1 - Um processador \n2 - Dois processadores \n4 - Quatro processadores");
		
		try {
			numCPUs = input.nextInt();
						
			while(numCPUs != 1 && numCPUs != 2 && numCPUs != 4) {
				System.out.println("Seleção inválida.");
				System.out.println("Selecione a quantidade de de processadores: ");
				System.out.println("1 - Um processador \n2 - Dois processadores \n4 - Quatro processadores");
				numCPUs = input.nextInt();
			}
			System.out.println("Simulação com " + numCPUs + " CPUs");			
		}		
		catch(Exception e) {
			System.out.println("Formato inválido");
			getNumCPUs();			
		}
		
		finally {
			input.close();
		}
		
		return numCPUs;
	}

	private static void simulate(int numCPUs) {
		
		cpus = new ArrayList<CPU>();
		for(int i = 0; i < numCPUs; i++)
			cpus.add(new CPU());
		
		rr = new RoundRobin(jobs, timeSlice);
		time = 1;
		timeLine = new StringBuilder();		
		running = true;
		
		while(running) {
			running = rr.run(cpus, jobs, time);
			timeLine.append(time);
			time++;
		}
		
		print();				
	}
	
	private static void print() {
						
		for(int i = 0; i < cpus.size(); i++) {
			System.out.println(cpus.get(i).getLog().length());
			System.out.println(timeLine.toString());
			System.out.println(cpus.get(i).getLog());
		}
	}

	private static void read(File file) {
				
		try {
			Scanner scan = new Scanner(file);
			
			numJobs = scan.nextInt();			
			timeSlice = scan.nextInt();
			System.out.println("Fatia de tempo: " + timeSlice);
			scan.nextLine();
			
			//Lê os dados dos processos
			ArrayList<String> jobsRead = new ArrayList<String>();
			
			for(int i = 0; i < numJobs; i++) {
				String line = scan.nextLine();				
				jobsRead.add(line);
				System.out.println("Job " + (i+1) + ": " + line );
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
		
		for(int i = 0; i < jobsRead.size(); i++) {
			scan = new Scanner(jobsRead.get(i));
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