import excepciones.CupoExcedidoException;
import modelo.Estudiante;
import modelo.EventoUniversitario;
import modelo.Sala;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class AppAlternativo {
    public static void main(String[] args) {

        List<Estudiante> estudiantes = new ArrayList<>();

        estudiantes.add(new Estudiante("52342", "Luis Gómes"));
        estudiantes.add(new Estudiante("50243", "Victor Fuentes"));
        estudiantes.add(new Estudiante("51878", "María Diaz"));

        EventoUniversitario evento = new EventoUniversitario("001", "Encuentro de POO",2000,true);

        Sala sala = new Sala(1, "Aula Magna");

        evento.asignarSala(sala);

        evento.crearActividad(1, "modelo.actividades.Taller de POO",2,"taller" );
        evento.crearActividad(2, "modelo.actividades.Charla de PL",30,"charla" );

        try {
            evento.getActividades().get(0).inscribir(estudiantes.get(0));
            evento.getActividades().get(0).inscribir(estudiantes.get(1));
            evento.getActividades().get(0).inscribir(estudiantes.get(2));

            evento.getActividades().get(1).inscribir(estudiantes.get(1));
            evento.getActividades().get(1).inscribir(estudiantes.get(2));
        } catch (CupoExcedidoException e) {
            System.out.println("Error al inscribir: " + e.getMessage());
        }

        evento.mostrarDatos();

        try { //bloque de intento para persistir el evento.
            System.out.println("*** Almacenando el evento Id° " + evento.getId()  + " ***");
            evento.persistirEvento();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Imposible guardar el evento id°  " +  evento.getId() +". Error al guardar el archivo: "+ e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println("Imposible guardar el evento id°  " + evento.getId()  + ".");
            e.printStackTrace();
        }

        try {
            EventoUniversitario copiaDesdeArchivo = evento.recuperarEvento(evento.getId());
            System.out.println("*** Recuperando y mostrando el evento Id° " + evento.getId()  + " almacenado previamente. ***");
            copiaDesdeArchivo.mostrarDatos();
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("No fue posible reconstruir el objeto almacenado: " + e.getMessage());
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Imposible recuperar el evento id°  " + evento.getId() +". Error al guardar el archivo: "+ e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println("Imposible recuperar el evento id°  " + evento.getId() + ".");
            e.printStackTrace();
        }
    }
}

