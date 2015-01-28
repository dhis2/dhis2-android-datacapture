package org.hisp.dhis.mobile.datacapture.ui.adapters.rows;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class DatePickerRow implements Row {
    private static final String EMPTY_FIELD = "";
    private Field mField;

    public DatePickerRow(Field field) {
        mField = field;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup container) {
        View view;
        DatePickerRowHolder holder;

        if (convertView == null) {
            View root = inflater.inflate(
                    R.layout.listview_row_datepicker, container, false);

            TextView textLabel = (TextView)
                    root.findViewById(R.id.text_label);
            ImageButton clearButton = (ImageButton)
                    root.findViewById(R.id.clear_edit_text);
            EditText pickerInvoker = (EditText)
                    root.findViewById(R.id.date_picker_edit_text);

            DateSetListener dateSetListener = new DateSetListener();
            OnEditTextClickListener invokerListener = new OnEditTextClickListener(inflater.getContext());
            ClearButtonListener clearButtonListener = new ClearButtonListener();

            holder = new DatePickerRowHolder(textLabel, pickerInvoker, clearButton,
                    clearButtonListener, dateSetListener, invokerListener);

            root.setTag(holder);
            view = root;
        } else {
            view = convertView;
            holder = (DatePickerRowHolder) view.getTag();
        }

        holder.updateViews(mField);
        return view;
    }

    @Override
    public int getViewType() {
        return RowTypes.DATE.ordinal();
    }

    private static class OnEditTextClickListener implements OnClickListener {
        private DateSetListener listener;
        private LocalDate currentDate;
        private Context context;

        public OnEditTextClickListener(Context context) {
            this.context = context;
            currentDate = new LocalDate();
        }

        public void setListener(DateSetListener listener) {
            this.listener = listener;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View view) {
            DatePickerDialog picker = new DatePickerDialog(context, listener,
                    currentDate.getYear(), currentDate.getMonthOfYear() - 1, currentDate.getDayOfMonth());
            picker.getDatePicker().setMaxDate(DateTime.now().getMillis());
            picker.show();
        }
    }

    private static class ClearButtonListener implements OnClickListener {
        private EditText editText;
        private Field field;

        public void setEditText(EditText editText) {
            this.editText = editText;
        }

        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public void onClick(View view) {
            editText.setText(EMPTY_FIELD);
            field.setValue(EMPTY_FIELD);
        }
    }

    private class DatePickerRowHolder {
        final TextView textLabel;
        final EditText editText;
        final ImageButton clearButton;

        final DateSetListener dateSetListener;
        final OnEditTextClickListener invokerListener;
        final ClearButtonListener clearButtonListener;

        public DatePickerRowHolder(TextView textLabel, EditText editText,
                                   ImageButton clearButton, ClearButtonListener clearButtonListener,
                                   DateSetListener dateSetListener, OnEditTextClickListener invokerListener) {
            this.textLabel = textLabel;
            this.editText = editText;
            this.clearButton = clearButton;

            this.dateSetListener = dateSetListener;
            this.invokerListener = invokerListener;
            this.clearButtonListener = clearButtonListener;
        }

        public void updateViews(Field field) {
            textLabel.setText(field.getLabel());

            dateSetListener.setField(field);
            dateSetListener.setEditText(editText);

            invokerListener.setListener(dateSetListener);

            editText.setText(field.getValue());
            editText.setOnClickListener(invokerListener);

            clearButtonListener.setEditText(editText);
            clearButtonListener.setField(field);
            clearButton.setOnClickListener(clearButtonListener);
        }
    }

    private class DateSetListener implements DatePickerDialog.OnDateSetListener {
        private static final String DATE_FORMAT = "YYYY-MM-dd";
        private Field field;
        private EditText editText;

        public void setField(Field field) {
            this.field = field;
        }

        public void setEditText(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            LocalDate date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
            String dateString = date.toString(DATE_FORMAT);
            field.setValue(dateString);
            editText.setText(dateString);
        }
    }
}
