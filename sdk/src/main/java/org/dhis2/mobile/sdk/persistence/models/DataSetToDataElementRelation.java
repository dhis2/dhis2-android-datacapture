package org.dhis2.mobile.sdk.persistence.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.dhis2.mobile.sdk.persistence.DbDhis;

@Table(databaseName = DbDhis.NAME, uniqueColumnGroups = {
        @UniqueGroup(groupNumber = DataSetToDataElementRelation.UNIQUE_DATASET_TO_DATAELEMENT_GROUP, uniqueConflict = ConflictAction.FAIL)
})
public class DataSetToDataElementRelation extends BaseModel implements RelationModel {
    static final int UNIQUE_DATASET_TO_DATAELEMENT_GROUP = 1;
    static final String DATASET_KEY = "dataSet";
    static final String DATAELEMENT_KEY = "dataElement";

    @Column @PrimaryKey(autoincrement = true) int id;

    @Column @Unique(unique = false, uniqueGroups = {UNIQUE_DATASET_TO_DATAELEMENT_GROUP})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = DATASET_KEY, columnType = String.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    ) DataSet dataSet;

    @Column @Unique(unique = false, uniqueGroups = {UNIQUE_DATASET_TO_DATAELEMENT_GROUP})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = DATAELEMENT_KEY, columnType = String.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    ) DataElement dataElement;

    @Override public String getFirstKey() {
        return dataSet.getId();
    }

    @Override public String getSecondKey() {
        return dataElement.getId();
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public DataElement getDataElement() {
        return dataElement;
    }

    public void setDataElement(DataElement dataElement) {
        this.dataElement = dataElement;
    }
}
