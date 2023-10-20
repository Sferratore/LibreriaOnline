import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibreriaOnline {

	// -------------------CONSTRUCTORS--------------------

	public LibreriaOnline() {
		
		this.listaLibri = new ArrayList<Libro>();
		this.listaUtenti = new ArrayList<Utente>();
		this.listaRecensioni = new ArrayList<Recensione>();
		
		this.connectToDb();
		try {
			this.sync();
		}
		catch(Exception e) {
			System.out.println("Qualcosa è andato storto: " + e.toString());
		}
		
	}

	// -------------------METHODS-------------------------
	
	public void aggiungiUtente(Utente u) {
		this.listaUtenti.add(u);
	}
	
	public void aggiungiLibro(Libro l) {
		this.listaLibri.add(l);
	}
	
	public void aggiungiRecensione(Recensione r) {
		this.listaRecensioni.add(r);
	}

	public void mostraLibri() {
		for(Libro l : this.listaLibri) {
			System.out.println(l);
		}
			
	}
	
	public void mostraUtenti() {
		for(Utente u : this.listaUtenti) {
			System.out.println(u);
		}
			
	}
	
	public void LibriConsigliati(Utente u) {
		
		PreparedStatement prpSt;
		ResultSet rs;
		
		try {
			prpSt = this.connection.prepareStatement("CALL LibriConsigliati(?)");
			prpSt.setInt(1, u.getId());
			rs = prpSt.executeQuery();
			
			System.out.println("LIBRI CONSIGLIATI: \n");
			while(rs.next()) {
				Libro l = new Libro();
				l.setId(rs.getInt("id"));
				l.setTitolo(rs.getString("titolo"));
				l.setAutore(rs.getString("autore"));
				l.setGenere(rs.getString("genere"));
				l.setPrezzo ((float) rs.getDouble("prezzo"));
				System.out.println(l);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private void sync() throws Exception {

		ArrayList<Utente> utentiDaAggiungere = new ArrayList<Utente>();
		ArrayList<Libro> libriDaAggiungere = new ArrayList<Libro>();
		ArrayList<Recensione> recensioniDaAggiungere = new ArrayList<Recensione>();

		// Utilities
		PreparedStatement prpSt;
		ResultSet rs;

		// Connection check
		if (this.connection == null) {
			throw new Exception("You are not connected to any Db!");
		}

		try {
			// Aggiungi Libri
			prpSt = this.connection.prepareStatement("SELECT * FROM Libro");
			rs = prpSt.executeQuery();

			while (rs.next()) {
				Libro l = new Libro();
				l.setId(rs.getInt("id"));
				l.setTitolo(rs.getString("titolo"));
				l.setAutore(rs.getString("autore"));
				l.setGenere(rs.getString("genere"));
				l.setPrezzo ((float) rs.getDouble("prezzo"));
				libriDaAggiungere.add(l);
			}

			// Aggiungi recensioni
			prpSt = this.connection.prepareStatement("SELECT * FROM Recensione");
			rs = prpSt.executeQuery();
			while (rs.next()) {
				Recensione r = new Recensione();
				r.setUtente(rs.getString("utente"));
				r.setLibro(rs.getString("libro"));
				r.setValutazione(rs.getInt("valutazione"));
				r.setCommento(rs.getString("commento"));
				recensioniDaAggiungere.add(r);
			}

			// Aggiunta Utenti
			prpSt = this.connection.prepareStatement("SELECT * FROM Utente");
			rs = prpSt.executeQuery();

			while (rs.next()) {
				
				Utente u = new Utente();
				ResultSet libriDiUtenteRs;
				ArrayList<Libro> libriDiUtente = new ArrayList<Libro>();
				
				u.setId(rs.getInt("id"));
				u.setNome(rs.getString("nome"));
				u.setEmail(rs.getString("email"));
				
				//Impostare libri acquistati
				prpSt= this.connection.prepareStatement("SELECT * FROM Libro WHERE id in (SELECT libro FROM Acquisti WHERE utente = " + u.getId() + ")");
				libriDiUtenteRs = prpSt.executeQuery();
				
				while(libriDiUtenteRs.next()) {
					Libro l = new Libro();
					l.setId(libriDiUtenteRs.getInt("id"));
					l.setTitolo(libriDiUtenteRs.getString("titolo"));
					l.setAutore(libriDiUtenteRs.getString("autore"));
					l.setGenere(libriDiUtenteRs.getString("genere"));
					l.setPrezzo ((float) libriDiUtenteRs.getDouble("prezzo"));
					libriDiUtente.add(l);
				}
				u.setLibriAcquistati(libriDiUtente);
				utentiDaAggiungere.add(u);
				
			}
			
			this.listaLibri = libriDaAggiungere;
			this.listaRecensioni = recensioniDaAggiungere;
			this.listaUtenti = utentiDaAggiungere;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Something went wrong with SQL operations: " + e.toString());
		} catch (Exception e) {
			System.out.println("Something went wrong with the operation: " + e.toString());
		}

	}

	public String connectToDb() {
		
		try {
			Class.forName(JConnectionClass);
		} catch (ClassNotFoundException e) {
			return "The class " + JConnectionClass + " has not been found by the program.";
		}

		String connectionString = String.format("jdbc:mysql://localhost:3306/%s", dbName);
		try {
			this.connection = DriverManager.getConnection(connectionString, dbUsername, dbPassword);
		} catch (SQLException e) {
			return "SQLException happened: " + e.toString();
		}

		if (this.connection != null) {
			return "Connection estabilished with database: \"" + dbName + "\"";
		} else {
			return "Connection not estabilished.";
		}

	}

	// -------------------FIELDS--------------------------
	private ArrayList<Utente> listaUtenti;
	private ArrayList<Libro> listaLibri;
	private ArrayList<Recensione> listaRecensioni;
	private Connection connection;

	public static String JConnectionClass = "com.mysql.cj.jdbc.Driver";
	public static String dbUsername = "root";
	public static String dbPassword = "password";
	public static String dbName = "libreriaonline";
}
