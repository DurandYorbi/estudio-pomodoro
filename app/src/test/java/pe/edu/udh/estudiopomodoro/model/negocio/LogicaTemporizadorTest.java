package pe.edu.udh.estudiopomodoro.model.negocio;

import org.junit.Test;
import static org.junit.Assert.*;
import pe.edu.udh.estudiopomodoro.model.Sesion;

public class LogicaTemporizadorTest {
    @Test public void pausaYContinuaSinPerderTiempo() {
        Sesion s = new Sesion();
        LogicaTemporizador.iniciar(s, 1000);
        LogicaTemporizador.pausar(s, 61000);
        assertEquals(24 * 60000L, s.restanteMs);
        assertEquals(ReglasEstudio.PAUSADA, s.estado);
        LogicaTemporizador.iniciar(s, 100000);
        assertEquals(100000 + 24 * 60000L, s.finMs);
    }
    @Test public void calculaTiempoAlVolverDespuesDeCerrarPantalla() {
        Sesion s = new Sesion();
        LogicaTemporizador.iniciar(s, 1000);
        assertEquals(23 * 60000L, LogicaTemporizador.restante(s, 121000));
    }
    @Test public void estudioCompletoRegistraMinutosYPreparaDescanso() {
        Sesion s = new Sesion();
        LogicaTemporizador.iniciar(s, 1000);
        LogicaTemporizador.actualizar(s, 1501000);
        assertEquals(25, s.minutosRealizados);
        assertEquals(1501000, s.completadaEn);
        assertEquals(ReglasEstudio.DESCANSO, s.fase);
        assertEquals(ReglasEstudio.PAUSADA, s.estado);
        assertEquals(300000, s.restanteMs);
    }
    @Test public void cuentaUnaSolaVezAunqueSeConsulteOtraVez() {
        Sesion s = new Sesion(); s.descanso = false;
        LogicaTemporizador.iniciar(s, 1000);
        LogicaTemporizador.actualizar(s, 1501000);
        LogicaTemporizador.actualizar(s, 1801000);
        assertEquals(25, s.minutosRealizados);
        assertEquals(1501000, s.completadaEn);
        assertEquals(ReglasEstudio.COMPLETADA, s.estado);
    }
    @Test public void descansoCompletadoNoSumaMinutosDeEstudio() {
        Sesion s = new Sesion();
        LogicaTemporizador.iniciar(s, 1000);
        LogicaTemporizador.actualizar(s, 1501000);
        LogicaTemporizador.iniciar(s, 1502000);
        LogicaTemporizador.actualizar(s, 1802000);
        assertEquals(25, s.minutosRealizados);
        assertEquals(ReglasEstudio.COMPLETADA, s.estado);
    }
    @Test public void cancelarUnBloqueIncompletoNoSumaMinutos() {
        Sesion s = new Sesion();
        LogicaTemporizador.iniciar(s, 1000);
        LogicaTemporizador.cancelar(s);
        assertEquals(0, s.minutosRealizados);
        assertEquals(ReglasEstudio.CANCELADA, s.estado);
    }
    @Test public void omitirDescansoConservaEstudioCompletado() {
        Sesion s = new Sesion();
        LogicaTemporizador.iniciar(s, 1000);
        LogicaTemporizador.actualizar(s, 1501000);
        LogicaTemporizador.cancelar(s);
        assertEquals(25, s.minutosRealizados);
        assertEquals(ReglasEstudio.COMPLETADA, s.estado);
    }
    @Test(expected = ReglaException.class) public void noReiniciaUnaSesionCompletada() {
        Sesion s = new Sesion(); s.descanso = false;
        LogicaTemporizador.iniciar(s, 1000);
        LogicaTemporizador.actualizar(s, 1501000);
        LogicaTemporizador.iniciar(s, 2000000);
    }
    @Test public void unCierreTardioConservaLaHoraRealDeFinalizacion() {
        Sesion s = new Sesion(); s.descanso = false;
        LogicaTemporizador.iniciar(s, 1000);
        LogicaTemporizador.actualizar(s, 9000000);
        assertEquals(1501000, s.completadaEn);
    }
}
