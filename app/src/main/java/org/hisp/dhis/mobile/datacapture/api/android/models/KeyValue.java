package org.hisp.dhis.mobile.datacapture.api.android.models;

public class KeyValue {
    private String mKey;
    private Type mType;
    private String mValue;

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public static enum Type {
        DATASET,
        DATASET_OPTION_SET,
        ORG_UNITS_WITH_DATASETS,
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }
}
