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

package org.dhis2.mobile.sdk.persistence.handlers;

import android.content.ContentProviderOperation;
import android.database.Cursor;

import org.dhis2.mobile.sdk.entities.CategoryOptionCombo;

import java.util.ArrayList;
import java.util.List;

final class CategoryOptionComboHandler implements IModelHandler<CategoryOptionCombo> {
    @Override
    public List<CategoryOptionCombo> map(Cursor cursor, boolean closeCursor) {
        return null;
    }

    @Override
    public CategoryOptionCombo mapSingleItem(Cursor cursor, boolean closeCursor) {
        return null;
    }

    @Override
    public String[] getProjection() {
        return new String[0];
    }

    @Override
    public ContentProviderOperation insert(CategoryOptionCombo object) {
        return null;
    }

    @Override
    public ContentProviderOperation update(CategoryOptionCombo object) {
        return null;
    }

    @Override
    public ContentProviderOperation delete(CategoryOptionCombo object) {
        return null;
    }

    @Override
    public <T> List<T> queryRelatedModels(Class<T> clazz, Object id) {
        return null;
    }

    @Override
    public List<CategoryOptionCombo> query(String selection, String[] selectionArgs) {
        return null;
    }

    @Override
    public List<CategoryOptionCombo> query() {
        return new ArrayList<>();
    }

    @Override
    public List<ContentProviderOperation> sync(List<CategoryOptionCombo> items) {
        return null;
    }
}
