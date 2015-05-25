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

package org.dhis2.mobile.sdk.persistence.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import org.dhis2.mobile.sdk.persistence.preferences.IPreferenceHandler;
import org.joda.time.DateTime;

import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

public final class LastUpdatedHandler implements IPreferenceHandler<DateTime> {
    private static final String META_DATA_PREFERENCES = "metaDataPreferences";
    private static final String LAST_UPDATED = "key:lastUpdated";
    private static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private SharedPreferences mPrefs;

    public LastUpdatedHandler(Context context) {
        isNull(context, "Context object must not be null");

        mPrefs = context.getSharedPreferences(META_DATA_PREFERENCES,
                Context.MODE_PRIVATE);
    }

    @Override
    public void put(DateTime dateTime) {
        isNull(dateTime, "DateTime object must not be null");
        String lastUpdated = dateTime.toString(FORMAT);
        mPrefs.edit().putString(LAST_UPDATED, lastUpdated).apply();
    }

    @Override
    public void delete() {
        mPrefs.edit().clear().apply();
    }

    @Override
    public DateTime get() {
        String lastUpdated = mPrefs.getString(LAST_UPDATED, null);
        DateTime dateTime = null;
        if (lastUpdated != null) {
            dateTime = DateTime.parse(lastUpdated);
        }
        return dateTime;
    }
}
