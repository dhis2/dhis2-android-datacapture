package org.dhis2.mobile.sdk.entities;


import org.joda.time.DateTime;

public interface TimeStampedModel {
    void setCreated(DateTime created);
    void setLastUpdated(DateTime lastUpdated);
    DateTime getCreated();
    DateTime getLastUpdated();
}
