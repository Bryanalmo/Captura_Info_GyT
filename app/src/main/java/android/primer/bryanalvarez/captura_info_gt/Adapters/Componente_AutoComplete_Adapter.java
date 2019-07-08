package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Componente;
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

public class Componente_AutoComplete_Adapter extends ArrayAdapter<Componente> implements Filterable {

    Context context;
    int resource, textViewResourceId;
    List<Componente> items, tempItems, suggestions;

    public Componente_AutoComplete_Adapter(Context context, int resource, int textViewResourceId, List<Componente> items) {
        super(context,resource,textViewResourceId,items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<Componente>(items);
        suggestions = new ArrayList<Componente>();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.autocomplete_item, parent, false);
        }
        Componente componente = items.get(position);
        if (componente != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null) lblName.setText(componente.getNombre());
        } return view;
    }

    @Override public Filter getFilter() {
        return nameFilter;
    }
    /** * Custom Filter implementation for custom suggestions we provide. */
    Filter nameFilter = new Filter() {
        @Override public CharSequence convertResultToString(Object resultValue) {
            String str = ((Componente) resultValue).getNombre();
            return str;
        }
        @Override protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Componente componente : tempItems) {
                    if (componente.getNombre().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(componente);
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
            List<Componente> filterList = (ArrayList<Componente>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
                clear();
                for (Componente componente : filterList) {
                    add(componente); notifyDataSetChanged();
                }
            } else {
                notifyDataSetInvalidated();
            }
        }
    };
}
