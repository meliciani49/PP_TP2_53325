package modelo.actividades;

import modelo.Estudiante;
import modelo.certificacion.Certificable;

public class Taller extends Actividad implements Certificable  {
    private boolean requiereNotebook;

    public Taller(int id, String titulo, boolean requiereNotebook, int cupo) {
        super(id, titulo,cupo);
        this.requiereNotebook = requiereNotebook;
    }

    public boolean isRequiereNotebook() {
        return requiereNotebook;
    }

    public void setRequiereNotebook(boolean requiereNotebook) {
        this.requiereNotebook = requiereNotebook;
    }

    @Override
    public double calcularCostoMateriales() {
        /* Método polimórfico */
        if (requiereNotebook) {
            return 5000.0;
        }
        return 2000.0;
    }

    @Override
    public String getTipo() {
        /* Método polimórfico */
        return this.getClass().getName();
    }

    @Override
    public String generarCertificado(Estudiante estudiante) {
        return "Certificado emitido por " + ENTIDAD_EMISORA
                + ": se deja constancia de que " + estudiante.getNombre()
                + " participó en el taller \"" + getTitulo() + "\".";
    }
}
