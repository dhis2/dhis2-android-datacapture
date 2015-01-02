package org.hisp.dhis.mobile.datacapture.io;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

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

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Dashboard";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Dashboard";

        public static final String NAME = "name";
        public static final String ITEM_COUNT = "itemCount";
    }

    public static interface DashboardItemColumns extends BaseDBColumns,
            TimeStampColumns, AccessColumns, StateColumns {
        public static final String TABLE_NAME = "dashboardItemColumns";
        public static final String PATH = TABLE_NAME;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.DashboardItem";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.DashboardItem";

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
}
