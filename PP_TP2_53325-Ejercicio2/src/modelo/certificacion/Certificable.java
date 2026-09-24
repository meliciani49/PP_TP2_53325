package modelo.certificacion;

import modelo.Estudiante;
public interface Certificable {
    String ENTIDAD_EMISORA = "UTN - FRM";

    /**
     * Genera el texto de la constancia correspondiente al estudiante indicado.
     *
     * @param estudiante estudiante para quien se emite la constancia.
     * @return texto de la constancia generada.
     */
    String generarCertificado(Estudiante estudiante);
}
