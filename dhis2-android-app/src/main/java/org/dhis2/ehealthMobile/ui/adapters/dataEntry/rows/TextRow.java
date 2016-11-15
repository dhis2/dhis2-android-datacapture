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

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class TextRow implements Row {
    private final LayoutInflater inflater;
    private final Field field;
    
    public TextRow(LayoutInflater inflater, Field field) {
        this.inflater = inflater;
        this.field = field;
    }

    @Override
    public View getView(int position, View convertView) {
        View view;
        EditTextHolder holder;

        
        if (convertView == null) {
            ViewGroup rowRoot = (ViewGroup) inflater.inflate(R.layout.listview_row_text, null);
            TextView label = (TextView) rowRoot.findViewById(R.id.text_label);
            EditText editText = (EditText) rowRoot.findViewById(R.id.edit_text_row);
            TextInputLayout inputLayout = (TextInputLayout) rowRoot.findViewById(R.id.edit_text_row_layout);


           
            EditTextWatcher watcher = new EditTextWatcher(field);
            editText.addTextChangedListener(watcher);
            
            holder = new EditTextHolder(label, editText, watcher);
            rowRoot.setTag(holder);
            view = rowRoot;
        } else {
            view = convertView;
            holder = (EditTextHolder) view.getTag();
        }
        
        holder.textLabel.setText(field.getLabel());
        
        holder.textWatcher.setField(field);
        holder.editText.addTextChangedListener(holder.textWatcher);
        holder.editText.setText(field.getValue());
        holder.editText.clearFocus();

        return view;
    }

    @Override
    public int getViewType() {
        return RowTypes.TEXT.ordinal();
    }    
}

class EditTextWatcher implements TextWatcher {
    private Field field;
    private Boolean changed = false;
    private String originalValue;

    EditTextWatcher(Field field) {
        this.field = field;

    }

    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public void afterTextChanged(Editable arg) {
        originalValue = field.getValue();
        field.setValue(arg.toString());
        if(!originalValue.equals(arg.toString())){
            changed = true;
        }
        originalValue = field.getValue();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    public Boolean hasChanged(){
        return changed;
    }
    public void setChanged(Boolean change){
        changed = change;
    }

}

class EditTextHolder {
    final TextView numberLabel;
    final TextView textLabel;
    final EditText editText;
    final EditTextWatcher textWatcher;
    final Boolean isCasesField;

    EditTextHolder(TextView numberLabel, TextView textLabel, EditText editText, EditTextWatcher textWatcher, Boolean isCasesField) {
        this.textLabel = textLabel;
        this.editText = editText;
        this.textWatcher = textWatcher;
        this.isCasesField = isCasesField;
        this.numberLabel = numberLabel;
    }


    EditTextHolder(TextView textLabel, EditText editText, EditTextWatcher textWatcher) {
        this.textLabel = textLabel;
        this.editText = editText;
        this.textWatcher = textWatcher;
        isCasesField = null;
        numberLabel = null;
    }


}