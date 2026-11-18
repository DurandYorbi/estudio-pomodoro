package pe.edu.udh.estudiopomodoro.controller;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import pe.edu.udh.estudiopomodoro.R;
import pe.edu.udh.estudiopomodoro.model.SesionDetalle;
import pe.edu.udh.estudiopomodoro.model.negocio.ReglasEstudio;

public class DetalleSesionActivity extends BaseActivity {
    private long id;
    private SesionDetalle datos;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        preparar(R.layout.activity_detalle_sesion, true);
        id = getIntent().getLongExtra("id", 0);
        findViewById(R.id.btnTimer).setOnClickListener(v -> abrirConId(TemporizadorActivity.class, id));
        findViewById(R.id.btnEditarSesion).setOnClickListener(v -> abrirConId(PlanificarSesionActivity.class, id));
        findViewById(R.id.btnEliminarSesion).setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.eliminar).setMessage(R.string.confirmar_sesion)
                .setNegativeButton(R.string.no, null).setPositiveButton(R.string.eliminar, (d, w) ->
                        ejecutar(() -> { repositorio.eliminarSesion(id); return true; },
                                ok -> { mensaje(R.string.eliminado); finish(); })).show());
        findViewById(R.id.btnVolverDetalle).setOnClickListener(v -> finish());
    }
    @Override protected void onResume() {
        super.onResume();
        ejecutar(() -> repositorio.detalle(id), this::mostrar);
    }
    private void mostrar(SesionDetalle d) {
        datos = d;
        ((TextView)findViewById(R.id.tvDetalleTarea)).setText(d.sesion.tarea);
        ((TextView)findViewById(R.id.tvDetalleAmbito)).setText(d.nombreAmbito);
        ((TextView)findViewById(R.id.tvDetalleDescripcion)).setText(d.descripcionAmbito);
        ((TextView)findViewById(R.id.tvDetalleFecha)).setText(d.sesion.fecha);
        ((TextView)findViewById(R.id.tvDetalleHora)).setText(d.sesion.hora);
        ((TextView)findViewById(R.id.tvDetalleDuracion)).setText(getString(R.string.duracion_minutos, d.sesion.duracion));
        ((TextView)findViewById(R.id.tvDetalleDescanso)).setText(d.sesion.descanso ? R.string.con_descanso : R.string.sin_descanso);
        ((TextView)findViewById(R.id.tvDetalleEstado)).setText(d.sesion.estado);
        findViewById(R.id.btnEditarSesion).setEnabled(ReglasEstudio.editable(d.sesion));
    }
}
