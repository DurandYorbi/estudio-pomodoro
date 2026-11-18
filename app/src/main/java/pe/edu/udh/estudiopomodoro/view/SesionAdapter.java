package pe.edu.udh.estudiopomodoro.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import pe.edu.udh.estudiopomodoro.R;
import pe.edu.udh.estudiopomodoro.model.SesionDetalle;

public class SesionAdapter extends RecyclerView.Adapter<SesionAdapter.SesionViewHolder> {
    private final List<SesionDetalle> datos = new ArrayList<>();
    private final OnSesionClickListener listener;
    public interface OnSesionClickListener { void onSesionClick(SesionDetalle sesion); }
    public SesionAdapter(OnSesionClickListener listener) { this.listener = listener; }
    public void setDatos(List<SesionDetalle> nuevos) {
        datos.clear(); datos.addAll(nuevos); notifyDataSetChanged();
    }
    @NonNull @Override public SesionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int tipo) {
        return new SesionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sesion, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull SesionViewHolder h, int posicion) {
        SesionDetalle d = datos.get(posicion);
        h.tarea.setText(d.sesion.tarea);
        h.ambito.setText(d.nombreAmbito);
        h.fecha.setText(h.itemView.getContext().getString(R.string.lista_sesion, d.sesion.fecha, d.sesion.hora, d.sesion.estado));
        h.duracion.setText(h.itemView.getContext().getString(R.string.duracion_minutos, d.sesion.duracion));
        h.itemView.setOnClickListener(v -> listener.onSesionClick(d));
    }
    @Override public int getItemCount() { return datos.size(); }
    public static class SesionViewHolder extends RecyclerView.ViewHolder {
        TextView tarea, ambito, fecha, duracion;
        public SesionViewHolder(@NonNull View view) {
            super(view);
            tarea = view.findViewById(R.id.tvTareaItem); ambito = view.findViewById(R.id.tvAmbitoItem);
            fecha = view.findViewById(R.id.tvFechaItem); duracion = view.findViewById(R.id.tvDuracionItem);
        }
    }
}
