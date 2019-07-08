package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion;
import android.primer.bryanalvarez.captura_info_gt.Models.Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by nayar on 15/10/2018.
 */

public class Vehiculos_Adapter extends BaseAdapter {

    private Context context;
    private List<Vehiculo> lista;
    private int layout;

    public Vehiculos_Adapter(Context context, List<Vehiculo> lista, int layout) {
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
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder();
            vh.tv_list_view_modelo_vehiculo = (TextView) convertView.findViewById(R.id.tv_list_view_modelo_vehiculo);
            vh.tv_list_view_capacidad_vehiculo = (TextView) convertView.findViewById(R.id.tv_list_view_capacidad_vehiculo);
            vh.iv_list_view_imagen = (ImageView) convertView.findViewById(R.id.iv_list_view_imagen);

            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        Vehiculo vehiculo = lista.get(position);
        vh.tv_list_view_modelo_vehiculo.setText(vehiculo.getModelo().toString());
        vh.tv_list_view_capacidad_vehiculo.setText(vehiculo.getCapacidad().toString());
        Picasso.with(context).load(vehiculo.getImagen()).into(vh.iv_list_view_imagen);;

        return convertView;
    }

    public class ViewHolder{

        TextView tv_list_view_modelo_vehiculo;
        TextView tv_list_view_capacidad_vehiculo;
        ImageView iv_list_view_imagen;

    }


}
