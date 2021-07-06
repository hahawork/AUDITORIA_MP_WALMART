package gv.haha.auditoria_mp_walmart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gv.haha.auditoria_mp_walmart.clases.AdapterActivComerc;
import gv.haha.auditoria_mp_walmart.clases.BaseDatos;
import gv.haha.auditoria_mp_walmart.clases.Globales;
import gv.haha.auditoria_mp_walmart.clases.JSONParser;
import gv.haha.auditoria_mp_walmart.clases.SQLHelper;
import gv.haha.auditoria_mp_walmart.clases.classActivComerc;
import gv.haha.auditoria_mp_walmart.clases.classCustomToast;
import gv.haha.auditoria_mp_walmart.clases.classPdv;

import static gv.haha.auditoria_mp_walmart.clases.Variables.CARPETA_RECURSOS;
import static gv.haha.auditoria_mp_walmart.clases.Variables.SETT_ULT_PDV_ACTCOMERC_SELECC;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_ACTIVIDAD_COMERCIAL_DATA;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_RPT_ACTIVIDAD_COMERCIAL;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_MULTIPLE_OPCIONES;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_OBTENER_ACTIVCOMERC_DATA;

public class ActividadComercial extends AppCompatActivity {

    public static View.OnClickListener myOnClickListener;
    static Uri ImagenTomadaCamara = null;
    BaseDatos baseDatos;
    RecyclerView recyclerView;
    int mColumnCount = 2;
    List<classActivComerc> arrContenido = new ArrayList<>();
    List<classPdv> arrPdV = new ArrayList<>();
    Spinner spnPdv;
    String idDataSeleccParaTomarFotografia = "";
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    ImageView ivFotoActivComerc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_comercial);


        onConfigurationChanged();

        iniciarInstancias();


        LlenarPuntosDeVenta();

    }

    private void iniciarInstancias() {
        baseDatos = new BaseDatos(this);
        spnPdv = (Spinner) findViewById(R.id.spnNombrePdv_AC);
        setting = PreferenceManager.getDefaultSharedPreferences(this);
        editor = setting.edit();

        myOnClickListener = new MyOnClickListener(this);

        spnPdv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                editor.putInt(SETT_ULT_PDV_ACTCOMERC_SELECC, position);
                editor.commit();

                LlenarInformacion(arrPdV.get(position).getId());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void LlenarPuntosDeVenta() {
        try {

            //se cargan los nombres de los pdv que ya se han ingresados
            Cursor cpdv = baseDatos.obtenerDistinctRegistro(TBL_ACTIVIDAD_COMERCIAL_DATA, "NumTienda", "NombTienda");
            if (cpdv.getCount() > 0) {

                String[] items = new String[cpdv.getCount()];

                for (cpdv.moveToFirst(); !cpdv.isAfterLast(); cpdv.moveToNext()) {
                    arrPdV.add(new classPdv(
                            cpdv.getInt(cpdv.getColumnIndex("NumTienda")),
                            cpdv.getString(cpdv.getColumnIndex("NombTienda"))
                    ));

                    items[cpdv.getPosition()] = cpdv.getString(cpdv.getColumnIndex("NombTienda"));
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnPdv.setAdapter(dataAdapter);

                int UltimaPosicSelecc = setting.getInt(SETT_ULT_PDV_ACTCOMERC_SELECC, 0);
                if (items.length >= UltimaPosicSelecc) {
                    spnPdv.setSelection(UltimaPosicSelecc);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void LlenarInformacion(int idTienda) {

        try {

            arrContenido.clear();

            Cursor cdata = baseDatos.obtenerRegistroWhereArgs(TBL_ACTIVIDAD_COMERCIAL_DATA,
                    String.format("NumTienda = %d ", idTienda));
            for (cdata.moveToFirst(); !cdata.isAfterLast(); cdata.moveToNext()) {

                arrContenido.add(
                        new classActivComerc(
                                cdata.getString(cdata.getColumnIndex("iddata")),
                                cdata.getString(cdata.getColumnIndex("Llave")),
                                cdata.getString(cdata.getColumnIndex("Formato")),
                                cdata.getString(cdata.getColumnIndex("Item")),
                                cdata.getString(cdata.getColumnIndex("Presentacion")),
                                cdata.getString(cdata.getColumnIndex("Nomenclatura")),
                                cdata.getString(cdata.getColumnIndex("Espacio")),
                                cdata.getString(cdata.getColumnIndex("Area")),
                                cdata.getString(cdata.getColumnIndex("NumTienda")),
                                cdata.getString(cdata.getColumnIndex("NombTienda")),
                                cdata.getString(cdata.getColumnIndex("Inventario")),
                                cdata.getString(cdata.getColumnIndex("FechaSubido")),
                                ""
                        )
                );
            }

            recyclerView = findViewById(R.id.rvActividadComercial);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(this, mColumnCount));
            }

            AdapterActivComerc adapterActivComerc = new AdapterActivComerc(this, arrContenido);
            recyclerView.setAdapter(adapterActivComerc);

        } catch (Exception we) {
            we.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actividad_comercial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_download) {

            if (new Globales(this).TieneConexion()) {
                ObtenerFechasDisponibles();
            } else {
                new classCustomToast(this).Show_ToastError("No tienes conexión a internet.");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo para controlar la cantidad de columnas dl recyclerview de
     * 2 columnas en portrait
     * 3 columnas en landscape
     */
    public void onConfigurationChanged() {

        // Checks the orientation of the screen
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            mColumnCount = 3;
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mColumnCount = 2;
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    private void TomarFotografia(String idData, String nombreTienda) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {

            final File root = new File(CARPETA_RECURSOS);
            if (!root.exists())
                root.mkdirs();

            idDataSeleccParaTomarFotografia = idData;

            final String fname = nombreTienda.replace(" ", "") + "_" + idDataSeleccParaTomarFotografia + "_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ImagenTomadaCamara = Uri.fromFile(new File(CARPETA_RECURSOS + fname));
            //si no va falla al abrir la camara en el j2 tambien debe de ir cuando se obtiene un archivo dsdd la memoria
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            //******************************************************
            intent.putExtra(MediaStore.EXTRA_OUTPUT, ImagenTomadaCamara);
            startActivityForResult(intent, 999);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case 999:

                if (resultCode == RESULT_OK) {  //si se tomo la fotografia
                    Uri selectedImageUri;
                    try {
                        selectedImageUri = ImagenTomadaCamara;//new CapturarEnviarImagenes(mContext).outputFileUri;  // se obtiene el path de la imagen guardada
                        //Bitmap factory
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        // se hace la imagen mas pequeña, esto es para evitar el throws OutOfMemory Exception  en imagenes grandes
                        options.inSampleSize = 8;
                        final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
                        //imgview.setImageBitmap(bitmap);

                        // se guarda la ruta de la imagen en la base de datos para llevar un registro de las fotos por punto visitado
                        ContentValues values = new ContentValues();
                        values.put("fotopath", selectedImageUri.getPath());

                        int actualizado = baseDatos.actualizarRegistro(TBL_RPT_ACTIVIDAD_COMERCIAL, values, "idData = " + idDataSeleccParaTomarFotografia);
                        if (actualizado > 0) {
                            new classCustomToast(this).Toast("Se ha guardado la foto con éxito", R.drawable.ic_success);
                            if (ivFotoActivComerc!=null){
                                Glide.with(ActividadComercial.this).load(selectedImageUri.getPath()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ivFotoActivComerc);
                            }
                        } else {
                            new classCustomToast(this).Toast("Aun no has reportado el estado actual de la dinámica comercial", R.drawable.ic_error);
                            //si no se actualiza, borra la foto para limpiar memoria.
                            File foto = new File(selectedImageUri.getPath());
                            if (foto.exists()) {
                                foto.delete();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

    /**
     * Metodo para guardar datos en la db
     *
     * @param Indicad
     * @param tvcodigo Para poner en verde cuando se ha reportado la presentacion
     */
    public void DialogoGuardarReporte(final int Indicad, final TextView tvcodigo) {

        final Dialog dialogIntro = new Dialog(this);
        dialogIntro.setCancelable(false);
        dialogIntro.setCanceledOnTouchOutside(false);
        dialogIntro.setContentView(R.layout.dialog_actividcomercial_informacion);

        final String[] Implementacion = {"-"};
        final String[] BajoInventario = {"-"};

        //se habilita el boton de la camara
        //Camara.setVisibility(View.VISIBLE);
        //todo poner este boton visible

        RadioGroup rgImplement = dialogIntro.findViewById(R.id.rgImplementacion_dgAC);
        final RadioGroup rgBajoInvent = dialogIntro.findViewById(R.id.rgBajoInventario_dgAC);

        final EditText etInventarioTienda = dialogIntro.findViewById(R.id.etInventarioTienda_dgAC);
        final TextInputEditText etComentario = dialogIntro.findViewById(R.id.etComentarios_dg_indicador);
        ivFotoActivComerc = dialogIntro.findViewById(R.id.ivFotoActividComercial_dg_indicador);
        Button btnGuardar = (Button) dialogIntro.findViewById(R.id.btnGuardar_dg_indicador);
        Button btnFoto = (Button) dialogIntro.findViewById(R.id.btnFoto_dg_indicador);
        Button btnCancelar = (Button) dialogIntro.findViewById(R.id.btnCancelar_dg_indicador);

        ((TextView) dialogIntro.findViewById(R.id.tvPresentacion_rowactividcomerc)).setText(arrContenido.get(Indicad).getPresentacion());
        ((TextView) dialogIntro.findViewById(R.id.tvTipo_rowactividcomerc)).setText(arrContenido.get(Indicad).getNomenclatura());
        ((TextView) dialogIntro.findViewById(R.id.tvUbicavcion_rowactividcomerc)).setText(arrContenido.get(Indicad).getEspacio());
        ((TextView) dialogIntro.findViewById(R.id.tvCodigoPresent_rowactividcomerc)).setText("Item(" + arrContenido.get(Indicad).getItem() + ")\nExistencia(" + arrContenido.get(Indicad).getInventario() + ")");

        //si ya guardo informacion se obtiene y se muestra
        Cursor cguard = baseDatos.obtenerRegistroWhereArgs(TBL_RPT_ACTIVIDAD_COMERCIAL, "idData = " + arrContenido.get(Indicad).getIddata() + " ORDER BY FechaRegistro DESC");
        if (cguard.moveToFirst()) {
            etInventarioTienda.setText(cguard.getString(cguard.getColumnIndex("InventarioActual")));
            etComentario.setText(cguard.getString(cguard.getColumnIndex("Comentarios")));
            String implement = cguard.getString(cguard.getColumnIndex("implementacion"));
            if (implement.equals("SI")) {
                ((RadioButton) dialogIntro.findViewById(R.id.rbSiImplementacion_dgAC)).setChecked(true);
            } else {
                ((RadioButton) dialogIntro.findViewById(R.id.rbNoImplementacion_dgAC)).setChecked(true);
            }
            String bajoinvent = cguard.getString(cguard.getColumnIndex("BajoInventario"));
            if (bajoinvent.equals("1")) {
                ((RadioButton) dialogIntro.findViewById(R.id.rbSiBajoInventario_dgAC)).setChecked(true);
            } else {
                ((RadioButton) dialogIntro.findViewById(R.id.rbNoBajoInventario_dgAC)).setChecked(true);
            }

            String fotopath = cguard.getString(cguard.getColumnIndex("fotopath"));
            if (fotopath != null) {
                if (fotopath.length() > 0) {
                    if (new File(fotopath).exists()) {
                        Glide.with(ActividadComercial.this).load(fotopath).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ivFotoActivComerc);
                    } else {
                        (dialogIntro.findViewById(R.id.ivFotoActividComercial_dg_indicador)).setVisibility(View.GONE);
                    }
                } else {

                    (dialogIntro.findViewById(R.id.ivFotoActividComercial_dg_indicador)).setVisibility(View.GONE);
                }
            }

        }

        //((LinearLayout)dialogIntro.findViewById(R.id.llPresentSelecc_dgAC)).addView(view);

        rgImplement.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rbSiImplementacion_dgAC:
                        Implementacion[0] = "SI";
                        //se inhabilitan los otro radios de opciones
                        for (int i = 0; i < rgBajoInvent.getChildCount(); i++) {
                            rgBajoInvent.getChildAt(i).setEnabled(false);
                            ((RadioButton) dialogIntro.findViewById(R.id.rbNoBajoInventario_dgAC)).setChecked(true);
                        }

                        break;
                    case R.id.rbNoImplementacion_dgAC:
                        Implementacion[0] = "NO";
                        //se habilitan los otro radios de opciones
                        for (int i = 0; i < rgBajoInvent.getChildCount(); i++) {
                            rgBajoInvent.getChildAt(i).setEnabled(true);
                        }
                        break;
                }

            }
        });
        rgBajoInvent.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbSiBajoInventario_dgAC:
                        BajoInventario[0] = "1";
                        break;
                    case R.id.rbNoBajoInventario_dgAC:
                        BajoInventario[0] = "0";
                        break;
                }
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues values = new ContentValues();

                //values.put("idRptActivComerc", (byte[]) null);
                values.put("idData", arrContenido.get(Indicad).getIddata());
                values.put("FechaSubido", arrContenido.get(Indicad).getFechaSubido());
                values.put("InventarioActual", etInventarioTienda.getText().toString());
                values.put("implementacion", Implementacion[0]);
                values.put("BajoInventario", rgBajoInvent.isEnabled() ? BajoInventario[0] : "-");
                values.put("FechaRegistro", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                values.put("Comentarios", TextUtils.isEmpty(etComentario.getText()) ? "" : etComentario.getText().toString());
                values.put("EstadoEnviado", 0);
                values.put("idEnviado", 0);

                //Long idInsert = baseDatos.insertarRegistro(TBL_RPT_ACTIVIDAD_COMERCIAL, values);
                Long idInsert = baseDatos.insertarRegistro(TBL_RPT_ACTIVIDAD_COMERCIAL/*, "idData = " + arrContenido.get(Indicad).getIddata()*/, values);

                if (idInsert != -1) {
                    new classCustomToast(ActividadComercial.this).Toast("Se ha guardado con éxito con id: " + idInsert, R.drawable.ic_success);
                    tvcodigo.setBackgroundColor(Color.parseColor("#BCF5A9"));


                } else {
                    new classCustomToast(ActividadComercial.this).Show_ToastError("Error al guardar actividad comercial.");
                }

            }
        });

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TomarFotografia(
                        arrContenido.get(Indicad).getIddata(),
                        arrContenido.get(Indicad).getNombTienda()
                );
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIntro.cancel();
            }
        });

        dialogIntro.show();
    }

    //esto es para que no se desparezcas el dialogo al rotar la pantalla, tambin va una configuracvion en el manifest
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Metodo para obtener desde el servidor las fechas disponible para descargar los datos
     */
    private void ObtenerFechasDisponibles() {
        try {

            StringRequest jsonRequest = new StringRequest(Request.Method.POST, URL_WS_MULTIPLE_OPCIONES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");

                                if (success == 1) {

                                    JSONArray datos = jsonObject.getJSONArray("Datos");

                                    final String[] fechas = new String[datos.length()];

                                    for (int i = 0; i < datos.length(); i++) {        // PARA CADA PUNTO DE VENTA REGISTRADO
                                        try {
                                            JSONObject c = datos.getJSONObject(i);

                                            fechas[i] = c.getString("FechaSubido");

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(ActividadComercial.this);
                                    builder.setTitle("Seleccione una de las siguientes fechas, para descargar la información");
                                    builder.setCancelable(true);
                                    builder.setItems(fechas, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            new ObtenerActividadComercial().execute(fechas[which]);
                                        }
                                    });
                                    builder.create();
                                    builder.show();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ActividadComercial.this, "Error en la consulta al ws: ObtenerFechasDisponibles " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parameters = new HashMap<String, String>();

                    parameters.put("opcion", "ObtenerFechasDataActivComerc_MP_WM");
                    parameters.put("listaParametros", "");
                    parameters.put("format", "json");

                    return parameters;
                }
            };
            Volley.newRequestQueue(ActividadComercial.this).add(jsonRequest);
            Volley.newRequestQueue(ActividadComercial.this).addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                @Override
                public void onRequestFinished(Request<String> request) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            PresentacionSeleccionada(v);
        }

        private void PresentacionSeleccionada(View v) {
            // la posicion seleccionada en la lista de del adaptr
            final int selectedItemPosition = recyclerView.getChildPosition(v);
            //String present = ((TextView) v.findViewById(R.id.tvPresentacion_rowactividcomerc)).getText().toString();
            //String tipo = ((TextView) v.findViewById(R.id.tvTipo_rowactividcomerc)).getText().toString();
            //String ubicacion = ((TextView) v.findViewById(R.id.tvUbicavcion_rowactividcomerc)).getText().toString();
            //String codigo = ((TextView) v.findViewById(R.id.tvCodigoPresent_rowactividcomerc)).getText().toString();
            TextView tvCodigo = (TextView) v.findViewById(R.id.tvCodigoPresent_rowactividcomerc);

            DialogoGuardarReporte(selectedItemPosition, tvCodigo);
        }
    }

    class ObtenerActividadComercial extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        ProgressDialog pDialog = new ProgressDialog(ActividadComercial.this);

        SQLiteDatabase db = new SQLHelper(ActividadComercial.this).getWritableDatabase();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ActividadComercial.this);
            pDialog.setTitle("Recuperando los datos. Espere...");
            pDialog.setCancelable(true);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ObtenerActividadComercial.this.cancel(true);
                    new AlertDialog.Builder(ActividadComercial.this).setMessage("Se ha cancelado la petición.").setIcon(R.drawable.ic_info).show();
                }
            });
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... strings) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("FechaSubido", strings[0]));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(URL_WS_OBTENER_ACTIVCOMERC_DATA, "POST", params);
            if (json != null) {
                try {
                    int success = json.getInt("success");

                    if (success == 1) {     //Descargado correctamente


                        //limpia la tabla de la base de datos
                        db.delete(TBL_ACTIVIDAD_COMERCIAL_DATA, null, null);

                        JSONArray datos = json.getJSONArray("datos");

                        for (int i = 0; i < datos.length(); i++) {        // PARA CADA PUNTO DE VENTA REGISTRADO
                            try {
                                JSONObject c = datos.getJSONObject(i);

                                publishProgreso("" + i, "" + datos.length(), c.getString("Presentacion"));

                                Log.w("descargando", c.getString("Presentacion"));

                                ContentValues values = new ContentValues();
                                values.put("iddata", c.getString("iddata"));
                                values.put("Llave", c.getString("Llave"));
                                values.put("Formato", c.getString("Formato"));
                                values.put("Item", c.getString("Item"));
                                values.put("Presentacion", c.getString("Presentacion"));
                                values.put("Nomenclatura", c.getString("Nomenclatura"));
                                values.put("Espacio", c.getString("Espacio"));
                                values.put("Area", c.getString("Area"));
                                values.put("NumTienda", c.getString("NumTienda"));
                                values.put("NombTienda", c.getString("NombTienda"));
                                values.put("Inventario", c.getString("Inventario"));
                                values.put("FechaSubido", c.getString("FechaSubido"));

                                db.insert(TBL_ACTIVIDAD_COMERCIAL_DATA, null, values); // se inserta en la base de datos local

                                //Log.w("id " + c.getString("IdPdV"), "Nombre: " + c.getString("NombrePdV"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return json;
        }


        private void publishProgreso(String... values) {
            int setMax = Integer.parseInt(values[1]);
            int setpregress = Integer.parseInt(values[0]);
            if (pDialog.isShowing()) {
                pDialog.setMax(setMax);
                pDialog.setProgress(setpregress);
                pDialog.setMessage(values[0] + " de " + values[1] + " \n " + values[2]);
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {

                if (jsonObject == null) {
                    new AlertDialog.Builder(ActividadComercial.this).setTitle("Error al Obtener.").setMessage("No se ha podido actualizar la información, pueda que no tengas internet.").setIcon(R.drawable.ic_error).show();
                    pDialog.dismiss();

                } else {
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");
                    if (success == 1) {

                        pDialog.dismiss();
                        new classCustomToast(ActividadComercial.this).Toast(message, R.drawable.ic_success);
                        ActividadComercial.this.recreate();
                    }
                }

            } catch (JSONException e) {

                pDialog.dismiss();

                e.printStackTrace();
            }
        }
    }

}
