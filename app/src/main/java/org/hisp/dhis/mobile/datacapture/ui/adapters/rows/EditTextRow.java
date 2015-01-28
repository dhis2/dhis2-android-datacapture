package org.hisp.dhis.mobile.datacapture.ui.adapters.rows;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.models.Field;

public class EditTextRow implements Row {
    private static final String EMPTY_FIELD = "";
    private final Field mField;
    private final RowTypes mRowType;
    
    public EditTextRow(Field field, RowTypes rowType) {
        mField = field;
        mRowType = rowType;

        if (!RowTypes.TEXT.equals(rowType) &&
                !RowTypes.LONG_TEXT.equals(rowType) &&
                !RowTypes.NUMBER.equals(rowType) &&
                !RowTypes.INTEGER.equals(rowType) &&
                !RowTypes.INTEGER_NEGATIVE.equals(rowType) &&
                !RowTypes.INTEGER_ZERO_OR_POSITIVE.equals(rowType) &&
                !RowTypes.INTEGER_POSITIVE.equals(rowType)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup container) {
        View view;
        EditTextHolder holder;
        
        if (convertView == null) {
            View root = inflater.inflate(R.layout.listview_row_edit_text, container, false);
            TextView label = (TextView) root.findViewById(R.id.text_label);
            EditText editText = (EditText) root.findViewById(R.id.edit_text_row);

            if (RowTypes.TEXT.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setHint(R.string.enter_text);
            } else if (RowTypes.LONG_TEXT.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setHint(R.string.enter_long_text);
            } else if (RowTypes.NUMBER.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                editText.setHint(R.string.enter_number);
            } else if (RowTypes.INTEGER.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                editText.setHint(R.string.enter_integer);
            } else if (RowTypes.INTEGER_NEGATIVE.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                editText.setHint(R.string.enter_negative_integer);
                editText.setFilters(new InputFilter[]{new NegInpFilter()});
            } else if (RowTypes.INTEGER_ZERO_OR_POSITIVE.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setHint(R.string.enter_positive_integer_or_zero);
                editText.setFilters(new InputFilter[] {new PosOrZeroFilter()});
            } else if (RowTypes.INTEGER_POSITIVE.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setHint(R.string.enter_positive_integer);
                editText.setFilters(new InputFilter[] {new PosFilter()});
            }

            EditTextWatcher watcher = new EditTextWatcher();
            editText.addTextChangedListener(watcher);
            
            holder = new EditTextHolder(label, editText, watcher);
            root.setTag(holder);
            view = root;
        } else {
            view = convertView;
            holder = (EditTextHolder) view.getTag();
        }

        holder.updateViews(mField);
        return view;
    }

    @Override
    public int getViewType() {
        return mRowType.ordinal();
    }

    private static class NegInpFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                                   Spanned spn, int spnStart, int spnEnd) {

            if ((str.length() > 0) && (spnStart == 0) && (str.charAt(0) != '-')) {
                return EMPTY_FIELD;
            }

            return str;
        }
    }

    private static class PosOrZeroFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                                   Spanned spn, int spStart, int spEnd) {

            if ((str.length() > 0) && (spn.length() > 0) && (spn.charAt(0) == '0')) {
                return EMPTY_FIELD;
            }

            if ((spn.length() > 0) && (spStart == 0)
                    && (str.length() > 0) && (str.charAt(0) == '0')) {
                return EMPTY_FIELD;
            }

            return str;
        }
    }

    private static class PosFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                                   Spanned spn, int spnStart, int spnEnd) {

            if ((str.length() > 0) && (spnStart == 0) && (str.charAt(0) == '0')) {
                return EMPTY_FIELD;
            }

            return str;
        }
    }

    private static class EditTextWatcher implements TextWatcher {
        private Field field;

        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public void afterTextChanged(Editable arg) {
            field.setValue(arg.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) { }

    }

    // TODO Pay more attention to addTextChangeListener
    private static class EditTextHolder {
        final TextView textLabel;
        final EditText editText;
        final EditTextWatcher textWatcher;

        public EditTextHolder(TextView textLabel,
                       EditText editText,
                       EditTextWatcher textWatcher) {
            this.textLabel = textLabel;
            this.editText = editText;
            this.textWatcher = textWatcher;
        }

        public void updateViews(Field field) {
            textLabel.setText(field.getLabel());
            textWatcher.setField(field);
            editText.addTextChangedListener(textWatcher);
            editText.setText(field.getValue());
            editText.clearFocus();
        }
    }
}