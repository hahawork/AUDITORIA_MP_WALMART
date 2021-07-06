package gv.haha.auditoria_mp_walmart.clases;

public class classPdv {
    private int Id;
    private String Nombre;

    public classPdv(int id, String nombre) {
        Id = id;
        Nombre = nombre;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }
}
