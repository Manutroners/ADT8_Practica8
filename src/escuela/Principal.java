package escuela;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.matisse.MtDatabase;
import com.matisse.MtException;
import com.matisse.MtObjectIterator;

public class Principal {

	public static void main(String[] args) {

		String hostname = "localhost";
		String dbname = "academia";
		//creaObjetos(hostname, dbname);
		// borrarTodos(hostname, dbname);
		// modificaObjeto(hostname, dbname, "Manuel Jose", "111494830");
		 ejecutaOQL(hostname, dbname);

	}

	public static void creaObjetos(String hostname, String dbname) {
		try {
			// Abre la base de datos con el hostname (localhost), y el nombre de la base de
			// datos dbname (biblioteca).
			MtDatabase db = new MtDatabase(hostname, dbname);
			db.open();
			db.startTransaction();
			System.out.println("Conectado a la base de datos " + db.getName() + " de Matisse");
			// Crea un objeto Autor
			Profesores p1 = new Profesores(db);
			p1.setDni("12345678A");
			p1.setTelefono("611968375");
			p1.setApellidos("Saucedo Ramirez");
			p1.setNombre("Manuel Jose");
			System.out.println("Objeto de tipo Autor creado.");
			// Crea un objeto Libro
			Cursos l1 = new Cursos(db);
			l1.setHoraInicio("9:30");
			l1.setDuracion("1:30 h");
			l1.setAula("Aula 1");
			l1.setNombre("Programacion");
			l1.setFecha("14/1/2024");
			// Crea otro objeto libro
			Asignaturas a1 = new Asignaturas(db);
			a1.setNombre("1ºA");
			a1.setHoraInicio("8:00");
			a1.setDuracion("1:30 h");
			a1.setAula("Aula 1");
			a1.setDiaSemana("Lunes");
			System.out.println("Objetos de tipo Libro creados.");
			// Crea un array de Obras para guardar los libros y hacer las relaciones
			Clases o1[] = new Clases[2];
			o1[0] = l1;
			o1[1] = a1;
			// Guarda las relaciones del autor con los libros que ha escrito.
			p1.setImparten(o1);
			// Ejecuta un commit para materializar las peticiones.
			db.commit();
			// Cierra la base de datos.
			db.close();
			System.out.println("\nHecho.");
		} catch (MtException mte) {
			System.out.println("MtException : " + mte.getMessage());
		}
	}

	// Borrar todos los objetos de una clase
	public static void borrarTodos(String hostname, String dbname) {
		System.out.println("====================== Borrar Todos=====================\n");
		try {
			MtDatabase db = new MtDatabase(hostname, dbname);
			db.open();
			db.startTransaction();
			System.out.println("Conectado a la base de datos " + db.getName() + " de Matisse.");
			/*
			 * El método getInstanceNumber(db) cuenta el número de objetos del tipo de la
			 * clase con la que lo llamemos que en este caso es Obra
			 */
			System.out.println("\n" + Clases.getInstanceNumber(db) + " objetos de tipo Obra tenemos en la DB.");
			// Borra todas las instancias de Obra
			Clases.getClass(db).removeAllInstances();
			// Materializa los cambios y cierra la BD
			db.commit();
			db.close();
			System.out.println("\nTodos los objetos de tipo Obra eliminados correctamente de la base de datos.");
		} catch (MtException mte) {
			System.out.println("MtException : " + mte.getMessage());
		}
	}

	public static void modificaObjeto(String hostname, String dbname, String nombre, String nuevaEdad) {
		System.out.println("=========== Modifica un objeto ==========\n");
		int nAutores = 0;
		try {
			MtDatabase db = new MtDatabase(hostname, dbname);
			db.open();
			db.startTransaction();
			System.out.println("Conectado a la base de datos " + db.getName() + " de Matisse.");
			/*
			 * El método getInstanceNumber(db) cuenta el número de objetos del tipo de la
			 * clase con la que lo llamemos que en este caso es Autor.
			 */
			System.out.println("\n" + Profesores.getInstanceNumber(db) + " objetos de tipo Autor tenemos en la DB.");
			nAutores = (int) Profesores.getInstanceNumber(db);
			// Crea un Iterador (propio de Java)
			MtObjectIterator<Profesores> iter = Profesores.<Profesores>instanceIterator(db);
			System.out.println("\nRecorro el iterador de uno en uno y cambio cuando encuentro 'nombre'");
			while (iter.hasNext()) {
				Profesores[] autores = iter.next(nAutores);
				for (int i = 0; i < autores.length; i++) {
					/*
					 * Si el nombre del Autor coincide con el parámetro nombre pasado al método, le
					 * establecemos el parámetro edad que le pasamos al método.
					 */
					if (autores[i].getNombre().compareTo(nombre) == 0) {
						autores[i].setTelefono(nuevaEdad);
					} else {
						System.out.println("No se ha encontrado ningún Autor de nombre " + nombre
								+ " en la base de datos " + db.getName() + ".");
					}
				}
			}
			iter.close();
			// Materializa los cambios y cierra la BD
			db.commit();
			db.close();
			System.out.println("\nLa modificación del objeto, finalizada correctamrnte.");
		} catch (MtException mte) {
			System.out.println("MtException : " + mte.getMessage());
		}
	}

	public static void ejecutaOQL(String hostname, String dbname) {
		MtDatabase dbcon = new MtDatabase(hostname, dbname);
		// Abre una conexión a la base de datos
		dbcon.open();
		try {
			// Crea una instancia de Statement
			Statement stmt = dbcon.createStatement();
			/*
			 * Asigna una consulta OQL. Esta consulta lo que hace es utilizar REF() para
			 * obtener el objeto directamente. biblioteca2020.Autor es el mapeo a la clase
			 * Autor. Es decir biblioteca2020 es el paquete en el que tenemos la clase
			 * Autor.
			 */
			String commandText = "SELECT REF(a) from escuela.Profesores a;";
			/*
			 * Ejecuta la consulta y obtiene un ResultSet que contendrá las referencias a
			 * los objetos que en este caso serán de tipo Autor.
			 */
			ResultSet rset = stmt.executeQuery(commandText);
			/*
			 * Creamos una referencia a un objeto de tipo Autor donde almacenaremos los
			 * objetos devueltos en el ResultSet.
			 */
			Profesores a1;
			// Recorremos el ResultSet.
			while (rset.next()) {
				/*
				 * Con el método getObject() recuperamos cada objeto del ResultSet y lo
				 * almacenamos en a1. El casteo es necesario porque el método getObject devuelve
				 * un tipo Object.
				 */
				a1 = (Profesores) rset.getObject(1);
				/*
				 * Una vez el objeto es referenciado en a1, ya se pueden recuperar de él los
				 * valores de sus atributos.
				 */
				System.out.println("Los valores de los atributos del objeto de tipo Autor son: " + a1.getNombre() + " "
				 + a1.getApellidos() + " " + a1.getDni()+ " " + a1.getTelefono() + ".");
			}
			/*
			 * Cierra las conexiones. Solamente debemos cerrar el ResultSet y el Statement,
			 * no el MtDatabase porque lanza una excepción de que no conoce la fuente.
			 */
			rset.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}
	}

}
