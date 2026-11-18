package pe.edu.udh.estudiopomodoro.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "sesiones", foreignKeys = @ForeignKey(entity = Ambito.class,
        parentColumns = "id", childColumns = "ambitoId", onDelete = ForeignKey.RESTRICT),
        indices = {@Index("ambitoId"), @Index("inicioProgramado")})
public class Sesion {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long ambitoId;
    @NonNull public String tarea = "";
    @NonNull public String fecha = "";
    @NonNull public String hora = "";
    public long inicioProgramado;
    public int duracion = 25;
    public boolean descanso = true;
    @NonNull public String estado = "Planificada";
    @NonNull public String fase = "Estudio";
    public long restanteMs;
    public long finMs;
    public int minutosRealizados;
    public long completadaEn;
}
