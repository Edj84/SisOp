import java.util.ArrayList;

public class Memory {
	private LinkedList blocks;
	private int begin;
	private int end;
	private int totalSize;
	private int freeSpace;	
	
	public Memory(int begin, int end) {
		
		blocks = new LinkedList(begin, end);
		this.begin = begin;
		this.end = end;
		totalSize = this.end - this.begin;
		freeSpace = totalSize;
	}
	
	//Getters & setters
	
	public Block getFirstBlock() {
		return blocks.getHead();
	}
	
	public Block getLastBlock() {
		return blocks.getTail();
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(int reqSize) {
		this.freeSpace -= reqSize;
		System.out.println("Mem agora tem " + freeSpace + " livres");
	}
	
	public Block getBlockByID(int ID) {		
		return blocks.getBlockByID(ID);		
	}
	
	public int insert(int spotID, Request req) {
		
		Block spot = blocks.getBlockByID(spotID);		
		Block newBlock = new Block(req.getNumeral(), spot);
		Block spotNext = spot.getNext();
		Block spotFather = spot.getFather();
		
		newBlock.setNext(spotNext);
		
		if(spotNext != null) {			
			spotNext.setFather(newBlock);
		}
		
		//If the two block are the same size, the new one should entirely replace the older in the linked list
		//Creating a new block is needed even in this situation, instead of simply redirecting blocks' references, for the sake of IDs control		
		if(spot.getSize() == newBlock.getSize()) { 
			newBlock.setFather(spotFather);
			
			if(spotFather != null)
				spotFather.setNext(newBlock);
		}
		
		//Father is bigger then the new block, and, thus, it gets split. By default, a new block is always put in the end of the block it was severed from.		
		else {
			spot.setEnd(newBlock.getBegin());
			System.out.print("Bloco " + spot.getID() + " dividido. Tamanho original " + spot.getSize() + " "); 
			spot.setSize();
			System.out.println(spot);			
			spot.setNext(newBlock);
		}
		
		setFreeSpace(newBlock.getSize()); 
		
		return newBlock.getID();
	}
	
	@Override
	public String toString() {
		return blocks.toString();
	}
	
}
