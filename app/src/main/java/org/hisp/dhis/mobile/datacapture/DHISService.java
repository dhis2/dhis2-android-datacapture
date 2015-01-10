package org.hisp.dhis.mobile.datacapture;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.GetReportTableEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.LoginUserEvent;
import org.hisp.dhis.mobile.datacapture.api.android.processors.DashboardSyncProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.GetReportTableProcessor;
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
        (new LoginUserProcessor(getBaseContext(), event)).execute();
    }

    @Subscribe
    public void onDashboardSyncEvent(DashboardSyncEvent event) {
        (new DashboardSyncProcessor(getBaseContext())).execute();
    }

    @Subscribe
    public void onGetReportTable(GetReportTableEvent event) {
        (new GetReportTableProcessor(event)).execute();
    }
}
