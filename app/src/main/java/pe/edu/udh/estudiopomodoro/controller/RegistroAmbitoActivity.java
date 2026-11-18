package pe.edu.udh.estudiopomodoro.controller;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.widget.SwitchCompat;
import pe.edu.udh.estudiopomodoro.R;
import pe.edu.udh.estudiopomodoro.model.Ambito;

public class RegistroAmbitoActivity extends BaseActivity {
    private EditText nombre, descripcion, meta;
    private Spinner tipo;
    private CheckBox definirMeta;
    private SwitchCompat activo;
    private long id;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        preparar(R.layout.activity_registro_ambito, true);
        id = getIntent().getLongExtra("id", 0);
        nombre = findViewById(R.id.etNombre);
        descripcion = findViewById(R.id.etDescripcion);
        meta = findViewById(R.id.etMeta);
        tipo = findViewById(R.id.spTipo);
        definirMeta = findViewById(R.id.cbDefinirMeta);
        activo = findViewById(R.id.swActivo);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_ambito, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adapter);
        definirMeta.setOnCheckedChangeListener((b, checked) -> meta.setEnabled(checked));
        findViewById(R.id.btnGuardarAmbito).setOnClickListener(v -> guardar());
        findViewById(R.id.btnVolverAmbito).setOnClickListener(v -> finish());
        if (id != 0 && state == null) ejecutar(() -> repositorio.ambito(id), this::mostrar);
    }
    private void mostrar(Ambito a) {
        nombre.setText(a.nombre); descripcion.setText(a.descripcion);
        tipo.setSelection(a.tipo.equals("Asignatura") ? 1 : 2);
        definirMeta.setChecked(a.metaSemanal > 0);
        if (a.metaSemanal > 0) meta.setText(String.valueOf(a.metaSemanal));
        activo.setChecked(a.activo);
    }
    private boolean vacio(EditText campo) {
        if (!campo.getText().toString().trim().isEmpty()) return false;
        campo.setError(getString(R.string.error_vacio)); campo.requestFocus(); return true;
    }
    private void guardar() {
        if (vacio(nombre) || vacio(descripcion)) return;
        Ambito a = new Ambito();
        a.id = id; a.nombre = nombre.getText().toString();
        a.descripcion = descripcion.getText().toString();
        a.tipo = tipo.getSelectedItemPosition() == 0 ? "" : tipo.getSelectedItem().toString();
        a.activo = activo.isChecked();
        if (definirMeta.isChecked()) {
            try { a.metaSemanal = Integer.parseInt(meta.getText().toString()); }
            catch (NumberFormatException e) { a.metaSemanal = -1; }
            if (a.metaSemanal <= 0) {
                meta.setError(getString(R.string.regla_meta)); meta.requestFocus(); return;
            }
        }
        ejecutar(() -> repositorio.guardarAmbito(a), resultado -> { mensaje(R.string.guardado); finish(); });
    }
}
