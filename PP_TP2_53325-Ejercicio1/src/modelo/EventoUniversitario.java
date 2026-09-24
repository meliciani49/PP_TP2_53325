package modelo;
import modelo.actividades.Actividad;
import modelo.actividades.Charla;
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
        this.costoBase = gratuito ? 0 : costo;        cantidadEventos++;
        cantidadEventos++;

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
    public void asignarSala(Sala sala) {
        this.sala = sala;
    }

    public void crearActividad(int id, String titulo, int cupo, String  tipoActividad) {

        Scanner scanner = new Scanner(System.in);

        switch (tipoActividad) {
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
                Actividad taller = new Taller(id, titulo,requiereNotebook, cupo);
                this.actividades.add(taller);
                break;
            default:
                System.out.println("Error: Tipo de actividad no reconocido.");
        }
    }

    public List<Actividad>  getActividades() {
        return Collections.unmodifiableList(actividades);
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
        FileOutputStream ofos=null;
        ObjectOutputStream oos=null;
        try {
            ofos = new FileOutputStream(nombreArchivo);
            oos = new ObjectOutputStream(ofos);
            oos.writeObject(this);
        }
        finally {
            if (oos != null) {
                oos.close();
            }
            else if (ofos != null) {
                ofos.close();
            }

            return true;
        }
    }

    public EventoUniversitario recuperarEvento(String id)  throws IOException, ClassNotFoundException
    {
        String nombreArchivo = "evento_" + id + ".dat";
        EventoUniversitario oeu=null;
        FileInputStream ofis=null;
        ObjectInputStream ois=null;
        try {
            ofis = new FileInputStream(nombreArchivo);
            ois = new ObjectInputStream(ofis);
            oeu = (EventoUniversitario) ois.readObject();
        }
        finally {
            if (ois != null) {
                ois.close();
            }
            else if (ofis != null) {
                ofis.close();
            }

            return oeu;
        }
    }
}
