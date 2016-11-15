package org.dhis2.ehealthMobile.ui.fragments;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.dhis2.ehealthMobile.R;
import org.joda.time.LocalDate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

/**
 * A simple dialog containing an {@link android.widget.DatePicker}.
 *
 * <p>See the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide.</p>
 */
public class DatePickerDialog extends AlertDialog implements OnDateChangedListener {
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";

    private final DatePicker mDatePicker;
    private OnDateSetListener mCallBack;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {
        void onDateSet(LocalDate date);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public DatePickerDialog(Context context,
            OnDateSetListener callBack,
            String title,
            int year,
            int monthOfYear,
            int dayOfMonth) {
        this(context, 0, title, callBack, year, monthOfYear, dayOfMonth);
    }
    
    /**
     * @param context The context the dialog is to run in.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public DatePickerDialog(Context context,
            String title,
            int year,
            int monthOfYear,
            int dayOfMonth) {
        this(context, 0, title, null, year, monthOfYear, dayOfMonth);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param theme the theme to apply to this dialog
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public DatePickerDialog(Context context,
            int theme,
            String title,
            OnDateSetListener callBack,
            int year,
            int monthOfYear,
            int dayOfMonth) {
        super(context, theme);

        mCallBack = callBack;

        Context themeContext = getContext();
        
        DialogInterface.OnClickListener negButtonClickListener = new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        };
        
        DialogInterface.OnClickListener posButtonListener = new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker dp = getDatePicker();
                LocalDate date = new LocalDate(dp.getYear(), dp.getMonth() + 1, dp.getDayOfMonth());
                mCallBack.onDateSet(date);
            }
        };
        
        setButton(DialogInterface.BUTTON_POSITIVE, 
                themeContext.getText(android.R.string.ok), posButtonListener);     
        setButton(DialogInterface.BUTTON_NEGATIVE, 
                themeContext.getText(android.R.string.cancel), negButtonClickListener);
        setIcon(0);

        LayoutInflater inflater =
                (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_date_picker, null);
        setView(view);
        
        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        mDatePicker.init(year, monthOfYear, dayOfMonth, this);
        setTitle(title);
    }

    public void onDateChanged(DatePicker view, int year,
            int month, int day) {
        mDatePicker.init(year, month, day, this);
    }

    /**
     * Gets the {@link DatePicker} contained in this dialog.
     *
     * @return The calendar view.
     */
    public DatePicker getDatePicker() {
        return mDatePicker;
    }

    /**
     * Sets the current date.
     *
     * @param year The date year.
     * @param monthOfYear The date month.
     * @param dayOfMonth The date day of month.
     */
    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, mDatePicker.getYear());
        state.putInt(MONTH, mDatePicker.getMonth());
        state.putInt(DAY, mDatePicker.getDayOfMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        int day = savedInstanceState.getInt(DAY);
        mDatePicker.init(year, month, day, this);
    }
    
    public void setOnDateSetListener(OnDateSetListener callback) {
    	mCallBack = callback;
    }
}
