package org.dhis2.mobile.sdk.persistence;

import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.dhis2.mobile.sdk.entities.BaseIdentifiableModel;
import org.dhis2.mobile.sdk.entities.RelationModel;
import org.dhis2.mobile.sdk.persistence.database.DhisDatabase;
import org.dhis2.mobile.sdk.persistence.models.DbOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static org.dhis2.mobile.sdk.persistence.DbUtils.toMap;
import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

/**
 * This class is intended to process list of DbOperations
 * during single database transaction
 */
public final class DbHelper {
    private DbHelper() {
        // no instances
    }

    /**
     * Performs each DbOperation during one database transaction
     *
     * @param operations List of DbOperations to be performed.
     */
    public static void applyBatch(final Queue<DbOperation> operations) {
        isNull(operations, "List<DbOperation> object must not be null");

        if (operations.isEmpty()) {
            return;
        }

        TransactionManager.transact(DhisDatabase.NAME, new Runnable() {
            @Override public void run() {
                for (DbOperation operation : operations) {
                    switch (operation.getOperationType()) {
                        case INSERT: {
                            System.out.println("*** Inserting ***");
                            operation.getModel().insert();
                            break;
                        }
                        case UPDATE: {
                            System.out.println("*** Updating ***");
                            operation.getModel().update();
                            break;
                        }
                        case DELETE: {
                            System.out.println("*** Deleting ***");
                            operation.getModel().delete();
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * This utility method allows to determine which type of operation to apply to
     * each BaseIdentifiableModel depending on TimeStamp.
     *
     * @param oldModels List of models from local storage.
     * @param newModels List of models of distance instance of DHIS.
     */
    public static <T extends BaseIdentifiableModel> List<DbOperation> syncBaseIdentifiableModels(List<T> oldModels,
                                                                                                  List<T> newModels) {
        List<DbOperation> ops = new ArrayList<>();

        Map<String, T> newModelsMap = toMap(newModels);
        Map<String, T> oldModelsMap = toMap(oldModels);

        for (String oldModelKey : oldModelsMap.keySet()) {
            T newModel = newModelsMap.get(oldModelKey);
            T oldModel = oldModelsMap.get(oldModelKey);

            if (newModel == null) {
                ops.add(DbOperation.delete(oldModel));
                continue;
            }

            if (newModel.getLastUpdated().isAfter(oldModel.getLastUpdated())) {
                ops.add(DbOperation.update(newModel));
            }

            newModelsMap.remove(oldModelKey);
        }

        for (String newModelKey : newModelsMap.keySet()) {
            T item = newModelsMap.get(newModelKey);
            ops.add(DbOperation.insert(item));
        }

        return ops;
    }

    public static <T extends BaseModel & RelationModel> List<DbOperation> syncRelationModels(List<T> oldRelationsList,
                                                                                             List<T> newRelationsList) {
        List<DbOperation> ops = new ArrayList<>();
        Map<String, T> oldRelations = relationModelListToMap(oldRelationsList);
        Map<String, T> newRelations = relationModelListToMap(newRelationsList);
        for (String oldRelationKey : oldRelations.keySet()) {
            T oldRelation = oldRelations.get(oldRelationKey);
            T newRelation = newRelations.get(oldRelationKey);

            if (newRelation == null) {
                ops.add(DbOperation.delete(oldRelation));
                continue;
            }

            newRelations.remove(oldRelationKey);
        }

        for (String newRelationKey : newRelations.keySet()) {
            ops.add(DbOperation.insert(newRelations.get(newRelationKey)));
        }

        return ops;
    }

    private static <T extends RelationModel> Map<String, T> relationModelListToMap(List<T> relations) {
        Map<String, T> relationMap = new HashMap<>();
        if (relations != null && !relations.isEmpty()) {
            for (T relation : relations) {
                relationMap.put(relation.getFirstKey() + relation.getSecondKey(), relation);
            }
        }
        return relationMap;
    }
}