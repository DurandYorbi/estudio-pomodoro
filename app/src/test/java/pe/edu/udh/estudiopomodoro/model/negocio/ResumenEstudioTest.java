package pe.edu.udh.estudiopomodoro.model.negocio;

import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.*;
import pe.edu.udh.estudiopomodoro.model.ResumenAmbito;

public class ResumenEstudioTest {
    @Test public void calculaTotalesDeVariosAmbitos() {
        ResumenAmbito a = new ResumenAmbito(); a.sesiones = 2; a.minutos = 50;
        ResumenAmbito b = new ResumenAmbito(); b.sesiones = 1; b.minutos = 50;
        ResumenEstudio resumen = new ResumenEstudio(Arrays.asList(a, b));
        assertEquals(3, resumen.bloques);
        assertEquals(100, resumen.minutos);
    }
    @Test public void calculaAvanceParcialDeMeta() {
        assertEquals(25, ResumenEstudio.porcentajeMeta(30, 120));
    }
    @Test public void superarMetaNoPasaDeCienPorCiento() {
        assertEquals(100, ResumenEstudio.porcentajeMeta(180, 120));
    }
    @Test public void sinMetaNoDivideEntreCero() {
        assertEquals(0, ResumenEstudio.porcentajeMeta(50, 0));
    }
}
