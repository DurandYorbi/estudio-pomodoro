package pe.edu.udh.estudiopomodoro.model;

import android.content.Context;
import pe.edu.udh.estudiopomodoro.model.negocio.ResumenEstudio;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import pe.edu.udh.estudiopomodoro.model.negocio.LogicaTemporizador;
import pe.edu.udh.estudiopomodoro.model.negocio.ReglasEstudio;
import pe.edu.udh.estudiopomodoro.model.negocio.ReglaException;

public class Repositorio {
    private static volatile Repositorio instancia;
    public final ExecutorService io = Executors.newSingleThreadExecutor();
    private final AppDatabase db;

    private Repositorio(Context context) { db = AppDatabase.getInstance(context); }

    public static Repositorio getInstance(Context context) {
        if (instancia == null) {
            synchronized (Repositorio.class) {
                if (instancia == null) instancia = new Repositorio(context.getApplicationContext());
            }
        }
        return instancia;
    }

    public List<Ambito> ambitos() { return db.ambitoDao().listar(); }
    public List<Ambito> activos() { return db.ambitoDao().activos(); }
    public Ambito ambito(long id) {
        Ambito a = db.ambitoDao().buscar(id);
        if (a == null) throw new ReglaException("no_existe");
        return a;
    }

    public long guardarAmbito(Ambito a) {
        return db.runInTransaction(() -> {
            a.nombre = ReglasEstudio.limpiar(a.nombre);
            a.descripcion = a.descripcion.trim();
            a.claveNombre = a.nombre.toLowerCase(Locale.ROOT);
            ReglasEstudio.validarAmbito(a);
            if (db.ambitoDao().repetidos(a.claveNombre, a.id) > 0) throw new ReglaException("repetido");
            if (a.id == 0) return db.ambitoDao().insertar(a);
            ambito(a.id);
            db.ambitoDao().actualizar(a);
            return a.id;
        });
    }

    public void eliminarAmbito(long id) {
        db.runInTransaction(() -> {
            if (db.ambitoDao().contarSesiones(id) > 0) throw new ReglaException("con_sesiones");
            db.ambitoDao().eliminar(ambito(id));
        });
    }

    private Sesion buscarSesion(long id) {
        Sesion s = db.sesionDao().buscar(id);
        if (s == null) throw new ReglaException("no_existe");
        return s;
    }

    private void sincronizar() {
        db.runInTransaction(() -> {
            long ahora = System.currentTimeMillis();
            for (Sesion s : db.sesionDao().enCurso()) {
                if (LogicaTemporizador.restante(s, ahora) == 0) {
                    LogicaTemporizador.actualizar(s, ahora);
                    db.sesionDao().actualizar(s);
                }
            }
        });
    }

    public List<SesionDetalle> sesiones() { sincronizar(); return db.sesionDao().listar(); }
    public SesionDetalle detalle(long id) {
        sincronizar();
        SesionDetalle d = db.sesionDao().detalle(id);
        if (d == null) throw new ReglaException("no_existe");
        return d;
    }

    public long guardarSesion(Sesion s) {
        return db.runInTransaction(() -> {
            if (s.id != 0 && !ReglasEstudio.editable(buscarSesion(s.id))) throw new ReglaException("solo_planificada");
            s.tarea = ReglasEstudio.limpiar(s.tarea);
            ReglasEstudio.validarSesion(s, db.ambitoDao().buscar(s.ambitoId));
            for (Sesion otra : db.sesionDao().pendientes()) {
                if (otra.id != s.id && ReglasEstudio.seCruzan(s, otra)) throw new ReglaException("cruce");
            }
            s.restanteMs = s.duracion * 60000L;
            if (s.id == 0) return db.sesionDao().insertar(s);
            db.sesionDao().actualizar(s);
            return s.id;
        });
    }

    public void eliminarSesion(long id) {
        db.runInTransaction(() -> {
            Sesion s = buscarSesion(id);
            if (ReglasEstudio.EN_CURSO.equals(s.estado) || ReglasEstudio.PAUSADA.equals(s.estado)) {
                throw new ReglaException("sesion_activa");
            }
            db.sesionDao().eliminar(s);
        });
    }

    public SesionDetalle cambiarTemporizador(long id, String accion) {
        sincronizar();
        db.runInTransaction(() -> {
            Sesion s = buscarSesion(id);
            long ahora = System.currentTimeMillis();
            if (accion.equals("iniciar")) {
                if (db.sesionDao().otrasEnProceso(id) > 0) throw new ReglaException("otra_activa");
                if (!ambito(s.ambitoId).activo && s.minutosRealizados == 0) throw new ReglaException("inactivo");
                LogicaTemporizador.iniciar(s, ahora);
            } else if (accion.equals("pausar")) {
                LogicaTemporizador.pausar(s, ahora);
            } else if (accion.equals("cancelar")) {
                LogicaTemporizador.cancelar(s);
            }
            db.sesionDao().actualizar(s);
        });
        return db.sesionDao().detalle(id);
    }

    public ResumenEstudio resumen() {
        sincronizar();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if (c.getTimeInMillis() > System.currentTimeMillis()) c.add(Calendar.WEEK_OF_YEAR, -1);
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
        return new ResumenEstudio(db.ambitoDao().resumen(c.getTimeInMillis(), System.currentTimeMillis()));
    }
}
