package gv.haha.auditoria_mp_walmart.clases;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static gv.haha.auditoria_mp_walmart.clases.Variables.CARPETA_RECURSOS;
import static gv.haha.auditoria_mp_walmart.clases.Variables.SETT_FECHA_ULTIM_BK_DB_CSV;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_PUNTOSDEVENTA;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_ENCABEZADO;

public class BaseDatos {

    SQLiteDatabase db;
    static Context mcontext;

    public BaseDatos(Context context) {

        mcontext = context;
    }

    public Long insertarRegistro(String nombretabla, ContentValues values) {
        try {
            db = new SQLHelper(mcontext).getWritableDatabase();
            Long result = db.insert(nombretabla, null, values);
            db.close();

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return -1l;
        }
    }

    public int actualizarRegistro(String nombreTabla, ContentValues values, String where) {
        try {
            db = new SQLHelper(mcontext).getWritableDatabase();
            int updated = db.update(nombreTabla, values, where, null);
            db.close();
            return updated;


        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Cursor obtenerRegistro(String nombreTabla) {

        Cursor cMax = null;
        try {
            db = new SQLHelper(mcontext).getWritableDatabase();
            cMax = db.rawQuery("SELECT * FROM " + nombreTabla, null);

            //db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cMax;
    }

    public Cursor obtenerMaxRegistro(String nombreTabla, String Campomax) {

        Cursor cMax = null;
        try {
            db = new SQLHelper(mcontext).getWritableDatabase();
            String sql = "SELECT * FROM " + nombreTabla + " WHERE " + Campomax + " = (SELECT max(" + Campomax + ") FROM " + nombreTabla + ")";
            cMax = db.rawQuery(sql, null);

            // db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cMax;
    }

    public Cursor obtenerRegistroWhereArgs(String nombreTabla, String whereColumns) {

        /*
        String[] tableColumns = new String[] {
            "column1",
            "(SELECT max(column1) FROM table2) AS max"
        };
        String whereClause = "column1 = ? OR column1 = ?";
        String[] whereArgs = new String[] {
            "value1",
            "value2"
        };
        String orderBy = "column1";
        Cursor c = sqLiteDatabase.query("table1", tableColumns, whereClause, whereArgs,
        null, null, orderBy);
        ///////////////////////////OTRO EJEMPLO////////////////////////////////
        String table = "table2";
        String[] columns = {"column1", "column3"};
        String selection = "column3 =?";
        String[] selectionArgs = {"apple"};
        String groupBy = null;
        String having = null;
        String orderBy = "column3 DESC";
        String limit = "10";

        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        ///////////////////////////////////////////////////////////
         */
        Cursor cursor = null;
        try {
            db = new SQLHelper(mcontext).getWritableDatabase();
            cursor = db.query(nombreTabla, null, whereColumns, null, null, null, null);

            //db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;

    }

    public void BorrarRegistro(String nombreTabla) {
        try {
            db = new SQLHelper(mcontext).getWritableDatabase();
            String sql = "DELETE FROM " + nombreTabla;
            db.execSQL(sql);

            //db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void BorrarRegistroWhere(String nombreTabla, String where) {
        try {
            db = new SQLHelper(mcontext).getWritableDatabase();
            String sql = "DELETE FROM " + nombreTabla + " WHERE " + where;
            db.execSQL(sql);
            //db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifica si ya existe un campo en la tabla en la base de datos
     *
     * @param Tabla   Nombre de la tabla en la que se va verificar
     * @param Columna Nombre de la columna a verificar
     * @return Verdadero si ya existe, Falso si no exciste el campo
     */
    public boolean ExisteColumna(String Tabla, String Columna) {

        boolean Existe = false;
        try {
            db = new SQLHelper(mcontext).getWritableDatabase();
            Cursor cVerificaColumna = db.rawQuery("select * from " + Tabla + " where 0", null);

            String[] ColumnasExis = cVerificaColumna.getColumnNames();
            if (ColumnasExis.length > 0) {
                for (int i = 0; i < ColumnasExis.length; i++) {
                    if (ColumnasExis[i].equalsIgnoreCase(Columna)) {
                        Existe = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return Existe;
    }

    public String[] getArrayDataFromTable(String tabla, String campo) {

        try {

            db = new SQLHelper(mcontext).getWritableDatabase();
            Cursor cursor = db.rawQuery("select distinct " + campo + " from " + tabla, null);

            String[] array = new String[cursor.getCount()];

            if (cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    array[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(campo));
                }

            }
            db.close();
            return array;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String[] getArrayDispayFromList(List<DisplayList> arr) {

        try {


            String[] array = new String[arr.size()];

            if (arr.size() > 0) {
                for (int i = 0; i < arr.size(); i++) {

                    array[i] = arr.get(i).getNombreDisplay();
                }

            }
            db.close();
            return array;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Agrega una columna a una tabla en la base de datos
     *
     * @param Tabla      Nombre de la tabla donde se va a agregsr el nuevo campo
     * @param newColumna El nommbre de la columna nueva
     * @param TipoDato   el tipo de dato del campo.
     */
    public void AgregarColumna(String Tabla, String newColumna, String TipoDato) {
        try {
            db = new SQLHelper(mcontext).getWritableDatabase();
            db.execSQL("ALTER TABLE " + Tabla + " ADD COLUMN " + newColumna + " " + TipoDato);
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Agrega una tabla a la base de datos
     *
     * @param queryTabla Consulta de creacion de la tabla
     */
    public void AgregarTabla(String queryTabla) {
        try {
            db = new SQLHelper(mcontext).getWritableDatabase();
            db.execSQL(queryTabla);
            db.close();
        } catch (Exception e) {

        }
    }

    public void exportDB2CSV() {

        try {

            ArrayList<String> arrTblNames = new ArrayList<String>();

            //obtiene todas las tablas de la base de datos
            Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

            if (c.moveToFirst()) { // se desplaza al primer registro
                while (!c.isAfterLast()) {// mientras no sea el ultimo registro

                    //guarda en nombre de la tabla en el arreglo
                    arrTblNames.add(c.getString(c.getColumnIndex("name")));
                    c.moveToNext(); //se mueve el cursor al siguiente registro
                }
            }

            File exportDir = new File(CARPETA_RECURSOS, "");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file = new File(exportDir, "Respaldo_db_audit_mp_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv");

            //si no existe el archivo de respaldo
            if (!file.exists()) {
                file.createNewFile();

                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

                //ciclo para recorrer el arreglo de las tablas
                for (int i = 0; i < arrTblNames.size(); i++) {

                    //esto es para no respaldar las tablas no importantes.
                    if (arrTblNames.get(i).equalsIgnoreCase(TBL_PUNTOSDEVENTA) ||
                            arrTblNames.get(i).equalsIgnoreCase("sqlite_sequence") ||
                            arrTblNames.get(i).equalsIgnoreCase("android_metadata"))

                        continue;

                    //se hace la consulta a la tabla
                    Cursor cursor = db.rawQuery("select * from " + arrTblNames.get(i), null);

                    //obtiene los campos de la tabla
                    String[] columnNames = cursor.getColumnNames();


                    if (cursor.moveToFirst()) {
                        //escribe en el archivo el nombre de la tabla
                        csvWrite.writeNext(new String[]{"", arrTblNames.get(i)});
                        //escribe en el archivo el nombre de las columnas
                        csvWrite.writeNext(columnNames);

                        //ciclo para recorrer el cursos con los datos de la tabla
                        while (!cursor.isAfterLast()) {

                            //se hace un arreglo con la cantidad de campos de la tabla
                            String[] strdata = new String[columnNames.length];


                            //recorre la fila actual del cursor
                            for (int j = 0; j < columnNames.length; j++) {
                                //guarda en el arreglo los datos de cada campo
                                strdata[j] = cursor.getString(cursor.getColumnIndex(columnNames[j]));

                            }
                            //escribe en el archivo la linea completa con los datos del registro
                            csvWrite.writeNext(strdata);

                            cursor.moveToNext();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
