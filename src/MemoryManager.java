import java.util.ArrayList;

public class MemoryManager {
	
	private Memory mem;
	private ArrayList<Request> pending;
	private ArrayList<Request> queued;	
	
	public MemoryManager(int begin, int end, ArrayList<Request> requests) {
		mem = new Memory(begin, end);
		System.out.println("Tamanho da mem: " + mem.getTotalSize());
		System.out.println("Espa�o livre: " + mem.getFreeSpace());
		pending = requests;
		queued = new ArrayList<Request>();
	}
	
	public int process(Request req) {		
		
		System.out.println("Processando " + req);
			
		if(req.getType())
			return allocate(req, queued);			
		else 
			return release(req);		
	}

	private int release(Request req) {
		
		Block releasedBlock = mem.getBlockByID(req.getNumeral());
		releasedBlock.setFree(true);
		System.out.println("Liberei " + releasedBlock);
		mem.setFreeSpace(-releasedBlock.getSize());
		Block father = releasedBlock.getFather();
		Block next = releasedBlock.getNext();
		
		//Check if the block being released has a father
		if(father != null) {
			System.out.println("Bloco " + releasedBlock.getID() + " tem pai (Bloco " + father.getID() + ")");
			Block grandpa = father.getFather();
			
			//If father exists and is also free, join the two blocks
			if(father.isFree()){
				System.out.println("Pai do bloco " + releasedBlock.getID() + " est� livre");
				releasedBlock.setBegin(father.getBegin());
				releasedBlock.setFather(grandpa);
				releasedBlock.setSize();
				System.out.println("Grudados " + father.getID() + " e " + releasedBlock.getID() + "!" + releasedBlock);
			
				//If father was free && had a father of it's own (grandpa), redirects it's next reference to the new block
				if(grandpa != null) {
					System.out.println("Novo av� - " + grandpa.getID());				
					grandpa.setNext(releasedBlock);								
				}
			}
			
			else System.out.println("Pai ocupado, imposs�vel grudar");
		}
		
		
		
		//Check if the block being released has a next one (child)
		if(next != null) {
			Block grandchild = next.getNext();
			
			//If a next block exists and is also free, join the two blocks
			if(next.isFree()){
				releasedBlock.setEnd(next.getEnd());
				releasedBlock.setNext(grandchild);
			
				//If the next block was free && had a next of it's own (grandchild), redirects it's father reference to the new block
				if(grandchild != null)
					grandchild.setFather(releasedBlock); //"next.getNext(), I am your father."			
			}
		}
		
		//Check if after the release there is any blocks waiting on the queue that can now be allocated
		checkQueued();
		
		//returns released block ID
		return releasedBlock.getID();
	}

	private void checkQueued() {
		
		ArrayList<Request> nonReallocated = new ArrayList<Request>();
		
		for(Request r : queued) {
			allocate(r, nonReallocated);
		}
		
		queued = nonReallocated;		
	}

	private int allocate(Request req, ArrayList<Request> waitingQueue) {
		
		int reqSize = req.getNumeral();		
		
		//Check if mem has enough free space to answer request.		
		if (memHasFreeSpace(reqSize)){
			System.out.println("Mem�ria comporta requisi��o - " + mem.getFreeSpace() + " livres x " + reqSize + " requisitados");
			//gets the ID of the first free block that is big enough to fit the new one, if there's any 
			int spotID = getSpot(reqSize); 
			System.out.println("Alocar em " + spotID);
			
			if(spotID != -1) {
				//Spot found sucessfully. 
				//Calls mem method to insert new block and, on return, get it's ID
				int ret = mem.insert(spotID, req); 
				System.out.println("Novo " + mem.getBlockByID(ret));
				return ret;
			}
			
			//Total free memory space is bigger then request size, but no free block alone is big enough to receive it. External fragmentation occurs. 
			else 
				fragmentation(req.getNumeral());			
		}		
		
		//If execution got here, the request could not be attended. Default return is -1 in this case. 
		//Request gets in queue to wait for a memory release to happen and then try again being allocated.		
		waitingQueue.add(req);		
		return -1;
	}

	private void fragmentation(int reqSize) {
		
		Block aux = mem.getFirstBlock();
		
		while(aux != null) {			
			System.out.println(aux);
			aux = aux.getNext();
		}
		
		System.out.println(mem.getFreeSpace() + " livre(s), " + reqSize + " solicitado(s) - fragmenta��o externa.");
		
	}

	private int getSpot(int reqSize) {
		
		Block auxBlock = mem.getFirstBlock();
		
		while(auxBlock != null) {	
			if(auxBlock.isFree() && auxBlock.getSize() >= reqSize) {
				return auxBlock.getID();
			}
			else 
				auxBlock = auxBlock.getNext();
		}
		
		return -1;
	}

	private boolean memHasFreeSpace(int blockSize) {
		return mem.getFreeSpace() >= blockSize;
	}

}
