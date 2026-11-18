package pe.edu.udh.estudiopomodoro.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pe.edu.udh.estudiopomodoro.R;
import pe.edu.udh.estudiopomodoro.model.Ambito;
import pe.edu.udh.estudiopomodoro.view.AmbitoAdapter;

public class AmbitosActivity extends BaseActivity {
    private AmbitoAdapter adaptador;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        preparar(R.layout.activity_ambitos, true);
        RecyclerView lista = findViewById(R.id.rvAmbitos);
        lista.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AmbitoAdapter(a -> new AlertDialog.Builder(this)
                .setTitle(a.nombre)
                .setItems(R.array.acciones_ambito, (dialog, opcion) -> {
                    if (opcion == 0) abrirConId(RegistroAmbitoActivity.class, a.id);
                    else confirmarEliminar(a);
                }).show());
        lista.setAdapter(adaptador);
        findViewById(R.id.btnNuevoAmbito).setOnClickListener(v -> abrir(RegistroAmbitoActivity.class));
        findViewById(R.id.btnRecargar).setOnClickListener(v -> cargar());
    }
    @Override protected void onResume() { super.onResume(); cargar(); }
    private void cargar() {
        ejecutar(() -> repositorio.ambitos(), datos -> {
            adaptador.setDatos(datos);
            TextView estado = findViewById(R.id.tvEstado);
            estado.setText(R.string.vacio_ambitos);
            estado.setVisibility(datos.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
    private void confirmarEliminar(Ambito ambito) {
        new AlertDialog.Builder(this).setTitle(R.string.eliminar)
                .setMessage(getString(R.string.confirmar_ambito, ambito.nombre))
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.eliminar, (d, w) -> ejecutar(() -> {
                    repositorio.eliminarAmbito(ambito.id); return true;
                }, ok -> { mensaje(R.string.eliminado); cargar(); })).show();
    }
}
