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

import org.dhis2.mobile.io.models.CategoryCombo;
import org.dhis2.mobile.io.models.CategoryOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DatasetInfoHolder extends BaseFormInfoHolder {
    public static final String TAG = "org.dhis2.mobile.io.holders.DatasetInfoHolder";
    private String periodLabel;
    private String period;
    private List<CategoryOption> categoryOptions = new ArrayList<>();
    private CategoryCombo defaultCategoryCombo;
    private HashMap<String, List<CategoryCombo>> categoryComboByDataElement = new HashMap<>();
    private HashMap<String, List<String>> categoryOptionComboUIdsBySection = new HashMap<>();

    public DatasetInfoHolder() {
    }

    protected DatasetInfoHolder(Parcel in) {
        super(in);
        periodLabel = in.readString();
        period = in.readString();
        in.readTypedList(categoryOptions, CategoryOption.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(periodLabel);
        parcel.writeString(period);
        parcel.writeTypedList(categoryOptions);
    }

    @Override
    public int describeContents() {
        return TAG.length();
    }

    public static final Parcelable.Creator<DatasetInfoHolder> CREATOR = new Parcelable.Creator<DatasetInfoHolder>() {

        public DatasetInfoHolder createFromParcel(Parcel in) {
            return new DatasetInfoHolder(in);
        }

        public DatasetInfoHolder[] newArray(int size) {
            return new DatasetInfoHolder[size];
        }
    };

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPeriod() {
        return period;
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public void setCategoryOptions(List<CategoryOption> categoryOptions) {
        this.categoryOptions.clear();

        if (categoryOptions != null) {
            this.categoryOptions.addAll(categoryOptions);
        }
    }

    public List<CategoryOption> getCategoryOptions() {
        return categoryOptions;
    }

    public static String buildKey(DatasetInfoHolder info) {
        List<String> options = new ArrayList<>();

        if (info.getCategoryOptions() != null && !info.getCategoryOptions().isEmpty()) {
            for (CategoryOption categoryOption : info.getCategoryOptions()) {
                options.add(categoryOption.getId());
            }
        }

        // we need to sort ids in order to achieve consistency
        Collections.sort(options);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(info.getOrgUnitId());
        stringBuilder.append(info.getFormId());
        stringBuilder.append(info.getPeriod());

        if (!options.isEmpty()) {
            for (String option : options) {
                stringBuilder.append(option);
            }
        }

        return stringBuilder.toString();
    }

    public HashMap<String, List<CategoryCombo>> getCategoryComboByDataElement() {
        return categoryComboByDataElement;
    }

    public void setCategoryComboByDataElement(
            HashMap<String, List<CategoryCombo>> categoryComboByDataElement) {
        this.categoryComboByDataElement = categoryComboByDataElement;
    }

    public CategoryCombo getDefaultCategoryCombo() {
        return defaultCategoryCombo;
    }

    public void setDefaultCategoryCombo(CategoryCombo defaultCategoryCombo) {
        this.defaultCategoryCombo = defaultCategoryCombo;
    }

    public HashMap<String, List<String>>
    getCategoryComboDataElementBySection() {
        return categoryOptionComboUIdsBySection;
    }

    public void setCategoryOptionComboUIdsBySection(
            HashMap<String, List<String>>
                    categoryOptionComboUIdsBySection) {
        this.categoryOptionComboUIdsBySection = categoryOptionComboUIdsBySection;
    }
}
