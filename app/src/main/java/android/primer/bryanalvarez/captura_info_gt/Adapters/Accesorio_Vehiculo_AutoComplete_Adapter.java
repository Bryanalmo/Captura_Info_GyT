package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio;
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

public class Accesorio_Vehiculo_AutoComplete_Adapter extends ArrayAdapter<Accesorio> implements Filterable {

    Context context;
    int resource, textViewResourceId;
    List<Accesorio> items, tempItems, suggestions;

    public Accesorio_Vehiculo_AutoComplete_Adapter(Context context, int resource, int textViewResourceId, List<Accesorio> items) {
        super(context,resource,textViewResourceId,items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<Accesorio>(items);
        suggestions = new ArrayList<Accesorio>();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.autocomplete_item, parent, false);
        }
        Accesorio accesorio = items.get(position);
        if (accesorio != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null) lblName.setText(accesorio.getReferencia());
        } return view;
    }

    @Override public Filter getFilter() {
        return nameFilter;
    }
    /** * Custom Filter implementation for custom suggestions we provide. */
    Filter nameFilter = new Filter() {
        @Override public CharSequence convertResultToString(Object resultValue) {
            String str = ((Accesorio) resultValue).getReferencia();
            return str;
        }
        @Override protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Accesorio accesorio : tempItems) {
                    if (accesorio.getReferencia().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(accesorio);
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
            List<Accesorio> filterList = (ArrayList<Accesorio>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
                clear();
                for (Accesorio accesorio : filterList) {
                    add(accesorio); notifyDataSetChanged();
                }
            } else {
                notifyDataSetInvalidated();
            }
        }
    };
}
