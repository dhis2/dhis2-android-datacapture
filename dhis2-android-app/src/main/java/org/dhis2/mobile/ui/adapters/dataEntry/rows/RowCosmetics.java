package org.dhis2.mobile.ui.adapters.dataEntry.rows;

import android.graphics.Color;
import android.icu.text.DateFormat;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import org.dhis2.mobile.io.models.Field;

class RowCosmetics {

    public static void setTextLabel(Field field, TextView textLabel) {
        if(field.isCompulsory()){
            int red = Color.RED;
            String formattedColor = String.format("%X", red).substring(2);
            Spanned spannedQuestion= Html.fromHtml(String.format("<font color=\"#%s\"><b>", formattedColor) + "*  " + "</b></font>");
            textLabel.setText(spannedQuestion);
            textLabel.append(field.getLabel());
        }else {
            textLabel.setText(field.getLabel());
        }
    }
}
