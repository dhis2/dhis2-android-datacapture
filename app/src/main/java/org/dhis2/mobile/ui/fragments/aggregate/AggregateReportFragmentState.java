/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.mobile.ui.fragments.aggregate;

import android.os.Parcel;
import android.os.Parcelable;

import org.dhis2.mobile.api.models.DateHolder;

import java.util.ArrayList;
import java.util.List;

import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

public class AggregateReportFragmentState implements Parcelable {
    public static final Creator<AggregateReportFragmentState> CREATOR
            = new Creator<AggregateReportFragmentState>() {

        public AggregateReportFragmentState createFromParcel(Parcel in) {
            return new AggregateReportFragmentState(in);
        }

        public AggregateReportFragmentState[] newArray(int size) {
            return new AggregateReportFragmentState[size];
        }
    };
    private static final String TAG = AggregateReportFragmentState.class.getName();
    private boolean syncInProcess;

    private String orgUnitLabel;
    private String orgUnitId;

    private String dataSetLabel;
    private String dataSetId;
    private String dataSetCategoryComboId;

    private String periodLabel;
    private String periodDate;
    private List<CategoryState> categoryStates;

    public AggregateReportFragmentState() {
        categoryStates = new ArrayList<>();
    }

    public AggregateReportFragmentState(AggregateReportFragmentState state) {
        if (state != null) {
            setSyncInProcess(state.isSyncInProcess());
            setOrgUnit(state.getOrgUnitId(), state.getOrgUnitLabel());
            setDataSet(state.getDataSetId(), state.getDataSetLabel(), state.getDataSetCategoryComboId());
            setPeriod(state.getPeriod());
            setCategoryStates(new ArrayList<>(state.getCategoryStates()));
        }
    }

    private AggregateReportFragmentState(Parcel in) {
        syncInProcess = in.readInt() == 1;

        orgUnitLabel = in.readString();
        orgUnitId = in.readString();

        dataSetLabel = in.readString();
        dataSetId = in.readString();
        dataSetCategoryComboId = in.readString();

        periodLabel = in.readString();
        periodDate = in.readString();
        categoryStates = new ArrayList<>();

        in.readTypedList(categoryStates, CategoryState.CREATOR);
    }

    @Override
    public int describeContents() {
        return TAG.length();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(syncInProcess ? 1 : 0);

        parcel.writeString(orgUnitLabel);
        parcel.writeString(orgUnitId);

        parcel.writeString(dataSetLabel);
        parcel.writeString(dataSetId);
        parcel.writeString(dataSetCategoryComboId);

        parcel.writeString(periodLabel);
        parcel.writeString(periodDate);
        parcel.writeTypedList(categoryStates);
    }

    public boolean isSyncInProcess() {
        return syncInProcess;
    }

    public void setSyncInProcess(boolean syncInProcess) {
        this.syncInProcess = syncInProcess;
    }

    public void setOrgUnit(String orgUnitId, String orgUnitLabel) {
        this.orgUnitId = orgUnitId;
        this.orgUnitLabel = orgUnitLabel;
    }

    public void resetOrgUnit() {
        orgUnitId = null;
        orgUnitLabel = null;
    }

    public boolean isOrgUnitEmpty() {
        return (orgUnitId == null || orgUnitLabel == null);
    }

    public String getOrgUnitLabel() {
        return orgUnitLabel;
    }

    public String getOrgUnitId() {
        return orgUnitId;
    }

    public void setDataSet(String dataSetId, String dataSetLabel, String categoryComboId) {
        this.dataSetId = dataSetId;
        this.dataSetLabel = dataSetLabel;
        this.dataSetCategoryComboId = categoryComboId;
    }

    public void resetDataSet() {
        dataSetId = null;
        dataSetLabel = null;
        dataSetCategoryComboId = null;
    }

    public boolean isDataSetEmpty() {
        return (dataSetId == null || dataSetLabel == null || dataSetCategoryComboId == null);
    }

    public String getDataSetLabel() {
        return dataSetLabel;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public String getDataSetCategoryComboId() {
        return dataSetCategoryComboId;
    }

    public DateHolder getPeriod() {
        return new DateHolder(periodDate, periodLabel);
    }

    public void setPeriod(DateHolder dateHolder) {
        if (dateHolder != null) {
            periodLabel = dateHolder.getLabel();
            periodDate = dateHolder.getDate();
        }
    }

    public void resetPeriod() {
        periodLabel = null;
        periodDate = null;
    }

    public boolean isPeriodEmpty() {
        return (periodLabel == null || periodDate == null);
    }

    public List<CategoryState> getCategoryStates() {
        return categoryStates;
    }

    public void setCategoryStates(List<CategoryState> categoryStates) {
        this.categoryStates = categoryStates;
    }

    public void resetCategoryStates() {
        categoryStates = null;
    }

    public boolean areCategoryStatesEmpty() {
        return categoryStates == null || categoryStates.isEmpty();
    }

    public static class CategoryState implements Parcelable {
        public static final Creator<CategoryState> CREATOR
                = new Creator<CategoryState>() {

            public CategoryState createFromParcel(Parcel in) {
                return new CategoryState(in);
            }

            public CategoryState[] newArray(int size) {
                return new CategoryState[size];
            }
        };

        private static final String TAG = CategoryState.class.getSimpleName();
        private final String categoryId;
        private final String categoryName;
        private String categoryOptionId;
        private String categoryOptionName;

        public CategoryState(String categoryId, String categoryName) {
            isNull(categoryId, "Category ID must not be null");
            isNull(categoryName, "Category name must not be null");
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }

        private CategoryState(Parcel in) {
            categoryId = in.readString();
            categoryName = in.readString();
            categoryOptionId = in.readString();
            categoryOptionName = in.readString();
        }

        @Override
        public int describeContents() {
            return TAG.length();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(categoryId);
            dest.writeString(categoryName);
            dest.writeString(categoryOptionId);
            dest.writeString(categoryOptionName);
        }

        public boolean isCategoryOptionSelected() {
            return categoryOptionId != null;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getCategoryOptionId() {
            return categoryOptionId;
        }

        public String getCategoryOptionName() {
            return categoryOptionName;
        }

        public void setCategoryOptionId(String categoryOptionId) {
            this.categoryOptionId = categoryOptionId;
        }

        public void setCategoryOptionName(String categoryOptionName) {
            this.categoryOptionName = categoryOptionName;
        }
    }
}