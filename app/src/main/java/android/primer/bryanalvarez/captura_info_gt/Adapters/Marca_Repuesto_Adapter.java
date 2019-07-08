package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Marca;
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

public class Marca_Repuesto_Adapter extends ArrayAdapter<Marca> implements Filterable {

    Context context;
    int resource, textViewResourceId;
    List<Marca> items, tempItems, suggestions;

    public Marca_Repuesto_Adapter(Context context, int resource, int textViewResourceId, List<Marca> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<Marca>(items);
        suggestions = new ArrayList<Marca>();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.autocomplete_item, parent, false);
        }
        Marca marca = items.get(position);
        if (marca != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null) lblName.setText(marca.getMarca());
        } return view;
    }

    @Override public Filter getFilter() {
        return nameFilter;
    }
    /** * Custom Filter implementation for custom suggestions we provide. */
    Filter nameFilter = new Filter() {
        @Override public CharSequence convertResultToString(Object resultValue) {
            String str = ((Marca) resultValue).getMarca();
            return str;
        }
        @Override protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Marca marca : tempItems) {
                    if (marca.getMarca().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(marca);
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
            List<Marca> filterList = (ArrayList<Marca>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
                clear();
                for (Marca marca : filterList) {
                    add(marca); notifyDataSetChanged();
                }
            } else {
                notifyDataSetInvalidated();
            }
        }
    };
}
