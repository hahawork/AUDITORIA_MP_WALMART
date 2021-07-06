package gv.haha.auditoria_mp_walmart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gv.haha.auditoria_mp_walmart.clases.AdapterEvaluarpdv;
import gv.haha.auditoria_mp_walmart.clases.BaseDatos;
import gv.haha.auditoria_mp_walmart.clases.Contenido;
import gv.haha.auditoria_mp_walmart.clases.classCustomToast;

import static gv.haha.auditoria_mp_walmart.clases.Globales.isExternalStorageAvailable;
import static gv.haha.auditoria_mp_walmart.clases.Globales.isExternalStorageReadOnly;
import static gv.haha.auditoria_mp_walmart.clases.Variables.CARPETA_RECURSOS;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_FOTO_INDCADOR;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_PUNTOSDEVENTA;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_DETALLE;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_ENCABEZADO;

public class RevisionPdv extends AppCompatActivity implements View.OnClickListener {

    // ArrayList for person names
    //ArrayList personNames = new ArrayList<>(Arrays.asList("Person 1", "Person 2", "Person 3", "Person 4", "Person 5", "Person 6", "Person 7","Person 8", "Person 9", "Person 10", "Person 11", "Person 12", "Person 13", "Person 14"));
    //ArrayList personImages = new ArrayList<>(Arrays.asList(R.drawable.person1, R.drawable.person2, R.drawable.person3, R.drawable.person4, R.drawable.person5, R.drawable.person6, R.drawable.person7,R.drawable.person1, R.drawable.person2, R.drawable.person3, R.drawable.person4, R.drawable.person5, R.drawable.person6, R.drawable.person7));

    RecyclerView recyclerView;
    ArrayList<Contenido> arrContenido = new ArrayList<>();
    static Uri ImagenTomadaCamara = null;
    int FotoParaIndSeleccionado = 0;

    ArrayList<String> arrFotosInd1 = new ArrayList<>();
    ArrayList<String> arrFotosInd2 = new ArrayList<>();
    ArrayList<String> arrFotosInd3 = new ArrayList<>();
    ArrayList<String> arrFotosInd4 = new ArrayList<>();
    ArrayList<String> arrFotosInd5 = new ArrayList<>();
    ArrayList<String> arrFotosInd6 = new ArrayList<>();
    ArrayList<String> arrFotosInd7 = new ArrayList<>();
    ArrayList<String> arrFotosInd8 = new ArrayList<>();
    ArrayList<String> arrFotosInd9 = new ArrayList<>();
    ArrayList<String> arrFotosInd10 = new ArrayList<>();


    public static View.OnClickListener myOnClickListener;
    static SimpleDateFormat format_fecha = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat format_hora = new SimpleDateFormat("hh:mm a");
    SimpleDateFormat format_fechahora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    EditText tvFechaVisita, etitemsTienda, etItemsAgotados, etParticipacion, etHoraVisita, etResponsableTurno, etObservaciones;
    AutoCompleteTextView tvPDV;
    Button btnFirmaMP, btnFirmaPDV;
    Bitmap bmFirmaMP, bmFirmaPDV;

