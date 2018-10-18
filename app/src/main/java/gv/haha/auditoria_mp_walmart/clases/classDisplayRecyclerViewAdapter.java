package gv.haha.auditoria_mp_walmart.clases;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gv.haha.auditoria_mp_walmart.FragmentTodosReportes;
import gv.haha.auditoria_mp_walmart.R;


public class classDisplayRecyclerViewAdapter extends RecyclerView.Adapter<classDisplayRecyclerViewAdapter.ViewHolder> {

    private final List<DisplayList> mValues;
    public static View.OnClickListener myOnClickListener;

    public classDisplayRecyclerViewAdapter(List<DisplayList> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_classdisplay, parent, false);

        view.setOnClickListener(FragmentTodosReportes.myOnClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText("");
            holder.mNombreView.setText(mValues.get(position).getNombreDisplay());
            holder.mFechaView.setText(mValues.get(position).getFecha());

            //get first letter of each String item
            String firstLetter = String.valueOf(mValues.get(position).getIdRegistro());
            classColorGenerator generator = classColorGenerator.MATERIAL; // or use DEFAULT
            // generate random color
            int color = generator.getColor(mValues.get(position));

            classTextDrawable drawable = classTextDrawable.builder()
                    .buildRound(firstLetter, color); // radius in px

            holder.mIdView.setBackground(drawable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mIdView;
        public TextView mNombreView;
        public TextView mFechaView;
        public DisplayList mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.tvIdRpt_ListDisp_frgmtodosRpt);
            mNombreView = (TextView) view.findViewById(R.id.tvNombre_ListDisp_frgmtodosRpt);
            mFechaView = (TextView) view.findViewById(R.id.tvFecha_ListDisp_frgmtodosRpt);
        }

        @Override
        public String toString() {

            return super.toString() + " '" + mNombreView.getText() + "'";
        }
    }
}
