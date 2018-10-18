package gv.haha.auditoria_mp_walmart.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gv.haha.auditoria_mp_walmart.DescargarActualizacionWebView;
import gv.haha.auditoria_mp_walmart.R;

import static gv.haha.auditoria_mp_walmart.clases.Variables.CARPETA_RECURSOS;
import static gv.haha.auditoria_mp_walmart.clases.Variables.SETT_COD_USUARIO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_EVAL_DISPLAY_DET;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_EVAL_DISPLAY_ENC;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_FOTO_INDCADOR;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_NOMBRE_DISPLAY;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_PUNTOSDEVENTA;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_DETALLE;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_ENCABEZADO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_GUARDA_EVALDISPLAY_DET;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_GUARDA_EVALDISPLAY_ENC;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_GUARDA_REVISIONPDV_DET;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_GUARDA_REVISIONPDV_ENC;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_GUARDA_REVISIONPDV_FOTOS;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_OBTENER_NOMBRES_DISPLAY;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_VERIFICA_NUEVA_VERSION;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_VERIFICA_PUNTOS_NUEVOS;

public class Globales {


    private static Context mContext;
    SharedPreferences setting;
    BaseDatos baseDatos;

    public Globales(Context context) {
        mContext = context;
        setting = PreferenceManager.getDefaultSharedPreferences(mContext);
        baseDatos = new BaseDatos(mContext);
    }

