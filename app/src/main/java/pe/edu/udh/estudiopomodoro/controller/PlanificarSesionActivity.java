package pe.edu.udh.estudiopomodoro.controller;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import pe.edu.udh.estudiopomodoro.R;
import pe.edu.udh.estudiopomodoro.model.Ambito;
import pe.edu.udh.estudiopomodoro.model.Sesion;
import pe.edu.udh.estudiopomodoro.model.negocio.ReglasEstudio;

public class PlanificarSesionActivity extends BaseActivity {
    private EditText tarea, fecha, hora;
    private Spinner ambito;
    private RadioButton minutos25;
    private CheckBox descanso;
    private List<Ambito> opciones = new ArrayList<>();
    private long id;
    private boolean primeraEntrada = true;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        preparar(R.layout.activity_planificar_sesion, true);
        id = getIntent().getLongExtra("id", 0);
        tarea = findViewById(R.id.etTarea); fecha = findViewById(R.id.etFecha);
        hora = findViewById(R.id.etHora); ambito = findViewById(R.id.spAmbito);
        minutos25 = findViewById(R.id.rb25); descanso = findViewById(R.id.cbDescanso);
        findViewById(R.id.btnGuardarSesion).setOnClickListener(v -> guardar());
        findViewById(R.id.btnVolverSesion).setOnClickListener(v -> finish());
        if (state == null) cargar(null);
        else cargar(state);
    }
    @Override protected void onResume() {
        super.onResume();
        if (primeraEntrada) { primeraEntrada = false; return; }
        Bundle estado = new Bundle();
        int posicion = ambito.getSelectedItemPosition();
        if (posicion > 0 && posicion <= opciones.size()) estado.putLong("ambitoId", opciones.get(posicion - 1).id);
        cargar(estado);
    }
    private void cargar(Bundle state) {
        ejecutar(() -> repositorio.activos(), datos -> {
            opciones = datos;
            List<String> nombres = new ArrayList<>();
            nombres.add(getString(R.string.seleccionar_ambito));
            for (Ambito a : datos) nombres.add(a.nombre);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ambito.setAdapter(adapter);
            if (datos.isEmpty()) ((TextView)findViewById(R.id.tvEstado)).setText(R.string.sin_ambitos_activos);
            else ((TextView)findViewById(R.id.tvEstado)).setText("");
            if (state != null) {
                long seleccionado = state.getLong("ambitoId", 0);
                seleccionarAmbito(seleccionado);
            } else if (id != 0) {
                ejecutar(() -> repositorio.detalle(id), d -> {
                    if (!ReglasEstudio.editable(d.sesion)) { mensaje(R.string.regla_solo_planificada); finish(); return; }
                    tarea.setText(d.sesion.tarea); fecha.setText(d.sesion.fecha); hora.setText(d.sesion.hora);
                    ((RadioButton)findViewById(d.sesion.duracion == 25 ? R.id.rb25 : R.id.rb50)).setChecked(true);
                    descanso.setChecked(d.sesion.descanso);
                    seleccionarAmbito(d.sesion.ambitoId);
                });
            }
        });
    }
    private void seleccionarAmbito(long seleccionado) {
        for (int i = 0; i < opciones.size(); i++) if (opciones.get(i).id == seleccionado) ambito.setSelection(i + 1);
    }
    @Override protected void onSaveInstanceState(Bundle state) {
        int posicion = ambito.getSelectedItemPosition();
        if (posicion > 0 && posicion <= opciones.size()) state.putLong("ambitoId", opciones.get(posicion - 1).id);
        super.onSaveInstanceState(state);
    }
    private void guardar() {
        for (EditText campo : new EditText[]{tarea, fecha, hora}) {
            if (campo.getText().toString().trim().isEmpty()) {
                campo.setError(getString(R.string.error_vacio)); campo.requestFocus(); return;
            }
        }
        int posicion = ambito.getSelectedItemPosition();
        if (posicion <= 0 || posicion > opciones.size()) { mensaje(R.string.error_ambito); return; }
        Sesion s = new Sesion(); s.id = id; s.ambitoId = opciones.get(posicion - 1).id;
        s.tarea = tarea.getText().toString(); s.fecha = fecha.getText().toString().trim();
        s.hora = hora.getText().toString().trim(); s.duracion = minutos25.isChecked() ? 25 : 50;
        s.descanso = descanso.isChecked();
        ejecutar(() -> repositorio.guardarSesion(s), resultado -> { mensaje(R.string.guardado); finish(); });
    }
}
