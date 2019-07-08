package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.primer.bryanalvarez.captura_info_gt.Models.Maquina;
import android.primer.bryanalvarez.captura_info_gt.Models.Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by nayar on 28/11/2018.
 */

public class Maquinas_Adapter extends RecyclerView.Adapter<Maquinas_Adapter.ViewHolder> {

    private Context context;
    private List<Maquina> lista;
    private int layout;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private int position;

    public Maquinas_Adapter(Context context, List<Maquina> lista, int layout, OnItemClickListener itemClickListener,OnItemLongClickListener onItemLongClickListener) {
        this.context = context;
        this.lista = lista;
        this.layout = layout;
        this.itemClickListener = itemClickListener;
        this.onItemLongClickListener =onItemLongClickListener;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        context = parent.getContext();
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(lista.get(position),itemClickListener,onItemLongClickListener);
        if (lista.get(position).isAgregado()){
            //holder.tv_nombre_maquina.setBackgroundColor(Color.parseColor("#9769c463"));
            holder.tv_nombre_maquina.setTextColor(Color.parseColor("#076b07"));
            holder.tv_nombre_maquina.setTypeface(null, Typeface.BOLD);
        }else{
            //holder.tv_nombre_maquina.setBackgroundColor(Color.WHITE);
            holder.tv_nombre_maquina.setTextColor(Color.DKGRAY);
            holder.tv_nombre_maquina.setTypeface(null, Typeface.NORMAL);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_nombre_maquina;
        public ImageView iv_imagen_maquina;
        public LinearLayout linear_layout_maquina;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_nombre_maquina = (TextView) itemView.findViewById(R.id.tv_nombre_maquina);
            iv_imagen_maquina = (ImageView) itemView.findViewById(R.id.iv_imagen_maquina);
            linear_layout_maquina = (LinearLayout) itemView.findViewById(R.id.linear_layout_maquina);
        }

        public void bind(final Maquina maquina, final OnItemClickListener itemClickListener, final OnItemLongClickListener onItemLongClickListener) {
            tv_nombre_maquina.setText(maquina.getModelo_maquina().toString());
            Picasso.with(context).load(maquina.getImagen_equipo()).into(iv_imagen_maquina);
            if (maquina.isAgregado()){
                //tv_nombre_maquina.setBackgroundColor(Color.parseColor("#9769c463"));
                tv_nombre_maquina.setTextColor(Color.parseColor("#076b07"));
                tv_nombre_maquina.setTypeface(null, Typeface.BOLD);
            }else{
                //tv_nombre_maquina.setBackgroundColor(Color.WHITE);
                tv_nombre_maquina.setTextColor(Color.DKGRAY);
                tv_nombre_maquina.setTypeface(null, Typeface.NORMAL);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(maquina, getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    PopupMenu popup = new PopupMenu(context,itemView);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.context_menu_maquinas);
                    //adding click listener
                    onItemLongClickListener.onItemLongClick(maquina,getAdapterPosition(),popup);
                    return false;

                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(Maquina maquina, int position);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(Maquina maquina, int position, PopupMenu popupMenu);
    }
}