    public float getVersionApp() {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            String version = pInfo.versionName;
            //int versionCode = android.support.design.BuildConfig.VERSION_CODE;
            // String versionName = android.support.design.BuildConfig.VERSION_NAME;
            return Float.parseFloat(version);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean TieneConexion() {
        boolean bConectado = false;
        try {
            ConnectivityManager connec = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] redes = connec.getAllNetworkInfo();
            for (int i = 0; i < 2; i++) {
                if (redes[i].getState() == NetworkInfo.State.CONNECTED && redes[i].isConnected() && redes[i].isAvailable()) {
                    bConectado = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bConectado;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public void VerificarNuevosPuntosguardados(final boolean mostrarinfo, final int idPdVActual) {

        class VerificarNuevosPuntosguardados extends AsyncTask<String, String, JSONObject> {

            JSONParser jsonParser = new JSONParser();
            ProgressDialog pDialog = new ProgressDialog(mContext);

            SQLiteDatabase db = new SQLHelper(mContext).getWritableDatabase();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if (mostrarinfo) {
                    pDialog = new ProgressDialog(mContext);
                    pDialog.setTitle("Recuperando los puntos de venta. Espere...");
                    pDialog.setIndeterminate(true);
                    pDialog.setCancelable(true);
                    pDialog.setCanceledOnTouchOutside(false);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            VerificarNuevosPuntosguardados.this.cancel(true);
                            new AlertDialog.Builder(mContext).setMessage("Se ha cancelado la petición.").setIcon(R.drawable.ic_info).show();
                        }
                    });
                    pDialog.show();
                }
            }

            @Override
            protected JSONObject doInBackground(String... strings) {

                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idPdVActual", String.valueOf(idPdVActual)));

                // getting JSON Object
                // Note that create product url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(URL_WS_VERIFICA_PUNTOS_NUEVOS, "POST", params);
                if (json != null) {
                    try {
                        int success = json.getInt("success");

                        if (success == 1) {     //Descargado correctamente

                            if (idPdVActual == 0) {
                                //limpia la tabla de la base de datos
                                db.delete(TBL_PUNTOSDEVENTA, null, null);
                            }

                            JSONArray PdV = json.getJSONArray("pdv");

                            for (int i = 0; i < PdV.length(); i++) {        // PARA CADA PUNTO DE VENTA REGISTRADO

                                JSONObject c = PdV.getJSONObject(i);

                                if (mostrarinfo && pDialog.isShowing())
                                    publishProgress("" + i, "" + PdV.length(), c.getString("NombrePdV"));

                                ContentValues values = new ContentValues();
                                values.put("IdPdV", c.getString("IdPdV"));
                                values.put("NombrePdV", c.getString("NombrePdV"));
                                values.put("LocationGPS", c.getString("LocationGPS"));
                                values.put("IdDepto", c.getString("Departamento"));
                                db.insert(TBL_PUNTOSDEVENTA, null, values); // se inserta en la base de datos local

                                //Log.w("id " + c.getString("IdPdV"), "Nombre: " + c.getString("NombrePdV"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return json;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                int setMax = Integer.parseInt(values[1]);
                int setpregress = Integer.parseInt(values[0]);
                pDialog.setMax(setMax);
                pDialog.setProgress(setpregress);
                pDialog.setMessage(values[0] + " de " + values[1] + " \n " + values[2]);
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                try {

                    if (jsonObject == null) {
                        if (mostrarinfo && pDialog.isShowing()) {
                            new AlertDialog.Builder(mContext).setTitle("Error al Obtener.").setMessage("No se ha podido actualizar clientes nuevos, pueda que no tengas internet.").setIcon(R.drawable.ic_error).show();
                            pDialog.dismiss();
                        }
                    } else {
                        int success = jsonObject.getInt("success");
                        String message = jsonObject.getString("message");
                        if (success == 1) {

                            if (mostrarinfo && pDialog.isShowing()) {
                                pDialog.dismiss();

                                new classCustomToast(((Activity) mContext)).Toast(message, R.drawable.ic_success);
                                ((Activity) mContext).recreate();
                            }
                        }
                    }

                } catch (JSONException e) {
                    if (mostrarinfo && pDialog.isShowing())
                        pDialog.dismiss();

                    e.printStackTrace();
                }
            }
        }

        new VerificarNuevosPuntosguardados().execute();
    }

    public void wsObtnerNombresDisplayOnline(final int idLocalActual) {

        try {
            StringRequest jsonRequest = new StringRequest(Request.Method.POST, URL_WS_OBTENER_NOMBRES_DISPLAY,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                Log.d("Consulta nomb display", response);

                                int success = jsonObject.getInt("success");

                                String message = jsonObject.getString("message");

                                if (success == 1) {
                                    JSONArray display = jsonObject.getJSONArray("nombres");

                                    for (int i = 0; i < display.length(); i++) {        // PARA CADA display REGISTRADO

                                        JSONObject c = display.getJSONObject(i);

                                        //se verifica si el nombre ya existe en la base de datos
                                        Cursor cursor = baseDatos.obtenerRegistroWhereArgs(
                                                TBL_NOMBRE_DISPLAY,
                                                String.format("Nombre = '%s' or idEnviado = '%s'",
                                                        c.getString("Nombre"),
                                                        c.getString("idNombDispl")
                                                ));
                                        // si hay ceros registro con ese nombre o ese id en la db local
                                        if (cursor.getCount() == 0) {
                                            ContentValues values = new ContentValues();
                                            values.put("idNombDispl", (byte[]) null);
                                            values.put("Nombre", c.getString("Nombre"));
                                            values.put("EstadoEnviado", 1);
                                            values.put("idEnviado", c.getString("idNombDispl"));
                                            //se inserta el nombre en la db local.
                                            baseDatos.insertarRegistro(TBL_NOMBRE_DISPLAY, values); // se inserta en la base de datos local
                                        }
                                        //Log.w("id " + c.getString("IdPdV"), "Nombre: " + c.getString("NombrePdV"));
                                    }

                                } else {
                                    //new classCustomToast((Activity) mContext).Toast(message, R.drawable.ic_success);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Error en la consulta al ws: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("idDisplayActual", String.valueOf(idLocalActual));
                        parameters.put("idUsuario",String.valueOf(setting.getInt(SETT_COD_USUARIO, 0)));

                    return parameters;
                }
            };
            Volley.newRequestQueue(mContext).add(jsonRequest);
            Volley.newRequestQueue(mContext).addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                @Override
                public void onRequestFinished(Request<String> request) {
                    //if (progressDialog !=  null && progressDialog.isShowing())
                    //progressDialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * este metodo es para obtener los datos del desde el web service
     *
     * @param params estos son loss parametros para enviar al web service
     */
    public void webServicePost(String URL_WS, final List<classWebService> params, final int option) {

        final AsyncTaskComplete mCallBack = (AsyncTaskComplete) mContext;
        //parametros para enviar al web service
        new classCustomToast((Activity) mContext).Toast("Sincronizando los datos, espere...",R.drawable.ic_info);

        try {
            StringRequest jsonRequest = new StringRequest(Request.Method.POST, URL_WS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject jObj = null;
                            try {
                                jObj = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mCallBack.onAsyncTaskComplete(jObj, option);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    new classCustomToast((Activity) mContext).Show_ToastError("Error en la consulta al ws: " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parameters = new HashMap<String, String>();
                    for (int i = 0; i < params.size(); i++)
                        parameters.put(params.get(i).getParametro(), params.get(i).getValor());

                    return parameters;
                }
            };
            Volley.newRequestQueue(mContext).add(jsonRequest);
            Volley.newRequestQueue(mContext).addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                @Override
                public void onRequestFinished(Request<String> request) {
                    //if (progressDialog !=  null && progressDialog.isShowing())
                    //progressDialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void enviarRevisionPdvEncabezado() {

        try {

            Cursor cursor = baseDatos.obtenerRegistroWhereArgs(TBL_REPORTE_ENCABEZADO, "EstadoEnviado = 0 and EstadoTerminado = 1");
            if (cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    List<classWebService> params = new ArrayList<>();
                    params.add(new classWebService("IdPdv", cursor.getString(cursor.getColumnIndex("NombrePDV"))));
                    params.add(new classWebService("FechaVisita", cursor.getString(cursor.getColumnIndex("FechaRegistro"))));
                    params.add(new classWebService("ItemsTienda", cursor.getString(cursor.getColumnIndex("ItemsTienda"))));
                    params.add(new classWebService("ItemsAgotados", cursor.getString(cursor.getColumnIndex("ItemsAgotados"))));
                    params.add(new classWebService("Participacion", cursor.getString(cursor.getColumnIndex("Participacion"))));
                    params.add(new classWebService("HoraVisita", cursor.getString(cursor.getColumnIndex("HoraVisita"))));
                    params.add(new classWebService("ResponsableTurno", cursor.getString(cursor.getColumnIndex("ResponsableTurno"))));
                    params.add(new classWebService("Observaciones", cursor.getString(cursor.getColumnIndex("Observaciones"))));
                    params.add(new classWebService("IdUsuario", String.valueOf(setting.getInt(SETT_COD_USUARIO, 0))));

                    //se ejecuta l web service para enviar el detalle ala nube
                    webServiceGuardarRevisionPdvEnc(
                            cursor.getInt(cursor.getColumnIndex("IdRptEnc")),
                            params
                    );
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * este metodo es para enviar los datos al web service
     *
     * @param IdRptEncLocalDB Estoes para actualizar el registro de estado enviado
     * @param params          estos son loss parametros para enviar al web service
     */
    public void webServiceGuardarRevisionPdvEnc(final int IdRptEncLocalDB, final List<classWebService> params) {

        try {
            StringRequest jsonRequest = new StringRequest(Request.Method.POST, URL_WS_GUARDA_REVISIONPDV_ENC,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                Log.d("Consulta env enc rPdv", response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");

                                if (success == 1) {
                                    int idInsertado = jsonObject.getInt("idInsertado");
                                    ContentValues values = new ContentValues();
                                    values.put("EstadoEnviado", 1);
                                    values.put("idEnviado", idInsertado);
                                    baseDatos.actualizarRegistro(TBL_REPORTE_ENCABEZADO, values, "IdRptEnc = " + IdRptEncLocalDB);

                                    //despues de enviar el encabezado se procede a enviar el detalle del reporte
                                    enviarRevisionPdvDetalles(IdRptEncLocalDB, idInsertado);

                                } else {
                                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Error en la consulta al ws: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parameters = new HashMap<String, String>();
                    for (int i = 0; i < params.size(); i++)
                        parameters.put(params.get(i).getParametro(), params.get(i).getValor());

                    return parameters;
                }
            };
            Volley.newRequestQueue(mContext).add(jsonRequest);
            Volley.newRequestQueue(mContext).addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                @Override
                public void onRequestFinished(Request<String> request) {
                    //if (progressDialog !=  null && progressDialog.isShowing())
                    //progressDialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enviarRevisionPdvDetalles(int idLocalDB, int idOnlineEncDB) {
        try {

            Cursor cursor = baseDatos.obtenerRegistroWhereArgs(TBL_REPORTE_DETALLE, "idRptEnc = " + idLocalDB);
            if (cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    List<classWebService> params = new ArrayList<>();
                    params.add(new classWebService("idRptEnc", String.valueOf(idOnlineEncDB)));
                    params.add(new classWebService("idIndicador", cursor.getString(cursor.getColumnIndex("idIndicador"))));
                    params.add(new classWebService("descIndicador", cursor.getString(cursor.getColumnIndex("descIndicador"))));
                    params.add(new classWebService("cantAplicado", cursor.getString(cursor.getColumnIndex("cantAplicado"))));
                    params.add(new classWebService("cantPendiente", cursor.getString(cursor.getColumnIndex("cantPendiente"))));
                    params.add(new classWebService("comentarios", cursor.getString(cursor.getColumnIndex("comentarios"))));

                    //se ejecuta l web service para enviar el detalle ala nube
                    webServiceGuardarRevisionPdvDet(
                            idLocalDB,
                            idOnlineEncDB,
                            cursor.getInt(cursor.getColumnIndex("IdRptDet")),
                            params
                    );

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * * este metodo es para enviar los datos al web service
     *
     * @param IdRptDetLocalDB Esto es para actualizar el estado de enviado de la db local
     * @param params          estos son loss parametros para enviar al web service
     */
    public void webServiceGuardarRevisionPdvDet(final int IdRptEncLocalDB, final int idOnlineEncDB, final int IdRptDetLocalDB, final List<classWebService> params) {

        try {
            StringRequest jsonRequest = new StringRequest(Request.Method.POST, URL_WS_GUARDA_REVISIONPDV_DET,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                Log.d("Consulta env det rPdv", response);

                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");

                                if (success == 1) {
                                    int idInsertado = jsonObject.getInt("idInsertado");
                                    ContentValues values = new ContentValues();
                                    values.put("EstadoEnviado", 1);
                                    values.put("idEnviado", idInsertado);
                                    baseDatos.actualizarRegistro(TBL_REPORTE_DETALLE, values, "IdRptDet = " + IdRptDetLocalDB);

                                    //despues de enviar el detalle se procede a enviar las fotos del reporte
                                    enviarRevisionPdvDetallesFotos(IdRptEncLocalDB, IdRptDetLocalDB, idOnlineEncDB, idInsertado);

                                } else {
                                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Error en la consulta al ws: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parameters = new HashMap<String, String>();
                    for (int i = 0; i < params.size(); i++)
                        parameters.put(params.get(i).getParametro(), params.get(i).getValor());

                    return parameters;
                }
            };
            Volley.newRequestQueue(mContext).add(jsonRequest);
            Volley.newRequestQueue(mContext).addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                @Override
                public void onRequestFinished(Request<String> request) {
                    //if (progressDialog !=  null && progressDialog.isShowing())
                    //progressDialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void enviarRevisionPdvDetallesFotos(int idLocalEncDB, int idLocalDetDB, int idOnlineEncDB, int idOnlineDetDB) {
        try {

            Cursor cursor = baseDatos.obtenerRegistroWhereArgs(TBL_FOTO_INDCADOR, String.format("idRptEnc = %d and idRptDet = %d", idLocalEncDB, idLocalDetDB));
            if (cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    //se ejecuta l web service para enviar el detalle ala nube
                    EnviarImagen(
                            cursor.getInt(cursor.getColumnIndex("Idfoto")),
                            cursor.getString(cursor.getColumnIndex("pathFoto")),
                            String.valueOf(idOnlineEncDB),
                            String.valueOf(idOnlineDetDB),
                            cursor.getString(cursor.getColumnIndex("idIndicad")),
                            cursor.getString(cursor.getColumnIndex("FechaToma"))

                    );

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getFilename(String path) {

        if (path.length() > 0) {
            File file = new File(path);
            return file.getName();
        } else {
            return "";
        }

    }

    private void EnviarImagen(final int IdfotoLocalDB, final String... parametros) {
        try {

            //SI Exist el nombre del archivo
            if (parametros[0].length() > 0) {

                //si existe el archivo (foto)
                if (new File(parametros[0]).exists()) {

                    final String filename = getFilename(parametros[0]);

                    String uploadId = UUID.randomUUID().toString();
                    new MultipartUploadRequest(mContext, uploadId, URL_WS_GUARDA_REVISIONPDV_FOTOS)
                            .addFileToUpload(parametros[0], "picture")
                            .addParameter("idRptEnc", parametros[1])
                            .addParameter("idRptDet", parametros[2])
                            .addParameter("idIndicad", parametros[3])
                            .addParameter("FechaToma", parametros[4])
                            .addParameter("filename", filename)
                            //aqui otros parametros
                            .setMaxRetries(2)
                            .setDelegate(new UploadStatusDelegate() {
                                @Override
                                public void onProgress(UploadInfo uploadInfo) {

                                }

                                @Override
                                public void onError(UploadInfo uploadInfo, Exception e) {
                                }

                                @Override
                                public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(serverResponse.getBodyAsString());
                                        int success = jsonObject.getInt("success");
                                        if (success == 1) {
                                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                                            ContentValues values = new ContentValues();
                                            values.put("EstadoEnviado", 1);
                                            values.put("idEnviado", jsonObject.getInt("idInsertado"));
                                            baseDatos.actualizarRegistro(TBL_FOTO_INDCADOR, values, "Idfoto = " + IdfotoLocalDB);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(UploadInfo uploadInfo) {
                                }
                            })

                            //esto muestra una notificacion en la barra con el progreso de subida
                            .setNotificationConfig(new UploadNotificationConfig())
                            .startUpload();
                }
            }

        } catch (Exception exc) {
            System.out.println(exc.getMessage() + " " + exc.getLocalizedMessage());
        }
    }

    /**
     * este metodo es para obtener los datos del desde el web service
     *
     * @param params estos son loss parametros para enviar al web service
     */
    public void webServiceVerificaNuevaVersion(final List<classWebService> params) {

        try {
            StringRequest jsonRequest = new StringRequest(Request.Method.POST, URL_WS_VERIFICA_NUEVA_VERSION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);

                                int success = jsonObject.getInt("success");
                                if (success == 1) {

                                    final JSONObject finalJsonObject = jsonObject;
                                    new AlertDialog.Builder(mContext)
                                            .setTitle(jsonObject.getString("MensajeActualizacion"))
                                            .setCancelable(false)
                                            .setMessage(jsonObject.getString("NotasVersion"))
                                            .setPositiveButton("Descargar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        mContext.startActivity(new Intent(mContext, DescargarActualizacionWebView.class)
                                                                .putExtra("URL", finalJsonObject.getString("URLDescargaApp")));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("No por ahora", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        if (finalJsonObject.getInt("isNecesary") == 1) {
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                                ((Activity) mContext).finishAffinity();
                                                            } else {
                                                                ((Activity) mContext).finish();
                                                            }
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            })
                                            .show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Error en la consulta al ws: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parameters = new HashMap<String, String>();
                    for (int i = 0; i < params.size(); i++)
                        parameters.put(params.get(i).getParametro(), params.get(i).getValor());

                    return parameters;
                }
            };
            Volley.newRequestQueue(mContext).add(jsonRequest);
            Volley.newRequestQueue(mContext).addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                @Override
                public void onRequestFinished(Request<String> request) {
                    //if (progressDialog !=  null && progressDialog.isShowing())
                    //progressDialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enviarEvaluacionDisplayEncabezado() {

        try {

            Cursor cursor = baseDatos.obtenerRegistroWhereArgs(TBL_EVAL_DISPLAY_ENC, "EstadoEnviado = 0 and idEnviado = 0");
            if (cursor.getCount() > 0) {

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    List<classWebService> params = new ArrayList<>();
                    params.add(new classWebService("NombreDislay", cursor.getString(cursor.getColumnIndex("NombreDislay"))));
                    params.add(new classWebService("IdDislay", cursor.getString(cursor.getColumnIndex("IdDislay"))));
                    params.add(new classWebService("idPdv", cursor.getString(cursor.getColumnIndex("idPdv"))));
                    params.add(new classWebService("NombrePDV", cursor.getString(cursor.getColumnIndex("NombrePDV"))));
                    params.add(new classWebService("FechReg", cursor.getString(cursor.getColumnIndex("FechReg"))));
                    params.add(new classWebService("GPSCoordenadas", cursor.getString(cursor.getColumnIndex("GPSCoordenadas"))));
                    params.add(new classWebService("IdUsuario", String.valueOf(setting.getInt(SETT_COD_USUARIO, 0))));

                    //se ejecuta l web service para enviar el detalle ala nube
                    webServiceGuardarEvaluacionDisplayEnc(
                            cursor.getInt(cursor.getColumnIndex("idEvalDispEnc")),
                            params
                    );
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * este metodo es para enviar los datos al web service
     *
     * @param IdEvalDisplEncLocalDB Estoes para actualizar el registro de estado enviado
     * @param params                estos son loss parametros para enviar al web service
     */
    public void webServiceGuardarEvaluacionDisplayEnc(final int IdEvalDisplEncLocalDB, final List<classWebService> params) {

        try {
            StringRequest jsonRequest = new StringRequest(Request.Method.POST, URL_WS_GUARDA_EVALDISPLAY_ENC,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                Log.d("Consulta env enc ED", response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");

                                if (success == 1) {
                                    int idInsertado = jsonObject.getInt("idInsertado");
                                    ContentValues values = new ContentValues();
                                    values.put("EstadoEnviado", 1);
                                    values.put("idEnviado", idInsertado);
                                    baseDatos.actualizarRegistro(TBL_EVAL_DISPLAY_ENC, values, "idEvalDispEnc = " + IdEvalDisplEncLocalDB);

                                    //despues de enviar el encabezado se procede a enviar el detalle del reporte
                                    enviarEvaluacioDisplayDetalles(IdEvalDisplEncLocalDB, idInsertado);

                                } else {
                                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Error en la consulta al ws: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parameters = new HashMap<String, String>();
                    for (int i = 0; i < params.size(); i++)
                        parameters.put(params.get(i).getParametro(), params.get(i).getValor());

                    return parameters;
                }
            };
            Volley.newRequestQueue(mContext).add(jsonRequest);
            Volley.newRequestQueue(mContext).addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                @Override
                public void onRequestFinished(Request<String> request) {
                    //if (progressDialog !=  null && progressDialog.isShowing())
                    //progressDialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enviarEvaluacioDisplayDetalles(int idLocalDB, int idOnlineEncDB) {
        try {

            Cursor cursor = baseDatos.obtenerRegistroWhereArgs(TBL_EVAL_DISPLAY_DET, "idEDE = " + idLocalDB);
            if (cursor.getCount() > 0) {

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    List<classWebService> params = new ArrayList<>();
                    params.add(new classWebService("idEDEnc", String.valueOf(idOnlineEncDB)));
                    params.add(new classWebService("idIndic", cursor.getString(cursor.getColumnIndex("idIndic"))));
                    params.add(new classWebService("descIndicador", cursor.getString(cursor.getColumnIndex("descIndicador"))));
                    params.add(new classWebService("puntaje", cursor.getString(cursor.getColumnIndex("puntaje"))));

                    //se ejecuta l web service para enviar el detalle ala nube
                    webServiceGuardarEvaluarDisplayDet(
                            cursor.getInt(cursor.getColumnIndex("idEvalDispDet")),
                            params
                    );

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * * este metodo es para enviar los datos al web service
     *
     * @param IdEDDetLocalDB Esto es para actualizar el estado de enviado de la db local
     * @param params         estos son loss parametros para enviar al web service
     */
    public void webServiceGuardarEvaluarDisplayDet(final int IdEDDetLocalDB, final List<classWebService> params) {

        try {
            StringRequest jsonRequest = new StringRequest(Request.Method.POST, URL_WS_GUARDA_EVALDISPLAY_DET,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                Log.d("Consulta env det ED", response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");

                                if (success == 1) {
                                    int idInsertado = jsonObject.getInt("idInsertado");
                                    ContentValues values = new ContentValues();
                                    values.put("EstadoEnviado", 1);
                                    values.put("idEnviado", idInsertado);
                                    baseDatos.actualizarRegistro(TBL_EVAL_DISPLAY_DET, values, "idEvalDispDet = " + IdEDDetLocalDB);

                                } else {
                                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Error en la consulta al ws: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parameters = new HashMap<String, String>();
                    for (int i = 0; i < params.size(); i++)
                        parameters.put(params.get(i).getParametro(), params.get(i).getValor());

                    return parameters;
                }
            };
            Volley.newRequestQueue(mContext).add(jsonRequest);
            Volley.newRequestQueue(mContext).addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                @Override
                public void onRequestFinished(Request<String> request) {
                    //if (progressDialog !=  null && progressDialog.isShowing())
                    //progressDialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void BackupDatabase() throws IOException {
        boolean success = true;
        File file = null;
        file = new File(Environment.getExternalStorageDirectory() + "/grupovalor/WalmartMP_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".db");

        if (file.exists()) {
            success = false;
        }

        if (success) {
            String inFileName = "/data/data/gv.haha.auditoria_mp_walmart/databases/WalmartMP.db";
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = CARPETA_RECURSOS + "WalmartMP_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".db";

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();
        }
    }
}
