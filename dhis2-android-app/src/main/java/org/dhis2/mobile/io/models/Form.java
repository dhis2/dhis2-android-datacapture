/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile.io.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Form implements Serializable, Parcelable {
    public static final String TAG = Form.class.getSimpleName();

    // Comparator which is used to sort forms in alphabetical order
    public static Comparator<Form> COMPARATOR = new Comparator<Form>() {

        @Override
        public int compare(Form one, Form two) {
            return one.getLabel().compareTo(two.getLabel());
        }
    };

    private String id;
    private String label;
    private CategoryCombo categoryCombo;
    private FormOptions options;
    private ArrayList<Group> groups = new ArrayList<>();
    private boolean fieldCombinationRequired;
    private boolean isApproved;

    private Form(Parcel in) {
        id = in.readString();
        label = in.readString();
        categoryCombo = in.readParcelable(CategoryCombo.class.getClassLoader());
        options = in.readParcelable(FormOptions.class.getClassLoader());
        in.readTypedList(groups, Group.CREATOR);
        fieldCombinationRequired = in.readByte() != 0;
        isApproved = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Form> CREATOR = new Parcelable.Creator<Form>() {

        public Form createFromParcel(Parcel in) {
            return new Form(in);
        }

        public Form[] newArray(int size) {
            return new Form[size];
        }
    };

    @Override
    public int describeContents() {
        return TAG.length();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(label);
        parcel.writeParcelable(categoryCombo, flags);
        parcel.writeParcelable(options, flags);
        parcel.writeTypedList(groups);
        parcel.writeByte((byte) (fieldCombinationRequired ? 1 : 0));
        parcel.writeByte((byte) (isApproved ? 1 : 0));
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public FormOptions getOptions() {
        return options;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public CategoryCombo getCategoryCombo() {
        return categoryCombo;
    }

    public boolean isFieldCombinationRequired() {
        return fieldCombinationRequired;
    }

    public void setFieldCombinationRequired(boolean fieldCombinationRequired) {
        this.fieldCombinationRequired = fieldCombinationRequired;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
