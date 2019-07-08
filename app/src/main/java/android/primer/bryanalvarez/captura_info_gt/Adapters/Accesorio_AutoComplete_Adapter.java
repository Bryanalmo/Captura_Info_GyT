package android.primer.bryanalvarez.captura_info_gt.Adapters;

import android.content.Context;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio_Maquina;
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

public class Accesorio_AutoComplete_Adapter extends ArrayAdapter<Accesorio_Maquina> implements Filterable {

    Context context;
    int resource, textViewResourceId;
    List<Accesorio_Maquina> items, tempItems, suggestions;

    public Accesorio_AutoComplete_Adapter(Context context, int resource, int textViewResourceId, List<Accesorio_Maquina> items) {
        super(context,resource,textViewResourceId,items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<Accesorio_Maquina>(items);
        suggestions = new ArrayList<Accesorio_Maquina>();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.autocomplete_item, parent, false);
        }
        Accesorio_Maquina accesorio_maquina = items.get(position);
        if (accesorio_maquina != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null) lblName.setText(accesorio_maquina.getNombre());
        } return view;
    }

    @Override public Filter getFilter() {
        return nameFilter;
    }
    /** * Custom Filter implementation for custom suggestions we provide. */
    Filter nameFilter = new Filter() {
        @Override public CharSequence convertResultToString(Object resultValue) {
            String str = ((Accesorio_Maquina) resultValue).getNombre();
            return str;
        }
        @Override protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Accesorio_Maquina accesorio_maquina : tempItems) {
                    if (accesorio_maquina.getNombre().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(accesorio_maquina);
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
            List<Accesorio_Maquina> filterList = (ArrayList<Accesorio_Maquina>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
                clear();
                for (Accesorio_Maquina accesorio_maquina : filterList) {
                    add(accesorio_maquina); notifyDataSetChanged();
                }
            } else {
                notifyDataSetInvalidated();
            }
        }
    };
}
