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

package org.dhis2.mobile.sdk.entities;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

import org.dhis2.mobile.sdk.persistence.database.DhisDatabase;

import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

@Table(databaseName = DhisDatabase.NAME)
public final class UnitToDataSetRelation extends BaseModel implements RelationModel {
    private static final String ORG_UNIT_KEY = "orgUnitId";
    private static final String DATA_SET_KEY = "dataSetId";

    @Column @PrimaryKey(autoincrement = true) int id;

    @Column @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = ORG_UNIT_KEY, columnType = String.class, foreignColumnName = "id"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    ) final ForeignKeyContainer<OrganisationUnit> organisationUnit;

    @Column @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = DATA_SET_KEY, columnType = String.class, foreignColumnName = "id"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    ) final ForeignKeyContainer<DataSet> dataSet;

    @Override
    public String getFirstKey() {
        return (String) organisationUnit.getValue(ORG_UNIT_KEY);
    }

    @Override
    public String getSecondKey() {
        return (String) dataSet.getValue(DATA_SET_KEY);
    }

    public int getId() {
        return id;
    }

    public UnitToDataSetRelation(OrganisationUnit unit, DataSet dataSet) {
        isNull(unit, "OrganisationUnit object must not be null");
        isNull(dataSet, "DataSet object must not be null");

        this.organisationUnit = new ForeignKeyContainer<>(OrganisationUnit.class);
        this.dataSet = new ForeignKeyContainer<>(DataSet.class);

        this.organisationUnit.setModel(unit);
        this.dataSet.setModel(dataSet);

    }

    public OrganisationUnit getOrganisationUnit() {
        return organisationUnit != null ? organisationUnit.toModel() : null;
    }

    public DataSet getDataSet() {
        return dataSet != null ? dataSet.toModel() : null;
    }
}