package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Producto;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

public class Productos_Adapter extends RecyclerView.Adapter<Productos_Adapter.ViewHolder> {

    private Context context;
    private List<Producto> list;
    private int layout;
    private OnItemClickListener itemClickListener;
    private OnButtonBorrarClickListener onButtonBorrarClickListener;

    public Productos_Adapter(Context context, List<Producto> list, int layout, OnItemClickListener itemClickListener, OnButtonBorrarClickListener onButtonBorrarClickListener) {
        this.context = context;
        this.list = list;
        this.layout = layout;
        this.itemClickListener = itemClickListener;
        this.onButtonBorrarClickListener = onButtonBorrarClickListener;
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
        holder.bind(list.get(position),itemClickListener,onButtonBorrarClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_item_producto_referencia;
        public TextView tv_item_producto_precio;
        public TextView tv_item_producto_cantidad;
        public ImageButton ib_item_producto_borrar;


        public ViewHolder(View itemView) {
            super(itemView);

            tv_item_producto_referencia = (TextView) itemView.findViewById(R.id.tv_item_producto_referencia);
            tv_item_producto_precio = (TextView) itemView.findViewById(R.id.tv_item_producto_precio);
            tv_item_producto_cantidad = (TextView) itemView.findViewById(R.id.tv_item_producto_cantidad);
            ib_item_producto_borrar = (ImageButton) itemView.findViewById(R.id.ib_item_producto_borrar);

        }

        public void bind(final Producto producto, final OnItemClickListener itemClickListener, final OnButtonBorrarClickListener onButtonBorrarClickListener) {
            tv_item_producto_referencia.setText(producto.getReferencia());
            tv_item_producto_precio.setText("$ "+NumberFormat.getInstance().format(producto.getPrecio()));
            tv_item_producto_cantidad.setText(producto.getCantidad());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(producto, getAdapterPosition());
                }
            });

            ib_item_producto_borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonBorrarClickListener.onButtonBorrarClick(producto,getAdapterPosition());
                }
            });
        }


    }
    public interface OnItemClickListener {
        void onItemClick(Producto producto, int position);
    }
    public interface OnButtonBorrarClickListener {
        void onButtonBorrarClick(Producto producto, int position);
    }
}
