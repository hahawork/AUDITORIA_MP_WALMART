package gv.haha.auditoria_mp_walmart.clases;

public class ContenidoDisplay {

    private int IdIndicador, Puntaje,idGuardadoLocalDB;
    private String DescIndicador;

    public ContenidoDisplay(int idIndicador, int puntaje, String descIndicador) {
        IdIndicador = idIndicador;
        Puntaje = puntaje;
        DescIndicador = descIndicador;
    }

    public int getIdIndicador() {
        return IdIndicador;
    }

    public void setIdIndicador(int idIndicador) {
        IdIndicador = idIndicador;
    }

    public int getPuntaje() {
        return Puntaje;
    }

    public void setPuntaje(int puntaje) {
        Puntaje = puntaje;
    }

    public String getDescIndicador() {
        return DescIndicador;
    }

    public void setDescIndicador(String descIndicador) {
        DescIndicador = descIndicador;
    }

    public int getIdGuardadoLocalDB() {
        return idGuardadoLocalDB;
    }

    public void setIdGuardadoLocalDB(int idGuardadoLocalDB) {
        this.idGuardadoLocalDB = idGuardadoLocalDB;
    }
}
