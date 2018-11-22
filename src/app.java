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
