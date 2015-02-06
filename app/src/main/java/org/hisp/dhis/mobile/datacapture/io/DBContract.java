package org.hisp.dhis.mobile.datacapture.io;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import org.hisp.dhis.mobile.datacapture.api.android.models.KeyValue;

public class DBContract {
    public static final String AUTHORITY = "org.hisp.dhis.mobile.datacapture.io.DBContentProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static interface BaseDBColumns {
        public static final String DB_ID = BaseColumns._ID;
        public static final String ID = "id";
    }

    public static interface TimeStampColumns {
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
    }

    public static interface StateColumns {
        public static final String STATE = "state";
    }

    public static interface AccessColumns {
        public static final String ACCESS = "access";
    }

    public static interface DashboardColumns extends BaseDBColumns,
            TimeStampColumns, AccessColumns, StateColumns {
        public static final String TABLE_NAME = "dashboardTable";
        public static final String PATH = TABLE_NAME;
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Dashboard";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Dashboard";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
        public static final String NAME = "name";
        public static final String ITEM_COUNT = "itemCount";


    }

    public static interface DashboardItemColumns extends BaseDBColumns,
            TimeStampColumns, AccessColumns, StateColumns {
        public static final String TABLE_NAME = "dashboardItemColumns";
        public static final String PATH = TABLE_NAME;
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.DashboardItem";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.DashboardItem";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
        public static final String DASHBOARD_DB_ID = "dashboardDBId";
        public static final String TYPE = "type";
        public static final String CONTENT_COUNT = "contentCount";
        public static final String MESSAGES = "messages";
        public static final String USERS = "users";
        public static final String REPORTS = "reports";
        public static final String RESOURCES = "resources";
        public static final String REPORT_TABLES = "reportTables";
        public static final String CHART = "chart";
        public static final String EVENT_CHART = "eventChart";
        public static final String REPORT_TABLE = "reportTable";
        public static final String MAP = "map";


    }

    public static interface InterpretationColumns extends BaseDBColumns,
            TimeStampColumns, AccessColumns, StateColumns {
        public static final String TABLE_NAME = "interpretationsTable";
        public static final String PATH = TABLE_NAME;

        public static final String TYPE = "type";
        public static final String NAME = "name";
        public static final String DISPLAY_NAME = "displayName";
        public static final String TEXT = "text";
        public static final String EXTERNAL_ACCESS = "externalAccess";

        public static final String MAP = "map";
        public static final String CHART = "chart";
        public static final String REPORT_TABLE = "reportTable";

        public static final String DATASET = "dataSet";
        public static final String ORGANIZATION_UNIT = "organisationUnit";
        public static final String PERIOD = "period";
        public static final String USER = "user";
        public static final String COMMENTS = "comments";
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Interpretation";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Interpretation";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);


    }

    public static interface KeyValueColumns {
        public static final String TABLE_NAME = "keyValuesTable";
        public static final String PATH = TABLE_NAME;

        public static final String DB_ID = BaseColumns._ID;
        public static final String KEY = "key";
        public static final String TYPE = "type";
        public static final String VALUE = "value";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.android.models.KeyValue";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.android.models.KeyValue";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static interface ReportColumns extends StateColumns {
        public static final String TABLE_NAME = "reportTable";
        public static final String PATH = TABLE_NAME;
        public static final String PATH_WITH_GROUPS = ReportColumns.TABLE_NAME +
                "/" + ReportGroupColumns.TABLE_NAME;

        public static final String DB_ID = BaseColumns._ID;
        public static final String ORG_UNIT_ID = "orgUnitId";
        public static final String DATASET_ID = "dataSetId";
        public static final String PERIOD = "period";
        public static final String COMPLETE_DATE = "completeDate";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Report";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Report";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
        public static final Uri CONTENT_URI_WITH_GROUPS = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WITH_GROUPS);
    }

    public static interface ReportGroupColumns {
        public static final String TABLE_NAME = "reportsGroupTable";
        public static final String PATH = TABLE_NAME;

        public static final String DB_ID = BaseColumns._ID;
        public static final String LABEL = "groupLabel";
        public static final String DATA_ELEMENT_COUNT = "dataElementCount";

        // ForeignKey to ReportColumns(_id)
        public static final String REPORT_DB_ID = "reportDBId";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Group";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Group";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static interface ReportFieldColumns {
        public static final String TABLE_NAME = "reportFieldsTable";
        public static final String PATH = TABLE_NAME;

        public static final String DB_ID = BaseColumns._ID;
        public static final String LABEL = "label";
        public static final String TYPE = "type";
        public static final String DATA_ELEMENT = "dataElement";
        public static final String CATEGORY_OPTION_COMBO = "categoryOptionCombo";
        public static final String OPTION_SET = "optionSet";
        public static final String VALUE = "value";

        // ForeignKey to ReportGroupColumns(_id)
        public static final String GROUP_DB_ID = "reportGroupId";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Field";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Field";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }


    public static interface OrganizationUnitColumns {
        public static final String TABLE_NAME = "organizationUnitTable";
        public static final String PATH = TABLE_NAME;

        public static final String DB_ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String LABEL = "label";
        public static final String LEVEL = "level";
        public static final String PARENT = "parent";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.OrganisationUnit";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.OrganisationUnit";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }


    public static interface DataSetColumns {
        public static final String TABLE_NAME = "dataSetTable";
        public static final String PATH = TABLE_NAME;

        public static final String DB_ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String LABEL = "label";
        public static final String SUBTITLE = "subtitle";
        public static final String ALLOW_FUTURE_PERIODS = "allowFuturePeriods";
        public static final String EXPIRY_DAYS = "expiryDays";
        public static final String PERIOD_TYPE = "periodType";

        // ForeignKey to OrganizationUnitColumns(_id)
        public static final String ORGANIZATION_UNIT_DB_ID = "organizationUnitDBId";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.DataSet";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.DataSet";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }


    public static interface GroupColumns {
        public static final String TABLE_NAME = "dataSetGroupTable";
        public static final String PATH = TABLE_NAME;

        public static final String DB_ID = BaseColumns._ID;
        public static final String LABEL = "label";
        public static final String DATA_ELEMENT_COUNT = "dataElementCount";

        // ForeignKey to DataSetColumns(_id)
        public static final String DATA_SET_DB_ID = "dataSetDBId";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Group";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Group";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }


    public static interface FieldColumns {
        public static final String TABLE_NAME = "fieldTable";
        public static final String PATH = TABLE_NAME;

        public static final String DB_ID = BaseColumns._ID;
        public static final String LABEL = "label";
        public static final String TYPE = "type";
        public static final String DATA_ELEMENT = "dataElement";
        public static final String CATEGORY_OPTION_COMBO = "categoryOptionCombo";
        public static final String OPTION_SET = "optionSet";
        public static final String VALUE = "value";

        // ForeignKey to GroupColumns(_id)
        public static final String GROUP_DB_ID = "groupId";

        // ForeignKey to OptionSetColumns(_id)
        public static final String OPTION_SET_DB_ID = "optionSetDBId";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Field";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Field";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }


    public static interface OptionSetColumns {
        public static final String TABLE_NAME = "optionSetTable";
        public static final String PATH = TABLE_NAME;

        public static final String DB_ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String NAME = "name";
        public static final String DISPLAY_NAME = "displayName";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.OptionSet";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.OptionSet";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }


    public static interface OptionColumns {
        public static final String TABLE_NAME = "optionTable";
        public static final String PATH = TABLE_NAME;

        public static final String DB_ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String NAME = "name";

        // ForeignKey to OptionSetColumns(_id)
        public static final String OPTION_SET_DB_ID = "optionSetDBId";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Option";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Option";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }
}