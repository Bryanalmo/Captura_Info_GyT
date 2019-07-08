package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

public class Accesorio_Vehiculo_Adapter extends RecyclerView.Adapter<Accesorio_Vehiculo_Adapter.ViewHolder>{

    private Context context;
    private List<Accesorio> list;
    private int layout;
    private OnItemClickListener itemClickListener;
    private OnButtonBorrarClickListener btnBorrarClickListener;

    public Accesorio_Vehiculo_Adapter(Context context, List<Accesorio> list, int layout, OnItemClickListener itemClickListener, OnButtonBorrarClickListener btnBorrarClickListener) {
        this.context = context;
        this.list = list;
        this.layout = layout;
        this.itemClickListener = itemClickListener;
        this.btnBorrarClickListener = btnBorrarClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        context = parent.getContext();
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position),btnBorrarClickListener,itemClickListener);
        holder.ib_borrar_agregar_accesorio_vehiculo.setChecked(list.get(position).isCheck());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_nombre_accesorio_vehiculo;
        public CheckBox ib_borrar_agregar_accesorio_vehiculo;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_nombre_accesorio_vehiculo = (TextView) itemView.findViewById(R.id.tv_nombre_accesorio_vehiculo);
            ib_borrar_agregar_accesorio_vehiculo = (CheckBox) itemView.findViewById(R.id.ib_borrar_agregar_accesorio_vehiculo);
        }

        public void bind(final Accesorio accesorio, final OnButtonBorrarClickListener onButtonBorrarClickListener, final OnItemClickListener itemClickListener) {
            long valor = accesorio.getValor();
            String format_valor= NumberFormat.getInstance().format(valor);
            tv_nombre_accesorio_vehiculo.setText(accesorio.getReferencia() + " - " + "$" + format_valor + " + IVA");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(accesorio, getAdapterPosition());
                }
            });

            ib_borrar_agregar_accesorio_vehiculo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = ib_borrar_agregar_accesorio_vehiculo.isChecked();
                    onButtonBorrarClickListener.onButtonBorrarClick(accesorio, getAdapterPosition(),isChecked);
                }
            });


        }
    }

    public interface OnItemClickListener {
        void onItemClick(Accesorio accesorio, int position);
    }

    public interface OnButtonBorrarClickListener {
        void onButtonBorrarClick(Accesorio accesorio, int position, boolean isChecked);
    }

}
