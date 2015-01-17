package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.InterpretationDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnInterpretationDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.InterpretationHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.Interpretation;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.io.DBContract.InterpretationColumns;

public class InterpretationDeleteProcessor extends AsyncTask<Void, Void, OnInterpretationDeleteEvent> {
    private Context mContext;
    private InterpretationDeleteEvent mEvent;

    public InterpretationDeleteProcessor(Context context, InterpretationDeleteEvent event) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        if (event == null) {
            throw new IllegalArgumentException("InterpretationDeleteEvent must not be null");
        }

        mContext = context;
        mEvent = event;
    }

    @Override
    protected OnInterpretationDeleteEvent doInBackground(Void... params) {
        final DBItemHolder<Interpretation> dbItem = readInterpretation();
        final ResponseHolder<String> holder = new ResponseHolder<>();
        final OnInterpretationDeleteEvent event = new OnInterpretationDeleteEvent();

        updateInterpretationState(State.DELETING);
        DHISManager.getInstance().deleteInterpretation(new ApiRequestCallback<String>() {
            @Override
            public void onSuccess(Response response, String s) {
                holder.setItem(s);
                holder.setResponse(response);
                deleteInterpretation();
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, dbItem.getItem().getId());

        event.setResponseHolder(holder);
        return event;
    }

    @Override
    protected void onPostExecute(OnInterpretationDeleteEvent event) {
        BusProvider.getInstance().post(event);
    }

    private DBItemHolder<Interpretation> readInterpretation() {
        Uri uri = ContentUris.withAppendedId(
                InterpretationColumns.CONTENT_URI, mEvent.getInterpretationId()
        );
        Cursor cursor = mContext.getContentResolver().query(
                uri, InterpretationHandler.PROJECTION, null, null, null
        );

        DBItemHolder<Interpretation> dbItem = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            dbItem = InterpretationHandler.fromCursor(cursor);
            cursor.close();
        }

        return dbItem;
    }

    private void deleteInterpretation() {
        Uri uri = ContentUris.withAppendedId(
                InterpretationColumns.CONTENT_URI, mEvent.getInterpretationId()
        );

        mContext.getContentResolver().delete(uri, null, null);
    }

    private void updateInterpretationState(State state) {
        Uri uri = ContentUris.withAppendedId(
                InterpretationColumns.CONTENT_URI, mEvent.getInterpretationId()
        );

        ContentValues values = new ContentValues();
        values.put(InterpretationColumns.STATE, state.toString());
        mContext.getContentResolver().update(uri, values, null, null);
    }
}
