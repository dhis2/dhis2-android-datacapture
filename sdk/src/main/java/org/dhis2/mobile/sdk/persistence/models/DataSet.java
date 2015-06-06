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

package org.dhis2.mobile.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.dhis2.mobile.sdk.persistence.DbDhis;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class DataSet extends BaseIdentifiableObject implements DisplayNameModel {
    private static final String CATEGORY_COMBO_KEY = "categoryComboKey";

    @JsonProperty("displayName") @Column String displayName;
    @JsonProperty("version") @Column int version;
    @JsonProperty("expiryDays") @Column int expiryDays;
    @JsonProperty("allowFuturePeriods") @Column boolean allowFuturePeriods;
    @JsonProperty("periodType") @Column String periodType;

    @JsonProperty("organisationUnits") List<OrganisationUnit> organisationUnits;
    @JsonProperty("sections") List<Object> sections;
    @JsonProperty("dataElements") List<DataElement> dataElements;
    @JsonProperty("categoryCombo") @Column @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = CATEGORY_COMBO_KEY, columnType = String.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    ) CategoryCombo categoryCombo;

    public DataSet() {
    }

    public static List<DataElement> queryRelatedDataElementsFromDb(String id) {
        List<DataSetToDataElementRelation> relations = new Select()
                .from(DataSetToDataElementRelation.class)
                .where(Condition
                        .column(DataSetToDataElementRelation$Table.DATASET_DATASET)
                        .is(id))
                .queryList();
        // read full versions of datasets
        List<DataElement> dataElements = new ArrayList<>();
        if (relations != null && !relations.isEmpty()) {
            for (DataSetToDataElementRelation relation : relations) {
                dataElements.add(relation.getDataElement());
            }
        }
        return dataElements;
    }

    @JsonIgnore
    public List<OrganisationUnit> getOrganisationUnits() {
        return organisationUnits;
    }

    @JsonIgnore
    public void setOrganisationUnits(List<OrganisationUnit> organisationUnits) {
        this.organisationUnits = organisationUnits;
    }

    @JsonIgnore
    public CategoryCombo getCategoryCombo() {
        return categoryCombo;
    }

    @JsonIgnore
    public void setCategoryCombo(CategoryCombo categoryCombo) {
        this.categoryCombo = categoryCombo;
    }

    @JsonIgnore
    public void setSections(List<Object> sections) {
        this.sections = sections;
    }

    @JsonIgnore
    public String getDisplayName() {
        return displayName;
    }

    @JsonIgnore
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore
    public int getVersion() {
        return version;
    }

    @JsonIgnore
    public void setVersion(int version) {
        this.version = version;
    }

    @JsonIgnore
    public int getExpiryDays() {
        return expiryDays;
    }

    @JsonIgnore
    public void setExpiryDays(int expiryDays) {
        this.expiryDays = expiryDays;
    }

    @JsonIgnore
    public boolean isAllowFuturePeriods() {
        return allowFuturePeriods;
    }

    @JsonIgnore
    public void setAllowFuturePeriods(boolean allowFuturePeriods) {
        this.allowFuturePeriods = allowFuturePeriods;
    }

    @JsonIgnore
    public String getPeriodType() {
        return periodType;
    }

    @JsonIgnore
    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    @JsonIgnore
    public List<Object> getSections() {
        return sections;
    }

    @JsonIgnore
    public List<DataElement> getDataElements() {
        return dataElements;
    }

    @JsonIgnore
    public void setDataElements(List<DataElement> dataElements) {
        this.dataElements = dataElements;
    }
}
