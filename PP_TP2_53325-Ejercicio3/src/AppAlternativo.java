import excepciones.CupoExcedidoException;
import modelo.Estudiante;
import modelo.EventoUniversitario;
import modelo.Inscripcion;
import modelo.Sala;
import modelo.actividades.Actividad;
import modelo.actividades.Charla;
import modelo.actividades.Curso;
import modelo.actividades.Taller;
import modelo.certificacion.Certificable;

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

        evento.crearActividad(1, "modelo.actividades.Taller de POO",3,"taller" );
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

        try {
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

        for (Actividad actividad: evento.getActividades()){
            if (actividad instanceof Certificable certificable){
                System.out.println("CERTIFICADOS EMITIDOS PARA LA ACTIVIDAD " + actividad.getTitulo());
                for (Inscripcion inscripcion: actividad.getInscripciones()){
                    String certificado = certificable.generarCertificado(inscripcion.getEstudiante());
                    System.out.println(certificado);
                }
            }
        }

        System.out.println("\n\n************  Filtrando la lista de actividades **********");
        List<Taller> talleres = evento.filtrarActividadesPorTipo(Taller.class);
        List<Curso> cursos = evento.filtrarActividadesPorTipo(Curso.class);
        List<Charla> charlas = evento.filtrarActividadesPorTipo(Charla.class);


        System.out.println("Actividades filtradas por tipo usando método parametrizado acotado:");
        System.out.println("Charlas encontradas: " + charlas.size());
        System.out.println("Talleres encontrados: " + talleres.size());
        System.out.println("Cursos encontrados: " + cursos.size());

        System.out.println("\n************ Calculando costos de las listas filtradas **********");

        // Uso de wildcard acotado: el mismo método puede recibir List<Taller>, List<Curso>, etc.
        System.out.println("Costo de materiales de talleres: $" + evento.calcularCostoMateriales(talleres));
        System.out.println("Costo de materiales de cursos: $" + evento.calcularCostoMateriales(cursos));
        System.out.println("Costo de materiales de todas las actividades: $" + evento.calcularCostoMateriales(evento.getActividades()));

    }


}

