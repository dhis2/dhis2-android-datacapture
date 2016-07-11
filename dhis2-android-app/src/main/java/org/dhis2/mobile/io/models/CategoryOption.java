package org.dhis2.mobile.io.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class CategoryOption implements Serializable, Parcelable {
    private static final String TAG = CategoryOption.class.getCanonicalName();

    private String id;
    private String label;

    public CategoryOption() {
        // empty constructor
    }

    private CategoryOption(Parcel in) {
        this.id = in.readString();
        this.label = in.readString();
    }

    public static final Parcelable.Creator<CategoryOption> CREATOR = new Parcelable.Creator<CategoryOption>() {

        public CategoryOption createFromParcel(Parcel in) {
            return new CategoryOption(in);
        }

        public CategoryOption[] newArray(int size) {
            return new CategoryOption[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int describeContents() {
        return TAG.length();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(label);
    }
}
