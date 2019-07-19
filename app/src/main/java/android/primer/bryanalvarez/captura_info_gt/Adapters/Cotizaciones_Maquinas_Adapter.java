package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_Maquina;
import android.primer.bryanalvarez.captura_info_gt.Models.SubCotizacion;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nayar on 6/12/2018.
 */

public class Cotizaciones_Maquinas_Adapter extends BaseAdapter {

    private Context context;
    private List<Cotizacion_Maquina> lista;
    private int layout;

    public Cotizaciones_Maquinas_Adapter(Context context, List<Cotizacion_Maquina> lista, int layout) {
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
            vh.tv_list_view_cliente_maquina = (TextView) convertView.findViewById(R.id.tv_list_view_cliente_maquina);
            vh.tv_list_view_maquina = (TextView) convertView.findViewById(R.id.tv_list_view_maquina);
            vh.tv_list_view_comercial_maquina = (TextView) convertView.findViewById(R.id.tv_list_view_comercial_maquina);
            vh.tv_list_view_valor_cotizacion = (TextView) convertView.findViewById(R.id.tv_list_view_valor_cotizacion);
            vh.iv_list_view_foto_maquina = (ImageView) convertView.findViewById(R.id.iv_list_view_foto_maquina);
            vh.iv_enviar_cotizacion = (ImageView) convertView.findViewById(R.id.iv_enviar_cotizacion);

            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        Cotizacion_Maquina cotizacion = lista.get(position);
        if (cotizacion.getId_estado_envio().equals("1")){
            vh.iv_enviar_cotizacion.setVisibility(View.VISIBLE);
        }else{
            vh.iv_enviar_cotizacion.setVisibility(View.GONE);
        }
        vh.tv_list_view_cliente_maquina.setText(cotizacion.getCliente());
        ArrayList<SubCotizacion> subCotizaciones = cotizacion.getSubCotizaciones();
        String maquinas = "";
        for (int i = 0; i < subCotizaciones.size(); i++) {
            if (i == (subCotizaciones.size()-1)){
                maquinas += subCotizaciones.get(i).getModelo_maquina();
            }else{
                maquinas += subCotizaciones.get(i).getModelo_maquina() + ", ";
            }

        }
        vh.tv_list_view_maquina.setText(maquinas);
        vh.tv_list_view_comercial_maquina.setText(cotizacion.getComercial());
        vh.tv_list_view_valor_cotizacion.setText("$ " +NumberFormat.getInstance().format(cotizacion.getValor()));
        Picasso.with(context).load(cotizacion.getSubCotizaciones().get(0).getImagen()).into(vh.iv_list_view_foto_maquina);

        return convertView;

    }

    public class ViewHolder{

        TextView tv_list_view_cliente_maquina;
        TextView tv_list_view_maquina;
        TextView tv_list_view_comercial_maquina;
        TextView tv_list_view_valor_cotizacion;
        ImageView iv_list_view_foto_maquina;
        ImageView iv_enviar_cotizacion;

    }
}
