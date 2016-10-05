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
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.io.Constants;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.utils.IsCritical;
import org.dhis2.mobile.utils.IsDisabled;

import java.util.ArrayList;

public class PosOrZeroIntegerRow2 implements Row {
    public static final String TAG = PosOrZeroIntegerRow2.class.getSimpleName();
    public static final String PREFIX = " EIDSR-";
    private final LayoutInflater inflater;
    private final Field field,field2, field3, field4;
    private AlertDialog alertDialog;
    private AlertDialog criticalDiseaseAlertDialog;
    private final String defaultValue = "0";



    public PosOrZeroIntegerRow2(LayoutInflater inflater, Field field, Field field2, Field field3, Field field4) {
        this.inflater = inflater;
        this.field = field;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
    }

    @Override
    public View getView(View convertView) {
        View view;

        ArrayList<EditTextHolder> holders = new ArrayList<>();

        ArrayList<Field> fields = new ArrayList<>();
        fields.add(field);
        fields.add(field2);
        fields.add(field3);
        fields.add(field4);


        if (convertView == null) {
            ViewGroup rowRoot = (ViewGroup) inflater.inflate(R.layout.listview_row_integer_positive_or_zero_3, null);
            final EditText editText = (EditText) rowRoot.findViewById(R.id.edit_integer_pos_row);
            EditText editText2 = (EditText) rowRoot.findViewById(R.id.edit_integer_pos_row_2);
            EditText editText3 = (EditText) rowRoot.findViewById(R.id.edit_integer_pos_row_3);
            EditText editText4 = (EditText) rowRoot.findViewById(R.id.edit_integer_pos_row_4);


            ArrayList<EditText> editTexts = new ArrayList<>();
            editTexts.add(editText);
            editTexts.add(editText2);
            editTexts.add(editText3); editTexts.add(editText4);


            initializeEditTextHolders(editTexts, fields, rowRoot, holders);

            view = rowRoot;

            alertDialog = new AlertDialog.Builder(view.getContext()).create();
            criticalDiseaseAlertDialog = new AlertDialog.Builder(view.getContext()).create();
        } else {
            view = convertView;

            holders.add((EditTextHolder) view.getTag(R.id.TAG_HOLDER1_ID));
            holders.add((EditTextHolder) view.getTag(R.id.TAG_HOLDER2_ID));
            holders.add((EditTextHolder) view.getTag(R.id.TAG_HOLDER3_ID));
            holders.add((EditTextHolder) view.getTag(R.id.TAG_HOLDER4_ID));

            alertDialog = new AlertDialog.Builder(view.getContext()).create();
            criticalDiseaseAlertDialog = new AlertDialog.Builder(view.getContext()).create();
        }


        setupEditTextHolders(holders, fields, view);

        setOnFocusChangeListeners(holders, view.getContext());

        view.setTag(field.getDataElement());

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


    private void setOnFocusChangeListeners(final ArrayList<EditTextHolder> editTextHolders, final Context context){
        for( int i = 0; i < editTextHolders.size(); i++){
            final int finalI = i;
            editTextHolders.get(i).editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasLostFocusAndIsNotEmpty(hasFocus,editTextHolders.get(finalI).editText)) {
                        setupValidations(editTextHolders.get(finalI), context);
                    }
                }
            });
        }
    }

    private void setupValidations(EditTextHolder editTextHolder, Context context){
        if(editTextHolder.textWatcher.hasChanged()  && Integer.parseInt(editTextHolder.editText.getText().toString()) > 0 ){
            editTextHolder.textWatcher.setChanged(false);
            if(IsCritical.check(field, context)){
                showCriticalValidation(editTextHolder, context);
            }
        }
    }

    private void showCriticalValidation(final EditTextHolder holder,Context context ){
        if(!alertDialog.isShowing()){
            criticalDiseaseAlertDialog.setTitle(context.getString(R.string.validation_alert_dialog_title));
            if(holder.isCasesField){
                criticalDiseaseAlertDialog.setMessage("You are about to submit "+ holder.editText.getText()+" cases(s) for "+ field.getLabel().split(PREFIX)[0].substring(6));
            }else{
                criticalDiseaseAlertDialog.setMessage("You are about to submit "+ holder.editText.getText()+" death(s) for "+ field.getLabel().split(PREFIX)[0].substring(6));
            }
            criticalDiseaseAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.validation_alert_dialog_confirmation), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int position) {
                    dialogInterface.dismiss();
                }
            });

            criticalDiseaseAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.validation_alert_dialog_rejection), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int position) {
                    holder.editText.setText(defaultValue);
                    dialogInterface.dismiss();
                }
            });
            criticalDiseaseAlertDialog.setCanceledOnTouchOutside(false);
            if (!criticalDiseaseAlertDialog.isShowing()) {
                criticalDiseaseAlertDialog.show();
            }
        }

    }

    /**
     * Rather than initializing everything line by line, the filters, holders and watchers are put in a list
     * and iterated over initializing every editText and editTextHolder accordingly.
     * @param editTexts ArrayList<EditText>
     * @param fields ArrayList<Field>
     * @param rowRoot ViewGroup
     * @param holders ArrayList<EditTextHolder>
     */
    private void initializeEditTextHolders(ArrayList<EditText> editTexts, ArrayList<Field> fields, ViewGroup rowRoot, final ArrayList<EditTextHolder> holders){
        TextView label = (TextView) rowRoot.findViewById(R.id.text_label);

        ArrayList<Integer> tagsIds = new ArrayList<>();
        tagsIds.add(R.id.TAG_HOLDER1_ID);
        tagsIds.add(R.id.TAG_HOLDER2_ID);
        tagsIds.add(R.id.TAG_HOLDER3_ID);
        tagsIds.add(R.id.TAG_HOLDER4_ID);

        for(int i = 0; i < editTexts.size(); i++){
            editTexts.get(i).setFilters(new InputFilter[]{new InpFilter()});
            EditTextWatcher watcher = new EditTextWatcher(fields.get(i));
            editTexts.get(i).addTextChangedListener(watcher);
            holders.add(new EditTextHolder(label, editTexts.get(i), watcher, isCasesField(fields.get(i))));
            rowRoot.setTag(tagsIds.get(i), holders.get(i));
        }
    }

    /**
     * Sets up the editTextHolders that have been initialized in an array list.
     * It then iterates over the list and sets properties and checks accordingly.
     * This is so we don't have repeating lines of code doing the same thing.
     * @param holders ArrayList<EditTextHolder>
     * @param fields ArrayList<Field>
     * @param view View
     */
    private void setupEditTextHolders(ArrayList<EditTextHolder> holders, ArrayList<Field> fields, View view){
        for(int i = 0; i < holders.size(); i++){
            String[] label = fields.get(i).getLabel().split(PREFIX);

            holders.get(i).textLabel.setText(label[0].substring(6));
            holders.get(i).textWatcher.setField(fields.get(i));
            holders.get(i).editText.addTextChangedListener(holders.get(i).textWatcher);
            holders.get(i).editText.setText(fields.get(i).getValue());
            holders.get(i).editText.setSelectAllOnFocus(true);
            holders.get(i).editText.clearFocus();

            IsDisabled.setEnabled(holders.get(i).editText, fields.get(i), view.getContext());

        }
    }

    private Boolean isCasesField(Field field){
        Boolean isCasesField = false;
        if(field.getCategoryOptionCombo().equals(Constants.UNDER_FIVE_CASES)
                || field.getCategoryOptionCombo().equals(Constants.OVER_FIVE_CASES)){
            isCasesField = true;
        }
        return isCasesField;
    }

    private Boolean hasLostFocusAndIsNotEmpty(Boolean hasFocus, EditText editText){
        Boolean hasLostFocusAndNotIsEmpty = false;
        if(!hasFocus && !isEmptyEditText(editText)){
            hasLostFocusAndNotIsEmpty = true;
        }
        return hasLostFocusAndNotIsEmpty;
    }

    private Boolean isEmptyEditText(EditText editText){
        Boolean isEmpty = false;
        if(editText.getText().toString().equals("")){
            isEmpty = true;
        }
        return isEmpty;
    }

}
