/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.ehealthMobile.ui.adapters.dataEntry.rows;

import org.dhis2.ehealthMobile.io.models.Field;

import org.dhis2.ehealthMobile.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CheckBoxRow implements Row {
    private LayoutInflater inflater;
    private Field field;
    
    public CheckBoxRow(LayoutInflater inflater, Field field) {
        this.inflater = inflater;
        this.field = field;
    }

    @Override
    public View getView(int position, View convertView) {
        View view;
        CheckBoxHolder holder;
        
        if (convertView == null) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.listview_row_checkbox, null);
            TextView textLabel = (TextView) rootView.findViewById(R.id.text_label);
            CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.checkbox);
            CheckBoxListener listener = new CheckBoxListener(field);
            
            checkBox.setOnCheckedChangeListener(listener);
            holder = new CheckBoxHolder(textLabel, checkBox, listener);
            
            rootView.setTag(holder);
            view = rootView;
        } else {
            view = convertView;
            holder = (CheckBoxHolder) view.getTag();
        }
        
        holder.textLabel.setText(field.getLabel());
        holder.listener.setField(field);
        
        if (field.getValue().equals(Field.TRUE)) holder.checkBox.setChecked(true);
        else if (field.getValue().equals(Field.EMPTY_FIELD)) holder.checkBox.setChecked(false);
        
        return view;
    }

    @Override
    public int getViewType() {
        return RowTypes.TRUE_ONLY.ordinal();
    }
    
    private class CheckBoxListener implements OnCheckedChangeListener {
        private Field field;
        
        CheckBoxListener(Field field) {
            this.field = field;
        }
        
        void setField(Field field) {
            this.field = field;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                field.setValue(Field.TRUE);
            } else {
                field.setValue(Field.EMPTY_FIELD);
            }
        }
        
    }

    private class CheckBoxHolder {
        final TextView textLabel;
        final CheckBox checkBox;
        final CheckBoxListener listener;
        
        CheckBoxHolder(TextView textLabel, CheckBox checkBox, CheckBoxListener listener) {
            this.textLabel = textLabel;
            this.checkBox = checkBox;
            this.listener = listener;
        }
    }   
}


