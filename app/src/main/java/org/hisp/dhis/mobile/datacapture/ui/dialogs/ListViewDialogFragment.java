package org.hisp.dhis.mobile.datacapture.ui.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.ui.adapters.SimpleAdapter;

import java.util.List;

public class ListViewDialogFragment extends DialogFragment
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final String TAG = ListViewDialogFragment.class.getName();
    private static final String ID_EXTRA = "dialogId";

    private ListView mListView;
    private SimpleAdapter mAdapter;
    private Button mCloseButton;
    private List<String> mListValues;
    private OnDialogItemClickListener mListener;
    private int mId;

    public static ListViewDialogFragment newInstance(int id) {
        ListViewDialogFragment fragment = new ListViewDialogFragment();
        Bundle args = new Bundle();

        args.putInt(ID_EXTRA, id);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new SimpleAdapter(getActivity());
        mListView = (ListView) view.findViewById(R.id.simple_listview);
        mCloseButton = (Button) view.findViewById(R.id.close_listview_dialog_button);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mCloseButton.setOnClickListener(this);

        mAdapter.swapData(mListValues);
        Bundle args = getArguments();
        if (args != null) {
            mId = args.getInt(ID_EXTRA);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        if (mListener != null) {
            mListener.onItemClickListener(mId, position);
            dismiss();
            return;
        }

        if (getParentFragment() instanceof OnDialogItemClickListener) {
            OnDialogItemClickListener listener = (OnDialogItemClickListener) getParentFragment();
            listener.onItemClickListener(mId, position);
            dismiss();
        }
    }

    public void swapData(List<String> listValues) {
        mListValues = listValues;
        if (mAdapter != null) {
            mAdapter.swapData(listValues);
        }
    }

    public void setOnItemClickListener(OnDialogItemClickListener listener) {
        mListener = listener;
    }

    public interface OnDialogItemClickListener {
        public void onItemClickListener(int dialogId, int itemPosition);
    }
}