    LinearLayout llFotosInd2, llFotosIndic4;
    Long idRptEncabezadoActual = 0l;
    int estadoTerminado = 0;
    BaseDatos baseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision_pdv);

        iniciarToolBar();

        iniciarComponentes();

        getReporteEnProceso();


        //startService(new Intent(this,GPSService.class));
    }

    private void iniciarToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Explode());
        }
    }

    private void iniciarComponentes() {

        baseDatos = new BaseDatos(this);
        myOnClickListener = new MyOnClickListener(this);

        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                //  Toast.makeText(getApplicationContext(),newState.name().toString(),Toast.LENGTH_SHORT).show();
                if (newState.name().toString().equalsIgnoreCase("Collapsed")) {

                    ((ImageView) findViewById(R.id.iv_slideup_indicator)).setImageResource(R.drawable.slide_up_indicador);
                    //action when collapsed

                } else if (newState.name().equalsIgnoreCase("Expanded")) {

                    //action when expanded
                    ((ImageView) findViewById(R.id.iv_slideup_indicator)).setImageResource(R.drawable.slide_down_indicador);
                }

            }
        });

        btnFirmaMP = (Button) findViewById(R.id.btnFirmaMP_supanel);
        btnFirmaMP.setOnClickListener(this);
        btnFirmaPDV = (Button) findViewById(R.id.btnFirmaPDV_supanel);
        btnFirmaPDV.setOnClickListener(this);
        tvPDV = (AutoCompleteTextView) findViewById(R.id.tvNombrePdV_RP);
        tvFechaVisita = (EditText) findViewById(R.id.tvFechaVisita_RP);

        etitemsTienda = (EditText) findViewById(R.id.etItemsTienda_RP);
        etItemsAgotados = (EditText) findViewById(R.id.etItemsAgotados_RP);
        etParticipacion = (EditText) findViewById(R.id.etParticipacion_RP);
        etHoraVisita = (EditText) findViewById(R.id.etHoraVisita_RP);
        etResponsableTurno = (EditText) findViewById(R.id.etResponsableTurno_RP);
        etObservaciones = (EditText) findViewById(R.id.etObservaciones_RP);

        llFotosInd2 = (LinearLayout) findViewById(R.id.llFotosParaIndic2_RP);
        llFotosIndic4 = (LinearLayout) findViewById(R.id.llFotosParaIndic4_RP);

        tvFechaVisita.setText(format_fecha.format(new Date()));
        etHoraVisita.setText(format_hora.format(new Date()));


        //se cargan los nombres de los pdv que ya se han ingresados
        String[] arrPdv = baseDatos.getArrayDataFromTable(TBL_PUNTOSDEVENTA, "NombrePdV");
        ArrayAdapter<String> adapterPdv = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrPdv);
        tvPDV.setAdapter(adapterPdv);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // set a GridLayoutManager with default vertical orientation
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        // call the constructor of AdapterEvaluarpdv to send the reference and data to Adapter
        getDataIndicadores();
        AdapterEvaluarpdv customAdapter = new AdapterEvaluarpdv(RevisionPdv.this, arrContenido);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView

    }

    private void getDataIndicadores() {
        // Add some sample items.
        arrContenido.add(new Contenido(1, "ACTIVIDAD COMERCIAL", false));
        arrContenido.add(new Contenido(2, "REVISION DE MODULARES", true));
        arrContenido.add(new Contenido(3, "ROTULACIÓN GP-RB-LIQUIDACIONES-REBAJAS", false));
        arrContenido.add(new Contenido(4, "LLENADO DE GONDOLA CATEGORIA FALT/CON INV.", true));
        arrContenido.add(new Contenido(5, "AJUSTE DE IP", false));
        arrContenido.add(new Contenido(6, "AJUSTE NEGATIVA", false));
        arrContenido.add(new Contenido(7, "REVISIÓN ITEM SIN MOV. 5 SEM.", false));
        arrContenido.add(new Contenido(8, "USO EMPAQUE EFICIENTE", false));
        arrContenido.add(new Contenido(9, "PRECIOS GONDOLA", false));
        arrContenido.add(new Contenido(10, "EVENTOS POR FECHA", false));

        for (int i = 0; i < arrContenido.size(); i++) {
            arrContenido.get(i).setComentario("");
        }
    }

    private void getReporteEnProceso() {

        final Cursor cEncabez = baseDatos.obtenerRegistroWhereArgs(TBL_REPORTE_ENCABEZADO, "EstadoTerminado = 0");
        //si hay un reporte en proceso.
        if (cEncabez.getCount() > 0) {

            //se ubica en el primer registro del cursor
            cEncabez.moveToFirst();


            new AlertDialog.Builder(this)
                    .setTitle("Reporte de " + cEncabez.getString(cEncabez.getColumnIndex("NombrePDV")) + " en proceso.")
                    .setMessage("Que desea hacer?")
                    .setCancelable(false)
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                            //encabezado del reporte
                            idRptEncabezadoActual = cEncabez.getLong(cEncabez.getColumnIndex("IdRptEnc"));
                            tvPDV.setText(cEncabez.getString(cEncabez.getColumnIndex("NombrePDV")));
                            tvFechaVisita.setText(cEncabez.getString(cEncabez.getColumnIndex("FechaVisita")));
                            etitemsTienda.setText(cEncabez.getString(cEncabez.getColumnIndex("ItemsTienda")));
                            etItemsAgotados.setText(cEncabez.getString(cEncabez.getColumnIndex("ItemsAgotados")));
                            etParticipacion.setText(cEncabez.getString(cEncabez.getColumnIndex("Participacion")));
                            etHoraVisita.setText(cEncabez.getString(cEncabez.getColumnIndex("HoraVisita")));
                            etResponsableTurno.setText(cEncabez.getString(cEncabez.getColumnIndex("ResponsableTurno")));
                            etObservaciones.setText(cEncabez.getString(cEncabez.getColumnIndex("Observaciones")));


                            if (cEncabez.getInt(cEncabez.getColumnIndex("TieneFirmaMP")) == 1) {
                                String path = CARPETA_RECURSOS + "ultimafirmamp.png";
                                bmFirmaMP = BitmapFactory.decodeFile(path);
                                ((ImageView) findViewById(R.id.ivFirmaMP_supanel)).setImageBitmap(bmFirmaMP);
                            }
                            if (cEncabez.getInt(cEncabez.getColumnIndex("TieneFirmaPDV")) == 1) {
                                String path = CARPETA_RECURSOS + "ultimafirmapdv.png";
                                bmFirmaPDV = BitmapFactory.decodeFile(path);
                                ((ImageView) findViewById(R.id.ivFirmaPDV_supanel)).setImageBitmap(bmFirmaPDV);
                            }


                            //recoge el detalle del reporte
                            Cursor cRptDet = baseDatos.obtenerRegistroWhereArgs(
                                    TBL_REPORTE_DETALLE,
                                    "idRptEnc = " + String.valueOf(idRptEncabezadoActual)
                            );
                            for (cRptDet.moveToFirst(); !cRptDet.isAfterLast(); cRptDet.moveToNext()) {

                                arrContenido.get(cRptDet.getPosition()).setIdGuardadoLocaldb(cRptDet.getInt(cRptDet.getColumnIndex("IdRptDet")));
                                arrContenido.get(cRptDet.getPosition()).setAplicado(cRptDet.getInt(cRptDet.getColumnIndex("cantAplicado")));
                                arrContenido.get(cRptDet.getPosition()).setPendiente(cRptDet.getInt(cRptDet.getColumnIndex("cantPendiente")));
                                arrContenido.get(cRptDet.getPosition()).setComentario(cRptDet.getString(cRptDet.getColumnIndex("comentarios")));
                            }

                            //recoge las imagenes del reporte
                            Cursor cRptDetFotos = baseDatos.obtenerRegistroWhereArgs(
                                    TBL_FOTO_INDCADOR,
                                    "idRptEnc = " + String.valueOf(idRptEncabezadoActual)
                            );
                            for (cRptDetFotos.moveToFirst(); !cRptDetFotos.isAfterLast(); cRptDetFotos.moveToNext()) {
                                int indicador = cRptDetFotos.getInt(cRptDetFotos.getColumnIndex("idIndicad"));
                                //if (indicador == 2) {
                                    String pathfoto = cRptDetFotos.getString(cRptDetFotos.getColumnIndex("pathFoto"));
                                    File foto = new File(pathfoto);
                                    if (foto.exists()) {
                                        arrFotosInd2.add(pathfoto);
                                        AgregarImagenLLayout(pathfoto, llFotosInd2);
                                    } else {
                                        String where = "Idfoto = " + cRptDetFotos.getString(cRptDetFotos.getColumnIndex("Idfoto"));
                                        baseDatos.BorrarRegistroWhere(TBL_FOTO_INDCADOR, where);
                                    }
                               // }
                                /*else if (indicador == 4) {

                                    String pathfoto = cRptDetFotos.getString(cRptDetFotos.getColumnIndex("pathFoto"));
                                    File foto = new File(pathfoto);
                                    if (foto.exists()) {
                                        arrFotosInd4.add(pathfoto);
                                        AgregarImagenLLayout(pathfoto, llFotosIndic4);
                                    } else {
                                        String where = "Idfoto = " + cRptDetFotos.getString(cRptDetFotos.getColumnIndex("Idfoto"));
                                        baseDatos.BorrarRegistroWhere(TBL_FOTO_INDCADOR, where);
                                    }

                                }*/
                            }


                        }
                    })
                    .setNegativeButton("Nuevo Reporte", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            borrarTodo();

                        }
                    })
                    .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            startActivity(new Intent(RevisionPdv.this, MainActivity.class));
                        }
                    })
                    .show();

        }
    }


    private void guardarTemporal(int EstadoTerminado) {

        ContentValues valuesEnc = new ContentValues();

        if (idRptEncabezadoActual == 0) {

            if (tvPDV.getText().length() > 0) {
                valuesEnc.put("IdRptEnc", (byte[]) null);
                valuesEnc.put("NombrePDV", tvPDV.getText().toString());
                valuesEnc.put("FechaVisita", tvFechaVisita.getText().toString());
                valuesEnc.put("ItemsTienda", etitemsTienda.getText().toString());
                valuesEnc.put("ItemsAgotados", etItemsAgotados.getText().toString());
                valuesEnc.put("Participacion", etParticipacion.getText().toString());
                valuesEnc.put("HoraVisita", etHoraVisita.getText().toString());
                valuesEnc.put("ResponsableTurno", etResponsableTurno.getText().toString());
                valuesEnc.put("Observaciones", etObservaciones.getText().toString());
                valuesEnc.put("FechaRegistro", format_fechahora.format(new Date()));
                valuesEnc.put("TieneFirmaMP", bmFirmaMP == null ? 0 : 1);
                valuesEnc.put("TieneFirmaPDV", bmFirmaPDV == null ? 0 : 1);
                valuesEnc.put("EstadoTerminado", EstadoTerminado);
                valuesEnc.put("EstadoEnviado", 0);

                Long result = baseDatos.insertarRegistro(TBL_REPORTE_ENCABEZADO, valuesEnc);
                //si se inserto el encabezado
                if (result > 0) {

                    idRptEncabezadoActual = result;

                    //se guarda tambien el detalle del reporte
                    for (int i = 0; i < arrContenido.size(); i++) {
                        ContentValues valuesDet = new ContentValues();
                        valuesDet.put("IdRptDet", (byte[]) null);
                        valuesDet.put("idRptEnc", idRptEncabezadoActual);
                        valuesDet.put("idIndicador", arrContenido.get(i).getNumIndicador());
                        valuesDet.put("descIndicador", arrContenido.get(i).getIndicador());
                        valuesDet.put("cantAplicado", arrContenido.get(i).getAplicado());
                        valuesDet.put("cantPendiente", arrContenido.get(i).getPendiente());
                        valuesDet.put("comentarios", arrContenido.get(i).getComentario());

                        Long resultDet = baseDatos.insertarRegistro(TBL_REPORTE_DETALLE, valuesDet);

                        arrContenido.get(i).setIdGuardadoLocaldb(Integer.valueOf(String.valueOf(resultDet)));
                    }
                }
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Ingrese nombre del pdv")
                        .setPositiveButton("Entendido", null)
                        .show();
            }

        } else {
            //se actualizan algunos datos
            valuesEnc.put("ItemsTienda", etitemsTienda.getText().toString());
            valuesEnc.put("ItemsAgotados", etItemsAgotados.getText().toString());
            valuesEnc.put("Participacion", etParticipacion.getText().toString());
            valuesEnc.put("ResponsableTurno", etResponsableTurno.getText().toString());
            valuesEnc.put("Observaciones", etObservaciones.getText().toString());
            valuesEnc.put("TieneFirmaMP", bmFirmaMP == null ? 0 : 1);
            valuesEnc.put("TieneFirmaPDV", bmFirmaPDV == null ? 0 : 1);
            valuesEnc.put("EstadoTerminado", EstadoTerminado);

            int result = baseDatos.actualizarRegistro(
                    TBL_REPORTE_ENCABEZADO,
                    valuesEnc,
                    "IdRptEnc = " + idRptEncabezadoActual);

            //se guarda tambien el detalle del reporte
            for (int i = 0; i < arrContenido.size(); i++) {
                ContentValues valuesDet = new ContentValues();
                valuesDet.put("cantAplicado", arrContenido.get(i).getAplicado());
                valuesDet.put("cantPendiente", arrContenido.get(i).getPendiente());
                valuesDet.put("comentarios", arrContenido.get(i).getComentario());

                int resultDet = baseDatos.actualizarRegistro(
                        TBL_REPORTE_DETALLE,
                        valuesDet,
                        "IdRptDet = " + arrContenido.get(i).getIdGuardadoLocaldb() + " and idRptEnc = " + idRptEncabezadoActual
                );
            }
        }

    }

    private void borrarTodo() {

        //limpiar las tablas de la DB
        baseDatos.BorrarRegistroWhere(TBL_FOTO_INDCADOR, "idRptEnc = " + idRptEncabezadoActual);
        baseDatos.BorrarRegistroWhere(TBL_REPORTE_DETALLE, "idRptEnc = " + idRptEncabezadoActual);
        baseDatos.BorrarRegistroWhere(TBL_REPORTE_ENCABEZADO, "IdRptEnc = " + idRptEncabezadoActual);

        //borrar las imagenes de las firmas
        File firmaMP = new File(CARPETA_RECURSOS + "ultimafirmamp.png");
        File firmaPDV = new File(CARPETA_RECURSOS + "ultimafirmapdv.png");

        if (firmaMP.exists())
            firmaMP.delete();

        if (firmaPDV.exists())
            firmaPDV.delete();

        //borra las fotos tomadas para el reporte
        File directorio = new File(CARPETA_RECURSOS);
        File[] files = directorio.listFiles();
        for (int i = 0; i < files.length; i++) {

            File imgFile = new File(CARPETA_RECURSOS + files[i].getName());
            if (imgFile.exists()) {

                String nombrearchivo = imgFile.getName();
                //si el nombre del archivo comienza con DBEnc + el id a borrar
                if (nombrearchivo.contains(String.format("DBEnc%d", idRptEncabezadoActual))) {
                    // se borra el archivo
                    imgFile.delete();
                }

            }
        }

    }

    public void DialogoDetalleIndicador(final int Indicad) {

        final Dialog dialogIntro = new Dialog(this);
        dialogIntro.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogIntro.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialogIntro.getWindow();
        lp.copyFrom(window.getAttributes());
        dialogIntro.setCancelable(false);
        dialogIntro.setCanceledOnTouchOutside(false);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        dialogIntro.setContentView(R.layout.indicador_detail);

        Button btnGuardar = (Button) dialogIntro.findViewById(R.id.btnGuardar_dg_indicador);
        Button btnCancelar = (Button) dialogIntro.findViewById(R.id.btnCancelar_dg_indicador);


        final TextInputEditText etAplicado = (TextInputEditText) dialogIntro.findViewById(R.id.etAplicado_dg_indicador);
        final TextInputEditText etPendiente = (TextInputEditText) dialogIntro.findViewById(R.id.etPendiente_dg_indicador);
        final EditText etComentario = (EditText) dialogIntro.findViewById(R.id.etComentarios_dg_indicador);

        etAplicado.setText((arrContenido.get(Indicad).getAplicado() > 0) ? String.valueOf(arrContenido.get(Indicad).getAplicado()) : "");
        etPendiente.setText((arrContenido.get(Indicad).getPendiente() > 0) ? String.valueOf(arrContenido.get(Indicad).getPendiente()) : "");
        etComentario.setText(String.valueOf(arrContenido.get(Indicad).getComentario()));

        ((TextView) dialogIntro.findViewById(R.id.tvIndicadorSelecc_dg_indicador)).setText(arrContenido.get(Indicad).getIndicador());

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean ok = true;
                String cant_aplicado = TextUtils.isEmpty(etAplicado.getText()) ? "" : etAplicado.getText().toString();
                if (cant_aplicado.length() == 0) {
                    ok = false;
                    ((TextInputLayout) dialogIntro.findViewById(R.id.tilAplicado_dg_indicador)).setError("(*) Este campo es requerido");
                }

                String cant_pendiente = TextUtils.isEmpty(etPendiente.getText()) ? "" : etPendiente.getText().toString();
                if (cant_pendiente.length() == 0) {
                    ok = false;
                    ((TextInputLayout) dialogIntro.findViewById(R.id.tilPendiente_dg_indicador)).setError("(*) Este campo es requerido");
                }

                if (ok) {

                    try {
                        arrContenido.get(Indicad).setAplicado((int) (Float.parseFloat(cant_aplicado)));
                        arrContenido.get(Indicad).setPendiente((int) (Float.parseFloat(cant_pendiente)));
                        arrContenido.get(Indicad).setComentario(etComentario.getText().toString());

                    } catch (Exception e) {
                        new classCustomToast(RevisionPdv.this).Show_ToastError("Error guardando " + e.getMessage());
                    }
                    dialogIntro.cancel();
                }
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

    @Override
    public void onClick(View view) {
        if (view == btnFirmaMP) {
            startActivityForResult(new Intent(this, Firmar.class), 111);
        }
        if (view == btnFirmaPDV) {
            startActivityForResult(new Intent(this, Firmar.class), 112);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_revision_pdv, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id==android.R.id.home){

        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            VerificarRequerimientosParaReporte();
            return true;
        }
        if (id == R.id.action_save_temp) {
            guardarTemporal(estadoTerminado);
            return true;
        }
        if (id == R.id.action_cancel) {
            borrarTodo();
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void VerificarRequerimientosParaReporte() {

        List<String> errores = new ArrayList<>();
        boolean ok = true;
        if (TextUtils.isEmpty(tvPDV.getText())) {
            ok = false;
            errores.add("- Punto de venta.");
        }
        if (TextUtils.isEmpty(tvFechaVisita.getText())) {
            ok = false;
            errores.add("- Fecha de visita.");
        }
        if (TextUtils.isEmpty(etHoraVisita.getText())) {
            ok = false;
            errores.add("- Hora de visita.");
        }
        if (TextUtils.isEmpty(etResponsableTurno.getText())) {
            ok = false;
            errores.add("- Responsable de turno.");
        }
        if (bmFirmaMP == null) {
            ok = false;
            errores.add("- Firma del MP.");
        }
        if (bmFirmaPDV == null) {
            ok = false;
            errores.add("- Firma del PDV.");
        }


        if (ok) {

            String nombreArchivo = String.format("FORMATO_R_MP_%s_%s.xls",
                    tvPDV.getText().toString().replace(" ", ""),
                    format_fecha.format(new Date()).replace("/", "")
            );
            if (saveExcelFile(this, nombreArchivo)) {
                EnviarCorreo(nombreArchivo);
            }

        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Por favor verificar")
                    .setItems(errores.toArray(new String[errores.size()]), null)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check that it is the SecondActivity with an OK result

        if (resultCode == RESULT_OK) {

            //firma MP
            if (requestCode == 111 & data != null) {
                bmFirmaMP = BitmapFactory.decodeByteArray(
                        data.getByteArrayExtra("byteArray"), 0,
                        data.getByteArrayExtra("byteArray").length);

                ((ImageView) findViewById(R.id.ivFirmaMP_supanel)).setImageBitmap(bmFirmaMP);
                GuardaImagenMemoria(bmFirmaMP, "ultimafirmamp.png");
            }
            //firma PDV
            if (requestCode == 112 & data != null) {
                bmFirmaPDV = BitmapFactory.decodeByteArray(
                        data.getByteArrayExtra("byteArray"), 0,
                        data.getByteArrayExtra("byteArray").length);


                ((ImageView) findViewById(R.id.ivFirmaPDV_supanel)).setImageBitmap(bmFirmaPDV);

                GuardaImagenMemoria(bmFirmaPDV, "ultimafirmapdv.png");
            }

            // tomar foto desde la camara
            if (requestCode == 113) {
                try {
                    Uri selectedImageUri;
                    //si aun no hay un registro temporal en la DB se guarda
                    if (idRptEncabezadoActual == 0) {
                        guardarTemporal(estadoTerminado);
                    }

                    selectedImageUri = ImagenTomadaCamara;

                    //obtine la posicion del indicador al que se va a adjuntar la foto tomada
                    int idIndicador = arrContenido.get(FotoParaIndSeleccionado).getNumIndicador();


                    AgregarImagenLLayout(
                            selectedImageUri.getPath(),llFotosInd2);

                    //guardamos el registro de la foto en la Db
                    ContentValues values = new ContentValues();
                    values.put("Idfoto", (byte[]) null);
                    values.put("idRptEnc", idRptEncabezadoActual);
                    values.put("idRptDet", arrContenido.get(FotoParaIndSeleccionado).getIdGuardadoLocaldb());
                    values.put("idIndicad", arrContenido.get(FotoParaIndSeleccionado).getNumIndicador());
                    values.put("pathFoto", selectedImageUri.getPath());
                    values.put("FechaToma", format_fechahora.format(new Date()));

                    Long id = baseDatos.insertarRegistro(TBL_FOTO_INDCADOR, values);


                    if (idIndicador == 1) {
                        arrFotosInd1.add(selectedImageUri.getPath());
                    }
                    if (idIndicador == 2) {
                        arrFotosInd2.add(selectedImageUri.getPath());
                    }
                    if (idIndicador == 3) {
                        arrFotosInd3.add(selectedImageUri.getPath());
                    }
                    if (idIndicador == 4) {
                        arrFotosInd4.add(selectedImageUri.getPath());
                    }
                    if (idIndicador == 5) {
                        arrFotosInd5.add(selectedImageUri.getPath());
                    }
                    if (idIndicador == 9) {
                        arrFotosInd9.add(selectedImageUri.getPath());
                    }
                    if (idIndicador == 6) {
                        arrFotosInd6.add(selectedImageUri.getPath());
                    }
                    if (idIndicador == 7) {
                        arrFotosInd7.add(selectedImageUri.getPath());
                    }
                    if (idIndicador == 8) {
                        arrFotosInd8.add(selectedImageUri.getPath());
                    }
                    if (idIndicador == 10) {
                        arrFotosInd10.add(selectedImageUri.getPath());
                    }

                    new classCustomToast(this).Toast("Ind: " + arrContenido.get(FotoParaIndSeleccionado).getNumIndicador() +
                            ", Se ha guardado la foto. con el id " + id, R.drawable.ic_success);

                } catch (Exception e) {
                    e.printStackTrace();
                    new classCustomToast(RevisionPdv.this).Show_ToastError("Upss algo ha salido mal.");
                }
            }
        }
    }

    private void AgregarImagenLLayout(String path, LinearLayout llfotos) {
        try {
            File foto = new File(path);
            if (foto.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 5;
                Bitmap bmp = BitmapFactory.decodeFile(path, options);

                LinearLayout.LayoutParams params = new LinearLayout
                        .LayoutParams(79, 79);
                ImageView item = new ImageView(this);
                item.setOnClickListener(this);
                item.setLayoutParams(params);
                item.isClickable();
                item.hasOnClickListeners();
                item.setImageBitmap(bmp);
                llfotos.addView(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean saveExcelFile(Context context, String fileName) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e("Error", "Storage not available or read only");
            return false;
        }

        boolean success = false;

        int posicFila = 0;

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(0, tvPDV.getText().toString());
        // Generate column headings
        HSSFRow hRow = null;
        HSSFCell hCell = null;

        sheet.setDisplayGridlines(false);

        // create 2 fonts objects
        HSSFFont fmainhead = workbook.createFont();
        HSSFFont fheader = workbook.createFont();
        HSSFFont ftableheader = workbook.createFont();
        HSSFFont fdata = workbook.createFont();
        // create 3 cell styles
        HSSFCellStyle stmainhead = workbook.createCellStyle();
        HSSFCellStyle sthead = workbook.createCellStyle();
        HSSFCellStyle sttablehead = workbook.createCellStyle();
        HSSFCellStyle stdata = workbook.createCellStyle();
        HSSFCellStyle stdata_centered = workbook.createCellStyle();


        sheet.setColumnWidth(0, (50 * 256));
        sheet.setColumnWidth(1, (15 * 256));
        sheet.setColumnWidth(2, (15 * 256));
        sheet.setColumnWidth(3, (50 * 256));

        fmainhead.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fmainhead.setFontHeightInPoints((short) 15);
        fmainhead.setColor(HSSFColor.WHITE.index);
        fmainhead.setBold(true);

        fheader.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fheader.setFontHeightInPoints((short) 12);
        fheader.setColor(HSSFColor.BLACK.index);

        ftableheader.setBoldweight(Font.BOLDWEIGHT_BOLD);
        ftableheader.setFontHeightInPoints((short) 12);
        ftableheader.setColor(HSSFColor.WHITE.index);

        fdata.setColor(HSSFColor.BLACK.index);
        fdata.setFontHeightInPoints((short) 10);

        stmainhead.setFont(fmainhead);
        stmainhead.setAlignment(CellStyle.ALIGN_CENTER);
        stmainhead.setFillPattern(CellStyle.SOLID_FOREGROUND);
        stmainhead.setFillForegroundColor(HSSFColor.DARK_BLUE.index);
        stmainhead.setAlignment(CellStyle.ALIGN_CENTER);
        stmainhead.setBorderBottom(CellStyle.BORDER_THICK);
        stmainhead.setBorderLeft(CellStyle.BORDER_THICK);
        stmainhead.setBorderRight(CellStyle.BORDER_THICK);
        stmainhead.setBorderTop(CellStyle.BORDER_THICK);

        sthead.setAlignment(CellStyle.ALIGN_RIGHT);
        sthead.setFont(fheader);
        sthead.setVerticalAlignment(CellStyle.VERTICAL_CENTER);


        sttablehead.setFont(ftableheader);
        sttablehead.setAlignment(CellStyle.ALIGN_CENTER);
        sttablehead.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        sttablehead.setFillForegroundColor(HSSFColor.DARK_BLUE.index);
        sttablehead.setFillPattern(CellStyle.SOLID_FOREGROUND);
        sttablehead.setBorderBottom(CellStyle.BORDER_MEDIUM);
        sttablehead.setBorderLeft(CellStyle.BORDER_MEDIUM);
        sttablehead.setBorderRight(CellStyle.BORDER_MEDIUM);
        sttablehead.setBorderTop(CellStyle.BORDER_MEDIUM);

        stdata.setFont(fdata);
        stdata.setWrapText(true);
        stdata.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        stdata.setBorderBottom(CellStyle.BORDER_THIN);
        stdata.setBorderLeft(CellStyle.BORDER_THIN);
        stdata.setBorderRight(CellStyle.BORDER_THIN);
        stdata.setBorderTop(CellStyle.BORDER_THIN);

        stdata_centered.setFont(fdata);
        stdata_centered.setAlignment(CellStyle.ALIGN_CENTER);
        stdata_centered.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        stdata_centered.setBorderBottom(CellStyle.BORDER_THIN);
        stdata_centered.setBorderLeft(CellStyle.BORDER_THIN);
        stdata_centered.setBorderRight(CellStyle.BORDER_THIN);
        stdata_centered.setBorderTop(CellStyle.BORDER_THIN);

        // Merges the cells
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, 3);
        sheet.addMergedRegion(cellRangeAddress);

        hRow = sheet.createRow(posicFila);
        hCell = hRow.createCell(0);
        hCell.setCellStyle(stmainhead);
        hCell.setCellValue("FORMATO REVISION MARCAS PRIVADAS");


        //region EncabezadoReporte

        posicFila = posicFila + 2;
        hRow = sheet.createRow(posicFila++);
        hCell = hRow.createCell(0);
        hCell.setCellStyle(sthead);
        hCell.setCellValue("NOMBRE DEL PDV");
        hCell = hRow.createCell(1);
        hCell.setCellStyle(stdata);
        hCell.setCellValue(tvPDV.getText().toString());


        hRow = sheet.createRow(posicFila++);
        hCell = hRow.createCell(0);
        hCell.setCellStyle(sthead);
        hCell.setCellValue("FECHA DE VISITA");
        hCell = hRow.createCell(1);
        hCell.setCellStyle(stdata);
        hCell.setCellValue(tvFechaVisita.getText().toString());

        hRow = sheet.createRow(posicFila++);
        hCell = hRow.createCell(0);
        hCell.setCellStyle(sthead);
        hCell.setCellValue("# ITEMS EN TIENDA");
        hCell = hRow.createCell(1);
        hCell.setCellStyle(stdata);
        hCell.setCellValue(etitemsTienda.getText().toString());

        hRow = sheet.createRow(posicFila++);
        hCell = hRow.createCell(0);
        hCell.setCellStyle(sthead);
        hCell.setCellValue("# AGOTADOS");
        hCell = hRow.createCell(1);
        hCell.setCellStyle(stdata);
        hCell.setCellValue(etItemsAgotados.getText().toString());

        hRow = sheet.createRow(posicFila++);
        hCell = hRow.createCell(0);
        hCell.setCellStyle(sthead);
        hCell.setCellValue("PARTICIPACION %");
        hCell = hRow.createCell(1);
        hCell.setCellStyle(stdata);
        hCell.setCellValue(etParticipacion.getText().toString());
//endregion

// region CuerpoReporte

        String[] headers = new String[]{"INDICADOR A REVISAR", "APLICADO", "PENDIENTE", "CAUSAS / SOLUCION COMENTARIOS"};
        posicFila = posicFila + 2;
        hRow = sheet.createRow(posicFila++);
        for (int i = 0; i < headers.length; ++i) {
            String header = headers[i];
            hCell = hRow.createCell(i);
            hCell.setCellStyle(sttablehead);
            hCell.setCellValue(header);
        }

        //para registerar en que fila queda cada indicador
        Integer[] posicIndicadores = new Integer[arrContenido.size()];

        for (int i = 0; i < arrContenido.size(); ++i) {

            posicIndicadores[i] = posicFila;

            HSSFRow dataRow = sheet.createRow(posicFila++);
            //si al menos un indicador tiene fotografia
            if (arrFotosInd1.size() > 0 || arrFotosInd2.size() > 0 || arrFotosInd3.size() > 0 || arrFotosInd4.size() > 0 || arrFotosInd5.size() > 0
                    || arrFotosInd6.size() > 0 || arrFotosInd7.size() > 0 || arrFotosInd8.size() > 0 || arrFotosInd9.size() > 0 || arrFotosInd10.size() > 0) {
                dataRow.setHeightInPoints(80);
            }

            String indicador = arrContenido.get(i).getIndicador();
            BigDecimal aplicado = (BigDecimal) new BigDecimal(arrContenido.get(i).getAplicado());
            BigDecimal pendiente = (BigDecimal) new BigDecimal(arrContenido.get(i).getPendiente());
            String comentario = arrContenido.get(i).getComentario();

            hCell = dataRow.createCell(0);
            hCell.setCellStyle(stdata);
            hCell.setCellValue(indicador);

            hCell = dataRow.createCell(1);
            hCell.setCellStyle(stdata_centered);
            hCell.setCellValue(aplicado.doubleValue());

            hCell = dataRow.createCell(2);
            hCell.setCellStyle(stdata_centered);
            hCell.setCellValue(pendiente.doubleValue());

            hCell = dataRow.createCell(3);
            hCell.setCellStyle(stdata);
            hCell.setCellValue(comentario);
        }

        //endregion

        //region Pie Reporte

        posicFila++;

        hRow = sheet.createRow(posicFila++);
        hCell = hRow.createCell(0);
        hCell.setCellStyle(sthead);
        hCell.setCellValue("OBSERVACIONES DE PDV");

        //sheet.addMergedRegion(new CellRangeAddress(24,24,0,3));
        hRow = sheet.createRow(posicFila++);
        hCell = hRow.createCell(0);
        hCell.setCellStyle(stdata);
        hCell.setCellValue(etObservaciones.getText().toString());

        posicFila = posicFila + 2;
        hRow = sheet.createRow(posicFila++);
        hCell = hRow.createCell(0);
        hCell.setCellStyle(sthead);
        hCell.setCellValue("HORA DE LA VISITA");
        hCell = hRow.createCell(1);
        hCell.setCellStyle(stdata);
        hCell.setCellValue(etHoraVisita.getText().toString());


        hRow = sheet.createRow(posicFila++);
        hCell = hRow.createCell(0);
        hCell.setCellStyle(sthead);
        hCell.setCellValue("RESPONSABLE DE TURNO");
        hCell = hRow.createCell(1);
        hCell.setCellStyle(stdata);
        hCell.setCellValue(etResponsableTurno.getText().toString());

        posicFila = posicFila + 2;
        HSSFRow rowfoto = sheet.createRow(posicFila++);
        rowfoto.createCell(1).setCellValue("FIRMA MP");
        rowfoto.createCell(2).setCellValue("FIRMA PDV");


        HSSFRow picture = sheet.createRow(posicFila);
        picture.setHeightInPoints(80);
        try {
            CreationHelper helper = workbook.getCreationHelper();

            //add a picture in this workbook.
            String pathfirmamp = CARPETA_RECURSOS + "ultimafirmamp.png";
            if (new File(pathfirmamp).exists()) {

                InputStream ismp = new FileInputStream(pathfirmamp);
                byte[] bytes = IOUtils.toByteArray(ismp);
                ismp.close();
                int pictureIdxmp = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);

                //create drawing
                Drawing drawingmp = sheet.createDrawingPatriarch();

                //add a picture shape
                ClientAnchor anchormp = helper.createClientAnchor();
                anchormp.setCol1(1);
                anchormp.setRow1(posicFila);
                anchormp.setCol2(2); //Column C
                anchormp.setRow2(posicFila + 1);

                Picture pictmp = drawingmp.createPicture(anchormp, pictureIdxmp);
            }
            //add a picture in this workbook.
            String pathfirmapdv = CARPETA_RECURSOS + "ultimafirmapdv.png";

            if (new File(pathfirmapdv).exists()) {
                InputStream ispdv = new FileInputStream(pathfirmapdv);
                byte[] bytespdv = IOUtils.toByteArray(ispdv);
                ispdv.close();
                int pictureIdxpdv = workbook.addPicture(bytespdv, Workbook.PICTURE_TYPE_PNG);

                //create drawing
                Drawing drawingpdv = sheet.createDrawingPatriarch();

                //add a picture shape
                ClientAnchor anchorpdv = helper.createClientAnchor();
                anchorpdv.setCol1(2);
                anchorpdv.setRow1(posicFila);
                anchorpdv.setCol2(3); //Column e
                anchorpdv.setRow2(posicFila + 1);
                Picture pictpdv = drawingpdv.createPicture(anchorpdv, pictureIdxpdv);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //se agregan las fotos a la fila segun el indicador
        int posicFoto1 = 5;//la columna inicial,luego incremente en 2 para separacion de fotos
        for (int i = 0; i < arrFotosInd1.size(); i++) {
            agregarFotosAlArchivoExcel(posicIndicadores[0], posicFoto1, workbook, sheet, arrFotosInd1.get(i));
            posicFoto1 = posicFoto1 + 2;
        }
        //se agregan las fotos a la fila segun el indicador
        int posicFoto2 = 5; //la columna inicial,luego incremente en 2 para separacion de fotos
        for (int i = 0; i < arrFotosInd2.size(); i++) {
            agregarFotosAlArchivoExcel(posicIndicadores[1], posicFoto2, workbook, sheet, arrFotosInd2.get(i));
            posicFoto2 = posicFoto2 + 2;
        }
        //se agregan las fotos a la fila segun el indicador
        int posicFoto3 = 5;//la columna inicial,luego incremente en 2 para separacion de fotos
        for (int i = 0; i < arrFotosInd3.size(); i++) {
            agregarFotosAlArchivoExcel(posicIndicadores[2], posicFoto3, workbook, sheet, arrFotosInd3.get(i));
            posicFoto3 = posicFoto3 + 2;
        }
        //se agregan las fotos a la fila segun el indicador
        int posicFoto4 = 5; //la columna inicial,luego incremente en 2 para separacion de fotos
        for (int i = 0; i < arrFotosInd4.size(); i++) {
            agregarFotosAlArchivoExcel(posicIndicadores[3], posicFoto4, workbook, sheet, arrFotosInd4.get(i));
            posicFoto4 = posicFoto4 + 2;
        }

        //se agregan las fotos a la fila segun el indicador
        int posicFoto5 = 5; //la columna inicial,luego incremente en 2 para separacion de fotos
        for (int i = 0; i < arrFotosInd5.size(); i++) {
            agregarFotosAlArchivoExcel(posicIndicadores[4], posicFoto5, workbook, sheet, arrFotosInd5.get(i));
            posicFoto5 = posicFoto5 + 2;
        }
        //se agregan las fotos a la fila segun el indicador
        int posicFoto6 = 5; //la columna inicial,luego incremente en 2 para separacion de fotos
        for (int i = 0; i < arrFotosInd6.size(); i++) {
            agregarFotosAlArchivoExcel(posicIndicadores[5], posicFoto6, workbook, sheet, arrFotosInd6.get(i));
            posicFoto6 = posicFoto6 + 2;
        }
        //se agregan las fotos a la fila segun el indicador
        int posicFoto7 = 5; //la columna inicial,luego incremente en 2 para separacion de fotos
        for (int i = 0; i < arrFotosInd7.size(); i++) {
            agregarFotosAlArchivoExcel(posicIndicadores[6], posicFoto7, workbook, sheet, arrFotosInd7.get(i));
            posicFoto7 = posicFoto7 + 2;
        }
        //se agregan las fotos a la fila segun el indicador
        int posicFoto8 = 5; //la columna inicial,luego incremente en 2 para separacion de fotos
        for (int i = 0; i < arrFotosInd8.size(); i++) {
            agregarFotosAlArchivoExcel(posicIndicadores[7], posicFoto8, workbook, sheet, arrFotosInd8.get(i));
            posicFoto8 = posicFoto8 + 2;
        }
        //se agregan las fotos a la fila segun el indicador
        int posicFoto9 = 5; //la columna inicial,luego incremente en 2 para separacion de fotos
        for (int i = 0; i < arrFotosInd9.size(); i++) {
            agregarFotosAlArchivoExcel(posicIndicadores[8], posicFoto9, workbook, sheet, arrFotosInd9.get(i));
            posicFoto9 = posicFoto9 + 2;
        }
        //se agregan las fotos a la fila segun el indicador
        int posicFoto10 = 5; //la columna inicial,luego incremente en 2 para separacion de fotos
        for (int i = 0; i < arrFotosInd10.size(); i++) {
            agregarFotosAlArchivoExcel(posicIndicadores[9], posicFoto10, workbook, sheet, arrFotosInd10.get(i));
            posicFoto10 = posicFoto10 + 2;
        }

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            workbook.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
            new classCustomToast(this).Toast("Se ha generado el archivo con éxito.", R.drawable.ic_success);
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
            new classCustomToast(this).Show_ToastError("Error guardando " + file + ", error: " + e.getMessage());
        } catch (Exception e) {
            Log.w("FileUtils", "Fallo guardar el archivo ", e);
            new classCustomToast(this).Show_ToastError("Failed to save file " + file + ", error: " + e.getMessage());
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    private void agregarFotosAlArchivoExcel(int fila, int columna, Workbook workbook, HSSFSheet sheet, String path) {

        try {
            CreationHelper helper = workbook.getCreationHelper();

            //add a picture in this workbook.
            String pathfirmamp = path;

            if (new File(pathfirmamp).exists()) {

                InputStream ismp = new FileInputStream(pathfirmamp);
                byte[] bytes = IOUtils.toByteArray(ismp);
                ismp.close();
                int pictureIdxmp = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);

                //create drawing
                Drawing drawingmp = sheet.createDrawingPatriarch();

                //add a picture shape
                ClientAnchor anchormp = helper.createClientAnchor();
                anchormp.setCol1(columna);
                anchormp.setRow1(fila);
                anchormp.setCol2(columna + 1); //Column C
                anchormp.setRow2(fila + 1);

                Picture pictmp = drawingmp.createPicture(anchormp, pictureIdxmp);
            }

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
            IndicadorSeleccionado(v);
        }

        private void IndicadorSeleccionado(View v) {

            final int selectedItemPosition = recyclerView.getChildPosition(v);

            DialogoDetalleIndicador(selectedItemPosition);

            Button btnCamara = (Button) v.findViewById(R.id.btnCamara_indicador);
            btnCamara.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //si aun no hay un registro temporal en la DB se guarda
                    if (idRptEncabezadoActual == 0) {
                        guardarTemporal(estadoTerminado);
                    }

                    if (tvPDV.getText().toString().length() > 0) {
                        FotoParaIndSeleccionado = selectedItemPosition;
                        TomarFotografia(
                                arrContenido.get(selectedItemPosition).getNumIndicador(),
                                tvPDV.getText().toString()
                        );
                    } else {
                        new classCustomToast(RevisionPdv.this).Show_ToastError("Primero ingrese nombre del pdv.");
                    }
                }
            });

            // Toast.makeText(RevisionPdv.this, String.format("Has seleccionado %s", arrContenido.get(selectedItemPosition).getIndicador()), Toast.LENGTH_LONG).show();
        }
    }

    private void TomarFotografia(int idIndicador, String nombreTienda) {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {

            final File root = new File(CARPETA_RECURSOS);
            if (!root.exists())
                root.mkdirs();


            String fileName = String.format("DBEnc%d_DBDet%d_Ind%d_Pdv%s_Fecha%s.jpg",
                    idRptEncabezadoActual,
                    arrContenido.get(idIndicador).getIdGuardadoLocaldb(),
                    idIndicador,
                    nombreTienda.replace(" ", ""),
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ImagenTomadaCamara = Uri.fromFile(new File(CARPETA_RECURSOS + fileName));
            //si no va falla al abrir la camara en el j2 tambien debe de ir cuando se obtiene un archivo dsdd la memoria
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            //******************************************************
            intent.putExtra(MediaStore.EXTRA_OUTPUT, ImagenTomadaCamara);
            startActivityForResult(intent, 113);
        }
    }


    private void GuardaImagenMemoria(Bitmap mBitmap, String fileName) {

        File f3 = new File(CARPETA_RECURSOS);
        if (!f3.exists())
            f3.mkdirs();
        OutputStream outStream = null;
        File file = new File(CARPETA_RECURSOS + fileName);
        try {
            outStream = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();
            Toast.makeText(getApplicationContext(), "guardada temporalmente", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void EnviarCorreo(String nombreArchivo) {

        try {
            estadoTerminado = 1;
            guardarTemporal(estadoTerminado);


            File filelocation = new File(getExternalFilesDir(null), nombreArchivo);
            if (filelocation.exists()) {
                Uri path = Uri.fromFile(filelocation);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                // set the type to 'email'
                emailIntent.setType("vnd.android.cursor.dir/email");
                String to[] = {""};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                // the attachment
                emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                // the mail subject
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Formato de revisión MARCAS PRIVADAS " + tvPDV.getText());
                // cualquier texto de mensaje
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Reporte para " + tvPDV.getText());

                startActivity(Intent.createChooser(emailIntent, "Enviar por correo..."));
            }


            // borrarTodo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
