package pe.edu.udh.estudiopomodoro.model;

import androidx.room.Embedded;

public class SesionDetalle {
    @Embedded public Sesion sesion;
    public String nombreAmbito;
    public String descripcionAmbito;
}
