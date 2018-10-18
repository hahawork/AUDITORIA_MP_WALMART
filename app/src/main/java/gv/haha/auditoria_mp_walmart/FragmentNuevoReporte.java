package gv.haha.auditoria_mp_walmart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import org.apache.poi.ss.formula.functions.T;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gv.haha.auditoria_mp_walmart.clases.AdapterIndicDisplay;
import gv.haha.auditoria_mp_walmart.clases.BaseDatos;
import gv.haha.auditoria_mp_walmart.clases.ContenidoDisplay;
import gv.haha.auditoria_mp_walmart.clases.DisplayList;
import gv.haha.auditoria_mp_walmart.clases.Globales;
import gv.haha.auditoria_mp_walmart.clases.SQLHelper;
import gv.haha.auditoria_mp_walmart.clases.classCustomToast;
import gv.haha.auditoria_mp_walmart.clases.classWebService;

import static gv.haha.auditoria_mp_walmart.clases.Variables.SETT_COD_USUARIO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_EVAL_DISPLAY_DET;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_EVAL_DISPLAY_ENC;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_NOMBRE_DISPLAY;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_PUNTOSDEVENTA;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_ENCABEZADO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_GUARDAR_DISPLAY;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_GUARDA_REVISIONPDV_ENC;

public class FragmentNuevoReporte extends Fragment {

    RecyclerView recyclerView;
    public static View.OnClickListener myOnClickListener;
    List<ContenidoDisplay> arrContenido = new ArrayList<>();
    List<DisplayList> arrDisplayList = new ArrayList<>();

    AutoCompleteTextView etNombrePDV;
    Spinner spnNombreDisplay;

    SharedPreferences setting;
    Globales G;

    //TextInputLayout tilNombreDisplay, tilNombrePdv;

