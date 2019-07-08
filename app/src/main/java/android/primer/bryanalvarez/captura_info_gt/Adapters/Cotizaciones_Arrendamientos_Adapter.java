package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Arrendamiento_Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_Arrendamiento;
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

public class Cotizaciones_Arrendamientos_Adapter extends BaseAdapter {

    private Context context;
    private List<Cotizacion_Arrendamiento> lista;
    private int layout;

    public Cotizaciones_Arrendamientos_Adapter(Context context, List<Cotizacion_Arrendamiento> lista, int layout) {
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
            vh.tv_list_view_cliente = (TextView) convertView.findViewById(R.id.tv_list_view_cliente);
            vh.tv_list_view_modelo = (TextView) convertView.findViewById(R.id.tv_list_view_modelo);
            vh.tv_list_view_comercial = (TextView) convertView.findViewById(R.id.tv_list_view_comercial);
            vh.tv_list_view_fecha = (TextView) convertView.findViewById(R.id.tv_list_view_fecha);
            vh.iv_list_view_foto_vehiculo = (ImageView) convertView.findViewById(R.id.iv_list_view_foto_vehiculo);

            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        Cotizacion_Arrendamiento cotizacion = lista.get(position);
        vh.tv_list_view_cliente.setText(cotizacion.getCliente().getNombre());
        ArrayList<Arrendamiento_Vehiculo> subCotizaciones = cotizacion.getSubCotizaciones();
        vh.tv_list_view_modelo.setText(subCotizaciones.get(0).getVehiculo().getModelo());
        vh.tv_list_view_comercial.setText(cotizacion.getComercial());
        vh.tv_list_view_fecha.setText(cotizacion.getFecha());
        Picasso.with(context).load(subCotizaciones.get(0).getVehiculo().getImagen()).into(vh.iv_list_view_foto_vehiculo);;

        return convertView;


    }

    public class ViewHolder{

        TextView tv_list_view_cliente;
        TextView tv_list_view_modelo;
        TextView tv_list_view_comercial;
        TextView tv_list_view_fecha;
        ImageView iv_list_view_foto_vehiculo;

    }
}
