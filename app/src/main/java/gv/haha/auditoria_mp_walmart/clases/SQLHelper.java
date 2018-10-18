package gv.haha.auditoria_mp_walmart.clases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_CAT_INDICADORES;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_EVAL_DISPLAY_DET;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_EVAL_DISPLAY_ENC;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_FOTO_INDCADOR;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_NOMBRE_DISPLAY;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_PUNTOSDEVENTA;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_DETALLE;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_ENCABEZADO;

public class SQLHelper extends SQLiteOpenHelper {

    String TBLPDV = "CREATE TABLE IF NOT EXISTS " + TBL_PUNTOSDEVENTA + " (" +
            "IdPdV INTEGER PRIMARY KEY, " +
            "NombrePdV text, " +
            "LocationGPS text, " +
            "IdDepto text)";

    String TBLRPTENCAB = "CREATE TABLE IF NOT EXISTS " + TBL_REPORTE_ENCABEZADO + " (" +
            "IdRptEnc INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "NombrePDV VARCHAR(50), " +
            "FechaVisita DATE, " +
            "ItemsTienda DECIMAL(10,2), " +
            "ItemsAgotados DECIMAL(10,2), " +
            "Participacion DECIMAL(10,2), " +
            "HoraVisita VARCHAR(20), " +
            "ResponsableTurno VARCHAR(50), " +
            "Observaciones TEXT, " +
            "FechaRegistro TIMESTAMP, " +
            "TieneFirmaMP INTEGER, " +
            "TieneFirmaPDV INTEGER, " +
            "EstadoTerminado INTEGER, " +
            "EstadoEnviado INTEGER, " +
            "idEnviado INTEGER)";

    String TBLRPTDETALLE = "CREATE TABLE IF NOT EXISTS " + TBL_REPORTE_DETALLE + " (" +
            "IdRptDet INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "idRptEnc INTEGER, " +
            "idIndicador INTEGER, " +
            "descIndicador VARCHAR(50), " +
            "cantAplicado DECIMAL(10,2), " +
            "cantPendiente DECIMAL(10,2), " +
            "comentarios TEXT, " +
            "EstadoEnviado integer, " +
            "idEnviado integer)";

    String TBLFOTOINDICADOR = "CREATE TABLE IF NOT EXISTS " + TBL_FOTO_INDCADOR + " (" +
            "Idfoto INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "idRptEnc INTEGER, " +
            "idRptDet INTEGER, " +
            "idIndicad INTEGER, " +
            "pathFoto VARCHAR(100), " +
            "FechaToma TIMESTAMP, " +
            "EstadoEnviado integer, " +
            "idEnviado integer)";

    String TBLCATINDICAD = "CREATE TABLE IF NOT EXISTS " + TBL_CAT_INDICADORES + "( " +
            "idIndic INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "descIndicador VARCHAR(50))";

    String TBLEVALDISPLAYENC = "CREATE TABLE IF NOT EXISTS " + TBL_EVAL_DISPLAY_ENC + "( " +
            "idEvalDispEnc INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "NombreDislay VARCHAR(50), " +
            "IdDislay INTEGER, " +
            "idPdv INTEGER, " +
            "NombrePDV VARCHAR(50), " +
            "FechRegAno INTEGER, " +
            "FechRegMes INTEGER, " +
            "FechRegDia INTEGER, " +
            "FechReg TIMESTAMP, " +
            "GPSCoordenadas NVARCHAR(20), " +
            "EstadoEnviado integer, " +
            "idEnviado integer)";

    String TBLEVALDISPLAYDET = "CREATE TABLE IF NOT EXISTS " + TBL_EVAL_DISPLAY_DET + "( " +
            "idEvalDispDet INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "idEDE INTEGER, " +
            "idIndic integer, " +
            "descIndicador nvarchar(50), " +
            "puntaje integer, " +
            "EstadoEnviado integer, " +
            "idEnviado integer)";

    String TBLNOMBREDISPLAY = "CREATE TABLE IF NOT EXISTS " + TBL_NOMBRE_DISPLAY + "( " +
            "idNombDispl INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "Nombre VARCHAR(50), " +
            "EstadoEnviado integer, " +
            "idEnviado integer)";


    public SQLHelper(Context context) {
        super(context, "WalmartMP.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(TBLPDV);
        sqLiteDatabase.execSQL(TBLRPTENCAB);
        sqLiteDatabase.execSQL(TBLRPTDETALLE);
        sqLiteDatabase.execSQL(TBLFOTOINDICADOR);
        sqLiteDatabase.execSQL(TBLCATINDICAD);
        sqLiteDatabase.execSQL(TBLNOMBREDISPLAY);
        sqLiteDatabase.execSQL(TBLEVALDISPLAYENC);
        sqLiteDatabase.execSQL(TBLEVALDISPLAYDET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
