package pe.edu.udh.estudiopomodoro.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "ambitos", indices = {@Index(value = "claveNombre", unique = true)})
public class Ambito {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull public String nombre = "";
    @NonNull public String claveNombre = "";
    @NonNull public String tipo = "";
    @NonNull public String descripcion = "";
    public int metaSemanal;
    public boolean activo = true;

    @Override
    public String toString() {
        return nombre;
    }
}
