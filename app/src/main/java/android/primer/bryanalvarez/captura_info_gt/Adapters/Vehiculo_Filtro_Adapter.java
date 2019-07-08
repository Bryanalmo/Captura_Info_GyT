package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class Vehiculo_Filtro_Adapter extends RecyclerView.Adapter<Vehiculo_Filtro_Adapter.ViewHolder>{

    private Context context;
    private List<Vehiculo> list;
    private int layout;
    private OnItemClickListener itemClickListener;
    private OnButtonBorrarClickListener btnBorrarClickListener;

    public Vehiculo_Filtro_Adapter(Context context, List<Vehiculo> list, int layout, OnItemClickListener itemClickListener, OnButtonBorrarClickListener btnBorrarClickListener) {
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
        holder.ib_borrar_agregar_vehiculo_filtro.setChecked(list.get(position).isAgregado());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_nombre_vehiculo_filtro;
        public CheckBox ib_borrar_agregar_vehiculo_filtro;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_nombre_vehiculo_filtro = (TextView) itemView.findViewById(R.id.tv_nombre_vehiculo_filtro);
            ib_borrar_agregar_vehiculo_filtro = (CheckBox) itemView.findViewById(R.id.ib_borrar_agregar_vehiculo_filtro);
        }

        public void bind(final Vehiculo vehiculo, final OnButtonBorrarClickListener onButtonBorrarClickListener, final OnItemClickListener itemClickListener) {
            tv_nombre_vehiculo_filtro.setText(vehiculo.getModelo());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(vehiculo, getAdapterPosition());
                }
            });

            ib_borrar_agregar_vehiculo_filtro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = ib_borrar_agregar_vehiculo_filtro.isChecked();
                    onButtonBorrarClickListener.onButtonBorrarClick(vehiculo, getAdapterPosition(),isChecked);
                }
            });


        }
    }

    public interface OnItemClickListener {
        void onItemClick(Vehiculo vehiculo, int position);
    }

    public interface OnButtonBorrarClickListener {
        void onButtonBorrarClick(Vehiculo vehiculo, int position, boolean isChecked);
    }
}
