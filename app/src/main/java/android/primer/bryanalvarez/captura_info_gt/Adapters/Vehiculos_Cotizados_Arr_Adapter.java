package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Arrendamiento_Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

public class Vehiculos_Cotizados_Arr_Adapter extends RecyclerView.Adapter<Vehiculos_Cotizados_Arr_Adapter.ViewHolder> {

    private Context context;
    private List<Arrendamiento_Vehiculo> list;
    private int layout;
    private OnItemClickListener itemClickListener;

    public Vehiculos_Cotizados_Arr_Adapter(Context context, List<Arrendamiento_Vehiculo> list, int layout, OnItemClickListener itemClickListener) {
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
        public TextView tv_plazo_precio_vehiculo;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_nombre_vehiculo_resumen = (TextView) itemView.findViewById(R.id.tv_nombre_vehiculo_resumen);
            tv_plazo_precio_vehiculo = (TextView) itemView.findViewById(R.id.tv_plazo_precio_vehiculo);
        }

        public void bind(final Arrendamiento_Vehiculo subCotizacion, final OnItemClickListener itemClickListener) {
            tv_nombre_vehiculo_resumen.setText("("+subCotizacion.getCantidad()+ ") -  "+ subCotizacion.getVehiculo().getModelo());
            tv_plazo_precio_vehiculo.setText(subCotizacion.getPlazos().get(0).getPlazo() + " - " + subCotizacion.getPlazos().get(0).getPrecio() );
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(subCotizacion, getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Arrendamiento_Vehiculo subCotizacion, int position);
    }
}
