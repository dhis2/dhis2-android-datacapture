package org.hisp.dhis.mobile.datacapture.io.loaders;

import android.database.Cursor;

public interface Transformation<T> {
    public T transform(Cursor cursor);
}