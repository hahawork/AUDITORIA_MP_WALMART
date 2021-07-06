package gv.haha.auditoria_mp_walmart;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_FOTO_INDCADOR;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_DETALLE;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_ENCABEZADO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_RPT_ACTIVIDAD_COMERCIAL;
import static gv.haha.auditoria_mp_walmart.clases.Variables.VAR_PARAMETRO;

public class FragmentEnvPendientesActividadComercial extends Fragment {

    BaseDatos baseDatos;
    Cursor cursor;
    LinearLayout llDatos;
    Globales G;
    SharedPreferences setting;
    List<classWebService> params = new ArrayList<>();


    public FragmentEnvPendientesActividadComercial() {
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


        cursor = baseDatos.obtenerRegistroWhereArgs(TBL_RPT_ACTIVIDAD_COMERCIAL, "EstadoEnviado = 0 and idEnviado = 0");
        params.clear();

        if (cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater1.inflate(R.layout.row_envios_pendientes_list, null);

                TextView tvidLocal = (TextView) rowView.findViewById(R.id.tvIdLocalDB_envPendRow);
                TextView tvNombre = (TextView) rowView.findViewById(R.id.tvInform_envPendRow);
                TextView tvOtraInfo = (TextView) rowView.findViewById(R.id.tvOtroDato_envPendRow);

                final int idLocal = cursor.getInt(cursor.getColumnIndex("idRptActivComerc"));
                final String Nombre = cursor.getString(cursor.getColumnIndex("idData"));
                String Otrainfo = cursor.getString(cursor.getColumnIndex("FechaRegistro"));

                String[] campos = cursor.getColumnNames();
                for (int i = 0; i < campos.length; i++) {
                    params.add(new classWebService(campos[i], cursor.getString(cursor.getColumnIndex(campos[i]))));
                }
                params.add(new classWebService("idUsuario", String.valueOf(setting.getInt(SETT_COD_USUARIO, 0))));

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
                                            /*G.webServiceGuardarActividadComercial(
                                                    idLocal,
                                                    params
                                            );*/
                                        } else {
                                            new classCustomToast(getActivity()).Show_ToastError("No tienes conexión a internet");
                                        }
                                    }
                                })
                                .setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        baseDatos.BorrarRegistroWhere(TBL_RPT_ACTIVIDAD_COMERCIAL, "idRptActivComerc = " + idLocal);

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

    public static FragmentEnvPendientesActividadComercial newInstance(int TipoEnvio) {
        FragmentEnvPendientesActividadComercial myFragment = new FragmentEnvPendientesActividadComercial();
        Bundle args = new Bundle();
        args.putInt(VAR_PARAMETRO, TipoEnvio);
        myFragment.setArguments(args);

        return myFragment;
    }
}
