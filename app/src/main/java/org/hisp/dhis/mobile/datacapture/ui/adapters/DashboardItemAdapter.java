package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.utils.PicassoProvider;

public class DashboardItemAdapter extends DBBaseAdapter<DashboardItem> {
    private Picasso mImageLoader;

    public DashboardItemAdapter(Context context) {
        super(context);

        mImageLoader = PicassoProvider.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return null;
    }
}
