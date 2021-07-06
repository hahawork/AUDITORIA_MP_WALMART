package gv.haha.auditoria_mp_walmart.clases;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import gv.haha.auditoria_mp_walmart.ActividadComercial;
import gv.haha.auditoria_mp_walmart.R;

public class AdapterActivComerc extends RecyclerView.Adapter<AdapterActivComerc.ViewHolder> {

    List<classActivComerc> Data = new ArrayList<>();

    Context mContext;

    String PathFotoTomada = "";

    public AdapterActivComerc(Context context, List<classActivComerc> data) {
        mContext = context;
        this.Data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_presentaciones_actividcomerc, parent, false);
        v.setOnClickListener(ActividadComercial.myOnClickListener);
        // set the view's size, margins, paddings and layout parameters
        AdapterActivComerc.ViewHolder vh = new AdapterActivComerc.ViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.tvPresentacion.setText(Data.get(position).getPresentacion());
        holder.tvTipo.setText(Data.get(position).getNomenclatura());
        holder.tvUbicacion.setText(Data.get(position).getEspacio());


        int id = VerificarYaHizoUnRegistro(Data.get(position).getIddata(), Data.get(position).getFechaSubido());
        if (id > 0) {
            holder.tvCodigo.setBackgroundColor(Color.parseColor("#BCF5A9"));
            holder.tvCodigo.setText(String.format("Item(%s), idlocal(%d)", Data.get(position).getItem(), id));
            Glide.with(mContext).load(PathFotoTomada).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.ivFotoTomada);
        } else {
            holder.tvCodigo.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.tvCodigo.setText(String.format("Item(%s)", Data.get(position).getItem()));
        }
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    private int VerificarYaHizoUnRegistro(String iddata, String fechasubido) {
        try {

            Cursor cursor = new BaseDatos(mContext).obtenerRegistroWhereArgs(Variables.TBL_RPT_ACTIVIDAD_COMERCIAL,
                    "idData = '" + iddata + "' and FechaSubido ='" + fechasubido + "' ORDER BY idRptActivComerc DESC");
            if (cursor.moveToFirst()) {
                PathFotoTomada = cursor.getString(cursor.getColumnIndex("fotopath"));
                return cursor.getCount();
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPresentacion, tvTipo, tvUbicacion, tvCodigo;
        ImageView ivFotoTomada;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPresentacion = itemView.findViewById(R.id.tvPresentacion_rowactividcomerc);
            tvTipo = itemView.findViewById(R.id.tvTipo_rowactividcomerc);
            tvUbicacion = itemView.findViewById(R.id.tvUbicavcion_rowactividcomerc);
            tvCodigo = itemView.findViewById(R.id.tvCodigoPresent_rowactividcomerc);
            ivFotoTomada = itemView.findViewById(R.id.ivFotoTomada_rowactividcomerc);
        }
    }
}
