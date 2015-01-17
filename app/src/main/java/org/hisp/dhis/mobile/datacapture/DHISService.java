package org.hisp.dhis.mobile.datacapture;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardCreateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardItemDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardUpdateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.GetReportTableEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.InterpretationDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.InterpretationSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.InterpretationUpdateTextEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.LoginUserEvent;
import org.hisp.dhis.mobile.datacapture.api.android.processors.DashboardCreateProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.DashboardDeleteProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.DashboardItemDeleteProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.DashboardSyncProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.DashboardUpdateProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.GetReportTableProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.InterpretationDeleteProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.InterpretationSyncProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.InterpretationUpdateTextProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.LoginUserProcessor;

public class DHISService extends Service {
    private static final String TAG = DHISService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");

        BusProvider.getInstance().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand()");

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind()");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");

        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onUserLoginEvent(LoginUserEvent event) {
        executeTask(new LoginUserProcessor(getBaseContext(), event));
    }

    @Subscribe
    public void onDashboardSyncEvent(DashboardSyncEvent event) {
        executeTask(new DashboardSyncProcessor(getBaseContext()));
    }

    @Subscribe
    public void onGetReportTable(GetReportTableEvent event) {
        executeTask(new GetReportTableProcessor(event));
    }

    @Subscribe
    public void onDashboardDeleteEvent(DashboardDeleteEvent event) {
        executeTask(new DashboardDeleteProcessor(getBaseContext(), event));
    }

    @Subscribe
    public void onDashboardUpdateEvent(DashboardUpdateEvent event) {
        executeTask(new DashboardUpdateProcessor(getBaseContext(), event));
    }

    @Subscribe
    public void onDashboardItemDeleteEvent(DashboardItemDeleteEvent event) {
        executeTask(new DashboardItemDeleteProcessor(getBaseContext(), event));
    }

    @Subscribe
    public void onDashboardCreateEvent(DashboardCreateEvent event) {
        executeTask(new DashboardCreateProcessor(event));
    }

    @Subscribe
    public void onInterpretationsSyncEvent(InterpretationSyncEvent event) {
        executeTask(new InterpretationSyncProcessor(getBaseContext()));
    }

    @Subscribe
    public void onInterpretationDeleteEvent(InterpretationDeleteEvent event) {
        executeTask(new InterpretationDeleteProcessor(getBaseContext(), event));
    }

    @Subscribe
    public void onInterpretationTextUpdateEvent(InterpretationUpdateTextEvent event) {
        executeTask(new InterpretationUpdateTextProcessor(getBaseContext(), event));
    }

    private <T> void executeTask(AsyncTask<Void, Void, T> task) {
        Log.d(TAG, "Starting: " + task.getClass().getSimpleName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }
}
