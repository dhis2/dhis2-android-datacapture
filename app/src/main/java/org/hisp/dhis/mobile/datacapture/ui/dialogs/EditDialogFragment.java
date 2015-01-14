package org.hisp.dhis.mobile.datacapture.ui.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.hisp.dhis.mobile.datacapture.R;

public class EditDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String EDIT_DIALOG_FRAGMENT = EditDialogFragment.class.getName();
    private EditText mEditText;
    private Button mOk;
    private Button mCancel;
    private EditNameDialogListener mListener;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save_changes && mListener != null) {
            mListener.onFinishEditDialog(mEditText.getText().toString());
        }

        dismiss();
    }

    public void setListener(EditNameDialogListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditText = (EditText) view.findViewById(R.id.dashboard_name);
        mOk = (Button) view.findViewById(R.id.save_changes);
        mCancel = (Button) view.findViewById(R.id.discard_changes);

        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    public interface EditNameDialogListener {
        void onFinishEditDialog(String inputText);
    }
}
