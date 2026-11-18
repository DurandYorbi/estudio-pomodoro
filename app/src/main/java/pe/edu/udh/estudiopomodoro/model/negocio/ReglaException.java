package pe.edu.udh.estudiopomodoro.model.negocio;

public class ReglaException extends IllegalArgumentException {
    public final String codigo;
    public ReglaException(String codigo) {
        super(codigo);
        this.codigo = codigo;
    }
}
