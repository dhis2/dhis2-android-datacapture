package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.ReportState;
import org.hisp.dhis.mobile.datacapture.ui.fragments.ReportsFragment;

public class ReportAdapter extends FragmentPagerAdapter {
    public static final int PENDING_REPORTS = 0;
    public static final int SENT_REPORTS = 1;

    private static final String EMPTY_TITLE = "";
    private final String pendingString;
    private final String sentString;

    public ReportAdapter(FragmentManager fm, Context context) {
        super(fm);
        pendingString = context.getString(R.string.pending);
        sentString = context.getString(R.string.sent);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == PENDING_REPORTS) {
            return ReportsFragment.newInstance(ReportState.PENDING);
        }
        if (position == SENT_REPORTS) {
            return ReportsFragment.newInstance(ReportState.SENT);
        }
        return null;
    }

    @Override
    public int getCount() {
        return ReportState.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == PENDING_REPORTS) {
            return pendingString;
        }
        if (position == SENT_REPORTS) {
            return sentString;
        }

        return EMPTY_TITLE;
    }
}
