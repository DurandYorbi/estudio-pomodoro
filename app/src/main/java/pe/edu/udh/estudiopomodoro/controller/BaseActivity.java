package pe.edu.udh.estudiopomodoro.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.concurrent.Callable;
import pe.edu.udh.estudiopomodoro.R;
import pe.edu.udh.estudiopomodoro.model.Repositorio;
import pe.edu.udh.estudiopomodoro.model.negocio.ReglaException;

public abstract class BaseActivity extends AppCompatActivity {
    protected Repositorio repositorio;
    protected boolean trabajando;
    protected interface Respuesta<T> { void recibir(T dato); }

    protected void preparar(int layout, boolean volver) {
        setContentView(layout);
        repositorio = Repositorio.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (volver && getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected <T> void ejecutar(Callable<T> trabajo, Respuesta<T> respuesta) {
        if (trabajando) return;
        ocupado(true);
        repositorio.io.execute(() -> {
            try {
                T dato = trabajo.call();
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    ocupado(false);
                    respuesta.recibir(dato);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    ocupado(false);
                    int mensaje = R.string.error_bd;
                    if (e instanceof ReglaException) {
                        int recurso = getResources().getIdentifier("regla_" + ((ReglaException)e).codigo,
                                "string", getPackageName());
                        if (recurso != 0) mensaje = recurso;
                    }
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                    TextView estado = findViewById(R.id.tvEstado);
                    if (estado != null) { estado.setText(mensaje); estado.setVisibility(View.VISIBLE); }
                });
            }
        });
    }

    private void ocupado(boolean valor) {
        trabajando = valor;
        ProgressBar progress = findViewById(R.id.progreso);
        if (progress != null) progress.setVisibility(valor ? View.VISIBLE : View.GONE);
        int[] botones = {R.id.btnGuardarAmbito, R.id.btnGuardarSesion, R.id.btnAccionTimer,
                R.id.btnCancelarTimer, R.id.btnEditarSesion, R.id.btnEliminarSesion, R.id.btnTimer};
        for (int id : botones) {
            View boton = findViewById(id);
            if (boton != null) boton.setEnabled(!valor);
        }
    }

    protected void abrir(Class<?> pantalla) { startActivity(new Intent(this, pantalla)); }
    protected void abrirConId(Class<?> pantalla, long id) {
        startActivity(new Intent(this, pantalla).putExtra("id", id));
    }
    protected void mensaje(int recurso) { Toast.makeText(this, recurso, Toast.LENGTH_SHORT).show(); }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        Class<?> destino;
        int id = item.getItemId();
        if (id == R.id.menuInicio) destino = MainActivity.class;
        else if (id == R.id.menuAmbito) destino = AmbitosActivity.class;
        else if (id == R.id.menuPlanificar) destino = PlanificarSesionActivity.class;
        else if (id == R.id.menuSesiones) destino = SesionesActivity.class;
        else if (id == R.id.menuResumen) destino = ResumenActivity.class;
        else return super.onOptionsItemSelected(item);
        if (getClass() != destino) {
            Intent intent = new Intent(this, destino);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}
