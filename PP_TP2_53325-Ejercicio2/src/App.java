import excepciones.CupoExcedidoException;
import modelo.Estudiante;
import modelo.EventoUniversitario;
import modelo.Inscripcion;
import modelo.Sala;
import modelo.actividades.Actividad;
import modelo.certificacion.Certificable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar=true;
        int id=1;

        /* Se crean estudiantes } */
        List<Estudiante> estudiantes = new ArrayList<>();

        System.out.println("REGISTRO DE ESTUDIANTES: ");
        System.out.println("======================");

        while (continuar){
            System.out.println("Ingese legajo del estudiante: ");
            String legajo = scanner.nextLine();
            System.out.println("Ingese nombre y apellido del estudiante: ");
            String apenomb = scanner.nextLine();
            estudiantes.add(new Estudiante(legajo, apenomb));
            System.out.println("desea crear otro estudiante  S/N?");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            continuar = (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) ? true : false;
        };

        System.out.println("\n\nREGISTRO DE EVENTOS: ");
        System.out.println("====================");
        continuar=true;
        while(continuar) {
            /* Se requieren datos por consola para construir un evento */
            System.out.println("Ingese un titulo para el evento: ");
            String titulo = scanner.nextLine();
            System.out.println("Ingese el costo base:  ");
            double costoBase = scanner.nextDouble();
            scanner.nextLine(); //limpia el Enter pendiente
            System.out.println("El evento tendra costo para los participantes s/n?");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            boolean esGratuito= false;
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                esGratuito= true;
            }

            EventoUniversitario evento = new EventoUniversitario(
                    "EVT-" + id,
                    titulo,
                    costoBase,
                    esGratuito
            );

            System.out.println("Ingese el nombre de la sala donde se realizará el evento: ");
            String nombreSala= scanner.nextLine();
            Sala sala = new Sala(id, nombreSala);
            evento.asignarSala(sala);

            System.out.println("\n\nREGISTRO DE ACTIVIDADES PARA EL EVENTO " + evento.getTitulo());
            System.out.println("================================================================");
            int idActividad=1;
            while (continuar){
                System.out.println("Ingese el título de la actividad: ");
                String tituloActividad= scanner.nextLine();
                System.out.println("Ingese el cupo máximo de estudiantes admitidos para la actividad: ");
                int cupo= scanner.nextInt();
                scanner.nextLine(); //Se consume la linea.
                System.out.println("La actividad es una Charla, un Taller o un Curso?  (Charla/Taller/Curso)? ");
                String tipo= scanner.nextLine().trim().toLowerCase();
                evento.crearActividad(idActividad, tituloActividad, cupo, tipo);
                System.out.println("Desea crear otra actividad para el  evento " + evento.getTitulo() + " S/N?");
                respuesta = scanner.nextLine().trim().toLowerCase();
                continuar  = (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) ? true : false;
                ++idActividad;
            }

            try {
                System.out.println("\n\nINSCRIPCION DE ESTUDIANTES EN ACTIVIDADES DEL  EVENTO " + evento.getTitulo());
                System.out.println("===============================================================================");
                continuar = true;
                while (continuar) {
                    System.out.println("Ingese legajo del estudiante a inscribir: ");
                    String legajo = scanner.nextLine();
                    System.out.println("Ingese id de la Actividad: ");
                    idActividad = scanner.nextInt();
                    scanner.nextLine(); // se consume linea
                    for (Estudiante estudiante : estudiantes) {
                        if (estudiante.getLegajo().equals(legajo)) {
                            evento.getActividades().get(--idActividad).inscribir(estudiante);
                        }
                    }
                    System.out.println("Desea generar otra inscripción  S/N?");
                    respuesta = scanner.nextLine().trim().toLowerCase();
                    continuar = (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) ? true : false;
                }
            } catch (CupoExcedidoException e) {
                System.out.println("Error al inscribir: " + e.getMessage());
            }

            try {

                evento.persistirEvento();

                EventoUniversitario copiaDesdeArchivo =
                        evento.recuperarEvento(evento.getId());

                System.out.println("\n\nDATOS DEL EVENTO");
                evento.mostrarDatos();

                System.out.println(
                        "\nDatos del evento recuperado desde archivo:"
                );

                copiaDesdeArchivo.mostrarDatos();

            } catch (FileNotFoundException e) {

                System.out.println(
                        "Error 01: No se encontró el archivo del evento: "
                                + e.getMessage()
                );

            } catch (ClassNotFoundException e) {

                System.out.println(
                        "No fue posible reconstruir el objeto almacenado: "
                                + e.getMessage()
                );

            } catch (IOException e) {

                System.out.println(
                        "Se produjo un error de entrada/salida: "
                                + e.getMessage()
                );
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

            System.out.println("\n\nDesea crear otro evento  S/N?");
            respuesta = scanner.nextLine().trim().toLowerCase();
            continuar  = (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) ? true : false;
        } ;

        System.out.println("\n\nTOTAL DE EVENTOS CREADOS: " + EventoUniversitario.getCantidadEventos());

    }
}