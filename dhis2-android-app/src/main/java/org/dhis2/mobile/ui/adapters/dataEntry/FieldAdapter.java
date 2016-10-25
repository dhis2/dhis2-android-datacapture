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

package org.dhis2.mobile.ui.adapters.dataEntry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.dhis2.mobile.io.Constants;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.io.models.OptionSet;
import org.dhis2.mobile.io.models.eidsr.Disease;
import org.dhis2.mobile.ui.activities.DataEntryActivity;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.AutoCompleteRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.BooleanRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.CheckBoxRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.DatePickerRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.GenderRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.IntegerRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.LongTextRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.NegativeIntegerRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.NumberRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.PosIntegerRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.PosOrZeroIntegerRow2;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.Row;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.RowTypes;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.TextRow;
import org.dhis2.mobile.utils.IsAdditionalDisease;
import org.dhis2.mobile.utils.IsCritical;
import org.dhis2.mobile.utils.TextFileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FieldAdapter extends BaseAdapter {
    private ArrayList<Row> rows;
    private final String adapterLabel;
    private final Group group;
    private LayoutInflater inflater;
    private IsCritical isCritical;
    private IsAdditionalDisease isAdditionalDisease;
    private Map<String, Map<String, PosOrZeroIntegerRow2>> additionalDiseasesRows = new HashMap<>();

    public FieldAdapter(Group group, Context context) {
        ArrayList<Field> fields = group.getFields();
        Collections.sort(fields, Field.COMPARATOR);
        ArrayList<Field> groupedFields = new ArrayList<Field>();
        String previousFieldId = "";
        this.group = group;
        this.rows = new ArrayList<Row>();
        this.adapterLabel = group.getLabel();
        inflater = LayoutInflater.from(context);
        isCritical = new IsCritical(context);
        isAdditionalDisease = new IsAdditionalDisease(context);
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            if (field.hasOptionSet()) {
                OptionSet optionSet = getOptionSet(context, field.getOptionSet());
                rows.add(new AutoCompleteRow(inflater, field, optionSet, context));
            } else if (field.getType().equals(RowTypes.TEXT.name())) {
                rows.add(new TextRow(inflater, field));
            } else if (field.getType().equals(RowTypes.LONG_TEXT.name())) {
                rows.add(new LongTextRow(inflater, field));
            } else if (field.getType().equals(RowTypes.NUMBER.name())) {
                rows.add(new NumberRow(inflater, field));
            } else if (field.getType().equals(RowTypes.INTEGER.name())) {
                rows.add(new IntegerRow(inflater, field));
            } else if (field.getType().equals(RowTypes.INTEGER_ZERO_OR_POSITIVE.name())) {
                //Changed from the others to support grouping of Diseases
                //Specific test case for eidsr form

                handleIntegerOrZeroRow2(field, groupedFields, previousFieldId);

                groupedFields.add(field);
                previousFieldId = field.getDataElement();
            } else if (field.getType().equals(RowTypes.INTEGER_POSITIVE.name())) {
                rows.add(new PosIntegerRow(inflater, field));
            } else if (field.getType().equals(RowTypes.INTEGER_NEGATIVE.name())) {
                rows.add(new NegativeIntegerRow(inflater, field));
            } else if (field.getType().equals(RowTypes.BOOLEAN.name())) {
                if(!field.getDataElement().equals(Constants.TIMELY)){
                    rows.add(new BooleanRow(inflater, field));
                }
            } else if (field.getType().equals(RowTypes.TRUE_ONLY.name())) {
                rows.add(new CheckBoxRow(inflater, field));
            } else if (field.getType().equals(RowTypes.DATE.name())) {
                rows.add(new DatePickerRow(inflater, field, this, context));
            } else if (field.getType().equals(RowTypes.GENDER.name())) {
                rows.add(new GenderRow(inflater, field));
            }
        }
        for(Map.Entry<String, Map<String, PosOrZeroIntegerRow2>> disease : additionalDiseasesRows.entrySet()){
            for(Map.Entry<String, PosOrZeroIntegerRow2> rows: additionalDiseasesRows.get(disease.getKey()).entrySet()){
                this.rows.add(rows.getValue());
                //we then need to tell the dataEntryActivity that this additional disease has already been displayed.
                if(context instanceof DataEntryActivity){
                    ((DataEntryActivity)context).addToDiseasesShown(disease.getKey(), rows.getKey());
                }
            }
        }

    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public int getViewTypeCount() {
        return RowTypes.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).getViewType();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return rows.get(position).getView(position, convertView);
    }

    public String getLabel() {
        return adapterLabel;
    }

    public Group getGroup() {
        return group;
    }

    private static OptionSet getOptionSet(Context context, String id) {
        String source = TextFileUtils.readTextFile(context, TextFileUtils.Directory.OPTION_SETS, id);
        try {
            JsonObject jOptionSet = JsonHandler.buildJsonObject(source);
            Gson gson = new Gson();
            return gson.fromJson(jOptionSet, OptionSet.class);
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addItem(Disease disease) {
        ArrayList<Field> fields = this.group.getFields();
        ArrayList<Field> additionalDiseaseFields = new ArrayList<>();

        for(Field field: fields){
            if(field.getDataElement().equals(disease.getId())){
                additionalDiseaseFields.add(field);
            }
        }

        this.rows.add(new PosOrZeroIntegerRow2(inflater, additionalDiseaseFields,false, true));
        notifyDataSetChanged();
    }

    public void removeItemAtPosition(int position){
        PosOrZeroIntegerRow2 row = (PosOrZeroIntegerRow2) this.rows.get(position);
        clearPosOrZeroIntegerRow2Fields(row);
        this.rows.remove(position);
        notifyDataSetChanged();
    }

    private Boolean groupedFieldsHasValue(ArrayList<Field> groupedFields){
        int size = groupedFields.size();

        for(int i = 1; i < 5; i ++){
            if(!isFieldValueEmpty(groupedFields.get(size - i))){
               return true;
            }
        }


        return false;
    }

    private void clearPosOrZeroIntegerRow2Fields(PosOrZeroIntegerRow2 row){
        row.field.setValue("");
        row.field2.setValue("");
        row.field3.setValue("");
        row.field4.setValue("");
    }
    private Boolean isFieldValueEmpty(Field field){
        Boolean isEmpty = false;
        if(field.getValue().equals("")){
            isEmpty = true;
        }
        return isEmpty;
    }

    private void handleIntegerOrZeroRow2(Field field, ArrayList<Field> groupedFields, String previousFieldId){
        if(!field.getDataElement().equals(previousFieldId) && groupedFields.size() > 0
                && !isAdditionalDisease.check(previousFieldId)){
            //each disease has four fields.
            Boolean isCriticalDisease = isCritical.check(previousFieldId);
            Boolean isAnAdditionalDisease = isAdditionalDisease.check(previousFieldId);
            rows.add(new PosOrZeroIntegerRow2(inflater, groupedFields,isCriticalDisease, isAnAdditionalDisease));

        }
        //check if its an additional disease and has values.
        if(!field.getDataElement().equals(previousFieldId) && groupedFields.size() > 0 && isAdditionalDisease.check(previousFieldId)
                && groupedFieldsHasValue(groupedFields))
        {
            //if it is an additional disease and does have value then we add it to a map of additional diseases rows
            //we later iterate over the map and add these additional diseases to the bottom of the listView
            Boolean isAnAdditionalDisease = isAdditionalDisease.check(previousFieldId);
            additionalDiseasesRows.put(previousFieldId, new HashMap<String, PosOrZeroIntegerRow2>());
            additionalDiseasesRows.get(previousFieldId).put(groupedFields.get(groupedFields.size()-1).getLabel(),
                    new PosOrZeroIntegerRow2(inflater, groupedFields,false, isAnAdditionalDisease));

        }
    }

}
