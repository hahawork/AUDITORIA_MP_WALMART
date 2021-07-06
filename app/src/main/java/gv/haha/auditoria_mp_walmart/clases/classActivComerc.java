package gv.haha.auditoria_mp_walmart.clases;

public class classActivComerc {
    private String iddata, Llave, Formato, Item, Presentacion, Nomenclatura, Espacio, Area, NumTienda, NombTienda, Inventario, FechaSubido, Comentario;

    public classActivComerc(String iddata, String llave, String formato, String item, String presentacion, String nomenclatura, String espacio, String area, String numTienda, String nombTienda, String inventario, String fechaSubido, String comentario) {
        this.iddata = iddata;
        Llave = llave;
        Formato = formato;
        Item = item;
        Presentacion = presentacion;
        Nomenclatura = nomenclatura;
        Espacio = espacio;
        Area = area;
        NumTienda = numTienda;
        NombTienda = nombTienda;
        Inventario = inventario;
        FechaSubido = fechaSubido;
        Comentario = comentario;
    }

    public String getIddata() {
        return iddata;
    }

    public String getLlave() {
        return Llave;
    }

    public String getFormato() {
        return Formato;
    }

    public String getItem() {
        return Item;
    }

    public String getPresentacion() {
        return Presentacion;
    }

    public String getNomenclatura() {
        return Nomenclatura;
    }

    public String getEspacio() {
        return Espacio;
    }

    public String getArea() {
        return Area;
    }

    public String getNumTienda() {
        return NumTienda;
    }

    public String getNombTienda() {
        return NombTienda;
    }

    public String getInventario() {
        return Inventario;
    }

    public String getFechaSubido() {
        return FechaSubido;
    }

    public String getComentario() {
        return Comentario;
    }

    public void setComentario(String comentario) {
        Comentario = comentario;
    }
}
