

public class LinkedList {
	private Block head;
	private Block tail;
	
	public LinkedList(int begin, int end) {
		this.head = new Block(begin, end);
		head.setFather(null);
		head.setNext(null);
		this.tail = null;
	}
	
	//Iterates across the list searching for matching ID block
	public Block getBlockByID(int ID) {
		
		Block aux = head;
		
		while(aux != null) {
			
			if(aux.getID() == ID) 
				return aux;
			
			else aux = aux.getNext();
		}
		
		return null;
	}
	
	//Getters & setters
	
	public Block getHead() {
		return head;
	}

	public void setHead(Block head) {
		this.head = head;
	}

	public Block getTail() {
		return tail;
	}

	public void setTail(Block tail) {
		this.tail = tail;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		Block aux = head;		
		
		while(aux != null) {			
			sb.append(aux + "\n");
			aux = aux.getNext();
		}
		
		return sb.toString();
	}
}
