package pe.edu.udh.estudiopomodoro.model.negocio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import pe.edu.udh.estudiopomodoro.model.Ambito;
import pe.edu.udh.estudiopomodoro.model.Sesion;

public class ReglasEstudio {
    public static final String PLANIFICADA = "Planificada";
    public static final String EN_CURSO = "En curso";
    public static final String PAUSADA = "Pausada";
    public static final String COMPLETADA = "Completada";
    public static final String CANCELADA = "Cancelada";
    public static final String ESTUDIO = "Estudio";
    public static final String DESCANSO = "Descanso";

    public static String limpiar(String texto) {
        return texto == null ? "" : texto.trim().replaceAll("\\s+", " ");
    }

    public static void validarAmbito(Ambito a) {
        if (limpiar(a.nombre).isEmpty() || limpiar(a.descripcion).isEmpty()) {
            throw new ReglaException("campos");
        }
        if (!a.tipo.equals("Asignatura") && !a.tipo.equals("Proyecto")) {
            throw new ReglaException("tipo");
        }
        if (a.metaSemanal < 0 || a.metaSemanal > 10080) {
            throw new ReglaException("meta");
        }
    }

    public static long fechaHora(String fecha, String hora) {
        if (fecha == null || !fecha.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) {
            throw new ReglaException("fecha");
        }
        if (hora == null || !hora.matches("([01][0-9]|2[0-3]):[0-5][0-9]")) {
            throw new ReglaException("hora");
        }
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ROOT);
        formato.setLenient(false);
        try {
            return formato.parse(fecha + " " + hora).getTime();
        } catch (ParseException e) {
            throw new ReglaException("fecha");
        }
    }

    public static void validarSesion(Sesion s, Ambito a) {
        if (limpiar(s.tarea).isEmpty()) throw new ReglaException("campos");
        if (a == null) throw new ReglaException("ambito");
        if (!a.activo) throw new ReglaException("inactivo");
        if (s.duracion != 25 && s.duracion != 50) throw new ReglaException("duracion");
        s.inicioProgramado = fechaHora(s.fecha, s.hora);
    }

    public static long finProgramado(Sesion s) {
        return s.inicioProgramado + (s.duracion + (s.descanso ? 5 : 0)) * 60000L;
    }

    public static boolean seCruzan(Sesion a, Sesion b) {
        return a.inicioProgramado < finProgramado(b) && b.inicioProgramado < finProgramado(a);
    }

    public static boolean editable(Sesion s) {
        return PLANIFICADA.equals(s.estado);
    }
}
