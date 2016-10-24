package org.dhis2.mobile.ui.adapters.dataEntry.rows;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.dhis2.mobile.R;


/**
 * Created by george on 10/24/16.
 */

public class LabelRow implements Row {
    private final LayoutInflater inflater;
    private final String label;

    public LabelRow(LayoutInflater inflater, String label){
        this.inflater = inflater;
        this.label = label;
    }
    @Override
    public View getView(View convertView) {
        View view;
        TextView labelView;


        if (convertView == null) {
            ViewGroup rowRoot = (ViewGroup) inflater.inflate(R.layout.listview_row_label_row, null);
            labelView = (TextView) rowRoot.findViewById(R.id.text_label);

            rowRoot.setTag(labelView);
            view = rowRoot;
        } else {
            view = convertView;
            labelView = (TextView) view.getTag();
        }

       labelView.setText(label);

        //Offset view 30dp from the left. Why? Because design.
        view.setX(30);

        return view;
    }

    @Override
    public int getViewType(){
        return RowTypes.LABEL.ordinal();
    }
}
