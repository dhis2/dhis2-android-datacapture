package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.RemoteException;

import com.google.gson.Gson;

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDatasetSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.KeyValueHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.KeyValue;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.DataSet;
import org.hisp.dhis.mobile.datacapture.api.models.DataSetHolder;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.api.models.Group;
import org.hisp.dhis.mobile.datacapture.api.models.OptionSet;
import org.hisp.dhis.mobile.datacapture.api.models.OrganisationUnit;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.io.DBContract;
import org.hisp.dhis.mobile.datacapture.io.DBContract.KeyValueColumns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DatasetSyncProcessor extends AsyncTask<Void, Void, OnDatasetSyncEvent> {
    private Context mContext;

    public DatasetSyncProcessor(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        mContext = context;
    }

    @Override
    protected OnDatasetSyncEvent doInBackground(Void... params) {
        final OnDatasetSyncEvent event = new OnDatasetSyncEvent();
        final ResponseHolder<String> holder = new ResponseHolder<>();

        try {
            updateDataSets();
        } catch (APIException e) {
            holder.setException(e);
        }

        event.setResponseHolder(holder);
        return event;
    }

    @Override
    protected void onPostExecute(OnDatasetSyncEvent event) {
        BusProvider.getInstance().post(event);
    }

    private void updateDataSets() throws APIException {
        // network operations should be done first, in order to make sure that
        // we have all data in hands before starting processing it
        DataSetHolder holder = getDatasets();
        List<OptionSet> optionSets = getOptionSets(holder);

        // Put options from full sized datasets into short versions
        // inside of organization units
        saveOrgUnits(holder);
        saveDatasets(holder);
        saveOptionSets(optionSets);
    }

    private List<OrganisationUnit> prepareOrgUnits(DataSetHolder holder) {
        Map<String, DataSet> dataSets = toMap(holder.getDataSets());
        List<OrganisationUnit> units = holder.getOrganisationUnits();

        if (units == null) {
            return units;
        }

        for (OrganisationUnit orgUnit : units) {
            List<DataSet> shortDataSets = orgUnit.getDataSets();

            if (shortDataSets == null) {
                continue;
            }

            for (DataSet shortDataSet : shortDataSets) {
                DataSet fullDataSet = dataSets.get(shortDataSet.getId());
                shortDataSet.setOptions(fullDataSet.getOptions());
            }
        }

        return units;
    }

    private Map<String, DataSet> toMap(List<DataSet> dataSets) {
        Map<String, DataSet> dataSetMap = new HashMap<>();
        if (dataSets != null && dataSets.size() > 0) {
            for (DataSet dataSet : dataSets) {
                dataSetMap.put(dataSet.getId(), dataSet);
            }
        }
        return dataSetMap;
    }

    private void saveOrgUnits(DataSetHolder holder) {
        // we need to remove old org.units before saving new ones
        final String ORG_UNITS_KEY = KeyValue.Type.ORG_UNITS_WITH_DATASETS.toString();
        final String SELECTION = KeyValueColumns.KEY + " = " + "'" + ORG_UNITS_KEY + "'" + " AND "
                + KeyValueColumns.TYPE + " = " + "'" + ORG_UNITS_KEY + "'";
        mContext.getContentResolver().delete(
                KeyValueColumns.CONTENT_URI, SELECTION, null
        );

        List<OrganisationUnit> units = prepareOrgUnits(holder);
        if (units == null) {
            return;
        }

        Gson gson = new Gson();
        KeyValue keyValue = new KeyValue();

        keyValue.setKey(KeyValue.Type.ORG_UNITS_WITH_DATASETS.toString());
        keyValue.setType(KeyValue.Type.ORG_UNITS_WITH_DATASETS);
        keyValue.setValue(gson.toJson(units));

        ContentValues values = KeyValueHandler.toContentValues(keyValue);
        mContext.getContentResolver().insert(KeyValueColumns.CONTENT_URI, values);
    }

    private void saveDatasets(DataSetHolder holder) {
        // remove old datasets before inserting new ones
        final String SELECTION = KeyValueColumns.TYPE + " = " + "'" + KeyValue.Type.DATASET.toString() + "'";
        mContext.getContentResolver().delete(KeyValueColumns.CONTENT_URI, SELECTION, null);

        List<DataSet> dataSets = holder.getDataSets();
        if (dataSets == null) {
            return;
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        Gson gson = new Gson();
        for (DataSet dataSet : dataSets) {
            KeyValue keyValue = new KeyValue();
            keyValue.setKey(dataSet.getId());
            keyValue.setType(KeyValue.Type.DATASET);
            keyValue.setValue(gson.toJson(dataSet));
            ops.add(KeyValueHandler.insert(keyValue));
        }

        try {
            mContext.getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private void saveOptionSets(List<OptionSet> optionSets) {
        final String SELECTION = KeyValueColumns.TYPE + " = " +
                "'" + KeyValue.Type.DATASET_OPTION_SET.toString() + "'";

        mContext.getContentResolver().delete(KeyValueColumns.CONTENT_URI, SELECTION, null);

        if (optionSets == null) {
            return;
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        Gson gson = new Gson();
        for (OptionSet optionSet : optionSets) {
            KeyValue keyValue = new KeyValue();
            keyValue.setKey(optionSet.getId());
            keyValue.setType(KeyValue.Type.DATASET_OPTION_SET);
            keyValue.setValue(gson.toJson(optionSet));
            ops.add(KeyValueHandler.insert(keyValue));
        }

        try {
            mContext.getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private DataSetHolder getDatasets() throws APIException {
        final ResponseHolder<DataSetHolder> holder = new ResponseHolder<>();

        DHISManager.getInstance().getDataSets(new ApiRequestCallback<DataSetHolder>() {
            @Override
            public void onSuccess(Response response, DataSetHolder dataSetHolder) {
                holder.setItem(dataSetHolder);
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

    private List<OptionSet> getOptionSets(DataSetHolder holder) throws APIException {
        List<OptionSet> optionSets = new ArrayList<>();
        Set<Field> fieldsWithOptionSets = findOptionSetFields(holder);

        for (Field field : fieldsWithOptionSets) {
            OptionSet optionSet = getOptionSet(field.getOptionSet());
            optionSets.add(optionSet);
        }

        return optionSets;
    }

    // This method searches for option sets in each field
    // of each group of each dataset
    private Set<Field> findOptionSetFields(DataSetHolder holder) {
        Set<Field> fields = new HashSet<>();

        if (holder.getDataSets() != null && holder.getDataSets().size() > 0) {
            for (DataSet dataSet : holder.getDataSets()) {
                for (Group group : dataSet.getGroups()) {
                    for (Field field : group.getFields()) {
                        if (field.getOptionSet() != null) {
                            fields.add(field);
                        }
                    }
                }
            }
        }
        return fields;
    }

    private OptionSet getOptionSet(String optionSetId) throws APIException {
        final ResponseHolder<OptionSet> holder = new ResponseHolder<>();

        DHISManager.getInstance().getOptionSet(new ApiRequestCallback<OptionSet>() {
            @Override
            public void onSuccess(Response response, OptionSet optionSet) {
                holder.setItem(optionSet);
                holder.setResponse(response);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, optionSetId);

        if (holder.getException() != null) {
            throw holder.getException();
        } else {
            return holder.getItem();
        }
    }
}
