package com.example.vincent.babynursinglayouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vincent.babynursinglayouts.models.PumpingEntry;

import java.util.List;

/**
 * Created by vincent on 3/14/17.
 */

public class PumpingArrayAdapter<T> extends ArrayAdapter {

    private List<T> list;
    private int resource, headerResource;

    public PumpingArrayAdapter(Context context, int resource, int headerResource, List<T> list){
        super(context,resource,headerResource,list);
        this.list = list;
        this.resource = resource;
        this.headerResource = headerResource;
    }

    public PumpingArrayAdapter(Context context, int resource,  List<T> list) {
        super(context,resource,list);
        this.resource = resource;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, resource);

    }

    @Override
    public boolean isEnabled(int position){
        if (position % 4 == 0)
            return false;
        return true;
    }

    @Override
    public T getItem(int position){
        return list.get(position);
    }

    public View createViewFromResource(final int position, View convertView, ViewGroup parent, int resource){
        View view;
        LayoutInflater mInflater = (LayoutInflater)  getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView text, rightText;
        ImageView img;
        if(convertView == null)
            if (resource == R.layout.list_view_new_pumping_entry_header_cell)   //specifically for new pumping entry page.
                if(position % 4 == 0) {//we have a header entry
                    this.resource=resource;
                    resource = R.layout.list_view_new_pumping_entry_header_cell;
                    view = mInflater.inflate(resource, parent, false); //we have a header entry

                } else {//we have a list entry

                    this.resource=resource;
                    resource = R.layout.list_view_new_pumping_entry;
                    view = mInflater.inflate(resource, parent, false);
                }
            else
                view = mInflater.inflate(resource,parent,false); //default call
        else
            view = convertView;
        switch(resource) {

            case R.layout.list_view_header_cell_pumping:
                TextView pumpingsTextView = (TextView) view.findViewById(R.id.pumpingsDataTextView);
                TextView averageWeightTextView = (TextView) view.findViewById(R.id.averageWeightDataTextView);
                TextView totalWeightTextView = (TextView) view.findViewById(R.id.weightTotalDataTextView);
                PumpingEntry pumpingEntry = ((PumpingEntry)getItem(position));
                pumpingsTextView.setText(pumpingEntry.getTotalPumps() + "");
                averageWeightTextView.setText(pumpingEntry.getAverageWeight()+"");
                totalWeightTextView.setText(pumpingEntry.getTotalWeight()+"");
                return view;

            case R.layout.list_view_new_pumping_entry_header_cell:
                TextView header = (TextView) view.findViewById(R.id.newPumpingHeaderTextView);
                try {
                    header.setText(((List<String>) getItem(position)).get(0));
                } catch (NullPointerException e){

                }

                return view;
            case R.layout.list_view_new_pumping_entry:
                TextView cellHeader = (TextView) view.findViewById(R.id.newPumpingCellHeaderTextView);
                TextView userEntry = (TextView) view.findViewById(R.id.newPumpingUserEntryTextView);
                cellHeader.setText(((List<String>) getItem(position)).get(0));
                userEntry.setText(((List<String>) getItem(position)).get(1));
                return view;
        }
        return view;
    }

}
