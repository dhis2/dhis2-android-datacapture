package org.dhis2.mobile.sdk.controllers;

import org.dhis2.mobile.sdk.entities.BaseIdentifiableObject;
import org.dhis2.mobile.sdk.network.APIException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.dhis2.mobile.sdk.persistence.DbUtils.toMap;


abstract class AbsBaseIdentifiableController<T extends BaseIdentifiableObject> implements IController<List<T>> {

    @Override public final List<T> run() throws APIException {
        // download only items with basic fields
        Map<String, T> newShortItems = toMap(getNewBasicItems());
        // read items from local storage
        Map<String, T> oldItems = toMap(getItemsFromDb());

        // find new items which we need to download
        List<String> itemsToDownload = new ArrayList<>();
        for (String newObjectKey : newShortItems.keySet()) {
            T newObject = newShortItems.get(newObjectKey);
            T oldObject = oldItems.get(newObjectKey);

            // it means we have to fetch full
            // version of new Item
            if (oldObject == null) {
                itemsToDownload.add(newObjectKey);
                continue;
            }

            if (newObject.getLastUpdated().isAfter(oldObject.getLastUpdated())) {
                // we need to update current version
                itemsToDownload.add(newObjectKey);
            }
        }

        // download new items with all necessary fields
        Map<String, T> newItems = toMap(getNewFullItems(itemsToDownload));

        // combine new items with those which were read from db
        List<T> combinedItems = new ArrayList<>();
        for (String newItemKey : newShortItems.keySet()) {
            T newItem = newItems.get(newItemKey);
            T oldItem = oldItems.get(newItemKey);

            if (newItem != null) {
                combinedItems.add(newItem);
                continue;
            }

            if (oldItem != null) {
                combinedItems.add(oldItem);
            }
        }

        return combinedItems;
    }

    abstract List<T> getNewBasicItems();

    abstract List<T> getNewFullItems(List<String> ids);

    abstract List<T> getItemsFromDb();
}
