package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

public class Vehiculos_Cotizados_Adapter extends RecyclerView.Adapter<Vehiculos_Cotizados_Adapter.ViewHolder> {

    private Context context;
    private List<Cotizacion> list;
    private int layout;
    private OnItemClickListener itemClickListener;

    public Vehiculos_Cotizados_Adapter(Context context, List<Cotizacion> list, int layout, OnItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.layout = layout;
        this.itemClickListener = itemClickListener;
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
        holder.bind(list.get(position),itemClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_nombre_vehiculo_resumen;
        public TextView tv_precio_vehiculo_resumen;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_nombre_vehiculo_resumen = (TextView) itemView.findViewById(R.id.tv_nombre_vehiculo_resumen);
            tv_precio_vehiculo_resumen = (TextView) itemView.findViewById(R.id.tv_precio_vehiculo_resumen);
        }

        public void bind(final Cotizacion subCotizacion, final OnItemClickListener itemClickListener) {
            tv_nombre_vehiculo_resumen.setText(subCotizacion.getVehiculo().getModelo());
            tv_precio_vehiculo_resumen.setText("$ "+ NumberFormat.getInstance().format(subCotizacion.getValor()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(subCotizacion, getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Cotizacion subCotizacion, int position);
    }
}
