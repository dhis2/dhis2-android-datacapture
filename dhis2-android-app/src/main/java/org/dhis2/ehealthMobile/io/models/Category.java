package org.dhis2.ehealthMobile.io.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable, Parcelable {
    private static final String TAG = Category.class.getCanonicalName();

    private String id;
    private String label;
    private List<CategoryOption> categoryOptions = new ArrayList<>();

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {

        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public Category() {
        // empty constructor
    }

    private Category(Parcel in) {
        this.id = in.readString();
        this.label = in.readString();
        in.readTypedList(categoryOptions, CategoryOption.CREATOR);
    }

    @Override
    public int describeContents() {
        return TAG.length();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(label);
        parcel.writeTypedList(categoryOptions);
    }

    public String getId() {
        return id;
    }

    public List<CategoryOption> getCategoryOptions() {
        return categoryOptions;
    }

    public String getLabel() {
        return label;
    }
}
