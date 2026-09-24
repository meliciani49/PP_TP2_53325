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
            System.out.println("Ingrese legajo del estudiante: ");
            String legajo = scanner.nextLine();
            System.out.println("Ingrese nombre y apellido del estudiante: ");
            String apenomb = scanner.nextLine();
            estudiantes.add(new Estudiante(legajo, apenomb));
            System.out.println("Desea crear otro estudiante?  S/N?");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            continuar = (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) ? true : false;
        };

        System.out.println("\n\nREGISTRO DE EVENTOS: ");
        System.out.println("====================");
        continuar=true;
        while(continuar) {
            System.out.println("Ingese un titulo para el evento: ");
            String titulo = scanner.nextLine();
            System.out.println("Ingese el costo base:  ");
            double costoBase = scanner.nextDouble();
            scanner.nextLine(); //limpia el Enter pendiente
            System.out.println("El evento tendra costo para los participantes S/N?");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            boolean esGratuito= true;
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                esGratuito= false;
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
                scanner.nextLine();
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
                    scanner.nextLine();
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

            try{
                evento.persistirEvento();
                System.out.println("\n\n DATOS DEL EVENTO");
                evento.mostrarDatos();

                Thread envioTicketsThread = new hilos.EnvioTicketsThread(evento);
                envioTicketsThread.start();

                List<Taller> talleres = evento.filtrarActividadesPorTipo(Taller.class);
                List<Curso> cursos = evento.filtrarActividadesPorTipo(Curso.class);
                List<Charla> charlas = evento.filtrarActividadesPorTipo(Charla.class);


                System.out.println("Actividades filtradas por tipo usando método parametrizado acotado:");
                System.out.println("Charlas encontradas: " + charlas.size());
                System.out.println("Talleres encontrados: " + talleres.size());
                System.out.println("Cursos encontrados: " + cursos.size());

                // Uso de wildcard acotado: el mismo método puede recibir List<Taller>, List<Curso>, etc.
                System.out.println("Costo de materiales de talleres: $" + evento.calcularCostoMateriales(talleres));
                System.out.println("Costo de materiales de cursos: $" + evento.calcularCostoMateriales(cursos));
                System.out.println("Costo de materiales de todas las actividades: $" + evento.calcularCostoMateriales(evento.getActividades()));
            }
            catch (FileNotFoundException e) {
                System.out.println("Error, no se puede encontrar el archivo del evento: " + e.getMessage());
                e.printStackTrace();
            }
            catch (IOException e) {
                System.out.println("Error de E/S: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Error inesperado: " + e.getMessage());
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
