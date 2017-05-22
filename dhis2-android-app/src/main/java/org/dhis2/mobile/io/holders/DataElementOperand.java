package org.dhis2.mobile.io.holders;

public class DataElementOperand {
    public String categoryOptionComboUid;
    public String dataElementUid;

    public DataElementOperand(String categoryOptionComboUid, String dataElementUid) {
        this.categoryOptionComboUid = categoryOptionComboUid;
        this.dataElementUid = dataElementUid;
    }

    public String getCategoryOptionComboUid() {
        return categoryOptionComboUid;
    }

    public void setCategoryOptionComboUid(String categoryOptionComboUid) {
        this.categoryOptionComboUid = categoryOptionComboUid;
    }

    public String getDataElementUid() {
        return dataElementUid;
    }

    public void setDataElementUid(String dataElementUid) {
        this.dataElementUid = dataElementUid;
    }
}
