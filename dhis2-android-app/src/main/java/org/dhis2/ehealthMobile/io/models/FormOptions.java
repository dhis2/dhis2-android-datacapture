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

package org.dhis2.ehealthMobile.io.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class FormOptions implements Serializable, Parcelable {
    public static final String TAG = "org.dhis2.mobile.io.models.FormOptions";

    // options related to datasets
    private int openFuturePeriods;
    private String periodType;

    // options related to programs
    private String dateOfIncidentDescription;
    private String dateOfEnrollmentDescription;
    private String description;
    private String captureCoordinates;
    private String type;

    private FormOptions(Parcel in) {
        openFuturePeriods = in.readInt();
        periodType = in.readString();

        dateOfIncidentDescription = in.readString();
        dateOfEnrollmentDescription = in.readString();
        description = in.readString();
        captureCoordinates = in.readString();
        type = in.readString();
    }

    public static final Parcelable.Creator<FormOptions> CREATOR = new Parcelable.Creator<FormOptions>() {

        public FormOptions createFromParcel(Parcel in) {
            return new FormOptions(in);
        }

        public FormOptions[] newArray(int size) {
            return new FormOptions[size];
        }
    };

    @Override
    public int describeContents() {
        return TAG.length();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(openFuturePeriods);
        parcel.writeString(periodType);

        parcel.writeString(dateOfIncidentDescription);
        parcel.writeString(dateOfEnrollmentDescription);
        parcel.writeString(description);
        parcel.writeString(captureCoordinates);
        parcel.writeString(type);
    }

    public int getOpenFuturePeriods() {
        return openFuturePeriods;
    }

    public String getPeriodType() {
        return periodType;
    }

    public String getDateOfIncidentDescription() {
        return dateOfIncidentDescription;
    }

    public String getDateOfEnrollmentDescription() {
        return dateOfEnrollmentDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getCaptureCoordinates() {
        return captureCoordinates;
    }

    public String getType() {
        return type;
    }
}
