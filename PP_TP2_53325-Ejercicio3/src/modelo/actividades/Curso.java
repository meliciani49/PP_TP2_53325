package modelo.actividades;

import modelo.Estudiante;
import modelo.certificacion.Certificable;

public class Curso extends Actividad implements Certificable {
    private int  nivel;

    public Curso(int id, String titulo,int nivel, int cupo) {
        super(id, titulo,cupo);
        this.nivel = nivel;
    }

    @Override
    public double calcularCostoMateriales() {
        /* Método polimórfico */
        switch (nivel) {
            case 1:
                return 1000.0;
            case 2:
                return 2000.0;
            case 3:
                return 3000.0;
            default:
                return 0.0;
        }
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
                + " asistió al curso \"" + getTitulo() + "\""
                + " de nivel " + nivel + ".";
    }
}