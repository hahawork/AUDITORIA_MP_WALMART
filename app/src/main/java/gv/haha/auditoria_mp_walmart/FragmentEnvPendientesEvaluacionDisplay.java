package gv.haha.auditoria_mp_walmart;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gv.haha.auditoria_mp_walmart.clases.BaseDatos;
import gv.haha.auditoria_mp_walmart.clases.Globales;
import gv.haha.auditoria_mp_walmart.clases.classCustomToast;
import gv.haha.auditoria_mp_walmart.clases.classWebService;

import static gv.haha.auditoria_mp_walmart.clases.Variables.SETT_COD_USUARIO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_EVAL_DISPLAY_DET;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_EVAL_DISPLAY_ENC;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_FOTO_INDCADOR;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_DETALLE;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_ENCABEZADO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.VAR_PARAMETRO;

public class FragmentEnvPendientesEvaluacionDisplay extends Fragment {

    BaseDatos baseDatos;
    Cursor cursor;
    LinearLayout llDatos;
    Globales G;
    SharedPreferences setting;
    List<classWebService> params = new ArrayList<>();


    public FragmentEnvPendientesEvaluacionDisplay() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_envios_pendientes, container, false);

        llDatos = (LinearLayout) view.findViewById(R.id.llDatosEnviosPendientes_frgEP);

        baseDatos = new BaseDatos(getContext());
        G = new Globales(getContext());
        setting = PreferenceManager.getDefaultSharedPreferences(getContext());


        cursor = baseDatos.obtenerRegistroWhereArgs(TBL_EVAL_DISPLAY_ENC, "EstadoEnviado = 0 and idEnviado = 0");

        if (cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater1.inflate(R.layout.row_envios_pendientes_list, null);

                TextView tvidLocal = (TextView) rowView.findViewById(R.id.tvIdLocalDB_envPendRow);
                TextView tvNombre = (TextView) rowView.findViewById(R.id.tvInform_envPendRow);
                TextView tvOtraInfo = (TextView) rowView.findViewById(R.id.tvOtroDato_envPendRow);

                final int idLocal =  cursor.getInt(cursor.getColumnIndex("idEvalDispEnc"));
                final String Nombre =  cursor.getString(cursor.getColumnIndex("NombreDislay"));
                String Otrainfo = cursor.getString(cursor.getColumnIndex("FechReg"));

                params.clear();
                params.add(new classWebService("NombreDislay", cursor.getString(cursor.getColumnIndex("NombreDislay"))));
                params.add(new classWebService("IdDislay", cursor.getString(cursor.getColumnIndex("IdDislay"))));
                params.add(new classWebService("idPdv", cursor.getString(cursor.getColumnIndex("idPdv"))));
                params.add(new classWebService("NombrePDV", cursor.getString(cursor.getColumnIndex("NombrePDV"))));
                params.add(new classWebService("FechReg", cursor.getString(cursor.getColumnIndex("FechReg"))));
                params.add(new classWebService("GPSCoordenadas", cursor.getString(cursor.getColumnIndex("GPSCoordenadas"))));
                params.add(new classWebService("IdUsuario", String.valueOf(setting.getInt(SETT_COD_USUARIO, 0))));


                tvidLocal.setText(String.valueOf(idLocal));
                tvNombre.setText(Nombre);
                tvOtraInfo.setText(Otrainfo);
                llDatos.addView(rowView);

                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Que deseas hacer?")
                                .setMessage("Has seleccionado: " + Nombre)
                                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //se ejecuta l web service para enviar el encabezado ala nube
                                        if (G.TieneConexion()) {
                                            G.webServiceGuardarEvaluacionDisplayEnc(
                                                    idLocal,
                                                    params
                                            );
                                        }else{
                                            new classCustomToast(getActivity()).Show_ToastError("No tienes conexión a internet");
                                        }
                                    }
                                })
                                .setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        baseDatos.BorrarRegistroWhere(TBL_EVAL_DISPLAY_ENC, "idEvalDispEnc = " + idLocal);
                                        baseDatos.BorrarRegistroWhere(TBL_EVAL_DISPLAY_DET, "idEDE = " + idLocal);

                                        llDatos.removeView(rowView);
                                    }
                                })
                                .show();
                    }
                });
            }
        }

        return view;
    }

    public static FragmentEnvPendientesEvaluacionDisplay newInstance(int TipoEnvio) {
        FragmentEnvPendientesEvaluacionDisplay myFragment = new FragmentEnvPendientesEvaluacionDisplay();
        Bundle args = new Bundle();
        args.putInt(VAR_PARAMETRO, TipoEnvio);
        myFragment.setArguments(args);

        return myFragment;
    }
}
