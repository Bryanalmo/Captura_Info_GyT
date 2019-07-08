package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_Repuesto;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Cotizaciones_Repuestos_Adapter extends BaseAdapter {

    private Context context;
    private List<Cotizacion_Repuesto> lista;
    private int layout;

    public Cotizaciones_Repuestos_Adapter(Context context, List<Cotizacion_Repuesto> lista, int layout) {
        this.context = context;
        this.lista = lista;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh;

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder();
            vh.tv_item_numero_cot_repuesto = (TextView) convertView.findViewById(R.id.tv_item_numero_cot_repuesto);
            vh.tv_item_fecha_cot_repuesto = (TextView) convertView.findViewById(R.id.tv_item_fecha_cot_repuesto);
            vh.tv_item_cliente_cot_repuesto = (TextView) convertView.findViewById(R.id.tv_item_cliente_cot_repuesto);
            vh.tv_item_estado_cot_repuesto = (TextView) convertView.findViewById(R.id.tv_item_estado_cot_repuesto);
            vh.tv_item_observaciones_cot_repuesto = (TextView) convertView.findViewById(R.id.tv_item_observaciones_cot_repuesto);

            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        Cotizacion_Repuesto cotizacion = lista.get(position);
        vh.tv_item_numero_cot_repuesto.setText(cotizacion.getNumero());
        vh.tv_item_fecha_cot_repuesto.setText(cotizacion.getFechaSolicitud());
        vh.tv_item_cliente_cot_repuesto.setText(cotizacion.getNombreCliente());
        vh.tv_item_estado_cot_repuesto.setText(cotizacion.getEstado());
        vh.tv_item_observaciones_cot_repuesto.setText(cotizacion.getObservaciones());

        return convertView;

    }

    public class ViewHolder{

        TextView tv_item_numero_cot_repuesto;
        TextView tv_item_fecha_cot_repuesto;
        TextView tv_item_cliente_cot_repuesto;
        TextView tv_item_estado_cot_repuesto;
        TextView tv_item_observaciones_cot_repuesto;

    }
}
