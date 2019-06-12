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

public class OrganizationUnit implements Serializable, Parcelable {

    // Comparator which is used to sort organization units in alphabetical order
    public static Comparator<OrganizationUnit> COMPARATOR = new Comparator<OrganizationUnit>() {

        @Override
        public int compare(OrganizationUnit one, OrganizationUnit two) {
            return one.getLabel().compareTo(two.getLabel());
        }
    };

    public static final String TAG = "org.dhis2.mobile.io.models.OrganizationUnit";

    private String id;
    private String label;
    private String level;
    private String parent;
    private ArrayList<Form> forms = new ArrayList<Form>();

    private OrganizationUnit(Parcel in) {
        id = in.readString();
        label = in.readString();
        level = in.readString();
        parent = in.readString();
        in.readTypedList(forms, Form.CREATOR);
    }

    public OrganizationUnit(String id, String label, String parent) {
        this.id = id;
        this.label = label;
        this.parent = parent;
    }

    @Override
    public int describeContents() {
        return TAG.length();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(label);
        parcel.writeString(level);
        parcel.writeString(parent);
        parcel.writeTypedList(forms);
    }

    public static final Parcelable.Creator<OrganizationUnit> CREATOR = new Parcelable.Creator<OrganizationUnit>() {

        public OrganizationUnit createFromParcel(Parcel in) {
            return new OrganizationUnit(in);
        }

        public OrganizationUnit[] newArray(int size) {
            return new OrganizationUnit[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getLevel() {
        return level;
    }

    public String getParent() {
        return parent;
    }

    public ArrayList<Form> getForms() {
        return forms;
    }
}
