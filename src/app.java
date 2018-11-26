/*Descrição do problema
 * Ler a partir de um arquivo de teste dados que indicam:
 * - o tamanho do espaço de endereçamento de uma memória;
 * - uma sequência de requisições, que tanto podem ser de alocação como de liberação de blocos de memória.
 * A partir destes dados, deve ser simulado processamento destas requisições pelo gerente de memória em um sistema que utiliza partições de tamanho variável. 
 * O sistema desenvolvido deve permitir visualizar o estado da memória sempre que necessário, bem como deve indicar eventuais ocorrências de insuficiência de memória e de fragmentação externa.
 */

/* Descrição da solução:
 * A memória é dividida em blocos, os quais são organizados em uma lista duplamente encadeada.
 * A lista é responsável por gerenciar a inclusão, exclusão e junção dos blocos de memória conforme as requisições são processadas.
 * Sempre que necessário, a lista é percorrida para imprimir o estado atual da memória no console.
 */

//@author: Eduardo José Silva, 25/11/2018. 


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
	
public class app {
	private static ArrayList<Request> requests;
	private static MemoryManager manager;	
				
	public static void main(String[ ] args) {
		requests = new ArrayList<Request>();
		//Set path before running!
		File file = new File ("requests.txt");
		
		//File read
		read(file);
		
		//Iterates until all request have been processed
		for(Request r : requests) {
			manager.process(r);
			
		}
		
	}		
		
	private static void read(File file) {
				
		try {
			Scanner scan = new Scanner(file);
			
			int mode = scan.nextInt();
			System.out.println(mode);
			int mi = scan.nextInt();
			System.out.println(mi);
			int mf = scan.nextInt();
			System.out.println(mf);
			
			scan.nextLine();
			
			//Read requests from file
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				String type = line.substring(0, 1);
				int numeral = Integer.parseInt(line.substring(2,line.length()));
				System.out.println("Read request " + type + " " + numeral);
				requests.add(new Request(type,numeral));
			}			
			
			scan.close();
			
			//Instantiates memory manager
			manager = new MemoryManager(mi, mf, requests);			
		}		
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}
		
}
