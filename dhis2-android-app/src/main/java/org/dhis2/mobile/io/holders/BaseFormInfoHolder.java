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

package org.dhis2.mobile.io.holders;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseFormInfoHolder implements Parcelable {
	public static final String TAG = "org.dhis2.mobile.io.holders.BaseFormInfoHolder";
	
	private String orgUnitLabel;
	private String orgUnitId;
	private String formLabel;
	private String formId;
	
	public BaseFormInfoHolder() { }
	
	protected BaseFormInfoHolder(Parcel in) {
		orgUnitLabel = in.readString();
		orgUnitId = in.readString();
		formLabel = in.readString();
		formId = in.readString();
	}
	
	@Override
	public int describeContents() {
		return TAG.length();
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(orgUnitLabel);
		parcel.writeString(orgUnitId);
		parcel.writeString(formLabel);
		parcel.writeString(formId);
	}
	
	public static final Parcelable.Creator<BaseFormInfoHolder> CREATOR = new Parcelable.Creator<BaseFormInfoHolder>() {

		public BaseFormInfoHolder createFromParcel(Parcel in) {
			return new BaseFormInfoHolder(in);
		}

		public BaseFormInfoHolder[] newArray(int size) {
			return new BaseFormInfoHolder[size];
		}
	};
	
	public void setOrgUnitLabel(String orgUnitLabel) {
		this.orgUnitLabel = orgUnitLabel;
	}
	
	public void setOrgUnitId(String orgUnitId) {
		this.orgUnitId = orgUnitId;
	}
	
	public void setFormLabel(String formLabel) {
		this.formLabel = formLabel;
	}
	
	public void setFormId(String formId) {
		this.formId = formId;
	}
	
	public String getOrgUnitLabel() {
		return orgUnitLabel;
	}
	
	public String getOrgUnitId() {
		return orgUnitId;
	}
	
	public String getFormLabel() {
		return formLabel;
	}
	
	public String getFormId() {
		return formId;
	}
}
