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

package org.dhis2.mobile.utils.date;

import android.os.Parcel;
import android.os.Parcelable;

public class DateHolder implements Parcelable {
    public static final String TAG = DateHolder.class.getSimpleName();

    private final String date;
    private final String dateTime;
    private final String label;

    public DateHolder(String date, String dateTime, String label) {
        this.date = date;
        this.dateTime = dateTime;
        this.label = label;
    }

    private DateHolder(Parcel in) {
        date = in.readString();
        dateTime = in.readString();
        label = in.readString();
    }

    public static final Parcelable.Creator<DateHolder> CREATOR = new Parcelable.Creator<DateHolder>() {

        public DateHolder createFromParcel(Parcel in) {
            return new DateHolder(in);
        }

        public DateHolder[] newArray(int size) {
            return new DateHolder[size];
        }
    };

    @Override
    public int describeContents() {
        return TAG.length();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(date);
        parcel.writeString(dateTime);
        parcel.writeString(label);
    }

    public String getLabel() {
        return label;
    }

    public String getDate() {
        return date;
    }

    public String getDateTime() {
        return dateTime;
    }
}
