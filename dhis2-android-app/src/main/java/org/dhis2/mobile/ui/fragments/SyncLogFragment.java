package org.dhis2.mobile.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.dhis2.mobile.R;
import org.dhis2.mobile.ui.views.FontButton;
import org.dhis2.mobile.ui.views.FontTextView;
import org.dhis2.mobile.utils.TextFileUtils;

public class SyncLogFragment extends Fragment {

    public static final String TAG = "org.dhis2.mobile.ui.fragments.SyncLogFragment";
    FontTextView logTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sync_log, container, false);

        logTextView = (FontTextView) root.findViewById(R.id.log_content);
        if(TextFileUtils.doesFileExist(getContext(), TextFileUtils.Directory.LOG, TextFileUtils.FileNames.LOG)) {
            String content = TextFileUtils.readTextFile(getContext(),
                    TextFileUtils.Directory.LOG,
                    TextFileUtils.FileNames.LOG);
            logTextView.setText(content);
        }

        FontButton fontButton = (FontButton) root.findViewById(R.id.delete_and_log_out_button);
        fontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextFileUtils.removeFile(getContext(), TextFileUtils.Directory.LOG,
                        TextFileUtils.FileNames.LOG);
                logTextView.setText("");
            }
        });
        return root;
    }
}