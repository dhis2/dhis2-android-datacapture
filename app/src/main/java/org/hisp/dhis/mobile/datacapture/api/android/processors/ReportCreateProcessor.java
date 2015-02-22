package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.Context;

import org.hisp.dhis.mobile.datacapture.api.android.events.CreateReportEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnCreateReportEvent;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.DataSet;
import org.hisp.dhis.mobile.datacapture.api.models.Report;
import org.hisp.dhis.mobile.datacapture.io.handlers.DataSetHandler;
import org.hisp.dhis.mobile.datacapture.io.handlers.ReportHandler;

public class ReportCreateProcessor extends AbsProcessor<CreateReportEvent, OnCreateReportEvent> {

    public ReportCreateProcessor(Context context, CreateReportEvent event) {
        super(context, event);
    }

    @Override
    public OnCreateReportEvent process() {
        ReportHandler reportHandler = new ReportHandler(getContext());
        DataSetHandler dataSetHandler = new DataSetHandler(getContext());
        OnCreateReportEvent event = new OnCreateReportEvent();

        Report eReport = getEvent().getReport();
        DbRow<Report> oldReport = reportHandler.query(
                eReport.getOrgUnit(), eReport.getDataSet(), eReport.getPeriod()
        );

        if (oldReport != null) {
            return event;
        }

        DbRow<DataSet> dataSet = dataSetHandler.queryById(eReport.getDataSet(), true);
        reportHandler.insert(eReport, dataSet.getItem());
        return event;
    }
}
