package t2SOP;

public class LinkedList {
	private Bloco head;
	private Bloco tail;
	
	public LinkedList() {
		this.head = null;
		this.tail = null;
	}
	
	public LinkedList(int inicio, int fim) {
		this.head = new Bloco(inicio, fim);		
	}
	
	public Bloco getBlocoByID(int ID) {
		Bloco aux = head;
		
		while(aux != null) {
			if(aux.getID() == ID) 
				return aux;
			else aux = aux.getNext();
		}
		
		return null;
	}
	
	

	
	
	
}
