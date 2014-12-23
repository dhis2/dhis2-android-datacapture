package org.hisp.dhis.mobile.datacapture;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class BusProvider {
    private static Bus mBus;

    private BusProvider() { }

    public static Bus getInstance() {
        if (mBus == null) {
            mBus = new Bus(ThreadEnforcer.MAIN);
        }

        return mBus;
    }
}
