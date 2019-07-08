package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio_Maquina;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class Accesorio_Maquina_Adapter extends RecyclerView.Adapter<Accesorio_Maquina_Adapter.ViewHolder>  {
    private Context context;
    private List<Accesorio_Maquina> list;
    private int layout;
    private OnItemClickListener itemClickListener;
    private OnButtonBorrarClickListener btnBorrarClickListener;

    public Accesorio_Maquina_Adapter(Context context, List<Accesorio_Maquina> list, int layout, OnItemClickListener itemClickListener, OnButtonBorrarClickListener btnBorrarClickListener) {
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
        holder.ib_borrar_agregar_accesorio_maquina.setChecked(list.get(position).isAgregado());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_nombre_accesorio_maquina;
        public CheckBox ib_borrar_agregar_accesorio_maquina;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_nombre_accesorio_maquina = (TextView) itemView.findViewById(R.id.tv_nombre_accesorio_maquina);
            ib_borrar_agregar_accesorio_maquina = (CheckBox) itemView.findViewById(R.id.ib_borrar_agregar_accesorio_maquina);
        }

        public void bind(final Accesorio_Maquina accesorio_maquina, final OnButtonBorrarClickListener onButtonBorrarClickListener, final OnItemClickListener itemClickListener) {
            tv_nombre_accesorio_maquina.setText(accesorio_maquina.toString());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(accesorio_maquina, getAdapterPosition());
                }
            });

            ib_borrar_agregar_accesorio_maquina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = ib_borrar_agregar_accesorio_maquina.isChecked();
                    onButtonBorrarClickListener.onButtonBorrarClick(accesorio_maquina, getAdapterPosition(),isChecked);
                }
            });


        }
    }

    public interface OnItemClickListener {
        void onItemClick(Accesorio_Maquina accesorio_maquina, int position);
    }

    public interface OnButtonBorrarClickListener {
        void onButtonBorrarClick(Accesorio_Maquina accesorio_maquina, int position, boolean isChecked);
    }

}
