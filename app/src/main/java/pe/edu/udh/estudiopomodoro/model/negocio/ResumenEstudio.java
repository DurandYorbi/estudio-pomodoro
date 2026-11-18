package pe.edu.udh.estudiopomodoro.model.negocio;

import java.util.List;
import pe.edu.udh.estudiopomodoro.model.ResumenAmbito;

public class ResumenEstudio {
    public final List<ResumenAmbito> ambitos;
    public final int bloques;
    public final int minutos;

    public ResumenEstudio(List<ResumenAmbito> ambitos) {
        this.ambitos = ambitos;
        int sumaBloques = 0;
        int sumaMinutos = 0;
        for (ResumenAmbito ambito : ambitos) {
            sumaBloques += ambito.sesiones;
            sumaMinutos += ambito.minutos;
        }
        bloques = sumaBloques;
        minutos = sumaMinutos;
    }

    public static int porcentajeMeta(int minutosSemana, int meta) {
        if (meta <= 0) return 0;
        return (int)Math.max(0, Math.min(100, minutosSemana * 100L / meta));
    }
}
