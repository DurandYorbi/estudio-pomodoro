package pe.edu.udh.estudiopomodoro.model;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Ambito.class, Sesion.class}, version = 1, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instancia;
    public abstract AmbitoDao ambitoDao();
    public abstract SesionDao sesionDao();

    public static AppDatabase getInstance(Context context) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "estudio_pomodoro.db").build();
                }
            }
        }
        return instancia;
    }
}
