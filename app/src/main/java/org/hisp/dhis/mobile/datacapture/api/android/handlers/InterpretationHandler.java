package org.hisp.dhis.mobile.datacapture.api.android.handlers;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.models.Access;
import org.hisp.dhis.mobile.datacapture.api.models.Comment;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItemElement;
import org.hisp.dhis.mobile.datacapture.api.models.Interpretation;
import org.hisp.dhis.mobile.datacapture.api.models.InterpretationDataSet;
import org.hisp.dhis.mobile.datacapture.api.models.InterpretationDataSetPeriod;
import org.hisp.dhis.mobile.datacapture.api.models.InterpretationOrganizationUnit;
import org.hisp.dhis.mobile.datacapture.api.models.User;
import org.hisp.dhis.mobile.datacapture.io.DBContract.InterpretationColumns;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class InterpretationHandler {
    public static final String[] PROJECTION = {
            InterpretationColumns.DB_ID,
            InterpretationColumns.ID,
            InterpretationColumns.CREATED,
            InterpretationColumns.LAST_UPDATED,
            InterpretationColumns.ACCESS,
            InterpretationColumns.TYPE,
            InterpretationColumns.NAME,
            InterpretationColumns.DISPLAY_NAME,
            InterpretationColumns.TEXT,
            InterpretationColumns.EXTERNAL_ACCESS,
            InterpretationColumns.MAP,
            InterpretationColumns.CHART,
            InterpretationColumns.REPORT_TABLE,
            InterpretationColumns.DATASET,
            InterpretationColumns.ORGANIZATION_UNIT,
            InterpretationColumns.PERIOD,
            InterpretationColumns.USER,
            InterpretationColumns.COMMENTS
    };

    private static final String TAG = InterpretationHandler.class.getSimpleName();

    private static final int DB_ID = 0;
    private static final int ID = 1;
    private static final int CREATED = 2;
    private static final int LAST_UPDATED = 3;
    private static final int ACCESS = 4;
    private static final int TYPE = 5;
    private static final int NAME = 6;
    private static final int DISPLAY_NAME = 7;
    private static final int TEXT = 8;
    private static final int EXTERNAL_ACCESS = 9;
    private static final int MAP = 10;
    private static final int CHART = 11;
    private static final int REPORT_TABLE = 12;
    private static final int DATASET = 13;
    private static final int ORGANIZATION_UNIT = 14;
    private static final int PERIOD = 15;
    private static final int USER = 16;
    private static final int COMMENTS = 17;

    private InterpretationHandler() {
    }

    public static ContentValues toContentValues(Interpretation interpretation) {
        if (interpretation == null) {
            throw new IllegalArgumentException("Interpretation object cannot be null");
        }

        ContentValues values = new ContentValues();
        Gson gson = new Gson();

        values.put(InterpretationColumns.ID, interpretation.getId());
        values.put(InterpretationColumns.CREATED, interpretation.getCreated());
        values.put(InterpretationColumns.LAST_UPDATED, interpretation.getLastUpdated());
        values.put(InterpretationColumns.ACCESS, gson.toJson(interpretation.getAccess()));
        values.put(InterpretationColumns.TYPE, interpretation.getType());
        values.put(InterpretationColumns.NAME, interpretation.getName());
        values.put(InterpretationColumns.DISPLAY_NAME, interpretation.getDisplayName());
        values.put(InterpretationColumns.TEXT, interpretation.getText());
        values.put(InterpretationColumns.EXTERNAL_ACCESS, interpretation.isExternalAccess() ? 1 : 0);
        values.put(InterpretationColumns.MAP, gson.toJson(interpretation.getMap()));
        values.put(InterpretationColumns.CHART, gson.toJson(interpretation.getChart()));
        values.put(InterpretationColumns.REPORT_TABLE, gson.toJson(interpretation.getReportTable()));
        values.put(InterpretationColumns.DATASET, gson.toJson(interpretation.getDataSet()));
        values.put(InterpretationColumns.ORGANIZATION_UNIT, gson.toJson(interpretation.getOrganisationUnit()));
        values.put(InterpretationColumns.PERIOD, gson.toJson(interpretation.getPeriod()));
        values.put(InterpretationColumns.USER, gson.toJson(interpretation.getUser()));
        values.put(InterpretationColumns.COMMENTS, gson.toJson(interpretation.getComments()));

        return values;
    }

    public static DBItemHolder<Interpretation> fromCursor(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("Cursor object cannot be null");
        }

        Interpretation interpretation = new Interpretation();
        Gson gson = new Gson();

        Access access = gson.fromJson(cursor.getString(ACCESS), Access.class);
        DashboardItemElement map = gson.fromJson(cursor.getString(MAP), DashboardItemElement.class);
        DashboardItemElement chart = gson.fromJson(cursor.getString(CHART), DashboardItemElement.class);
        DashboardItemElement reportTable = gson.fromJson(cursor.getString(REPORT_TABLE), DashboardItemElement.class);
        InterpretationDataSet dataset = gson.fromJson(cursor.getString(DATASET), InterpretationDataSet.class);
        InterpretationOrganizationUnit unit = gson.fromJson(cursor.getString(ORGANIZATION_UNIT), InterpretationOrganizationUnit.class);
        InterpretationDataSetPeriod period = gson.fromJson(cursor.getString(PERIOD), InterpretationDataSetPeriod.class);
        User user = gson.fromJson(cursor.getString(USER), User.class);

        Type type = new TypeToken<List<Comment>>() { }.getType();
        ArrayList<Comment> comments = gson.fromJson(cursor.getString(COMMENTS), type);

        interpretation.setId(cursor.getString(ID));
        interpretation.setCreated(cursor.getString(CREATED));
        interpretation.setLastUpdated(cursor.getString(LAST_UPDATED));
        interpretation.setAccess(access);
        interpretation.setType(cursor.getString(TYPE));
        interpretation.setName(cursor.getString(NAME));
        interpretation.setDisplayName(cursor.getString(DISPLAY_NAME));
        interpretation.setText(cursor.getString(TEXT));
        interpretation.setExternalAccess(cursor.getInt(EXTERNAL_ACCESS) == 1);

        interpretation.setMap(map);
        interpretation.setChart(chart);
        interpretation.setReportTable(reportTable);
        interpretation.setDataSet(dataset);
        interpretation.setOrganisationUnit(unit);
        interpretation.setPeriod(period);
        interpretation.setUser(user);
        interpretation.setComments(comments);

        DBItemHolder<Interpretation> holder = new DBItemHolder<>();
        holder.setItem(interpretation);
        holder.setDataBaseId(cursor.getInt(DB_ID));
        return holder;
    }

}
