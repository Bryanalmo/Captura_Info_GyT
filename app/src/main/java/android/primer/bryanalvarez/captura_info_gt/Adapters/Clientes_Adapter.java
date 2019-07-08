package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Cliente;
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

public class Clientes_Adapter extends ArrayAdapter<Cliente> implements Filterable {

    Context context;
    int resource, textViewResourceId;
    List<Cliente> items, tempItems, suggestions;

    public Clientes_Adapter(Context context, int resource, int textViewResourceId, List<Cliente> items) {
        super(context,resource,textViewResourceId,items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<Cliente>(items);
        suggestions = new ArrayList<Cliente>();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.autocomplete_item, parent, false);
        }
        Cliente cliente = items.get(position);
        if (cliente != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null) lblName.setText(cliente.getNombre());
        } return view;
    }

    @Override public Filter getFilter() {
        return nameFilter;
    }
    /** * Custom Filter implementation for custom suggestions we provide. */
    Filter nameFilter = new Filter() {
        @Override public CharSequence convertResultToString(Object resultValue) {
            String str = ((Cliente) resultValue).getNombre();
            return str;
        }
        @Override protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Cliente cliente : tempItems) {
                    if (cliente.getNombre().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(cliente);
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
            List<Cliente> filterList = (ArrayList<Cliente>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
                clear();
                for (Cliente cliente : filterList) {
                    add(cliente); notifyDataSetChanged();
                }
            } else {
                notifyDataSetInvalidated();
            }
        }
    };
}
