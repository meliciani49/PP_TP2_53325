package modelo;

import modelo.actividades.Actividad;
import modelo.actividades.Charla;
import modelo.actividades.Curso;
import modelo.actividades.Taller;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class EventoUniversitario  implements   Serializable {
    private final String Id;
    private String titulo;
    private double costoBase;
    private boolean gratuito;
    private Sala sala;
    private List <Actividad> actividades;
    private static int cantidadEventos;

    static {
        cantidadEventos = 0;
        System.out.println("Inicializador estático: se cargó la clase EventoUniversitario.");
    }

    public EventoUniversitario(String id, String nombre,  double costo, boolean esGratuito) {
        this.Id = id;
        setTitulo(nombre);
        this.gratuito = esGratuito;
        this.costoBase = gratuito ? 0 : costo;
        cantidadEventos++;

        /* Aquí notar como se implementa la relación de composición como una relación estructural del tipo Todo-Parte fuerte .
         * Si se destruye el evento se destruirán también sus actividades.
         * Es decir, la vida útil de cada actividad está fuertemente ligada a la vida útil del evento. */
        this.actividades = new ArrayList<>();
    }

    public EventoUniversitario(EventoUniversitario otroEvento) {
        this(
                otroEvento.Id + "-COPIA",
                otroEvento.titulo,
                otroEvento.costoBase,
                otroEvento.gratuito
        );
    }

    public String getId() {
        return Id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String nombre) {
        if (nombre != null && !nombre.isBlank())
            this.titulo = nombre;
    }

    public double calcularCostoEstimado() {
        if (this.gratuito) {
            return 0.0;
        }

        double costoTotal = costoBase;

        for (Actividad actividad : actividades) {
            costoTotal += actividad.calcularCostoMateriales();
        }

        return costoTotal * 1.21;
    }

    public Sala getSala() {
        return sala;
    }

    /* Implementa la agregación dinámica. Un evento se realiza en una sala, pero la relación Todo-Parte es débil.
     * Si el evento no se realiza y el objeto que lo representa se destruye, la sala sigue existiendo y puede asignarse a otro evento. */
    public void asignarSala(Sala sala) {
        this.sala = sala;
    }

    /**
     * Representa la composición: la actividad se crea para el evento y queda contenida por él.
     * La relación Todo-Parte es fuerte: si el evento se destruye, las actividades también se destruyen.
     */
    public void crearActividad(int id, String titulo, int cupo, String tipoActividad) {

        Scanner scanner = new Scanner(System.in);

        switch (tipoActividad.toLowerCase()) {
            case "charla":
                System.out.print("Ingrese el nombre del disertante para la charla " + titulo + " :  ");
                String disertante = scanner.nextLine();
                Actividad charla = new Charla(id, titulo,  disertante,cupo);
                this.actividades.add(charla);
                break;
            case "taller":
                System.out.print("El taller " + titulo + " requiere el uso de Notebook? : S/N  ");
                String respuesta = scanner.nextLine().trim().toLowerCase();
                boolean requiereNotebook = false;
                if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                    requiereNotebook = true;
                }
                Actividad taller = new Taller(id, titulo,requiereNotebook,cupo);
                this.actividades.add(taller);
                break;
            case "curso":
                System.out.print("Ingrese el nivel de curso " + titulo + ": ");
                int nivel = scanner.nextInt();
                Actividad curso = new Curso(id, titulo, nivel,cupo);
                this.actividades.add(curso);
                break;
            default:
                System.out.println("Error: Tipo de actividad no reconocido.");
        }
    }


    public List<Actividad>  getActividades() {
        return Collections.unmodifiableList(actividades);
    }

    /**
     * Método parametrizado acotado. Permite obtener, desde las actividades
     * que componen este evento, solo aquellas que sean del tipo solicitado
     * o de una subclase compatible.
     *
     * El parámetro T está acotado a Actividad para impedir que se pidan
     * tipos ajenos al modelo de actividades del evento.
     *
     * @param tipo clase concreta que se desea filtrar, por ejemplo Taller.class.
     * @param <T> tipo de actividad a recuperar.
     * @return lista tipada con las actividades del tipo indicado.
     */
    public <T extends Actividad> List<T> filtrarActividadesPorTipo(Class<T> tipo) {
        List<T> resultado = new ArrayList<>();

        for (Actividad actividad : actividades) {
            if (tipo.isInstance(actividad)) {
                resultado.add(tipo.cast(actividad));
            }
        }

        return resultado;
    }

    /**
     * Método que usa wildcard acotado. Permite calcular el costo de materiales
     * de una lista formada por Actividad o por cualquier subtipo de Actividad,
     * como List<Taller>, List<Curso> o List<Charla>.
     *
     * @param actividades lista de actividades o subtipos de actividad.
     * @return costo total de materiales.
     */
    public double calcularCostoMateriales(List<? extends Actividad> actividades) {
        double total = 0.0;

        for (Actividad actividad : actividades) {
            total += actividad.calcularCostoMateriales();
        }

        return total;
    }


    public void  mostrarDatos() {
        System.out.println("===============================================");
        System.out.println("Evento codigo=" + Id);
        System.out.println("TÍtulo=" + titulo);
        System.out.println("Costo=" + this.calcularCostoEstimado());
        System.out.println("Sala asignada: " + (sala != null ? sala.getNombre() : "Sin sala")+"\n");
        System.out.println("Actividades:");
        System.out.println("____________");
        for (Actividad actividad : actividades) {
            actividad.mostrarIdentificacion();
            actividad.mostrarInscripciones();
        }
        System.out.println("===============================================");
    }

    public static int getCantidadEventos() {
        return cantidadEventos;
    }

    public boolean persistirEvento() throws IOException {
        String nombreArchivo = "evento_" + this.Id + ".dat";
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(
                             new FileOutputStream(nombreArchivo))) {

            oos.writeObject(this);
            return true;
        }
    }

    public EventoUniversitario recuperarEvento(String id)  throws IOException, ClassNotFoundException {

        String nombreArchivo = "evento_" + id + ".dat";

        try (ObjectInputStream ois =
                     new ObjectInputStream(
                             new FileInputStream(nombreArchivo))) {

            return (EventoUniversitario) ois.readObject();
        }
    }
}

