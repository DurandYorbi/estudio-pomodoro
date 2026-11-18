package pe.edu.udh.estudiopomodoro.controller;

import android.os.Bundle;
import android.widget.TextView;
import pe.edu.udh.estudiopomodoro.R;

public class MainActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        preparar(R.layout.activity_main, false);
        findViewById(R.id.btnRegistrarAmbito).setOnClickListener(v -> abrir(AmbitosActivity.class));
        findViewById(R.id.btnPlanificarSesion).setOnClickListener(v -> abrir(PlanificarSesionActivity.class));
        findViewById(R.id.btnVerSesiones).setOnClickListener(v -> abrir(SesionesActivity.class));
        findViewById(R.id.btnResumen).setOnClickListener(v -> abrir(ResumenActivity.class));
    }
    @Override protected void onResume() {
        super.onResume();
        ejecutar(() -> repositorio.sesiones().size(), cantidad ->
                ((TextView)findViewById(R.id.tvResumenInicio)).setText(getString(R.string.total_sesiones, cantidad)));
    }
}
