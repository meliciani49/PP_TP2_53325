package modelo;

import modelo.actividades.Actividad;

import java.io.Serializable;
import java.time.LocalDate;

public class Inscripcion implements Serializable {
    private Actividad actividad;
    private Estudiante estudiante;
    private LocalDate fecha;
    private String estado;
    private TicketDeAcceso ticket;

    public Inscripcion(Actividad actividad, Estudiante estudiante, LocalDate fecha, String estado) {
        this.actividad = actividad;
        this.estudiante = estudiante;
        this.fecha = fecha;
        this.estado = estado;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void confirmar()    {
        this.estado = "CONFIRMADA";
        this.ticket = new TicketDeAcceso();
    }

    public TicketDeAcceso getTicket() {
        return ticket;
    }

    public final class TicketDeAcceso implements Serializable {
        private String idTicket;
        private LocalDate fechaEmision;

        public TicketDeAcceso() {
            this.idTicket = "TICKET-" + actividad.getId() + "-" + estudiante.getLegajo() + "-" + System.currentTimeMillis();
            this.fechaEmision = LocalDate.now();
            System.out.println("Ticket generado para la inscripción: " + idTicket);
        }

        public void enviarTicket() {
            System.out.println("Enviando ticket " + idTicket + " al estudiante " + estudiante.getNombre() + " para la actividad " + actividad.getTitulo());
        }
    }
}

