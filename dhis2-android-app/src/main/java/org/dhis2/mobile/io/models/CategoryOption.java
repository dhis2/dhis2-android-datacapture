package org.dhis2.mobile.io.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryOption implements Serializable, Parcelable {
    private static final String TAG = CategoryOption.class.getCanonicalName();

    private String id;
    private String label;
    private String startDate;
    private String endDate;
    private List<String> organisationUnits = new ArrayList<>();

    public CategoryOption() {
        // empty constructor
    }

    public CategoryOption(String id, String label){
        this.id = id;
        this.label = label;
    }

    private CategoryOption(Parcel in) {
        this.id = in.readString();
        this.label = in.readString();
        this.startDate = in.readString();
        this.endDate = in.readString();

        // read in string list
        if (organisationUnits != null) {
            in.readStringList(organisationUnits);
        }
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

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public List<String> getOrganisationUnits() {
        return organisationUnits;
    }

    @Override
    public int describeContents() {
        return TAG.length();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(label);
        parcel.writeString(startDate);
        parcel.writeString(endDate);
        parcel.writeStringList(organisationUnits);
    }
}
