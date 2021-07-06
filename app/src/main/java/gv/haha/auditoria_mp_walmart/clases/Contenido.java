package gv.haha.auditoria_mp_walmart.clases;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Contenido {

    private int NumIndicador, idGuardadoLocaldb;
    private String Indicador, Comentario;
    private int Aplicado, Pendiente;
    private boolean PermiteFoto;

    public Contenido(int numIndicador, String indicador) {
        NumIndicador = numIndicador;
        Indicador = indicador;
    }

    public Contenido(int numIndicador, String indicador, boolean permiteFoto) {
        NumIndicador = numIndicador;
        Indicador = indicador;
        PermiteFoto = permiteFoto;
    }

    public int getNumIndicador() {
        return NumIndicador;
    }

    public String getIndicador() {
        return Indicador;
    }

    public String getComentario() {
        return Comentario;
    }

    public float getAplicado() {
        return Aplicado;
    }

    public float getPendiente() {
        return Pendiente;
    }

    public void setNumIndicador(int numIndicador) {
        NumIndicador = numIndicador;
    }

    public void setIndicador(String indicador) {
        Indicador = indicador;
    }

    public void setComentario(String comentario) {
        Comentario = comentario;
    }

    public void setAplicado(int aplicado) {
        Aplicado = aplicado;
    }

    public void setPendiente(int pendiente) {
        Pendiente = pendiente;
    }

    public int getIdGuardadoLocaldb() {
        return idGuardadoLocaldb;
    }

    public void setIdGuardadoLocaldb(int idGuardadoLocaldb) {
        this.idGuardadoLocaldb = idGuardadoLocaldb;
    }

    public boolean isPermiteFoto() {
        return PermiteFoto;
    }

    public void setPermiteFoto(boolean permiteFoto) {
        PermiteFoto = permiteFoto;
    }
}
