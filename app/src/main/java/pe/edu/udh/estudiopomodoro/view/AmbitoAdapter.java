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
import pe.edu.udh.estudiopomodoro.model.Ambito;

public class AmbitoAdapter extends RecyclerView.Adapter<AmbitoAdapter.AmbitoViewHolder> {
    private final List<Ambito> datos = new ArrayList<>();
    private final OnAmbitoClickListener listener;
    public interface OnAmbitoClickListener { void onAmbitoClick(Ambito ambito); }
    public AmbitoAdapter(OnAmbitoClickListener listener) { this.listener = listener; }
    public void setDatos(List<Ambito> nuevos) { datos.clear(); datos.addAll(nuevos); notifyDataSetChanged(); }
    @NonNull @Override public AmbitoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int tipo) {
        return new AmbitoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ambito, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull AmbitoViewHolder h, int posicion) {
        Ambito a = datos.get(posicion);
        h.nombre.setText(a.nombre);
        h.datos.setText(h.itemView.getContext().getString(R.string.ambito_fila, a.tipo,
                h.itemView.getContext().getString(a.activo ? R.string.activo : R.string.inactivo), a.metaSemanal));
        h.itemView.setOnClickListener(v -> listener.onAmbitoClick(a));
    }
    @Override public int getItemCount() { return datos.size(); }
    public static class AmbitoViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, datos;
        public AmbitoViewHolder(@NonNull View v) {
            super(v); nombre = v.findViewById(R.id.tvNombreAmbito); datos = v.findViewById(R.id.tvDatosAmbito);
        }
    }
}
