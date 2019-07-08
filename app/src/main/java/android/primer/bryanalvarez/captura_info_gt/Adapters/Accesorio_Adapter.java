package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by nayar on 26/10/2018.
 */

public class Accesorio_Adapter extends RecyclerView.Adapter<Accesorio_Adapter.ViewHolder>{

    private Context context;
    private List<Accesorio> list;
    private int layout;
    private OnCheckChangeAccesorio onCheckChangeAccesorio;

    public Accesorio_Adapter(Context context, List<Accesorio> list, int layout, OnCheckChangeAccesorio onCheckChangeAccesorio) {
        this.context = context;
        this.list = list;
        this.layout = layout;
        this.onCheckChangeAccesorio = onCheckChangeAccesorio;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        context = parent.getContext();
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(list.get(position),onCheckChangeAccesorio);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cb_list_view_accesorio;

        public ViewHolder(View itemView) {
            super(itemView);
            cb_list_view_accesorio = (CheckBox) itemView.findViewById(R.id.cb_list_view_accesorio);
        }

        public void bind(final Accesorio accesorio, final OnCheckChangeAccesorio onCheckChangeAccesorio) {
            long valor = accesorio.getValor();
            String format_valor= NumberFormat.getInstance().format(valor);
            cb_list_view_accesorio.setText(accesorio.getReferencia() + " - " + "$" + format_valor + " + IVA");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            if (accesorio.isCheck()){
                cb_list_view_accesorio.setChecked(true);
            }

            cb_list_view_accesorio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onCheckChangeAccesorio.onCheckChange(accesorio, getAdapterPosition(),cb_list_view_accesorio.isChecked());
                }
            });
        }
    }

    public interface OnCheckChangeAccesorio {
        void onCheckChange(Accesorio accesorio, int position, boolean isChecked);
    }
}
