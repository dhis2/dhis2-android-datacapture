package org.hisp.dhis.mobile.datacapture.ui.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.models.Comment;
import org.hisp.dhis.mobile.datacapture.ui.adapters.CommentsAdapter;

import java.util.List;

public class CommentsDialog extends DialogFragment implements View.OnClickListener {
    public static final String COMMENTS_DIALOG = CommentsDialog.class.getName();
    private Button mCloseButton;
    private ListView mListView;

    private CommentsAdapter mAdapter;
    private List<Comment> mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_commets_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new CommentsAdapter(getActivity());
        mListView = (ListView) view.findViewById(R.id.comments_list);
        mListView.setAdapter(mAdapter);
        mAdapter.swapData(mData);

        mCloseButton = (Button) view.findViewById(R.id.button_close_comments_dialog);
        mCloseButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public void setData(List<Comment> comments) {
        mData = comments;
    }
}
