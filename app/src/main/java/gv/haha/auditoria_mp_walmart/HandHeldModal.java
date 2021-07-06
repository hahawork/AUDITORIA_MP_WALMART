package gv.haha.auditoria_mp_walmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appyvet.materialrangebar.RangeBar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gv.haha.auditoria_mp_walmart.clases.BaseDatos;
import gv.haha.auditoria_mp_walmart.clases.JSONParser;
import gv.haha.auditoria_mp_walmart.clases.ScreenshotUtil;
import gv.haha.auditoria_mp_walmart.clases.classCustomToast;

import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_DATA_HAND_HELD;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_DATA_HAND_HELD_PDV;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_MULTIPLE_OPCIONES;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_OBTENER_HANDHELD;

public class HandHeldModal extends AppCompatActivity {

    static String CodigoEscaneado = ""; // asi esta tambien en la DB online UPC
    LinearLayout llItems;
    Button btnSearch;
    EditText etSearch, etNumTienda;
    AutoCompleteTextView actvPdv;
    RangeBar rangeBar;
    BaseDatos baseDatos;
    private ImageView imageViewShowScreenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_held);
        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appBar);
        appBarLayout.setVisibility(View.GONE);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(HandHeldModal.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Enfoque el codigo de barras.");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(false);

                integrator.initiateScan();

            }
        });
        final FloatingActionButton fabDownloadData = (FloatingActionButton) findViewById(R.id.fabDownloadData);
        fabDownloadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(HandHeldModal.this)
                        .setTitle("Seleccione...")
                        .setMessage("seleccione que va a descargar")
                        .setPositiveButton("Puntos de Venta", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new DescargarDataHandHeld_pdv().execute();
                            }
                        })
                        .setNegativeButton("Información. " + etNumTienda.getText(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(etNumTienda.getText())) {
                                    new classCustomToast(HandHeldModal.this).Show_ToastError("Favor selecionar pdv de la lista o ingrese código tienda");
                                } else {
                                    new DescargarDataHandHeld().execute(etNumTienda.getText().toString());
                                }
                            }
                        })
                        .setNeutralButton("Screenshot",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LinearLayout parentView = findViewById(R.id.llHandHeldItems_HH);
                                        Bitmap bitmap = ScreenshotUtil.getInstance().takeScreenshotForView(parentView);// Take ScreenshotUtil for any view
                                        //bitmap = ScreenshotUtil.getInstance().takeScreenshotForScreen(HandHeldModal.this); // Take ScreenshotUtil for activity
                                        retornaScreenshot(bitmap);
                                        //imageViewShowScreenshot.setImageBitmap(bitmap);
                                    }
                                })
                        .show();
            }
        });

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        baseDatos = new BaseDatos(this);

        btnSearch = (Button) findViewById(R.id.btnSearch_HH);
        llItems = (LinearLayout) findViewById(R.id.llHandHeldItems_HH);
        etSearch = (EditText) findViewById(R.id.etTextSearchHandHeld_HH);
        actvPdv = (AutoCompleteTextView) findViewById(R.id.actvNombrePDV_HH);
        rangeBar = (RangeBar) findViewById(R.id.rangebar1);
        etNumTienda = (EditText) findViewById(R.id.etNumTienda_HH);
        imageViewShowScreenshot = findViewById(R.id.imageViewShowScreenshot);


        String[] arrPdv = baseDatos.getArrayDataFromTable(TBL_DATA_HAND_HELD_PDV, "NombreTienda");
        ArrayAdapter<String> adapterPdv = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrPdv);
        actvPdv.setAdapter(adapterPdv);
        //si no hay punmtos de venta descargados
        if (arrPdv.length == 0) {
            new DescargarDataHandHeld_pdv().execute();
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(etNumTienda.getText())) {
                    if (!TextUtils.isEmpty(etSearch.getText())) {

                        ConsultarLocalDB(
                                etSearch.getText().toString(),
                                etNumTienda.getText().toString()
                        );
                    } else {
                        new classCustomToast(HandHeldModal.this).Show_ToastError("Por favor ingrese un valor a buscar.");
                    }
                } else {
                    new classCustomToast(HandHeldModal.this).Show_ToastError("Por favor ingrese un punto de venta de la lista.");
                }
            }
        });
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                if (CodigoEscaneado.length() > 0) {
                    String nuevocodigo = CodigoEscaneado.substring(leftPinIndex, rightPinIndex + 1);
                    etSearch.setText(nuevocodigo);
                }
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TextUtils.isEmpty(etNumTienda.getText())) {
                        if (!TextUtils.isEmpty(etSearch.getText())) {

                            ConsultarLocalDB(
                                    etSearch.getText().toString(),
                                    etNumTienda.getText().toString()
                            );
                        } else {
                            new classCustomToast(HandHeldModal.this).Show_ToastError("Por favor ingrese un valor a buscar.");
                        }
                    } else {
                        new classCustomToast(HandHeldModal.this).Show_ToastError("Por favor ingrese un punto de venta de la lista.");
                    }
                }
                return handled;
            }
        });

        actvPdv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Cursor cursor = baseDatos.obtenerRegistroWhereArgs(TBL_DATA_HAND_HELD_PDV, "NombreTienda ='" + actvPdv.getText().toString() + "'");
                    if (cursor.moveToFirst()) {
                        etNumTienda.setText(cursor.getString(cursor.getColumnIndex("NumeroTienda")));
                    } else {
                        etNumTienda.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void retornaScreenshot(Bitmap bitmap) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs);
        Intent intent = new Intent();
        intent.putExtra("byteArray", bs.toByteArray());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0x0000c0de:
                try {
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (result != null) {
                        if (result.getContents() == null) {
                            new classCustomToast(this).Toast("Oops!, Has cancelado el escaneo ", R.drawable.ic_error);
                        } else {

                            CodigoEscaneado = result.getContents();
                            String init = CodigoEscaneado.substring(0, 3);
                            if (init.equals("400")) {
                                CodigoEscaneado = CodigoEscaneado.substring(3, CodigoEscaneado.length());
                            }
                            etSearch.setText(CodigoEscaneado);
                            rangeBar.setVisibility(View.VISIBLE);
                            rangeBar.setTickEnd(CodigoEscaneado.length());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private void ConsultarLocalDB(final String... params) {

        try {

            //verifica cuantos items hay descargado en el pdv seleccionado
            Cursor cCant = baseDatos.obtenerRegistroWhereArgs(TBL_DATA_HAND_HELD, "NumTienda = " + params[1]);

            Cursor cursor = baseDatos.obtenerRegistroWhereArgs(TBL_DATA_HAND_HELD,
                    "NumTienda = '" + params[1] + "' AND (UPC = '" + params[0] + "' OR Item = '" + params[0] + "' OR Descripcion LIKE '%" + params[0] + "%')");

            if (cursor.getCount() > 0) {

                llItems.removeAllViews();

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    try {

                        for (String campo : cursor.getColumnNames()) {
                            String key = campo;
                            String value = cursor.getString(cursor.getColumnIndex(campo));
                            Log.i("Key:Value", "" + key + ":" + value);
                            AgregarItem(key, value);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // si la cantidad de Items descargados en ese pdv es 0
            } else if (cCant.getCount() == 0) {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_warning)
                        .setTitle("Advertencia")
                        .setMessage("Aún no has descargado la información de " + params[1] +
                                "\nSeleccione una opción.")
                        .setPositiveButton("Descargar Info", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new DescargarDataHandHeld().execute(params[1]);
                            }
                        })
                        .setNegativeButton("Consultar Online", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ConsultarOnline(params);
                            }
                        })
                        .show();
            } else {
                new classCustomToast(this).Toast("No se encontró el producto en el pdv", R.drawable.ic_error);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ConsultarOnline(final String... params) {
        try {

            StringRequest jsonRequest = new StringRequest(Request.Method.POST,
                    URL_WS_OBTENER_HANDHELD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");

                                if (success == 1) {

                                    llItems.removeAllViews();

                                    JSONArray jsonArray = jsonObject.getJSONArray("Datos");
                                    for (int i = 0; i < jsonArray.length(); i++) {        // PARA CADA PUNTO DE VENTA REGISTRADO
                                        try {
                                            //si hay mas de un resultado se genera un color aleatorio
                                            Random rnd = new Random();
                                            int color = Color.argb(255, rnd.nextInt(255) + 200, rnd.nextInt(255) + 200, rnd.nextInt(255) + 200);


                                            JSONObject c = jsonArray.getJSONObject(i);
                                            Iterator it = c.keys();
                                            while (it.hasNext()) {
                                                String key = (String) it.next();
                                                String value = c.getString(key);
                                                AgregarItem(key, value);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }

                                } else {
                                    Toast.makeText(HandHeldModal.this, message, Toast.LENGTH_LONG).show();
                                    llItems.removeAllViews();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(HandHeldModal.this, "Error en la consulta al ws: ConsultarOnline " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("Codigo", params[0]);
                    parameters.put("NumTienda", params[1]);

                    return parameters;
                }
            };
            Volley.newRequestQueue(this).add(jsonRequest);
            Volley.newRequestQueue(this).addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                @Override
                public void onRequestFinished(Request<String> request) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AgregarItem(String key, String value) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.row_hand_held_item, null);

        TextView tvLabel = (TextView) rowView.findViewById(R.id.tvLabel_HHItem);
        TextView tvDato = (TextView) rowView.findViewById(R.id.tvDato_HHItem);
        tvLabel.setText(key);
        tvDato.setText(value);
        // Add the new row before the add field button.
        llItems.addView(rowView);

    }

    class DescargarDataHandHeld extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(HandHeldModal.this);
            pDialog.setTitle("Recuperando la información. Espere...");
            pDialog.setCancelable(true);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    DescargarDataHandHeld.this.cancel(true);
                    new AlertDialog.Builder(HandHeldModal.this).setMessage("Se ha cancelado la petición.").setIcon(R.drawable.ic_info).show();
                }
            });
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // getting JSON Object

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("opcion", "ObtenerHandHeldData"));
            params.add(new BasicNameValuePair("listaParametros", strings[0]));
            params.add(new BasicNameValuePair("format", "json"));


            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(URL_WS_MULTIPLE_OPCIONES, "POST", params);
            if (json != null) {
                try {
                    int success = json.getInt("success");

                    if (success == 1) {     //Descargado correctamente

                        JSONArray jsonArray = json.getJSONArray("Datos");

                        for (int i = 0; i < jsonArray.length(); i++) {        // PARA REGISTR0

                            JSONObject c = jsonArray.getJSONObject(i);

                            Iterator it = c.keys();
                            String key = "", value = "";
                            ContentValues values = new ContentValues();
                            while (it.hasNext()) {
                                key = (String) it.next();
                                value = c.getString(key);
                                values.put(key, value);

                                publishProgress("" + i, "" + jsonArray.length(), c.getString(key));
                            }
                            publishProgress("" + i,
                                    "" + jsonArray.length(),
                                    c.getString("STORE_NAME") + " - " + c.getString("SIGNING_DESC"));

                            baseDatos.insertarRegistroSiNoExiste(TBL_DATA_HAND_HELD,
                                    String.format("ITEM = '%s' AND STORE_NBR = '%s' AND fecha = '%s'",
                                            c.getString("ITEM"),
                                            c.getString("STORE_NBR"),
                                            c.getString("fecha")),
                                    values);
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
                    new AlertDialog.Builder(HandHeldModal.this).setTitle("Error al Obtener.").setMessage("No se ha podido descargar, pueda que no tengas internet.").setIcon(R.drawable.ic_error).show();
                    pDialog.dismiss();
                } else {
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");
                    if (success == 1) {
                        pDialog.dismiss();
                        HandHeldModal.this.recreate();
                    } else {
                        pDialog.dismiss();
                    }
                }

            } catch (JSONException e) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                e.printStackTrace();
            }
        }
    }

    class DescargarDataHandHeld_pdv extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(HandHeldModal.this);
            pDialog.setTitle("Descargando la información. Espere...");
            pDialog.setCancelable(true);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    DescargarDataHandHeld_pdv.this.cancel(true);
                    new AlertDialog.Builder(HandHeldModal.this).setMessage("Se ha cancelado la petición.").setIcon(R.drawable.ic_info).show();
                }
            });
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // getting JSON Object

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("opcion", "ObtenerHandHeldData_pdv"));
            params.add(new BasicNameValuePair("listaParametros", ""));
            params.add(new BasicNameValuePair("format", "json"));


            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(URL_WS_MULTIPLE_OPCIONES, "POST", params);
            if (json != null) {
                try {
                    int success = json.getInt("success");

                    if (success == 1) {     //Descargado correctamente

                        JSONArray jsonArray = json.getJSONArray("Datos");

                        for (int i = 0; i < jsonArray.length(); i++) {        // PARA REGISTR0

                            JSONObject c = jsonArray.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("NumeroTienda", c.getString("STORE_NBR"));
                            values.put("NombreTienda", c.getString("STORE_NAME"));
                            publishProgress("" + i,
                                    "" + jsonArray.length(),
                                    c.getString("STORE_NAME"));

                            baseDatos.insertarRegistroSiNoExiste(TBL_DATA_HAND_HELD_PDV,
                                    "NumeroTienda ='" + c.getString("STORE_NBR") + "'",
                                    values);
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
                    new AlertDialog.Builder(HandHeldModal.this).setTitle("Error al Obtener.").setMessage("No se ha podido descargar, pueda que no tengas internet.").setIcon(R.drawable.ic_error).show();
                    pDialog.dismiss();
                } else {
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");
                    if (success == 1) {
                        pDialog.dismiss();
                        HandHeldModal.this.recreate();
                    } else {
                        pDialog.dismiss();
                    }
                }

            } catch (JSONException e) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                e.printStackTrace();
            }
        }
    }
}
