package pe.edu.udh.estudiopomodoro.controller;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import java.util.Locale;
import pe.edu.udh.estudiopomodoro.R;
import pe.edu.udh.estudiopomodoro.model.Sesion;
import pe.edu.udh.estudiopomodoro.model.SesionDetalle;
import pe.edu.udh.estudiopomodoro.model.negocio.LogicaTemporizador;
import pe.edu.udh.estudiopomodoro.model.negocio.ReglasEstudio;

public class TemporizadorActivity extends BaseActivity {
    private long id;
    private Sesion sesion;
    private CountDownTimer reloj;
    private boolean visible;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        preparar(R.layout.activity_temporizador, true);
        id = getIntent().getLongExtra("id", 0);
        findViewById(R.id.btnAccionTimer).setOnClickListener(v -> {
            if (sesion != null) accion(ReglasEstudio.EN_CURSO.equals(sesion.estado) ? "pausar" : "iniciar");
        });
        findViewById(R.id.btnCancelarTimer).setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.cancelar_sesion).setMessage(R.string.confirmar_cancelar)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.si, (d, w) -> accion("cancelar")).show());
    }
    @Override protected void onResume() {
        super.onResume(); visible = true;
        ejecutar(() -> repositorio.detalle(id), this::mostrar);
    }
    @Override protected void onPause() {
        visible = false;
        if (reloj != null) reloj.cancel();
        super.onPause();
    }
    private void accion(String accion) {
        if (reloj != null) reloj.cancel();
        ejecutar(() -> repositorio.cambiarTemporizador(id, accion), this::mostrar);
    }
    private void mostrar(SesionDetalle d) {
        sesion = d.sesion;
        ((TextView)findViewById(R.id.tvTareaTimer)).setText(d.sesion.tarea);
        ((TextView)findViewById(R.id.tvFaseTimer)).setText(getString(R.string.fase_estado, sesion.fase, sesion.estado));
        boolean terminado = ReglasEstudio.COMPLETADA.equals(sesion.estado) || ReglasEstudio.CANCELADA.equals(sesion.estado);
        Button boton = findViewById(R.id.btnAccionTimer);
        boton.setText(ReglasEstudio.EN_CURSO.equals(sesion.estado) ? R.string.pausar : R.string.iniciar_reanudar);
        boton.setEnabled(!terminado);
        findViewById(R.id.btnCancelarTimer).setEnabled(!terminado);
        ((TextView)findViewById(R.id.tvAyudaTimer)).setText(terminado ? R.string.timer_terminado :
                ReglasEstudio.DESCANSO.equals(sesion.fase) && ReglasEstudio.PAUSADA.equals(sesion.estado)
                        ? R.string.iniciar_descanso : R.string.timer_ayuda);
        long restante = LogicaTemporizador.restante(sesion, System.currentTimeMillis());
        dibujar(restante);
        if (reloj != null) reloj.cancel();
        if (visible && ReglasEstudio.EN_CURSO.equals(sesion.estado)) {
            reloj = new CountDownTimer(Math.max(1, restante), 500) {
                @Override public void onTick(long ms) {
                    dibujar(LogicaTemporizador.restante(sesion, System.currentTimeMillis()));
                }
                @Override public void onFinish() { if (visible) accion("actualizar"); }
            }.start();
        }
    }
    private void dibujar(long ms) {
        long segundos = (ms + 999) / 1000;
        ((TextView)findViewById(R.id.tvTiempo)).setText(String.format(Locale.ROOT, "%02d:%02d", segundos / 60, segundos % 60));
    }
}
