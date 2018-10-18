package gv.haha.auditoria_mp_walmart;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.formula.functions.T;

import gv.haha.auditoria_mp_walmart.clases.BaseDatos;

import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_EVAL_DISPLAY_DET;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_EVAL_DISPLAY_ENC;

public class FragmentUltimoReporte extends Fragment {

    BaseDatos baseDatos;
    TextView tvNombrePdV, tvNombreDisplay, tvFecha;
    View view;
    int idguardado = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ultimo_reporte_evaldisplay, container, false);

        baseDatos = new BaseDatos(getContext());

        tvNombrePdV = (TextView) view.findViewById(R.id.tvNombrePDV_fragmUltmReport);
        tvNombreDisplay = (TextView) view.findViewById(R.id.tvNombreDisplay_fragmUltmReport);
        tvFecha = (TextView) view.findViewById(R.id.tvFecha_fragmUltmReport);


        getData();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //si se enva un argumento desde un activity u otro fragment
        if (getArguments() != null) {
            idguardado = getArguments().getInt("idGuardado");
        }

        //permite agregar menu
        setHasOptionsMenu(true);

    }

    private void getData() {
        try {


            Cursor cdata;
            if (idguardado == 0) {
                cdata = baseDatos.obtenerMaxRegistro(TBL_EVAL_DISPLAY_ENC, "idEvalDispEnc");
            } else {
                cdata = baseDatos.obtenerRegistroWhereArgs(TBL_EVAL_DISPLAY_ENC, "idEvalDispEnc = " + idguardado);
            }
            if (cdata.getCount() > 0) {

                //mueve el cursor al primer registro (al unico porque viene de un select max.)
                cdata.moveToFirst();
                // se setean los campos
                tvNombrePdV.setText(cdata.getString(cdata.getColumnIndex("NombrePDV")));
                tvNombreDisplay.setText(cdata.getString(cdata.getColumnIndex("NombreDislay")));
                String fecha = String.format("%d/%d/%d",
                        cdata.getInt(cdata.getColumnIndex("FechRegDia")),
                        cdata.getInt(cdata.getColumnIndex("FechRegMes")),
                        cdata.getInt(cdata.getColumnIndex("FechRegAno")));

                tvFecha.setText(fecha);

                //obtiene los datos del detalle del reporte.

                LinearLayout llDetalle = (LinearLayout) view.findViewById(R.id.lldataDetalle_UltReport_fragm);
                int sumatoria = 0;

                Cursor cDet = baseDatos.obtenerRegistroWhereArgs(
                        TBL_EVAL_DISPLAY_DET,
                        "idEDE = " + cdata.getInt(cdata.getColumnIndex("idEvalDispEnc")));

                for (cDet.moveToFirst(); !cDet.isAfterLast(); cDet.moveToNext()) {

                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.row_det_ultimrept, null);

                    TextView tvindic = (TextView) rowView.findViewById(R.id.tvIndicador_fragmUltRepo_ED);
                    TextView tveval = (TextView) rowView.findViewById(R.id.tvEvaluacion_fragmUltRepo_ED);
                    tvindic.setText(cDet.getString(cDet.getColumnIndex("descIndicador")));
                    int puntaje = cDet.getInt(cDet.getColumnIndex("puntaje"));
                    sumatoria = sumatoria + puntaje;

                    tveval.setText(String.valueOf(puntaje));
                    // Add the new row before the add field button.
                    llDetalle.addView(rowView);
                }

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.row_det_ultimrept, null);
                TextView tvindic = (TextView) rowView.findViewById(R.id.tvIndicador_fragmUltRepo_ED);
                tvindic.setGravity(Gravity.RIGHT);
                tvindic.setBackgroundColor(Color.YELLOW);
                tvindic.setText("SUMATORIA TOTAL");

                TextView tveval = (TextView) rowView.findViewById(R.id.tvEvaluacion_fragmUltRepo_ED);
                tveval.setText(String.valueOf(sumatoria));
                llDetalle.addView(rowView);


            } else {
                Toast.makeText(getContext(), "no hay registros aún.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
