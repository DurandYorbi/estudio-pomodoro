package pe.edu.udh.estudiopomodoro.controller;

import android.os.Bundle;
import pe.edu.udh.estudiopomodoro.model.negocio.ResumenEstudio;
import android.widget.TextView;
import pe.edu.udh.estudiopomodoro.R;
import pe.edu.udh.estudiopomodoro.model.ResumenAmbito;

public class ResumenActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        preparar(R.layout.activity_resumen, true);
        findViewById(R.id.btnRecargar).setOnClickListener(v -> cargar());
    }
    @Override protected void onResume() { super.onResume(); cargar(); }
    private void cargar() {
        ejecutar(() -> repositorio.resumen(), datos -> {
            StringBuilder texto = new StringBuilder();
            for (ResumenAmbito a : datos.ambitos) {
                texto.append(getString(R.string.resumen_por_ambito, a.nombre, a.sesiones, a.minutos, a.minutosSemana));
                if (a.metaSemanal > 0) texto.append(getString(R.string.avance_meta, a.minutosSemana, a.metaSemanal,
                        ResumenEstudio.porcentajeMeta(a.minutosSemana, a.metaSemanal)));
                texto.append("\n\n");
            }
            ((TextView)findViewById(R.id.tvTotales)).setText(getString(R.string.resumen_totales, datos.bloques, datos.minutos));
            ((TextView)findViewById(R.id.tvResumenAmbitos)).setText(datos.ambitos.isEmpty()
                    ? getString(R.string.vacio_ambitos) : texto.toString());
        });
    }
}
