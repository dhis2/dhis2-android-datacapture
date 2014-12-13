package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;

public class StubFragment extends Fragment {
    public static final String NUMBER_EXTRA = "numberExtra";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        int number = 0;

        if (getArguments() != null) {
            number = getArguments().getInt(NUMBER_EXTRA);
        }
        View view = inflater.inflate(R.layout.fragment_stub, group, false);
        TextView stubTextView = (TextView) view.findViewById(R.id.stub_text_view);
        stubTextView.setText("StubFragment: " + number);

        return view;
    }
}
