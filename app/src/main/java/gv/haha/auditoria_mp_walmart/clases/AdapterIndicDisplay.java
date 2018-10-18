package gv.haha.auditoria_mp_walmart.clases;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gv.haha.auditoria_mp_walmart.EvaluacionDisplay;
import gv.haha.auditoria_mp_walmart.FragmentNuevoReporte;
import gv.haha.auditoria_mp_walmart.R;
import gv.haha.auditoria_mp_walmart.RevisionPdv;


public class AdapterIndicDisplay extends RecyclerView.Adapter<AdapterIndicDisplay.MyViewHolder> {

    Context mContext;
    private List<ContenidoDisplay> arrContenido;

    public AdapterIndicDisplay(Context context, List<ContenidoDisplay> arrayList) {
        this.mContext = context;
        arrContenido = arrayList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_indicador, parent, false);
        v.setOnClickListener(FragmentNuevoReporte.myOnClickListener);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
// set the data in items
            int id = arrContenido.get(position).getIdIndicador();
            final String indicador = arrContenido.get(position).getDescIndicador();

            holder.tvNumero.setText(String.valueOf(id));
            holder.tvIndicador.setText(indicador);
            holder.btnCamara.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        return arrContenido.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView tvNumero, tvIndicador;
        Button btnCamara;

        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            tvNumero = (TextView) itemView.findViewById(R.id.tvNumero);
            tvIndicador = (TextView) itemView.findViewById(R.id.tvIndicador);
            btnCamara = (Button) itemView.findViewById(R.id.btnCamara_indicador);
        }
    }
}
