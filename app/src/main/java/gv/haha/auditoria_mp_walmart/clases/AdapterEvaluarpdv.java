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

import gv.haha.auditoria_mp_walmart.R;
import gv.haha.auditoria_mp_walmart.RevisionPdv;


public class AdapterEvaluarpdv extends RecyclerView.Adapter<AdapterEvaluarpdv.MyViewHolder> {

    Context mContext;
    private ArrayList<Contenido> arrContenido;
    private static int TomarFotoPara; //define que para cual indicador se va a tomar la fotografia aal abrir la camara

    public AdapterEvaluarpdv(Context context, ArrayList<Contenido> arrayList) {
        this.mContext = context;
        arrContenido = arrayList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_indicador, parent, false);
        v.setOnClickListener(RevisionPdv.myOnClickListener);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
// set the data in items
            int id = arrContenido.get(position).getNumIndicador();
            final String indicador = arrContenido.get(position).getIndicador();

            holder.tvNumero.setText(String.valueOf(id));
            holder.tvIndicador.setText(indicador);
            holder.btnCamara.setVisibility(arrContenido.get(position).isPermiteFoto() ? View.VISIBLE : View.GONE);

            holder.btnCamara.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TomarFotoPara = position;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if  (mContext.checkSelfPermission(Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            ((Activity)mContext).requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    110);
                        } else {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            ((Activity)mContext).startActivityForResult(cameraIntent, 113);
                        }
                    }else {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        ((Activity)mContext).startActivityForResult(cameraIntent, 113);
                    }
                }
            });
            // implement setOnClickListener event on item view.


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        return arrContenido.size();
    }


    public int getTomarFotoPara() {
        return TomarFotoPara;
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
