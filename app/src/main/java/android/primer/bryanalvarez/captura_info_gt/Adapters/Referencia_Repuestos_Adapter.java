package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Producto;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Referencia_Repuestos_Adapter extends ArrayAdapter<Producto> implements Filterable {

    Context context;
    int resource, textViewResourceId;
    List<Producto> items, tempItems, suggestions;
    private final Object mLock = new Object();

    public Referencia_Repuestos_Adapter(Context context, int resource, int textViewResourceId, List<Producto> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context; this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<Producto>(items);
        // this makes the difference.
        suggestions = new ArrayList<Producto>();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.autocomplete_item_repuesto, parent, false);
        }
        Producto producto = items.get(position);
        if (producto != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name_repuesto);
            if (lblName != null) lblName.setText(producto.getReferencia());
        } return view;
    }

    @Override public Filter getFilter() {
        return nameFilter;
    }
    /** * Custom Filter implementation for custom suggestions we provide. */
    Filter nameFilter = new Filter() {
        @Override public CharSequence convertResultToString(Object resultValue) {
            String str = ((Producto) resultValue).getReferencia();
            return str;
        }
        @Override protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Producto producto : tempItems) {
                    if (producto.getReferencia().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(producto);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            }else{
                return new FilterResults();
            }
        }
        @Override protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Producto> filterList = (ArrayList<Producto>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
                clear();
                for (Producto producto : filterList) {
                    add(producto);
                }
                notifyDataSetChanged();
            }
        }
    };
}
