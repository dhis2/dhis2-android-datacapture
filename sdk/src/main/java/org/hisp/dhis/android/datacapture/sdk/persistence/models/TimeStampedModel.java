package org.hisp.dhis.android.datacapture.sdk.persistence.models;


import org.joda.time.DateTime;

public interface TimeStampedModel {
    void setCreated(DateTime created);
    void setLastUpdated(DateTime lastUpdated);
    DateTime getCreated();
    DateTime getLastUpdated();
}