    BaseDatos baseDatos;
    private static int idInsertadoDB = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nuevo_evaluacion_display, container, false);

        baseDatos = new BaseDatos(getContext());
        setting = PreferenceManager.getDefaultSharedPreferences(getContext());
        myOnClickListener = new MyOnClickListener(getContext());
        G = new Globales(getContext());

        //tilNombreDisplay = (TextInputLayout) view.findViewById(R.id.tilNombreDisplay_ED);
        //tilNombrePdv = (TextInputLayout) view.findViewById(R.id.tilNombrePDV_ED);

        etNombrePDV = (AutoCompleteTextView) view.findViewById(R.id.actvNombrePDV_ED);
        etNombrePDV.setThreshold(1);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewEvaluacionDisplay);
        spnNombreDisplay = (Spinner) view.findViewById(R.id.spnNombreDisp_NuevoRPT_ED);
        ((Button) view.findViewById(R.id.btnAgregarDisplay_NuevoRPT_ED)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoGuardarDisplay();
            }
        });


        //se cargan los nombres de los pdv que ya se han ingresados
        String[] arrPdv = baseDatos.getArrayDataFromTable(TBL_PUNTOSDEVENTA, "NombrePdV");
        ArrayAdapter<String> adapterPdv = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrPdv);
        etNombrePDV.setAdapter(adapterPdv);

        getDataIndicadores();

        //se cargan los nombres de display que ya se han ingresados
        getDisplayList();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //permite agregar menu
        setHasOptionsMenu(true);
    }


    private void getDataIndicadores() {
        // Add some sample items.
        arrContenido.clear();
        arrContenido.add(new ContenidoDisplay(1, 0, "CONTROL DE DINAMICA COMERCIAL"));
        arrContenido.add(new ContenidoDisplay(2, 0, "LLENADO DE CATEGORIA PRIORIDAD PESO/VENTA TOP 10"));
        arrContenido.add(new ContenidoDisplay(3, 0, "CHECK OUT"));
        arrContenido.add(new ContenidoDisplay(4, 0, "INTERACCION CON PDV COMUNICACION ACTIVA"));
        arrContenido.add(new ContenidoDisplay(5, 0, "REPORTE DE RIESGO VENC/APLICAN ESTRATEGIA"));
        arrContenido.add(new ContenidoDisplay(6, 0, "HORARIO/PRESENTACION/SEGURIDAD/DISCIPLINA"));
        arrContenido.add(new ContenidoDisplay(7, 0, "MANTENIMIENTO DE ROTULACION"));
        arrContenido.add(new ContenidoDisplay(8, 0, "SEGUIMIENTO A MODULARES EJECUCION DE REPORTE ITEM SIN"));
        arrContenido.add(new ContenidoDisplay(9, 0, "MOV/ BLOQUEADO / NEGATIVA"));
        arrContenido.add(new ContenidoDisplay(10, 0, "LINEA DE FRIO"));

        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        // call the constructor of AdapterEvaluarpdv to send the reference and data to Adapter
        AdapterIndicDisplay customAdapter = new AdapterIndicDisplay(getContext(), arrContenido);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView

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
            int selectedItemPosition = recyclerView.getChildPosition(v);

            DialogoDetalleIndicador(selectedItemPosition);

            // Toast.makeText(RevisionPdv.this, String.format("Has seleccionado %s", arrContenido.get(selectedItemPosition).getIndicador()), Toast.LENGTH_LONG).show();
        }
    }

    public void DialogoGuardarDisplay() {

        final Dialog dialog = new Dialog(getContext());

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        dialog.setContentView(R.layout.dialog_guardarnombre_display);

        final EditText etNombre = dialog.findViewById(R.id.etNombre_dialog_guardarnombreDisplay);
        Button btnGuardar = (Button) dialog.findViewById(R.id.btnGuardar_dialog_guardarnombreDisplay);
        Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar_dialog_guardarnombreDisplay);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(etNombre.getText())) {

                    List<classWebService> params = new ArrayList<>();
                    params.add(new classWebService("Nombre", etNombre.getText().toString()));
                    params.add(new classWebService("IdUsuario", String.valueOf(setting.getInt(SETT_COD_USUARIO, 0))));

                    if (G.TieneConexion()) {
                        GuardarDisplayName(params);

                        dialog.cancel();

                    } else {
                        new classCustomToast(getActivity()).Show_ToastError("Revisa la conexión a internet.");
                    }

                } else

                {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Advertencia")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("Favor ingrese un nombre o presione cancelar.")
                            .setPositiveButton("Entendido!", null)
                            .show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void GuardarDisplayName(final List<classWebService> params) {
        try {
            StringRequest jsonRequest = new StringRequest(Request.Method.POST, URL_WS_GUARDAR_DISPLAY,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);

                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                int idInsertado = jsonObject.getInt("idInsertado");

                                if (success == 1) {

                                    ContentValues values = new ContentValues();
                                    values.put("idNombDispl", (byte[]) null);
                                    values.put("Nombre", params.get(0).getValor());
                                    values.put("EstadoEnviado", 1);
                                    values.put("idEnviado", idInsertado);


                                    Long insert = baseDatos.insertarRegistro(TBL_NOMBRE_DISPLAY, values);
                                    if (insert > 0) {
                                        getDisplayList();
                                    } else {
                                        Toast.makeText(getContext(), "No se ha podido guardar el nombre", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    new classCustomToast(getActivity()).Show_ToastError(message);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    new classCustomToast(getActivity()).Show_ToastError("Error al guardar al display Online, Verifique conexión a internet: " + error.getMessage());
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
            Volley.newRequestQueue(getContext()).add(jsonRequest);
            Volley.newRequestQueue(getContext()).addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
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

    public void DialogoDetalleIndicador(final int Indicad) {

        final Dialog dialogIntro = new Dialog(getContext());

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
        dialogIntro.setContentView(R.layout.dialog_reporte_display);


        ((TextView) dialogIntro.findViewById(R.id.tvIndicadorSeleccionado_dg_ED)).setText(arrContenido.get(Indicad).getDescIndicador());

        final int[] ok = {0};

        RadioGroup rgOpciones = dialogIntro.findViewById(R.id.rgOpciones_dg_ED);
        RadioButton rbEvaluacion4 = (RadioButton) dialogIntro.findViewById(R.id.rbEvaluacion4_ED);
        RadioButton rbEvaluacion6 = (RadioButton) dialogIntro.findViewById(R.id.rbEvaluacion6_ED);
        RadioButton rbEvaluacion7 = (RadioButton) dialogIntro.findViewById(R.id.rbEvaluacion7_ED);
        RadioButton rbEvaluacion8 = (RadioButton) dialogIntro.findViewById(R.id.rbEvaluacion8_ED);
        RadioButton rbEvaluacion10 = (RadioButton) dialogIntro.findViewById(R.id.rbEvaluacion10_ED);

        Button btnGuardar = (Button) dialogIntro.findViewById(R.id.btnGuardar_dg_indicador);
        Button btnCancelar = (Button) dialogIntro.findViewById(R.id.btnCancelar_dg_indicador);

        //cuando se abre el dialogo se pone el vaslor actual que esta en el array
        switch (arrContenido.get(Indicad).getPuntaje()) {
            case 4:
                rbEvaluacion4.setChecked(true);
                break;
            case 6:
                rbEvaluacion6.setChecked(true);
                break;
            case 7:
                rbEvaluacion7.setChecked(true);
                break;
            case 8:
                rbEvaluacion8.setChecked(true);
                break;
            case 10:
                rbEvaluacion10.setChecked(true);
                break;
        }

        ok[0] = arrContenido.get(Indicad).getPuntaje();

        rgOpciones.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rbEvaluacion4_ED:
                        ok[0] = 4;
                        break;
                    case R.id.rbEvaluacion6_ED:
                        ok[0] = 6;
                        break;
                    case R.id.rbEvaluacion7_ED:
                        ok[0] = 7;
                        break;
                    case R.id.rbEvaluacion8_ED:
                        ok[0] = 8;
                        break;
                    case R.id.rbEvaluacion10_ED:
                        ok[0] = 10;
                        break;
                }

            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ok[0] > 0) {

                    arrContenido.get(Indicad).setPuntaje(ok[0]);

                    dialogIntro.cancel();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Advertencia")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("Favor seleccionar una opción o presione cancelar.")
                            .setPositiveButton("Entendido!", null)
                            .show();
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

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_nuevo_evaldisplay, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_save) {
            guardarReporte();

        } else if (itemId == R.id.action_cancel) {
            startActivity(new Intent(getContext(), MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {

        spnNombreDisplay.setSelection(0, true);
        etNombrePDV.setText("");
        idInsertadoDB = 0;
        super.onDestroyView();

    }

    private void getDisplayList() {

        try {

            arrDisplayList.clear();
            arrDisplayList.add(new DisplayList(0, "Seleccione un display o agregue si no existe.", "", 0));

            SQLiteDatabase db = new SQLHelper(getContext()).getWritableDatabase();
            Cursor cDisplay = db.rawQuery("SELECT * FROM " + TBL_NOMBRE_DISPLAY, null);

            if (cDisplay.getCount() > 0) {
                for (cDisplay.moveToFirst(); !cDisplay.isAfterLast(); cDisplay.moveToNext()) {
                    arrDisplayList.add(
                            new DisplayList(
                                    cDisplay.getInt(cDisplay.getColumnIndex("idNombDispl")),
                                    cDisplay.getString(cDisplay.getColumnIndex("Nombre")),
                                    "",
                                    cDisplay.getInt(cDisplay.getColumnIndex("idEnviado"))
                            )
                    );
                }
            }

            String[] arrdispay = baseDatos.getArrayDispayFromList(arrDisplayList);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arrdispay);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnNombreDisplay.setAdapter(dataAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void guardarReporte() {

        try {

            boolean isOk = true;
            //nombre display no debe estar vacio
            if (spnNombreDisplay.getSelectedItemPosition() == 0) {
                isOk = false;
                new classCustomToast(getActivity()).Show_ToastError("Por favor seleccione un nombre de Display, si no hay registre uno nuevo.");
            }
            //nombre del pdv no debe estar vacio
            if (TextUtils.isEmpty(etNombrePDV.getText())) {
                isOk = false;
                etNombrePDV.setError("Por favor ingrese pdv");
            }

            if (getIdPdvPorNombre(etNombrePDV.getText().toString()) == 0){
                isOk = false;
                new classCustomToast(getActivity()).Show_ToastError("Por favor seleccione un punto de venta de la lista.");
            }

            if (isOk) {

                if (idInsertadoDB == 0) {

                    Date date = new Date();

                    ContentValues valuesEncab = new ContentValues();
                    valuesEncab.put("idEvalDispEnc", (byte[]) null);
                    valuesEncab.put("NombreDislay", arrDisplayList.get(spnNombreDisplay.getSelectedItemPosition()).getNombreDisplay());
                    valuesEncab.put("IdDislay", arrDisplayList.get(spnNombreDisplay.getSelectedItemPosition()).getIdDBOnline());
                    valuesEncab.put("idPdv", getIdPdvPorNombre(etNombrePDV.getText().toString()));
                    valuesEncab.put("NombrePDV", etNombrePDV.getText().toString());
                    valuesEncab.put("FechRegAno", new SimpleDateFormat("yyyy").format(date));
                    valuesEncab.put("FechRegMes", new SimpleDateFormat("MM").format(date));
                    valuesEncab.put("FechRegDia", new SimpleDateFormat("dd").format(date));
                    valuesEncab.put("FechReg", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
                    valuesEncab.put("GPSCoordenadas", "0.0,0.0");
                    valuesEncab.put("EstadoEnviado", "0");
                    valuesEncab.put("idEnviado", "0");

                    Long id = baseDatos.insertarRegistro(TBL_EVAL_DISPLAY_ENC, valuesEncab);
                    if (id > 0) {

                        idInsertadoDB = Integer.parseInt(String.valueOf(id));
                        for (int i = 0; i < arrContenido.size(); i++) {
                            ContentValues valuesDet = new ContentValues();
                            valuesDet.put("idEvalDispDet", (byte[]) null);
                            valuesDet.put("idEDE", idInsertadoDB);
                            valuesDet.put("idIndic", arrContenido.get(i).getIdIndicador());
                            valuesDet.put("descIndicador", arrContenido.get(i).getDescIndicador());
                            valuesDet.put("puntaje", arrContenido.get(i).getPuntaje());
                            valuesEncab.put("EstadoEnviado", "0");
                            valuesEncab.put("idEnviado", "0");

                            Long resultDet = baseDatos.insertarRegistro(TBL_EVAL_DISPLAY_DET, valuesDet);
                            if (resultDet > 0) {
                                arrContenido.get(i).setIdGuardadoLocalDB(Integer.valueOf(String.valueOf(resultDet)));
                            }
                        }

                        new classCustomToast(getActivity()).Toast(String.format("Se ha guardado el reporte con id: %d", idInsertadoDB), R.drawable.ic_success);
                        //limpa los campos
                        etNombrePDV.setText("");
                        spnNombreDisplay.setSelection(0, true);
                        //se vuelve a llenar el arreglo con los indicadores con valores iniciales
                        getDataIndicadores();
                    }
                } else { //si el id es mayor que  cero, actualiza los datos

                    ContentValues valuesEncab = new ContentValues();
                    valuesEncab.put("NombreDislay", arrDisplayList.get(spnNombreDisplay.getSelectedItemPosition()).getNombreDisplay());
                    valuesEncab.put("IdDislay", arrDisplayList.get(spnNombreDisplay.getSelectedItemPosition()).getIdDBOnline());
                    valuesEncab.put("idPdv", 0);
                    valuesEncab.put("NombrePDV", etNombrePDV.getText().toString());

                    int id = baseDatos.actualizarRegistro(TBL_EVAL_DISPLAY_ENC, valuesEncab, "idEvalDispEnc = " + idInsertadoDB);
                    if (id > 0) {

                        for (int i = 0; i < arrContenido.size(); i++) {
                            ContentValues valuesDet = new ContentValues();
                            valuesDet.put("idEDE", idInsertadoDB);
                            valuesDet.put("idIndic", arrContenido.get(i).getIdIndicador());
                            valuesDet.put("descIndicador", arrContenido.get(i).getDescIndicador());
                            valuesDet.put("puntaje", arrContenido.get(i).getPuntaje());

                            int cantActualiz = baseDatos.actualizarRegistro(
                                    TBL_EVAL_DISPLAY_DET,
                                    valuesDet,
                                    "idEvalDispDet = " + arrContenido.get(i).getIdGuardadoLocalDB() + " and idEDE = " + idInsertadoDB);
                        }

                        new classCustomToast(getActivity()).Toast(String.format("Se ha actualizado el reporte con id: %d", idInsertadoDB), R.drawable.ic_success);
                        //limpiar los campos
                        etNombrePDV.setText("");
                        spnNombreDisplay.setSelection(0, true);
                        idInsertadoDB = 0;
                        //se vuelve a llenar el arreglo con los indicadores con valores iniciales
                        getDataIndicadores();
                    }

                }

                getActivity().recreate();

            } else {
                new classCustomToast(getActivity()).Show_ToastError("Favor ingresar todos los datos requeridos.");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int getIdPdvPorNombre(String NombrePdv) {

        Cursor cursor = baseDatos.obtenerRegistroWhereArgs(TBL_PUNTOSDEVENTA, "NombrePDV = '" + NombrePdv + "'");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex("IdPdV"));
            return id;
        } else {
            return 0;
        }
    }
}
