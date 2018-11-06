package t2SOP;
public class Bloco {
	private int ID;
	private int inicio;
	private int fim;
	private Bloco father;
	private Bloco next;
	
	public Bloco (int inicio, int fim) {
		ID = 0;
		this.inicio = inicio;
		this.fim = fim;
		this.father = null; 
		this.next = null;		
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getInicio() {
		return inicio;
	}

	public void setInicio(int inicio) {
		this.inicio = inicio;
	}

	public int getFim() {
		return fim;
	}

	public void setFim(int fim) {
		this.fim = fim;
	}

	public Bloco getFather() {
		return father;
	}

	public void setFather(Bloco father) {
		this.father = father;
	}

	public Bloco getNext() {
		return next;
	}

	public void setNext(Bloco next) {
		this.next = next;
	}	
	

}
