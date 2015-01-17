package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.InterpretationUpdateTextEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnInterpretationTextUpdateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.InterpretationHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.Interpretation;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.io.DBContract.InterpretationColumns;

public class InterpretationUpdateTextProcessor extends AsyncTask<Void, Void, OnInterpretationTextUpdateEvent> {
    private Context mContext;
    private InterpretationUpdateTextEvent mEvent;

    public InterpretationUpdateTextProcessor(Context context, InterpretationUpdateTextEvent event) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        if (event == null) {
            throw new IllegalArgumentException("InterpretationUpdateTextEvent must not be null");
        }

        mContext = context;
        mEvent = event;
    }

    @Override
    protected OnInterpretationTextUpdateEvent doInBackground(Void... params) {
        final DBItemHolder<Interpretation> dbItem = readInterpretation();
        final ResponseHolder<String> holder = new ResponseHolder<>();
        final OnInterpretationTextUpdateEvent event = new OnInterpretationTextUpdateEvent();

        updateInterpretation(State.PUTTING);
        DHISManager.getInstance().updateInterpretationText(new ApiRequestCallback<String>() {
            @Override
            public void onSuccess(Response response, String s) {
                holder.setResponse(response);
                holder.setItem(s);
                updateInterpretation(State.GETTING);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, dbItem.getItem().getId(), mEvent.getText());

        event.setResponseHolder(holder);
        return event;
    }

    @Override
    protected void onPostExecute(OnInterpretationTextUpdateEvent event) {
        BusProvider.getInstance().post(event);
    }

    private DBItemHolder<Interpretation> readInterpretation() {
        Uri uri = ContentUris.withAppendedId(
                InterpretationColumns.CONTENT_URI, mEvent.getDbId()
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

    private void updateInterpretation(State state) {
        Uri uri = ContentUris.withAppendedId(
                InterpretationColumns.CONTENT_URI, mEvent.getDbId()
        );

        ContentValues values = new ContentValues();
        values.put(InterpretationColumns.TEXT, mEvent.getText());
        values.put(InterpretationColumns.STATE, state.toString());
        mContext.getContentResolver().update(uri, values, null, null);
    }
}
