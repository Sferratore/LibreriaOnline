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
		this.sync();
	}

	// -------------------METHODS-------------------------

	/**
	 * Registers a user inside the database and then resyncs.
	 * 
	 * @param u user to be registered
	 * @return true if the operation is successful, false otherwhise.
	 */
	public boolean registerUser(User u) {

		try (FileWriter file = new FileWriter("users.txt")) {

			// Checking for username duplication
			for (int i = 0; i < this.users.size(); i++) {
				if (this.users.get(i).getUsername().equals(u.getUsername())) {
					throw new Exception("Username already exists");
				}
			}

			// Adding the user
			file.append("Username: " + u.getUsername() + "\n");
			file.append("Password: " + u.getPassword() + "\n");
			file.append("isAdmin: false\n");
			file.append("---\n");

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		this.sync();

		return true;
	}

	/**
	 * Searches for the user inside the database to see if credentials are valid.
	 * 
	 * @param u user to be checked.
	 * @return result of the logIn.
	 */
	public boolean logUserIn(User u) {

		this.sync();
		for (int i = 0; i < this.users.size(); i++) {
			if (this.users.get(i).getUsername().equals(u.getUsername())
					&& this.users.get(i).getPassword().equals(u.getPassword())
					&& this.users.get(i).isAdmin() == u.isAdmin()) {
				return true;
			}
		}
		return false;

	}
	
	private void aggiungiUtente(Utente u) {
		this.listaUtenti.add(u);
	}
	
	private void aggiungiLibro(Libro l) {
		this.listaLibri.add(l);
	}
	
	private void aggiungiRecensione(Recensione r) {
		this.listaLibri.add(r);
	}

	private void sync() {

		List<Utente> utentiDaAggiungere = new ArrayList<Utente>();
		List<Libro> libriDaAggiungere = new ArrayList<Libro>();
		List<Recensione> recensioniDaAggiungere = new ArrayList<Recensione>();

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
				l.setPrezzo (rs.getDouble("prezzo"));
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
				r.setCommento(rs.getString("totalCost"));
				recensioniDaAggiungere.add(r);
			}

			// Aggiunta Utenti
			prpSt = this.connection.prepareStatement("SELECT * FROM product");
			rs = prpSt.executeQuery();

			while (rs.next()) {
				Utente u = new Utente();
				p.setId(rs.getInt("id"));
				p.setNome(rs.getString("nome"));
				p.setEmail(rs.getString("email"));
				
				p.setLibriAcquistati(null); //AGGIUNGERE LOGICA PER LIBRI ACQUISTATI!!!!!!!!!!!!!!
				
				utentiDaAggiungere.add(u);
				
			}

			// Build store object
			this.dbStore = new Store(productsToAdd, clientsToAdd, ordersToAdd);
			return "Operation successful.";

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return "Something went wrong with SQL operations: " + e.toString();
		} catch (Exception e) {
			return "Something went wrong with the operation: " + e.toString();
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
	public static String dbName = "LibreriaOnline";
}
