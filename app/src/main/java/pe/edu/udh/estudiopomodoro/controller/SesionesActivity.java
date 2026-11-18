package pe.edu.udh.estudiopomodoro.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pe.edu.udh.estudiopomodoro.R;
import pe.edu.udh.estudiopomodoro.view.SesionAdapter;

public class SesionesActivity extends BaseActivity {
    private SesionAdapter adaptador;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        preparar(R.layout.activity_sesiones, true);
        RecyclerView lista = findViewById(R.id.rvSesiones);
        lista.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new SesionAdapter(s -> abrirConId(DetalleSesionActivity.class, s.sesion.id));
        lista.setAdapter(adaptador);
        findViewById(R.id.btnNuevaSesion).setOnClickListener(v -> abrir(PlanificarSesionActivity.class));
        findViewById(R.id.btnRecargar).setOnClickListener(v -> cargar());
    }
    @Override protected void onResume() { super.onResume(); cargar(); }
    private void cargar() {
        ejecutar(() -> repositorio.sesiones(), datos -> {
            adaptador.setDatos(datos);
            TextView estado = findViewById(R.id.tvEstado);
            estado.setText(R.string.vacio_sesiones);
            estado.setVisibility(datos.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}
