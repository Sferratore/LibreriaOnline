

import java.util.ArrayList;

public class Utente {
	
	public void acquistaLibro(Libro l) {
		this.libriAcquistati.add(l);
	}
	
	public void lasciaRecensione(Recensione r) {
		
	}
	
	public Utente() {
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public ArrayList<Libro> getLibriAcquistati() {
		return libriAcquistati;
	}
	public void setLibriAcquistati(ArrayList<Libro> libriAcquistati) {
		this.libriAcquistati = libriAcquistati;
	}
	private int id;
	private String nome;
	private String email;
	private ArrayList<Libro> libriAcquistati;
}
