import java.util.ArrayList;

public class MemoryManager {
	
	private Memory mem;
	private ArrayList<Request> pending;
	private ArrayList<Request> queued;	
	
	public MemoryManager(int begin, int end, ArrayList<Request> requests) {
		mem = new Memory(begin, end);
		System.out.println("Tamanho da mem: " + mem.getTotalSize());
		System.out.println("Espaço livre: " + mem.getFreeSpace());
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
			System.out.println("Bloco " + releasedBlock.getID() + " tem pai (" + father.getID() + ")");
			Block grandpa = father.getFather();
			
			//If father exists and is also free, merge the two blocks
			if(father.isFree()){
				System.out.println("Pai do bloco " + releasedBlock.getID() + " está livre");
				releasedBlock.setBegin(father.getBegin());
				releasedBlock.setFather(grandpa);
				releasedBlock.setSize();
				releasedBlock.setNext(next);
				System.out.println("Merged " + father.getID() + " e " + releasedBlock.getID() + "!" + releasedBlock);
				
				//If father was free && had a father of it's own (grandpa), redirects it's next reference to the new block
				if(grandpa != null)
					grandpa.setNext(releasedBlock);			
				
				else
					mem.setHead(releasedBlock);
				
				System.out.println(mem.toString());
			}
			
			else System.out.println("Pai ocupado, impossível fazer merge");
		}		
		
		//Check if the block being released has a next one (child)
		if(next != null) {
			System.out.println("Bloco " + releasedBlock.getID() + " tem filho (" + releasedBlock.getNext() + ")");
			Block grandchild = next.getNext();
			
			//If a next block exists and is also free, join the two blocks
			if(next.isFree()){
				System.out.println("Filho do bloco " + releasedBlock.getID() + " está livre");
				releasedBlock.setEnd(next.getEnd());
				releasedBlock.setNext(grandchild);
			
				//If the next block was free && had a next of it's own (grandchild), redirects it's father reference to the new block
				if(grandchild != null) {
					System.out.println("Merged " + releasedBlock.getID() + " e " + next.getID() + "!" + releasedBlock);
					grandchild.setFather(releasedBlock); //"next.getNext(), I am your father."
				}
				
				System.out.println(mem.toString());
			}
			else
				System.out.println("Filho ocupado, impossível fazer merge");
		}
		
		//Check if after the release there is any blocks waiting on the queue that can now be allocated
		checkQueued();
		
		//returns released block ID
		return releasedBlock.getID();
	}

	private void checkQueued() {
		
		ArrayList<Request> nonReallocated = new ArrayList<Request>();
		
		for(Request r : queued) {
			System.out.println("Tentando atender requisição " + r.getID() + " - em fila de espera");
			if(allocate(r, nonReallocated) == -1)
				System.out.println("Impossível atender requisição " + r.getID() + " no momento");
		}
		
		queued = nonReallocated;		
	}

	private int allocate(Request req, ArrayList<Request> waitingQueue) {
		
		int reqSize = req.getNumeral();		
		
		//Check if mem has enough free space to answer request.		
		if (memHasFreeSpace(reqSize)){
			System.out.println("Memória pode comportar requisição " + req.getID() + " - total de " + mem.getFreeSpace() + " livres Vs " + reqSize + " requisitados.\nProcurando lugar...");
			
			//gets the ID of the first free block that is big enough to fit the new one, if there's any 
			int spotID = getSpot(reqSize); 
			
			if(spotID != -1) {
				//Spot found sucessfully. 
				//Calls mem method to insert new block and, on return, get it's ID
				System.out.println("Encontrei lugar: alocar no bloco " + spotID);
				int ret = mem.insert(spotID, req);				
				System.out.println("Criado novo " + mem.getBlockByID(ret));
				System.out.println(mem);
				return ret;
			}
			
			//Total free memory space is bigger then request size, but no free block alone is big enough to receive it. External fragmentation occurs. 
			else  
				fragmentation(req.getID());			
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
		
		System.out.println("Impossível atender solicitação.");
		System.out.println("Apesar de haver " + mem.getFreeSpace() + " livre(s), nenhum bloco comporta a requisição " + reqSize + ". Fragmentação externa.");		
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
