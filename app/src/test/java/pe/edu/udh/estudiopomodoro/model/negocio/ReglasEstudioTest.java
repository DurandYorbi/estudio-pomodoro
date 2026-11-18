package pe.edu.udh.estudiopomodoro.model.negocio;

import org.junit.Test;
import static org.junit.Assert.*;
import pe.edu.udh.estudiopomodoro.model.Ambito;
import pe.edu.udh.estudiopomodoro.model.Sesion;

public class ReglasEstudioTest {
    private Ambito ambito() {
        Ambito a = new Ambito();
        a.nombre = "Redes"; a.descripcion = "Subredes"; a.tipo = "Asignatura";
        a.metaSemanal = 120;
        return a;
    }
    private Sesion sesion(String hora) {
        Sesion s = new Sesion();
        s.tarea = "Repasar IPv4"; s.fecha = "19/11/2026"; s.hora = hora;
        s.inicioProgramado = ReglasEstudio.fechaHora(s.fecha, s.hora);
        return s;
    }
    @Test public void aceptaAmbitoSinMeta() {
        Ambito a = ambito(); a.metaSemanal = 0; ReglasEstudio.validarAmbito(a);
    }
    @Test public void normalizaEspaciosDelNombre() {
        assertEquals("Redes I", ReglasEstudio.limpiar("  Redes   I  "));
    }
    @Test(expected = ReglaException.class) public void rechazaNombreVacio() {
        Ambito a = ambito(); a.nombre = "   "; ReglasEstudio.validarAmbito(a);
    }
    @Test(expected = ReglaException.class) public void rechazaDescripcionVacia() {
        Ambito a = ambito(); a.descripcion = ""; ReglasEstudio.validarAmbito(a);
    }
    @Test(expected = ReglaException.class) public void rechazaMetaNegativa() {
        Ambito a = ambito(); a.metaSemanal = -1; ReglasEstudio.validarAmbito(a);
    }
    @Test(expected = ReglaException.class) public void rechazaMetaMayorQueSemana() {
        Ambito a = ambito(); a.metaSemanal = 10081; ReglasEstudio.validarAmbito(a);
    }
    @Test(expected = ReglaException.class) public void rechazaTipoNoSeleccionado() {
        Ambito a = ambito(); a.tipo = ""; ReglasEstudio.validarAmbito(a);
    }
    @Test public void aceptaFechaBisiesta() {
        assertTrue(ReglasEstudio.fechaHora("29/02/2028", "23:59") > 0);
    }
    @Test(expected = ReglaException.class) public void rechazaFechaInexistente() {
        ReglasEstudio.fechaHora("31/02/2026", "16:00");
    }
    @Test(expected = ReglaException.class) public void rechazaHoraFueraDeRango() {
        ReglasEstudio.fechaHora("19/11/2026", "24:00");
    }
    @Test(expected = ReglaException.class) public void rechazaAmbitoInactivo() {
        Ambito a = ambito(); a.activo = false; ReglasEstudio.validarSesion(sesion("16:00"), a);
    }
    @Test(expected = ReglaException.class) public void rechazaDuracionInvalida() {
        Sesion s = sesion("16:00"); s.duracion = 10; ReglasEstudio.validarSesion(s, ambito());
    }
    @Test public void elDescansoTambienOcupaHorario() {
        assertTrue(ReglasEstudio.seCruzan(sesion("16:00"), sesion("16:28")));
    }
    @Test public void permiteHorariosConsecutivosSinCruce() {
        assertFalse(ReglasEstudio.seCruzan(sesion("16:00"), sesion("16:30")));
    }
    @Test public void sinDescansoLiberaHorarioAlTerminarEstudio() {
        Sesion a = sesion("16:00"); a.descanso = false;
        assertFalse(ReglasEstudio.seCruzan(a, sesion("16:25")));
    }
}
