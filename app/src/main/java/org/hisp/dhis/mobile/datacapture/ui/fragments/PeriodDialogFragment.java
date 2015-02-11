package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.date.CustomDateIterator;
import org.hisp.dhis.mobile.datacapture.api.android.date.DateIteratorFactory;
import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;
import org.hisp.dhis.mobile.datacapture.ui.adapters.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;

public class PeriodDialogFragment extends DialogFragment
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = ListViewDialogFragment.class.getName();

    private ListView mListView;
    private Button mPrevious;
    private Button mNext;

    private SimpleAdapter mAdapter;
    private OnDialogItemClickListener mListener;

    private CustomDateIterator<List<DateHolder>> mIterator;
    private List<DateHolder> mDates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_listview_period, container, false);
        mListView = (ListView) view.findViewById(R.id.simple_listview);
        mPrevious = (Button) view.findViewById(R.id.previous);
        mNext = (Button) view.findViewById(R.id.next);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new SimpleAdapter(getActivity());

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mPrevious.setOnClickListener(this);
        mNext.setOnClickListener(this);

        updateAdapter();
        if (mIterator != null && mIterator.hasNext()) {
            mNext.setEnabled(true);
        } else {
            mNext.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previous: {
                mDates = mIterator.previous();
                mNext.setEnabled(true);
                updateAdapter();
                break;
            }
            case R.id.next: {
                if (mIterator.hasNext()) {
                    mDates = mIterator.next();
                    if (!mIterator.hasNext()) {
                        mNext.setEnabled(false);
                    }
                    updateAdapter();
                } else {
                    mNext.setEnabled(false);
                }
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            mListener.onItemClickListener(mDates.get(position));
            dismiss();
        }
    }

    public void setPeriodType(String periodType, boolean allowFuturePeriod) {
        mIterator = DateIteratorFactory.getDateIterator(periodType, allowFuturePeriod);
        mDates = mIterator.current();
    }

    public void show(FragmentManager manager) {
        show(manager, TAG);
    }

    private void updateAdapter() {
        List<String> labels = new ArrayList<>();
        if (mDates != null) {
            for (DateHolder dateHolder : mDates) {
                labels.add(dateHolder.getLabel());
            }
        }
        if (mAdapter != null) {
            mAdapter.swapData(labels);
        }
    }

    public void setOnItemClickListener(OnDialogItemClickListener listener) {
        mListener = listener;
    }

    public interface OnDialogItemClickListener {
        public void onItemClickListener(DateHolder date);
    }
}
