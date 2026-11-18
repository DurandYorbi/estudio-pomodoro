package pe.edu.udh.estudiopomodoro.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SesionDao {
    @Insert long insertar(Sesion sesion);
    @Update int actualizar(Sesion sesion);
    @Delete int eliminar(Sesion sesion);
    @Query("SELECT * FROM sesiones WHERE id = :id")
    Sesion buscar(long id);
    @Query("SELECT sesiones.*, ambitos.nombre AS nombreAmbito, ambitos.descripcion AS descripcionAmbito " +
            "FROM sesiones INNER JOIN ambitos ON sesiones.ambitoId = ambitos.id ORDER BY inicioProgramado DESC")
    List<SesionDetalle> listar();
    @Query("SELECT sesiones.*, ambitos.nombre AS nombreAmbito, ambitos.descripcion AS descripcionAmbito " +
            "FROM sesiones INNER JOIN ambitos ON sesiones.ambitoId = ambitos.id WHERE sesiones.id = :id")
    SesionDetalle detalle(long id);
    @Query("SELECT * FROM sesiones WHERE estado NOT IN ('Completada', 'Cancelada')")
    List<Sesion> pendientes();
    @Query("SELECT * FROM sesiones WHERE estado = 'En curso'")
    List<Sesion> enCurso();
    @Query("SELECT COUNT(*) FROM sesiones WHERE id != :id AND estado IN ('En curso', 'Pausada')")
    int otrasEnProceso(long id);
}
