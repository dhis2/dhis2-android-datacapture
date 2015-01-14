package org.hisp.dhis.mobile.datacapture.api.android.managers;

import org.hisp.dhis.mobile.datacapture.api.android.converters.DashboardConverter;
import org.hisp.dhis.mobile.datacapture.api.android.converters.DashboardItemConverter;
import org.hisp.dhis.mobile.datacapture.api.android.converters.DashboardNameConverter;
import org.hisp.dhis.mobile.datacapture.api.android.converters.DataSetConverter;
import org.hisp.dhis.mobile.datacapture.api.android.converters.OptionSetConverter;
import org.hisp.dhis.mobile.datacapture.api.android.converters.ProgramsConverter;
import org.hisp.dhis.mobile.datacapture.api.android.converters.RawConverter;
import org.hisp.dhis.mobile.datacapture.api.android.converters.ReportConverter;
import org.hisp.dhis.mobile.datacapture.api.android.converters.UserAccountConverter;
import org.hisp.dhis.mobile.datacapture.api.managers.IJsonConverter;
import org.hisp.dhis.mobile.datacapture.api.managers.IJsonManager;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.api.models.DataSetHolder;
import org.hisp.dhis.mobile.datacapture.api.models.OptionSet;
import org.hisp.dhis.mobile.datacapture.api.models.ProgramHolder;
import org.hisp.dhis.mobile.datacapture.api.models.Report;
import org.hisp.dhis.mobile.datacapture.api.models.UserAccount;

import java.util.List;

public final class JsonManager implements IJsonManager {

    @Override
    public IJsonConverter<UserAccount> getUserAccountConverter() {
        return new UserAccountConverter();
    }

    @Override
    public IJsonConverter<DataSetHolder> getDataSetConverter() {
        return new DataSetConverter();
    }

    @Override
    public IJsonConverter<OptionSet> getOptionSetConverter() {
        return new OptionSetConverter();
    }

    @Override
    public IJsonConverter<ProgramHolder> getProgramsConverter() {
        return new ProgramsConverter();
    }

    @Override
    public IJsonConverter<List<Dashboard>> getDashboardsConverter() {
        return new DashboardConverter();
    }

    @Override
    public IJsonConverter<Report> getReportConverter() {
        return new ReportConverter();
    }

    @Override
    public IJsonConverter<DashboardItem> getDashboardItemConverter() {
        return new DashboardItemConverter();
    }

    @Override
    public IJsonConverter<String> getRawConverter() {
        return new RawConverter();
    }

    @Override
    public IJsonConverter<String> getDashboardNameConverter() {
        return new DashboardNameConverter();
    }
}