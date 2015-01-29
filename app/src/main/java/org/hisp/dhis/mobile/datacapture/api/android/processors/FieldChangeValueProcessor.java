package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.hisp.dhis.mobile.datacapture.api.android.events.FieldValueChangeEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnFieldValueChangedEvent;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportFieldColumns;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;

public class FieldChangeValueProcessor extends AsyncTask<Void, Void, OnFieldValueChangedEvent> {
    private Context mContext;
    private FieldValueChangeEvent mEvent;

    public FieldChangeValueProcessor(Context context, FieldValueChangeEvent event) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        if (event == null) {
            throw new IllegalArgumentException("FieldValueChangeEvent must not be null");
        }

        mContext = context;
        mEvent = event;
    }

    @Override
    protected OnFieldValueChangedEvent doInBackground(Void... params) {
        Uri uri = ContentUris.withAppendedId(
                ReportFieldColumns.CONTENT_URI, mEvent.getFieldId());
        ContentValues values = new ContentValues();
        values.put(ReportFieldColumns.VALUE, mEvent.getValue());
        mContext.getContentResolver().update(uri, values, null, null);
        return new OnFieldValueChangedEvent();
    }

    @Override
    protected void onPostExecute(OnFieldValueChangedEvent event) {
        BusProvider.getInstance().post(event);
    }
}
