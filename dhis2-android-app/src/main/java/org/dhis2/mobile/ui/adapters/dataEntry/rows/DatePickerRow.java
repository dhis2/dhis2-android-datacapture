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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.ui.adapters.dataEntry.FieldAdapter;
import org.dhis2.mobile.ui.fragments.DatePickerDialog;
import org.dhis2.mobile.ui.fragments.DatePickerDialog.OnDateSetListener;
import org.joda.time.LocalDate;

public class DatePickerRow implements Row {
    private LayoutInflater inflater;
    private Field field;
    private FieldAdapter adapter;
    private Context context;
    private LocalDate currentDate;
    public boolean readOnly = false;
    
    public DatePickerRow(LayoutInflater inflater, Field field, FieldAdapter adapter, Context context) {
        this.inflater= inflater;
        this.field = field;
        this.adapter = adapter;
        this.context = context;
        
        currentDate = new LocalDate();
    }

    @Override
    public View getView(View convertView) {
        View view;
        DatePickerRowHolder holder;
        
        if (convertView == null) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.listview_row_datepicker, null);
            
            TextView textLabel = (TextView) rootView.findViewById(R.id.text_label);
            ImageView clearButton = (ImageView) rootView.findViewById(R.id.clearEditText);
            EditText pickerInvoker = (EditText) rootView.findViewById(R.id.date_picker_dialog_invoker);
          
            DateSetListener dateSetListener = new DateSetListener(field, adapter);
            OnEditTextClickListener invokerListener = new OnEditTextClickListener(field, currentDate, dateSetListener, context);
            ClearButtonListener clButtonListener = new ClearButtonListener(pickerInvoker, field);
            
            pickerInvoker.setOnClickListener(invokerListener);
            clearButton.setOnClickListener(clButtonListener);
            
            holder = new DatePickerRowHolder(textLabel, pickerInvoker, clearButton, 
                    clButtonListener, dateSetListener, invokerListener);
            
            rootView.setTag(holder);
            
            view = rootView; 
        } else {
            view = convertView;
            holder = (DatePickerRowHolder) view.getTag();  
        }
        RowCosmetics.setTextLabel(field, holder.textLabel);
        
        holder.dateSetListener.setField(field);
        holder.invokerListener.setFieldAndListener(field, holder.dateSetListener);
        holder.pickerInvoker.setText(field.getValue());
        holder.pickerInvoker.setOnClickListener(holder.invokerListener);
        
        holder.cbListener.setEditText(holder.pickerInvoker, field);
        holder.clearButton.setOnClickListener(holder.cbListener);

        if(readOnly){
            holder.clearButton.setEnabled(false);
            holder.pickerInvoker.setEnabled(false);
        } else {
            holder.clearButton.setEnabled(true);
            holder.pickerInvoker.setEnabled(true);
        }
        return view;
    }

    @Override
    public int getViewType() {
        return RowTypes.DATE.ordinal();
    }

    @Override
    public void setReadOnly(boolean value) {
        readOnly = value;
    }


    private class DatePickerRowHolder {
        final TextView textLabel;
        final EditText pickerInvoker;
        final DateSetListener dateSetListener;
        final OnEditTextClickListener invokerListener;
        final ImageView clearButton;
        final ClearButtonListener cbListener;
        
        DatePickerRowHolder(TextView textLabel, EditText pickerInvoker, 
                ImageView clearButton, ClearButtonListener cbListener, 
                DateSetListener dateSetListener, OnEditTextClickListener invokerListener) {
            
            this.textLabel = textLabel;
            this.pickerInvoker = pickerInvoker;
            this.dateSetListener = dateSetListener;
            this.invokerListener = invokerListener;
            this.clearButton = clearButton;
            this.cbListener = cbListener;
        }
    }
    
    private class DateSetListener implements OnDateSetListener {
        private Field field;
        private FieldAdapter adapter;
    
        DateSetListener(Field field, FieldAdapter adapter) {
            this.field = field;
            this.adapter = adapter;
        }
        
        void setField(Field field) {
            this.field = field;
        }
        
        @Override
        public void onDateSet(LocalDate date) {
            field.setValue(date.toString("YYYY-MM-dd"));
            adapter.notifyDataSetChanged();
        }
        
    }
    
    private class OnEditTextClickListener implements OnClickListener {
        private Field field;
        private DateSetListener listener;
        private LocalDate currentDate;
        private Context context;
       
        OnEditTextClickListener(Field field, LocalDate currentDate, 
                DateSetListener listener, Context context) {
            this.currentDate = currentDate;
            
            this.field = field;
            this.listener = listener;
            this.context = context;
        }
        
        void setFieldAndListener(Field field, DateSetListener listener) {
            this.field = field;
            this.listener = listener;
        } 
        
		@Override
        public void onClick(View view) {
            DatePickerDialog picker = new DatePickerDialog(context, listener, field.getLabel(),
                    currentDate.getYear(), currentDate.getMonthOfYear() - 1, currentDate.getDayOfMonth()); 
            picker.show();
        }   
    }
    
    private class ClearButtonListener implements OnClickListener {
        private EditText editText;
        private Field field;
        
        public ClearButtonListener(EditText editText, Field field) {
            this.editText = editText;
            this.field = field;
        }
        
        public void setEditText(EditText editText, Field field) {
            this.editText = editText;
            this.field = field;
        }

        @Override
        public void onClick(View view) {
            editText.setText(Field.EMPTY_FIELD);
            field.setValue(Field.EMPTY_FIELD);
        }     
    }
}
