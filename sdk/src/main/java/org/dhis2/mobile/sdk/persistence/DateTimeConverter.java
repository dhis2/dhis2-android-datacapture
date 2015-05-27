package org.dhis2.mobile.sdk.persistence;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.joda.time.DateTime;

/**
 * This class is used to automatically convert DateTime object to String
 * and backwards during read/write operations to database
 */
public final class DateTimeConverter extends TypeConverter<String, DateTime> {

    @Override public String getDBValue(DateTime model) {
        return model.toString();
    }

    @Override public DateTime getModelValue(String data) {
        return DateTime.parse(data);
    }
}
