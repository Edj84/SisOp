package t2SOP;

import java.util.ArrayList;

public class Memoria {
	private LinkedList blocosLivres;
	private LinkedList blocosOcupados;
	private ArrayList<Bloco> blocosPendentes;
	private int inicio;
	private int fim;
	private int tamanhoTotal;
	private int tamanhoLivre;
	
	
	public Memoria(int inicio, int fim) {
		blocosLivres = new LinkedList(inicio,fim);
		blocosOcupados = new LinkedList();
		blocosPendentes = new ArrayList<Bloco>();
		this.inicio = inicio;
		this.fim = fim;
		tamanhoTotal = this.fim - this.inicio;
		tamanhoLivre = tamanhoTotal;
	}

	public LinkedList getBlocosLivres() {
		return blocosLivres;
	}


	public void setBlocosLivres(LinkedList blocosLivres) {
		this.blocosLivres = blocosLivres;
	}


	public LinkedList getBlocosOcupados() {
		return blocosOcupados;
	}


	public void setBlocosOcupados(LinkedList blocosOcupados) {
		this.blocosOcupados = blocosOcupados;
	}


	public ArrayList<Bloco> getBlocosPendentes() {
		return blocosPendentes;
	}


	public void setBlocosPendentes(ArrayList<Bloco> blocosPendentes) {
		this.blocosPendentes = blocosPendentes;
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


	public int getTamanhoTotal() {
		return tamanhoTotal;
	}


	public void setTamanhoTotal(int tamanhoTotal) {
		this.tamanhoTotal = tamanhoTotal;
	}


	public int getTamanhoLivre() {
		return tamanhoLivre;
	}


	public void setTamanhoLivre(int tamanhoLivre) {
		this.tamanhoLivre = tamanhoLivre;
	}
	
	
	
}
