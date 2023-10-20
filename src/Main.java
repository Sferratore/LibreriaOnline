
public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LibreriaOnline o = new LibreriaOnline();
		Libro l = new Libro();
		Utente u = new Utente();
		
		l.setAutore("aa");
		l.setGenere("genere");
		l.setId(509);
		l.setPrezzo(22);
		l.setTitolo("Poggibonzi");
		
		o.aggiungiLibro(l);
		
		o.mostraUtenti();
		
		u.setId(1);
		
		o.LibriConsigliati(u);
		
		o.LibriPopolari();
	}

}
