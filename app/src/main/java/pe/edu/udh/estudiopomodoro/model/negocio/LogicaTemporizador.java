package pe.edu.udh.estudiopomodoro.model.negocio;

import pe.edu.udh.estudiopomodoro.model.Sesion;

public class LogicaTemporizador {
    public static long restante(Sesion s, long ahora) {
        return ReglasEstudio.EN_CURSO.equals(s.estado)
                ? Math.max(0, s.finMs - ahora) : Math.max(0, s.restanteMs);
    }

    public static void iniciar(Sesion s, long ahora) {
        if (!ReglasEstudio.PLANIFICADA.equals(s.estado) && !ReglasEstudio.PAUSADA.equals(s.estado)) {
            throw new ReglaException("estado");
        }
        if (ReglasEstudio.PLANIFICADA.equals(s.estado)) {
            s.fase = ReglasEstudio.ESTUDIO;
            s.restanteMs = s.duracion * 60000L;
        }
        s.finMs = ahora + s.restanteMs;
        s.estado = ReglasEstudio.EN_CURSO;
    }

    public static void actualizar(Sesion s, long ahora) {
        if (!ReglasEstudio.EN_CURSO.equals(s.estado) || restante(s, ahora) > 0) return;
        long finReal = s.finMs;
        s.finMs = 0;
        if (ReglasEstudio.ESTUDIO.equals(s.fase)) {
            s.minutosRealizados = s.duracion;
            s.completadaEn = finReal;
            if (s.descanso) {
                s.fase = ReglasEstudio.DESCANSO;
                s.estado = ReglasEstudio.PAUSADA;
                s.restanteMs = 5 * 60000L;
                return;
            }
        }
        s.restanteMs = 0;
        s.estado = ReglasEstudio.COMPLETADA;
    }

    public static void pausar(Sesion s, long ahora) {
        if (!ReglasEstudio.EN_CURSO.equals(s.estado)) throw new ReglaException("estado");
        if (restante(s, ahora) == 0) {
            actualizar(s, ahora);
        } else {
            s.restanteMs = restante(s, ahora);
            s.finMs = 0;
            s.estado = ReglasEstudio.PAUSADA;
        }
    }

    public static void cancelar(Sesion s) {
        if (ReglasEstudio.COMPLETADA.equals(s.estado) || ReglasEstudio.CANCELADA.equals(s.estado)) {
            throw new ReglaException("estado");
        }
        s.estado = s.minutosRealizados > 0 ? ReglasEstudio.COMPLETADA : ReglasEstudio.CANCELADA;
        s.restanteMs = 0;
        s.finMs = 0;
    }
}
