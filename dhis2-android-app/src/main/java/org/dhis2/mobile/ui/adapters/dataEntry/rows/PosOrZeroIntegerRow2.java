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

package org.dhis2.mobile.ui.adapters.dataEntry.rows;

import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.io.models.Field;

public class PosOrZeroIntegerRow2 implements Row {
    private final LayoutInflater inflater;
    private final Field field;
    private final Field field2, field3, field4;

    public PosOrZeroIntegerRow2(LayoutInflater inflater, Field field, Field field2, Field field3, Field field4 ) {
        this.inflater = inflater;
        this.field = field;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
    }

    @Override
    public View getView(View convertView) {
        View view;
        EditTextHolder holder;
        EditTextHolder holder2;
        EditTextHolder holder3;
        EditTextHolder holder4;



        if (convertView == null) {
            ViewGroup rowRoot = (ViewGroup) inflater.inflate(R.layout.listview_row_integer_positive_or_zero_3, null);
            TextView label = (TextView) rowRoot.findViewById(R.id.text_label);
            EditText editText = (EditText) rowRoot.findViewById(R.id.edit_integer_pos_row);
            EditText editText2 = (EditText) rowRoot.findViewById(R.id.edit_integer_pos_row_2);
            EditText editText3 = (EditText) rowRoot.findViewById(R.id.edit_integer_pos_row_3);
            EditText editText4 = (EditText) rowRoot.findViewById(R.id.edit_integer_pos_row_4);

            TextInputLayout inputLayout = (TextInputLayout) rowRoot.findViewById(R.id.edit_integer_pos_layout);
            TextInputLayout inputLayout2 = (TextInputLayout) rowRoot.findViewById(R.id.edit_integer_pos_layout_2);
            TextInputLayout inputLayout3 = (TextInputLayout) rowRoot.findViewById(R.id.edit_integer_pos_layout_3);
            TextInputLayout inputLayout4 = (TextInputLayout) rowRoot.findViewById(R.id.edit_integer_pos_layout_4);


            editText.setFilters(new InputFilter[]{new InpFilter()});
            editText2.setFilters(new InputFilter[]{new InpFilter()});
            editText3.setFilters(new InputFilter[]{new InpFilter()});
            editText4.setFilters(new InputFilter[]{new InpFilter()});


            EditTextWatcher watcher = new EditTextWatcher(field);
            editText.addTextChangedListener(watcher);

            EditTextWatcher watcher2 = new EditTextWatcher(field2);
            editText2.addTextChangedListener(watcher2);

            EditTextWatcher watcher3 = new EditTextWatcher(field3);
            editText3.addTextChangedListener(watcher3);

            EditTextWatcher watcher4 = new EditTextWatcher(field4);
            editText4.addTextChangedListener(watcher4);









            
            holder = new EditTextHolder(label, editText, watcher,inputLayout);


            holder2 = new EditTextHolder(label, editText2, watcher2,inputLayout2);
            holder3 = new EditTextHolder(label, editText3, watcher3,inputLayout3);
            holder4 = new EditTextHolder(label, editText4, watcher4,inputLayout4);
            rowRoot.setTag(R.id.TAG_HOLDER1_ID, holder);
            rowRoot.setTag(R.id.TAG_HOLDER2_ID, holder2);
            rowRoot.setTag(R.id.TAG_HOLDER3_ID, holder3);
            rowRoot.setTag(R.id.TAG_HOLDER4_ID, holder4);



            view = rowRoot;
        } else {
            view = convertView;
            holder = (EditTextHolder) view.getTag(R.id.TAG_HOLDER1_ID);
            holder2 = (EditTextHolder) view.getTag(R.id.TAG_HOLDER2_ID);
            holder3 = (EditTextHolder) view.getTag(R.id.TAG_HOLDER3_ID);
            holder4 = (EditTextHolder) view.getTag(R.id.TAG_HOLDER4_ID);
        }

            String[] label = field.getLabel().split("<");

            holder.textLabel.setText(label[0]);

            holder.textWatcher.setField(field);
            holder.editText.addTextChangedListener(holder.textWatcher);
            holder.editText.setText(field.getValue());
        if (holder.inputLayout != null) {
            holder.inputLayout.setHint("<"+field.getLabel().split("<")[1].split(",")[0]);
        }
        holder.editText.clearFocus();




            holder2.textWatcher.setField(field2);
            holder2.editText.addTextChangedListener(holder2.textWatcher);
            holder2.editText.setText(field2.getValue());
        assert holder2.inputLayout != null;
        holder2.inputLayout.setHint("<"+field2.getLabel().split("<")[1].split(",")[0]);
            holder2.editText.clearFocus();



            holder3.textWatcher.setField(field3);
            holder3.editText.addTextChangedListener(holder3.textWatcher);
            holder3.editText.setText(field3.getValue());
        assert holder3.inputLayout != null;
        holder3.inputLayout.setHint(">"+field3.getLabel().split(">")[1].split(",")[0]);
            holder3.editText.clearFocus();



            holder4.textWatcher.setField(field4);
            holder4.editText.addTextChangedListener(holder4.textWatcher);
            holder4.editText.setText(field4.getValue());
        assert holder4.inputLayout != null;
        holder4.inputLayout.setHint(">"+field4.getLabel().split(">")[1].split(",")[0]);
            holder4.editText.clearFocus();





        return view;
    }

    @Override
    public int getViewType() {
        return RowTypes.INTEGER_ZERO_OR_POSITIVE.ordinal();
    }
    
    private class InpFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                Spanned spn, int spStart, int spEnd) {
            
            if ((str.length() > 0) && (spn.length() > 0) && (spn.charAt(0) == '0')) {
                return Field.EMPTY_FIELD;
            }
            
            if ((spn.length() > 0) && (spStart == 0) && (str.length() > 0) && (str.charAt(0) == '0')) {
                return Field.EMPTY_FIELD;
            }
           
            return str;
        }       
    }
}
