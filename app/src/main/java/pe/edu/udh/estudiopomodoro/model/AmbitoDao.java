package pe.edu.udh.estudiopomodoro.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import java.util.List;

@Dao
public interface AmbitoDao {
    @Insert long insertar(Ambito ambito);
    @Update int actualizar(Ambito ambito);
    @Delete int eliminar(Ambito ambito);
    @Query("SELECT * FROM ambitos ORDER BY nombre COLLATE NOCASE")
    List<Ambito> listar();
    @Query("SELECT * FROM ambitos WHERE activo = 1 ORDER BY nombre COLLATE NOCASE")
    List<Ambito> activos();
    @Query("SELECT * FROM ambitos WHERE id = :id")
    Ambito buscar(long id);
    @Query("SELECT COUNT(*) FROM ambitos WHERE claveNombre = :clave AND id != :id")
    int repetidos(String clave, long id);
    @Query("SELECT COUNT(*) FROM sesiones WHERE ambitoId = :id")
    int contarSesiones(long id);
    @Query("SELECT a.nombre, a.metaSemanal, SUM(CASE WHEN s.minutosRealizados > 0 THEN 1 ELSE 0 END) AS sesiones, " +
            "COALESCE(SUM(s.minutosRealizados), 0) AS minutos, " +
            "COALESCE(SUM(CASE WHEN s.completadaEn >= :desde AND s.completadaEn <= :hasta THEN s.minutosRealizados ELSE 0 END), 0) AS minutosSemana " +
            "FROM ambitos a LEFT JOIN sesiones s ON a.id = s.ambitoId GROUP BY a.id ORDER BY a.nombre")
    List<ResumenAmbito> resumen(long desde, long hasta);
}
