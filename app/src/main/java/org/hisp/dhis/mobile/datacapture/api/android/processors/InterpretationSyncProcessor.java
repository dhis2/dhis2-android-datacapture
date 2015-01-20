package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import org.hisp.dhis.mobile.datacapture.utils.BusProvider;
import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnInterpretationsSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.InterpretationHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.Interpretation;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.io.DBContract;
import org.hisp.dhis.mobile.datacapture.io.DBContract.InterpretationColumns;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.mobile.datacapture.utils.DateTimeTypeAdapter.deserializeDateTime;

public class InterpretationSyncProcessor extends AsyncTask<Void, Void, OnInterpretationsSyncEvent> {
    private static final String TAG = InterpretationSyncProcessor.class.getSimpleName();
    private Context mContext;

    public InterpretationSyncProcessor(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context object must not be null");
        }

        mContext = context;
    }

    private ArrayList<ContentProviderOperation> updateInterpretations() throws APIException {
        final String SELECTION = InterpretationColumns.STATE + " = " + "'" + State.GETTING.toString() + "'";
        List<DBItemHolder<Interpretation>> oldInterpretations = readInterpretations(SELECTION);
        List<Interpretation> newInterpretationList = getInterpretations();
        Map<String, Interpretation> newInterpretations = InterpretationHandler.toMap(newInterpretationList);
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        for (DBItemHolder<Interpretation> dbItem : oldInterpretations) {
            Interpretation oldInterpretation = dbItem.getItem();
            Interpretation newInterpretation = newInterpretations.get(oldInterpretation.getId());


            if (newInterpretation == null) {
                Log.d(TAG, "Removing interpretation {id, name}: " +
                        oldInterpretation.getId() + " : " + oldInterpretation.getName());
                deleteInterpretation(ops, dbItem);
                continue;
            }

            DateTime newLastUpdated = deserializeDateTime(newInterpretation.getLastUpdated());
            DateTime oldLastUpdated = deserializeDateTime(oldInterpretation.getLastUpdated());

            if (newLastUpdated.isAfter(oldLastUpdated)) {
                Log.d(TAG, "Updating interpretation {id, name}: " +
                        oldInterpretation.getId() + " : " + oldInterpretation.getName());
                updateInterpretation(ops, dbItem, newInterpretation);
            }

            newInterpretations.remove(oldInterpretation.getId());
        }

        for (String key : newInterpretations.keySet()) {
            Interpretation interpretation = newInterpretations.get(key);
            Log.d(TAG, "Inserting new interpretation {id, name}: " +
                    interpretation.getName() + " " + interpretation.getId());
            insertInterpretation(ops, interpretation);
        }

        return ops;
    }

    private void deleteInterpretation(List<ContentProviderOperation> ops,
                                      DBItemHolder<Interpretation> dbItem) {
        ops.add(InterpretationHandler.delete(dbItem));
    }

    private void updateInterpretation(List<ContentProviderOperation> ops,
                                      DBItemHolder<Interpretation> dbItem,
                                      Interpretation interpretation) {
        ContentProviderOperation op = InterpretationHandler.update(dbItem, interpretation);
        if (op != null) {
            ops.add(op);
        }
    }

    private void insertInterpretation(List<ContentProviderOperation> ops,
                                      Interpretation interpretation) {
        ContentProviderOperation op = InterpretationHandler.insert(interpretation);
        if (op != null) {
            ops.add(op);
        }
    }

    private List<Interpretation> getInterpretations() throws APIException {
        final ResponseHolder<List<Interpretation>> holder = new ResponseHolder<>();
        DHISManager.getInstance().getInterpretations(new ApiRequestCallback<List<Interpretation>>() {
            @Override
            public void onSuccess(Response response, List<Interpretation> interpretations) {
                holder.setItem(interpretations);
                holder.setResponse(response);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        });

        if (holder.getException() != null) {
            throw holder.getException();
        } else {
            return holder.getItem();
        }
    }

    private List<DBItemHolder<Interpretation>> readInterpretations(String selection) {
        List<DBItemHolder<Interpretation>> dbItems = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(
                InterpretationColumns.CONTENT_URI, InterpretationHandler.PROJECTION, selection, null, null
        );

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                dbItems.add(InterpretationHandler.fromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return dbItems;
    }

    @Override
    protected OnInterpretationsSyncEvent doInBackground(Void... params) {
        final ResponseHolder<String> holder = new ResponseHolder<>();
        final OnInterpretationsSyncEvent event = new OnInterpretationsSyncEvent();

        ArrayList<ContentProviderOperation> ops = null;
        try {
            ops = updateInterpretations();
        } catch (APIException e) {
            holder.setException(e);
        }

        if (holder.getException() != null) {
            event.setResponseHolder(holder);
            return event;
        }

        try {
            mContext.getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

        return event;
    }

    @Override
    protected void onPostExecute(OnInterpretationsSyncEvent event) {
        BusProvider.getInstance().post(event);
    }
}
