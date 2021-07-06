package gv.haha.auditoria_mp_walmart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import gv.haha.auditoria_mp_walmart.clases.BaseDatos;
import gv.haha.auditoria_mp_walmart.clases.Globales;
import gv.haha.auditoria_mp_walmart.clases.ScreenshotFileUtil;
import gv.haha.auditoria_mp_walmart.clases.classCustomToast;

import static gv.haha.auditoria_mp_walmart.clases.Variables.CARPETA_RECURSOS;
import static gv.haha.auditoria_mp_walmart.clases.Variables.SETT_COD_USUARIO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_OPORTUNIDADES;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_PUNTOSDEVENTA;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_GUARDAR_OPORTUNIDAD;

public class Oportunidades extends AppCompatActivity {

    private final int REQ_CODE_FOTO_HANDHELD = 4, REQ_CODE_FOTO_GALERIA = 3, REQ_CODE_FOTO1 = 1, REQ_CODE_FOTO2 = 2;
    Uri ImagenTomadaCamara;
    FloatingActionButton fab;
    EditText etOportunidad, etSolucion;
    AutoCompleteTextView etTienda, etCategoria, etFormato, etProducto, etAreaResponsable;
    Button btnfoto1, btnFoto2;
    ImageView ivFoto1, ivFoto2;
    BaseDatos baseDatos;
    String strPathFoto1 = "", strPathFoto2 = "";
    Globales G;
    LinearLayout llEnviosPend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oportunidades);

        IniciarToolbar();
        IniciarComponentes();
        EventosControles();

    }

    private void IniciarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void IniciarComponentes() {

        baseDatos = new BaseDatos(this);
        G = new Globales(this);
        fab = findViewById(R.id.fab);
        etTienda = findViewById(R.id.etTienda_O);
        etCategoria = findViewById(R.id.etCategoria_O);
        etFormato = findViewById(R.id.etFormato_O);
        etAreaResponsable = findViewById(R.id.etAreaResponsable_O);
        etProducto = findViewById(R.id.etProducto_O);
        etOportunidad = findViewById(R.id.etOportunidad_O);
        etSolucion = findViewById(R.id.etSolucion_O);
        btnfoto1 = findViewById(R.id.btnFoto1_O);
        btnFoto2 = findViewById(R.id.btnFoto2_O);
        ivFoto1 = findViewById(R.id.ivFoto1_O);
        ivFoto2 = findViewById(R.id.ivFoto2_O);
        llEnviosPend = findViewById(R.id.llEnvPend_O);

        //se llenan los formatos de tienda
        etFormato.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Descuento", "Bodega", "Walmart", "Super"}));
        etAreaResponsable.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Operaciones", "Comercial", "Logistica"}));
        //se cargan los nombres de los pdv que ya se han ingresados
        String[] arrPdv = baseDatos.getArrayDataFromTable(TBL_PUNTOSDEVENTA, "NombrePdV");
        ArrayAdapter<String> adapterPdv = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrPdv);
        etTienda.setAdapter(adapterPdv);

        String[] categorias = getResources().getStringArray(R.array.ArrCATEGORIAS);
        ArrayAdapter<String> adapterCateg = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categorias);
        etCategoria.setAdapter(adapterCateg);

        LlenarEnviosPendientes();
    }

    private void EventosControles() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnviarOportunidad();
            }
        });

        btnfoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etTienda.getText())) {

                    seleccionarFuentedeImagen();
                    //TomarFotografia(etTienda.getText().toString(), REQ_CODE_FOTO1);
                } else {
                    new classCustomToast(Oportunidades.this).Show_ToastError("Favor ingrear punto de venta");
                }
            }
        });
        btnFoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etTienda.getText())) {
                    TomarFotografia(etTienda.getText().toString(), REQ_CODE_FOTO2);
                } else {
                    new classCustomToast(Oportunidades.this).Show_ToastError("Favor ingrear punto de venta");
                }
            }
        });

    }

    private void seleccionarFuentedeImagen() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.bg_logo);
        builderSingle.setTitle("Seleccione una opción");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Galeria");
        arrayAdapter.add("Cámara");
        arrayAdapter.add("Verificador");

        builderSingle.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, REQ_CODE_FOTO_GALERIA);

                } else if (which == 1) {
                    TomarFotografia(etTienda.getText().toString(), REQ_CODE_FOTO1);
                } else if (which == 2) {
                    startActivityForResult(new Intent(Oportunidades.this, HandHeldModal.class)
                            .putExtra("modo", "modal"), REQ_CODE_FOTO_HANDHELD);
                }

                /*String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(Oportunidades.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();*/
            }
        });
        builderSingle.show();

    }

    private void TomarFotografia(String nombreTienda, int Request_code) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {

            final File root = new File(CARPETA_RECURSOS);
            if (!root.exists())
                root.mkdirs();

            final String fname = "Oport_foto" +
                    Request_code + "_" +
                    G.remover_acentos(nombreTienda.replace(" ", "")) + "_" +
                    new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()) + ".jpg";

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ImagenTomadaCamara = Uri.fromFile(new File(CARPETA_RECURSOS + fname));
            //si no va falla al abrir la camara en el j2 tambien debe de ir cuando se obtiene un archivo dsdd la memoria
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            //******************************************************
            intent.putExtra(MediaStore.EXTRA_OUTPUT, ImagenTomadaCamara);
            startActivityForResult(intent, Request_code);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {  //si se tomo la fotografia

            if (requestCode == REQ_CODE_FOTO_GALERIA) {
                if (data != null) {

                    Uri selectedImage = data.getData();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    copyFileOrDirectory(getRealPathFromUri(selectedImage), CARPETA_RECURSOS);

                    try {
                        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, options);
                        //options.inSampleSize = calculateInSampleSize(options, 150, 100);
                        options.inJustDecodeBounds = false;
                        Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, options);
                        ivFoto1.setImageBitmap(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    // Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    // ivFoto1.setImageBitmap(bitmap);

                } else {
                    new classCustomToast(this).Show_ToastError("Has cancelado la captura desde galeria");
                }
               /* try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap bmpselectedImage = BitmapFactory.decodeStream(imageStream);
                    ivFoto1.setImageBitmap(bmpselectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }*/
            }

            if (requestCode == REQ_CODE_FOTO1) {
                Uri selectedImageUri;
                try {
                    selectedImageUri = ImagenTomadaCamara;//new CapturarEnviarImagenes(mContext).outputFileUri;  // se obtiene el path de la imagen guardada
                    strPathFoto1 = selectedImageUri.getPath();
                    //Bitmap factory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // se hace la imagen mas pequeña, esto es para evitar el throws OutOfMemory Exception  en imagenes grandes
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
                    ivFoto1.setImageBitmap(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == REQ_CODE_FOTO2) {
                Uri selectedImageUri;
                try {
                    selectedImageUri = ImagenTomadaCamara;//new CapturarEnviarImagenes(mContext).outputFileUri;  // se obtiene el path de la imagen guardada
                    strPathFoto2 = selectedImageUri.getPath();
                    //Bitmap factory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // se hace la imagen mas pequeña, esto es para evitar el throws OutOfMemory Exception  en imagenes grandes
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
                    ivFoto2.setImageBitmap(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            if (requestCode == REQ_CODE_FOTO_HANDHELD) {
                Bitmap Verificador = BitmapFactory.decodeByteArray(
                        data.getByteArrayExtra("byteArray"), 0,
                        data.getByteArrayExtra("byteArray").length);

                String fname = CARPETA_RECURSOS + "Oport_foto1" + "_" +
                        G.remover_acentos(etTienda.getText().toString().replace(" ", "")) + "_" +
                        new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()) + ".jpg";

                strPathFoto1 = fname;
                //Guarda el bitmap
                ScreenshotFileUtil.getInstance().storeBitmap(Verificador, fname);
                ivFoto1.setImageBitmap(Verificador);
            }
        }
    }

    private String getRealPathFromUri(Uri tempUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = this.getContentResolver().query(tempUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);

            final String fname = "Oport_foto1" + "_" +
                    G.remover_acentos(etTienda.getText().toString().replace(" ", "")) + "_" +
                    new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()) + ".jpg";
            File dst = new File(dstDir, fname);

            if (dst.exists()) {

            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
                strPathFoto1 = destFile.getPath();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    //esto es para que no se recargue el oncreate al rotar la pantalla, tambin va una configuracvion en el manifest
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void EnviarOportunidad() {
        try {

            boolean ok = true;
            StringBuilder errores = new StringBuilder("Errores:\n");

            if (TextUtils.isEmpty(etTienda.getText())) {
                ok = false;
                errores.append("👉 Nombre de la tienda.\n");
            }
            if (TextUtils.isEmpty(etCategoria.getText())) {
                ok = false;
                errores.append("👉 Categoria.\n");
            }
            if (TextUtils.isEmpty(etFormato.getText())) {
                ok = false;
                errores.append("👉 Formato.\n");
            }
            if (TextUtils.isEmpty(etOportunidad.getText())) {
                ok = false;
                errores.append("👉 Situación.\n");
            }
            if (TextUtils.isEmpty(etAreaResponsable.getText())) {
                ok = false;
                errores.append("👉 Area responsable.\n");
            }
            if (TextUtils.isEmpty(etProducto.getText())) {
                ok = false;
                errores.append("👉 Producto.\n");
            }
            if (TextUtils.isEmpty(etSolucion.getText())) {
                ok = false;
                errores.append("👉 Solución.\n");
            }

            if (strPathFoto1.length() == 0 || strPathFoto2.length() == 0) {
                ok = false;
                errores.append("👉 Son 2 fotografias.\n");
            }

            if (ok) {

                String pais = "Nicaragua"; //todo aqui va el pais
                String tienda = TextUtils.isEmpty(etTienda.getText()) ? "" : G.remover_acentos(etTienda.getText().toString());
                String categoria = TextUtils.isEmpty(etCategoria.getText()) ? "" : G.remover_acentos(etCategoria.getText().toString());
                String formato = TextUtils.isEmpty(etFormato.getText()) ? "" : G.remover_acentos(etFormato.getText().toString());
                String oportunidades = TextUtils.isEmpty(etOportunidad.getText()) ? "" : G.remover_acentos(etOportunidad.getText().toString());
                String arearesponsable = TextUtils.isEmpty(etAreaResponsable.getText()) ? "" : G.remover_acentos(etAreaResponsable.getText().toString());
                String producto = TextUtils.isEmpty(etProducto.getText()) ? "" : G.remover_acentos(etProducto.getText().toString());
                String solucion = TextUtils.isEmpty(etSolucion.getText()) ? "" : G.remover_acentos(etSolucion.getText().toString());
                String pathfoto1 = G.remover_acentos(strPathFoto1);
                String pathfoto2 = G.remover_acentos(strPathFoto2);
                String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


                ContentValues values = new ContentValues();
                values.put("idOport", (byte[]) null);
                values.put("Pais", pais);
                values.put("Tienda", tienda);
                values.put("Categoria", categoria);
                values.put("Formato", formato);
                values.put("Oportunidad", oportunidades);
                values.put("AreaResponsable", arearesponsable);
                values.put("Producto", producto);
                values.put("Solucion", solucion);
                values.put("PathFoto1", pathfoto1);
                values.put("PathFoto2", pathfoto2);
                values.put("FechaReg", fecha);
                Long insert = baseDatos.insertarRegistro(TBL_OPORTUNIDADES, values);
                if (insert > 0) {

                    //new classCustomToast(this).Toast("Se ha guardado en el dispositivo, se enviara al servidor.", R.drawable.ic_success);

                    if (G.TieneConexion()) {


                        EnviarOportunidadAlServidorOtro(
                                insert,
                                pais,
                                tienda,
                                categoria,
                                formato,
                                oportunidades,
                                arearesponsable,
                                producto,
                                solucion,
                                pathfoto1,
                                pathfoto2,
                                fecha,
                                "" + PreferenceManager.getDefaultSharedPreferences(this).getInt(SETT_COD_USUARIO, 0));
                    } else {
                        new classCustomToast(this).Toast("No tienes internet.", R.drawable.ic_error);
                        recreate();
                    }
                }

            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Favor ingresar.")
                        .setIcon(R.drawable.error)
                        .setMessage(errores)
                        .setPositiveButton("Ok", null)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void EnviarFTP( final String... params) {

        class EnviarFTP extends AsyncTask<String, String, String> {

            ProgressDialog progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = new ProgressDialog(Oportunidades.this);
                progressBar.setIndeterminate(true);
                progressBar.setTitle("Subiendo foto, espere...");
                progressBar.setIcon(android.R.drawable.ic_menu_send);
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.show();
            }

            @Override
            protected String doInBackground(String... strings) {

                    FTPClient mFTP = new FTPClient();
                    try {
                        // Connect to FTP Server
                        mFTP.connect("grupovalor.com.ni");
                        mFTP.login("uploads", "Gv123456*");
                        mFTP.setFileType(FTP.BINARY_FILE_TYPE);
                        mFTP.enterLocalPassiveMode();

                        // Prepare file to be uploaded to FTP Server
                        File file = new File(params[0]);
                        FileInputStream ifile = new FileInputStream(file);

                        // Upload file to FTP Server
                        mFTP.storeFile("filetotranfer",ifile);
                        mFTP.disconnect();
                    } catch (SocketException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.dismiss();
            }
        }

        new EnviarFTP().execute();

    }


    public void EnviarOportunidadAlServidorOtro(final Long idInsert, final String... params) {

        fab.setEnabled(false);
        final ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setIndeterminate(false);
        progressBar.setTitle("Subiendo foto, espere...");
        progressBar.setIcon(android.R.drawable.ic_menu_send);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.show();

        try {

            SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST,
                    URL_WS_GUARDAR_OPORTUNIDAD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.w("ResponseOport", response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success == 1) {
                                    Toast.makeText(Oportunidades.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                                    LimpiarCampos();

                                    try {
                                        StringBuilder statusFoto = new StringBuilder();

                                        int foto1success = jsonObject.getInt("foto1success"), foto2success = jsonObject.getInt("foto2success");

                                        if (foto1success == 1) {
                                            statusFoto.append("Foto1: OK. ").append(jsonObject.getString("foto1")).append("\n");
                                            if (new File(params[8]).exists()) {
                                                //boolean result = new File(params[8]).delete();
                                            }
                                        } else {
                                            EnviarFTP( params[8]);
                                            //statusFoto.append("Foto1: ERROR. ").append(jsonObject.getString("foto1")).append("\n");
                                        }
                                        if (foto2success == 1) {
                                            statusFoto.append("Foto2: OK. ").append(jsonObject.getString("foto2")).append("\n");
                                            if (new File(params[9]).exists()) {
                                               // new File(params[9]).delete();
                                            }
                                        } else {
                                            EnviarFTP( params[9]);
                                            //statusFoto.append("Foto2: ERROR. ").append(jsonObject.getString("foto2")).append("\n");
                                        }

                                        new classCustomToast(Oportunidades.this).Toast(statusFoto.toString(), R.drawable.ic_info);

                                        if (foto1success == 1 && foto2success == 1) {
                                            baseDatos.BorrarRegistroWhere(TBL_OPORTUNIDADES, "idOport = " + idInsert);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    Oportunidades.this.recreate();

                                } else {
                                    Toast.makeText(Oportunidades.this, message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                // JSON error
                                e.printStackTrace();
                                Toast.makeText(Oportunidades.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            progressBar.cancel();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    progressBar.cancel();
                    Toast.makeText(Oportunidades.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                }

            });

            //se agrega el parametro de la fotografia
            smr.addStringParam("Pais", params[0]);
            smr.addStringParam("Tienda", params[1]);
            smr.addStringParam("Categoria", params[2]);
            smr.addStringParam("Formato", params[3]);
            smr.addStringParam("Oportunidad", params[4]);
            smr.addStringParam("AreaResponsable", params[5]);
            smr.addStringParam("Producto", params[6]);
            smr.addStringParam("Solucion", params[7]);
            smr.addFile("PathFoto1", params[8]);
            smr.addStringParam("NameFoto1", new File(params[8]).getName());
            smr.addFile("PathFoto2", params[9]);
            smr.addStringParam("NameFoto2", new File(params[9]).getName());
            smr.addStringParam("FechaReg", params[10]);
            smr.addStringParam("Usuario", params[11]);

            smr.setOnProgressListener(new Response.ProgressListener() {
                @Override
                public void onProgress(long transferredBytes, long totalSize) {

                    progressBar.setMax((int) totalSize);
                    progressBar.setProgress((int) transferredBytes);
                    progressBar.setMessage(String.format("Se han enviado %s de %s", "" + transferredBytes, "" + totalSize));

                    if (transferredBytes == totalSize) {
                        progressBar.cancel();
                    }
                    Log.w("trnsferencia", String.format("Se han enviado %s de %s", "" + transferredBytes, "" + totalSize));
                }
            });

            RequestQueue mRequestQueue = Volley.newRequestQueue(Oportunidades.this);
            mRequestQueue.add(smr);
            //ClassMyApplication.getInstance().addToRequestQueue(smr);
        } catch (Exception e) {
            progressBar.cancel();
            e.printStackTrace();
        }
    }

    private void LimpiarCampos() {
        etTienda.setText("");
        etCategoria.setText("");
        etFormato.setText("");
        etOportunidad.setText("");
        etAreaResponsable.setText("");
        etProducto.setText("");
        etSolucion.setText("");
        strPathFoto1 = "";
        strPathFoto2 = "";
    }

    private void LlenarEnviosPendientes() {
        try {

            final Cursor cEncvPend = baseDatos.obtenerRegistro(TBL_OPORTUNIDADES);
            for (cEncvPend.moveToFirst(); !cEncvPend.isAfterLast(); cEncvPend.moveToNext()) {

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.row_envios_pendientes_oportunidades, null);

                TextView tvid = rowView.findViewById(R.id.tvIdLocalDB_envPendOport);
                TextView tvpdv = rowView.findViewById(R.id.tvPDV_envPendOport);
                TextView tvfecha = rowView.findViewById(R.id.tvFecha_envPendOport);

                String idOport = cEncvPend.getString(cEncvPend.getColumnIndex("idOport"));
                tvid.setText(idOport);
                tvpdv.setText(cEncvPend.getString(cEncvPend.getColumnIndex("Tienda")));
                tvfecha.setText(cEncvPend.getString(cEncvPend.getColumnIndex("FechaReg")));

                ImageView ivfoto1row = rowView.findViewById(R.id.ivFoto1_envPendOport);
                ImageView ivfoto2row = rowView.findViewById(R.id.ivFoto2_envPendOport);
                String foto1 = cEncvPend.getString(cEncvPend.getColumnIndex("PathFoto1"));
                String foto2 = cEncvPend.getString(cEncvPend.getColumnIndex("PathFoto2"));
                if (new File(foto1).exists() && new File(foto2).exists()) {
                    Glide.with(this).load(foto1).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ivfoto1row);
                    Glide.with(this).load(foto2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ivfoto2row);
                } else {
                    baseDatos.BorrarRegistroWhere(TBL_OPORTUNIDADES, "idOport = " + idOport);
                    continue;
                }

                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String id = ((TextView) v.findViewById(R.id.tvIdLocalDB_envPendOport)).getText().toString();
                        Cursor cEnvio = baseDatos.obtenerRegistroWhereArgs(TBL_OPORTUNIDADES, "idOport = " + id);
                        cEnvio.moveToFirst();
                        if (G.TieneConexion()) {

                            EnviarOportunidadAlServidorOtro(
                                    cEnvio.getLong(cEncvPend.getColumnIndex("idOport")),
                                    cEnvio.getString(cEncvPend.getColumnIndex("Pais")),
                                    cEnvio.getString(cEncvPend.getColumnIndex("Tienda")),
                                    cEnvio.getString(cEncvPend.getColumnIndex("Categoria")),
                                    cEnvio.getString(cEncvPend.getColumnIndex("Formato")),
                                    cEnvio.getString(cEncvPend.getColumnIndex("Oportunidad")),
                                    cEnvio.getString(cEncvPend.getColumnIndex("AreaResponsable")),
                                    cEnvio.getString(cEncvPend.getColumnIndex("Producto")),
                                    cEnvio.getString(cEncvPend.getColumnIndex("Solucion")),
                                    cEnvio.getString(cEncvPend.getColumnIndex("PathFoto1")),
                                    cEnvio.getString(cEncvPend.getColumnIndex("PathFoto2")),
                                    cEnvio.getString(cEncvPend.getColumnIndex("FechaReg")),
                                    "" + PreferenceManager.getDefaultSharedPreferences(Oportunidades.this).getInt(SETT_COD_USUARIO, 0));
                        } else {
                            new classCustomToast(Oportunidades.this).Toast("No tienes internet.", R.drawable.ic_error);
                            recreate();
                        }

                    }
                });
                // Add the new row.
                llEnviosPend.addView(rowView);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
