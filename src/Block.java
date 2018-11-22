
public class Block {
	private static int count = 0;
	private int ID;
	private int begin;
	private int end;
	private int size;
	private boolean isFree;
	private Block father;
	private Block next;
	
	//Constructor for the first block.
	//ID is set to 0 by default.
	public Block (int begin, int end) {
		ID = count;
		this.begin = begin;
		this.end = end;
		size = this.end - begin;
		this.isFree = true;
		father = null;
		next = null;
	}
	
	//Constructor for the remaining blocks.
	//ID starts in 1 and is incremented according to instantiation order.  
	public Block (int size, Block spot) {
		count++;
		ID = count;
		this.end = spot.getEnd();
		this.begin = this.end - size;
		this.size = size;
		this.isFree = false;
		this.father = spot;
		this.next = spot.next;		
	}	
	
	//Getters & setters
	
	public int getSize() {
		return size;
	}
	
	public void setSize() {
		size = end - begin;
	}
	
	public Block getFather() {
		return father;
	}

	public void setFather(Block father) {
		this.father = father;
	}

	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public int getID() {
		return ID;
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

	public Block getNext() {
		return next;
	}

	public void setNext(Block next) {
		this.next = next;
	}
	
	@Override
	public String toString(){
		return "Bloco " + ID + ", tamanho " + size + " inicio " + begin + " fim " + end + " status " + isFree; 
	}
}
