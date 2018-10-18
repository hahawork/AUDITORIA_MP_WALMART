package gv.haha.auditoria_mp_walmart.clases;

import java.util.ArrayList;
import java.util.List;

public class DisplayList {
    private int idRegistro;
    private String NombreDisplay;
    private String Fecha;
    private int IdDBOnline;


    public DisplayList(int idRegistro, String nombreDisplay, String fecha, int idDBOnline) {
        this.idRegistro = idRegistro;
        NombreDisplay = nombreDisplay;
        Fecha = fecha;
        IdDBOnline = idDBOnline;
    }

    public DisplayList(int idRegistro, String nombreDisplay, String fecha) {
        this.idRegistro = idRegistro;
        NombreDisplay = nombreDisplay;
        Fecha = fecha;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getNombreDisplay() {
        return NombreDisplay;
    }

    public void setNombreDisplay(String nombreDisplay) {
        NombreDisplay = nombreDisplay;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public int getIdDBOnline() {
        return IdDBOnline;
    }

    public void setIdDBOnline(int idDBOnline) {
        IdDBOnline = idDBOnline;
    }
}
