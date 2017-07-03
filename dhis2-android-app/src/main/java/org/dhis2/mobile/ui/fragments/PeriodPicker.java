package org.dhis2.mobile.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.dhis2.mobile.R;
import org.dhis2.mobile.ui.adapters.dataEntry.DateAdapter;
import org.dhis2.mobile.utils.date.CustomDateIterator;
import org.dhis2.mobile.utils.date.DateHolder;
import org.dhis2.mobile.utils.date.DateIteratorFactory;

import java.util.ArrayList;

public class PeriodPicker extends DialogFragment {
    private static final String TAG = PeriodPicker.class.getSimpleName();
    private static final String ARG_TITLE = "arg:title";
    private static final String ARG_PERIOD_TYPE = "arg:periodType";
    private static final String ARG_OPEN_FUTURE_PERIOD = "arg:openFuturePeriod";

    private OnPeriodClickListener onPeriodClickListener;

    public static PeriodPicker newInstance(String title, String periodType, int openFuturePeriod) {
        PeriodPicker periodPicker = new PeriodPicker();
        Bundle arguments = new Bundle();

        arguments.putString(ARG_TITLE, title);
        arguments.putString(ARG_PERIOD_TYPE, periodType);
        arguments.putInt(ARG_OPEN_FUTURE_PERIOD, openFuturePeriod);

        periodPicker.setArguments(arguments);
        periodPicker.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return periodPicker;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_period_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final TextView titleTextView = (TextView) view.findViewById(R.id.textview_titlebar_title);
        final ImageView cancelImageView = (ImageView) view.findViewById(R.id.imageview_cancel);

        final DateAdapter dateAdapter = new DateAdapter(LayoutInflater.from(getActivity()));
        final ListView listView = (ListView) view.findViewById(R.id.dates_listview);

        final Button add = (Button) view.findViewById(R.id.more);
        final Button sub = (Button) view.findViewById(R.id.less);

        final String periodType = getPeriodType();
        final int openFuturePeriods = getOpenFuturePeriods();
        try {


            final CustomDateIterator<ArrayList<DateHolder>> iterator =
                    DateIteratorFactory.getDateIterator(periodType, openFuturePeriods);



        final View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.more: {
                        if (iterator.hasNext()) {
                            dateAdapter.swapData(iterator.next());
                            listView.smoothScrollToPosition(0);

                            if (!iterator.hasNext()) {
                                add.setEnabled(false);
                            }
                        } else {
                            add.setEnabled(false);
                        }
                        break;
                    }
                    case R.id.less: {
                        dateAdapter.swapData(iterator.previous());
                        listView.smoothScrollToPosition(0);

                        add.setEnabled(true);
                        break;
                    }
                    case R.id.imageview_cancel: {
                        dismiss();
                        break;
                    }
                }
            }
        };


            listView.setAdapter(dateAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DateHolder dateHolder = dateAdapter.getData().get(position);

                if (onPeriodClickListener != null) {
                    onPeriodClickListener.onPeriodClicked(dateHolder);
                }

                dismiss();
            }
        });

        titleTextView.setText(getTitle());
        cancelImageView.setOnClickListener(onClickListener);

        add.setOnClickListener(onClickListener);
        sub.setOnClickListener(onClickListener);

        // render current dates
        dateAdapter.swapData(iterator.current());
        listView.smoothScrollToPosition(0);

        if (!iterator.hasNext()) {
            add.setEnabled(false);
        } else {
            add.setEnabled(true);
        }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Not supported period:" + e.getMessage());
            Toast.makeText(getContext(), R.string.dialog_period_not_supported,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void setOnPeriodClickListener(OnPeriodClickListener onPeriodClickListener) {
        this.onPeriodClickListener = onPeriodClickListener;
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    private String getTitle() {
        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_TITLE)) {
                return getArguments().getString(ARG_TITLE);
            }
        }

        return null;
    }

    private String getPeriodType() {
        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_PERIOD_TYPE)) {
                return getArguments().getString(ARG_PERIOD_TYPE);
            }
        }

        return null;
    }

    private int getOpenFuturePeriods() {
        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_OPEN_FUTURE_PERIOD)) {
                return getArguments().getInt(ARG_OPEN_FUTURE_PERIOD);
            }
        }

        return -1;
    }

    public interface OnPeriodClickListener {
        void onPeriodClicked(DateHolder dateHolder);
    }
}
