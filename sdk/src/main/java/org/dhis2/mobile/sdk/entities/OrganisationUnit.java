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

package org.dhis2.mobile.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.Table;

import org.dhis2.mobile.sdk.persistence.database.DhisDatabase;

import java.util.List;

@ModelContainer @Table(databaseName = DhisDatabase.NAME)
public final class OrganisationUnit extends BaseIdentifiableModel {
    @JsonProperty("displayName") @Column String displayName;
    @JsonProperty("level") @Column int level;
    @JsonProperty("parent") OrganisationUnit parent;
    @JsonProperty("children") List<OrganisationUnit> children;
    @JsonProperty("dataSets") List<DataSet> dataSets;

    public OrganisationUnit() {
    }

    @JsonIgnore
    public OrganisationUnit getParent() {
        return parent;
    }

    @JsonIgnore
    public void setParent(OrganisationUnit parent) {
        this.parent = parent;
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
    public int getLevel() {
        return level;
    }

    @JsonIgnore
    public void setLevel(int level) {
        this.level = level;
    }

    @JsonIgnore
    public List<OrganisationUnit> getChildren() {
        return children;
    }

    @JsonIgnore
    public void setChildren(List<OrganisationUnit> children) {
        this.children = children;
    }

    @JsonIgnore
    public List<DataSet> getDataSets() {
        return dataSets;
    }

    @JsonIgnore
    public void setDataSets(List<DataSet> dataSets) {
        this.dataSets = dataSets;
    }
}
