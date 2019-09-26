package org.dhis2.mobile.io.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryCombo implements Serializable, Parcelable {
    private static final String TAG = Category.class.getCanonicalName();

    private String id;
    private List<Category> categories = new ArrayList<>();
    private List<String> categoryOptionComboUIdList = new ArrayList<>();

    public static final Parcelable.Creator<CategoryCombo> CREATOR = new Parcelable.Creator<CategoryCombo>() {

        public CategoryCombo createFromParcel(Parcel in) {
            return new CategoryCombo(in);
        }

        public CategoryCombo[] newArray(int size) {
            return new CategoryCombo[size];
        }
    };

    public CategoryCombo() {
        // empty constructor
    }

    private CategoryCombo(Parcel in) {
        this.id = in.readString();
        in.readTypedList(categories, Category.CREATOR);
        in.readStringList(categoryOptionComboUIdList);
    }

    public CategoryCombo(String UId, List<String> categoryOptionComboUIds) {
        this.id = UId;
        this.categoryOptionComboUIdList = categoryOptionComboUIds;
    }

    public CategoryCombo(String id, List<Category> categories, List<String> categoryOptionComboUIds) {
        this.id = id;
        this.categories = categories;
        this.categoryOptionComboUIdList = categoryOptionComboUIds;
    }

    public String getId() {
        return id;
    }

    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public int describeContents() {
        return TAG.length();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeTypedList(categories);
        parcel.writeStringList(categoryOptionComboUIdList);
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getCategoryOptionComboUIdList() {
        return categoryOptionComboUIdList;
    }

    public void setCategoryOptionComboUIdList(List<String> categoryOptionComboUIdList) {
        this.categoryOptionComboUIdList = categoryOptionComboUIdList;
    }
}
