package com.deliverycircuit.ehtp.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.text.HtmlCompat;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AutoCompleteLocationAdapter extends ArrayAdapter<LocationItem> {
    private List<LocationItem> locationListFull;

    public AutoCompleteLocationAdapter(@NonNull Context context, @NonNull List<LocationItem> countryList) {
        super(context, 0, countryList);
        locationListFull = new ArrayList<>();
        locationListFull = countryList;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return countryFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.location_autocomplete_row, parent, false
            );
        }

        TextView locationName = convertView.findViewById(R.id.text_view_name);
        TextView locationDescription = convertView.findViewById(R.id.location_description);


        LocationItem locationItem = getItem(position);

        if (locationItem != null) {
            locationName.setText(locationItem.getLocationName());
            locationDescription.setText(HtmlCompat.fromHtml(locationItem.getLocationDescription(), 0));

        }

        return convertView;
    }

    private Filter countryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<LocationItem> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(locationListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (LocationItem item : locationListFull) {
                    if (item.getLocationName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (locationListFull != null) {
                clear();
                addAll((List) results.values);
                notifyDataSetChanged();
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((LocationItem) resultValue).getLocationName();
        }
    };
}
