package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.mobile.datacapture.R;

public abstract class AbsRefreshableScrollViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mRefreshLayout;
    private ViewGroup mViewContainer;

    private View mContentView;
    private View mMessageView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_refreshable_scroll_view,
                container, false);
        /* mRefreshLayout.setColorSchemeResources(R.color.navy_blue, R.color.light_grey,
                R.color.navy_blue, R.color.light_grey); */
        mRefreshLayout.setOnRefreshListener(this);

        mViewContainer = (ViewGroup) mRefreshLayout.findViewById(R.id.scroll_view);
        mContentView = onCreateContentView(inflater, mViewContainer, savedInstanceState);
        mMessageView = onCreateMessageView(inflater, mViewContainer, savedInstanceState);

        if (mContentView == null) {
            throw new IllegalArgumentException("Content view must not be null");
        }

        if (mMessageView == null) {
            throw new IllegalArgumentException("Message view must not be null");
        }

        mViewContainer.addView(mContentView);
        return mRefreshLayout;
    }

    public void setRefreshing(boolean isRefreshing) {
        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(isRefreshing);
        }
    }

    public boolean isRefreshing() {
        if (mRefreshLayout != null) {
            return mRefreshLayout.isRefreshing();
        } else {
            return false;
        }
    }

    public void showMessageView(boolean flag) {
        if (mViewContainer == null || mContentView == null ||
                mMessageView == null) {
            return;
        }

        if (flag) {
            mViewContainer.removeView(mContentView);
            if (mViewContainer.getChildCount() == 0) {
                mViewContainer.addView(mMessageView);
            }
        } else {
            mViewContainer.removeView(mMessageView);
            if (mViewContainer.getChildCount() == 0) {
                mViewContainer.addView(mContentView);
            }
        }
    }

    public abstract View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract View onCreateMessageView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
}
