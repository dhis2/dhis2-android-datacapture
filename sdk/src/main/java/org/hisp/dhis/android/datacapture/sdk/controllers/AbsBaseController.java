package org.hisp.dhis.android.datacapture.sdk.controllers;


import org.hisp.dhis.android.datacapture.sdk.persistence.models.BaseIdentifiableObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;

import static org.hisp.dhis.android.datacapture.sdk.utils.DbUtils.toMap;

public abstract class AbsBaseController<T extends BaseIdentifiableObject> implements IController<List<T>> {

    @Override public List<T> run() throws RetrofitError {
        List<T> existingItems = getExistingItems();
        List<T> updatedItems = getUpdatedItems();
        List<T> persistedItems = getPersistedItems();

        Map<String, T> updatedItemsMap = toMap(updatedItems);
        Map<String, T> persistedItemsMap = toMap(persistedItems);
        Map<String, T> existingItemsMap = new HashMap<>();

        if (existingItems == null || existingItems.isEmpty()) {
            return new ArrayList<>(existingItemsMap.values());
        }

        for (T existingItem : existingItems) {
            String id = existingItem.getId();
            T updatedItem = updatedItemsMap.get(id);
            T persistedItem = persistedItemsMap.get(id);

            if (updatedItem != null) {
                existingItemsMap.put(id, updatedItem);
                continue;
            }

            if (persistedItem != null) {
                existingItemsMap.put(id, persistedItem);
                continue;
            }

            throw new IllegalArgumentException("MetaData element is absent");
        }

        return new ArrayList<>(existingItemsMap.values());
    }

    public abstract List<T> getExistingItems();

    public abstract List<T> getUpdatedItems();

    public abstract List<T> getPersistedItems();
}
