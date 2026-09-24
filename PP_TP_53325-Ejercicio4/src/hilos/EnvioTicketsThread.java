package hilos;

import modelo.EventoUniversitario;
import modelo.Inscripcion;
import modelo.actividades.Actividad;

public class EnvioTicketsThread extends Thread {
    private EventoUniversitario evento;

    public EnvioTicketsThread(EventoUniversitario evento) {
        super("Hilo-Envio-Tickets");
        this.evento = evento;
    }

    /**
     * Método que se ejecuta cuando se inicia el hilo.
     * Recorre todas las actividades del evento y envía los tickets a los estudiantes confirmados.
     */
    @Override
    public void run() {
        System.out.println("[" + getName() + "] Inicio del envío de tickets.");

        for (Actividad actividad : evento.getActividades()) {
            for (Inscripcion inscripcion : actividad.getInscripciones()) {
                if ("CONFIRMADA".equals(inscripcion.getEstado())) {
                    inscripcion.getTicket().enviarTicket();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        System.out.println("[" + getName() + "] Fin del envío de tickets.");
    }
}

