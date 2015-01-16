package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.Context;
import android.os.AsyncTask;

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnInterpretationsSyncEvent;

public class InterpretationSyncProcessor extends AsyncTask<Void, Void, OnInterpretationsSyncEvent> {
    private Context mContext;

    public InterpretationSyncProcessor(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context object must not be null");
        }

        mContext = context;
    }

    @Override
    protected OnInterpretationsSyncEvent doInBackground(Void... params) {

        return null;
    }

    @Override
    protected void onPostExecute(OnInterpretationsSyncEvent event) {
        BusProvider.getInstance().post(event);
    }
}
