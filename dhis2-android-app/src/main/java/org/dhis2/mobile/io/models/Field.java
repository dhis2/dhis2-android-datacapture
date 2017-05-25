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

public class Field implements Serializable, Parcelable {
    public static final String DATA_ELEMENT = "dataElement";
    public static final String CATEGORY_OPTION_COMBO = "categoryOptionCombo";
    public static final String VALUE = "value";

    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String EMPTY_FIELD = "";

    private String dataElement;
    private String categoryOptionCombo;
    private String value = EMPTY_FIELD;

    private String label;
    private String optionSet;
    private String type;
    private boolean compulsory;

    public Field() {
    }

    private Field(Parcel in) {
        this.label = in.readString();
        this.dataElement = in.readString();
        this.categoryOptionCombo = in.readString();
        this.optionSet = in.readString();
        this.type = in.readString();
        this.value = in.readString();
    }

    public final static Parcelable.Creator<Field> CREATOR = new Parcelable.Creator<Field>() {

        public Field createFromParcel(Parcel in) {
            return new Field(in);
        }

        public Field[] newArray(int size) {
            return new Field[size];
        }
    };

    @Override
    public void writeToParcel(Parcel field, int flag) {
        field.writeString(label);
        field.writeString(dataElement);
        field.writeString(categoryOptionCombo);
        field.writeString(optionSet);
        field.writeString(type);
        field.writeString(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setValue(String value) {
        if (value != null) {
            this.value = value;
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }

    public boolean hasOptionSet() {
        return optionSet != null;
    }

    public String getLabel() {
        return label;
    }

    public String getDataElement() {
        return dataElement;
    }

    public String getCategoryOptionCombo() {
        return categoryOptionCombo;
    }

    public String getType() {
        return type;
    }

    public String getOptionSet() {
        return optionSet;
    }

    public String getValue() {
        return value;
    }

    public boolean isCompulsory() {
        return compulsory;
    }

    public void setCompulsory(boolean compulsory) {
        this.compulsory = compulsory;
    }
}

