package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Componente;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nayar on 4/12/2018.
 */

public class Componente_Adapter extends RecyclerView.Adapter<Componente_Adapter.ViewHolder> {

    private Context context;
    private List<Componente> list;
    private int layout;
    private OnItemClickListener itemClickListener;
    private OnButtonBorrarClickListener btnBorrarClickListener;

    public Componente_Adapter(Context context, List<Componente> list, int layout, OnItemClickListener itemClickListener, OnButtonBorrarClickListener btnBorrarClickListener) {
        this.context = context;
        this.list = list;
        this.layout = layout;
        this.itemClickListener = itemClickListener;
        this.btnBorrarClickListener = btnBorrarClickListener;
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
        holder.bind(list.get(position),btnBorrarClickListener,itemClickListener);
        holder.ib_borrar_agregar_componente.setChecked(list.get(position).isAgregado());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_nombre_componente;
        public CheckBox ib_borrar_agregar_componente;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_nombre_componente = (TextView) itemView.findViewById(R.id.tv_nombre_componente);
            ib_borrar_agregar_componente = (CheckBox) itemView.findViewById(R.id.ib_borrar_agregar_componente);
        }

        public void bind(final Componente componente, final OnButtonBorrarClickListener onButtonBorrarClickListener, final OnItemClickListener itemClickListener) {
            tv_nombre_componente.setText(componente.toString());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(componente, getAdapterPosition());
                }
            });

            ib_borrar_agregar_componente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = ib_borrar_agregar_componente.isChecked();
                    onButtonBorrarClickListener.onButtonBorrarClick(componente, getAdapterPosition(),isChecked);
                }
            });


        }
    }

    public interface OnItemClickListener {
        void onItemClick(Componente componente, int position);
    }

    public interface OnButtonBorrarClickListener {
        void onButtonBorrarClick(Componente componente, int position, boolean isChecked);
    }

}
