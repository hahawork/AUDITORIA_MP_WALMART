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
import gv.haha.auditoria_mp_walmart.clases.classWebService;

import static gv.haha.auditoria_mp_walmart.clases.Variables.ENV_PEND_ACTIVID_COMERC;
import static gv.haha.auditoria_mp_walmart.clases.Variables.ENV_PEND_EVAL_DISPLAY;
import static gv.haha.auditoria_mp_walmart.clases.Variables.ENV_PEND_REVIS_PDV;
import static gv.haha.auditoria_mp_walmart.clases.Variables.SETT_COD_USUARIO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_FOTO_INDCADOR;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_DETALLE;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_REPORTE_ENCABEZADO;
import static gv.haha.auditoria_mp_walmart.clases.Variables.VAR_PARAMETRO;

public class FragmentEnvPendientes extends Fragment {

    BaseDatos baseDatos;
    Cursor cursor;
    LinearLayout llDatos;
    Globales G;
    SharedPreferences setting;
    List<classWebService> paramRevisPdv = new ArrayList<>();


    public FragmentEnvPendientes() {
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


        cursor = baseDatos.obtenerRegistroWhereArgs(TBL_REPORTE_ENCABEZADO, "EstadoEnviado = 0 and EstadoTerminado = 1");
        paramRevisPdv.clear();

        if (cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater1.inflate(R.layout.row_envios_pendientes_list, null);

                TextView tvidLocal = (TextView) rowView.findViewById(R.id.tvIdLocalDB_envPendRow);
                TextView tvNombre = (TextView) rowView.findViewById(R.id.tvInform_envPendRow);
                TextView tvOtraInfo = (TextView) rowView.findViewById(R.id.tvOtroDato_envPendRow);

                final int idLocal = cursor.getInt(cursor.getColumnIndex("IdRptEnc"));
                final String Nombre = cursor.getString(cursor.getColumnIndex("NombrePDV"));
                String Otrainfo = cursor.getString(cursor.getColumnIndex("FechaRegistro"));

                paramRevisPdv.add(new classWebService("IdPdv", Nombre));
                paramRevisPdv.add(new classWebService("FechaVisita", cursor.getString(cursor.getColumnIndex("FechaRegistro"))));
                paramRevisPdv.add(new classWebService("ItemsTienda", cursor.getString(cursor.getColumnIndex("ItemsTienda"))));
                paramRevisPdv.add(new classWebService("ItemsAgotados", cursor.getString(cursor.getColumnIndex("ItemsAgotados"))));
                paramRevisPdv.add(new classWebService("Participacion", cursor.getString(cursor.getColumnIndex("Participacion"))));
                paramRevisPdv.add(new classWebService("HoraVisita", cursor.getString(cursor.getColumnIndex("HoraVisita"))));
                paramRevisPdv.add(new classWebService("ResponsableTurno", cursor.getString(cursor.getColumnIndex("ResponsableTurno"))));
                paramRevisPdv.add(new classWebService("Observaciones", cursor.getString(cursor.getColumnIndex("Observaciones"))));
                paramRevisPdv.add(new classWebService("IdUsuario", String.valueOf(setting.getInt(SETT_COD_USUARIO, 0))));

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
                                        G.webServiceGuardarRevisionPdvEnc(
                                                idLocal,
                                                paramRevisPdv
                                        );
                                    }
                                })
                                .setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        baseDatos.BorrarRegistroWhere(TBL_REPORTE_ENCABEZADO, "IdRptEnc = " + idLocal);
                                        baseDatos.BorrarRegistroWhere(TBL_REPORTE_DETALLE, "idRptEnc = " + idLocal);
                                        baseDatos.BorrarRegistroWhere(TBL_FOTO_INDCADOR, "idRptEnc = " + idLocal);

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

    public static FragmentEnvPendientes newInstance(int TipoEnvio) {
        FragmentEnvPendientes myFragment = new FragmentEnvPendientes();
        Bundle args = new Bundle();
        args.putInt(VAR_PARAMETRO, TipoEnvio);
        myFragment.setArguments(args);

        return myFragment;
    }
}
