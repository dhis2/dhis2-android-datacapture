package org.hisp.dhis.mobile.datacapture.utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectHolder {
    private static ObjectHolder mStateHolder;

    private Map<Integer, WeakReference<Object>> mData;
    private AtomicInteger mId;

    private ObjectHolder() {
        mData = new HashMap<>();
        mId = new AtomicInteger();
    }

    public Object pop(int id) {
        WeakReference<Object> reference = mData.get(id);
        mData.remove(id);
        return reference.get();
    }

    public int put(Object object) {
        int id = mId.getAndIncrement();
        WeakReference<Object> reference = new WeakReference<>(object);
        mData.put(id, reference);
        return id;
    }

    public static ObjectHolder getInstance() {
        if (mStateHolder == null) {
            mStateHolder = new ObjectHolder();
        }

        return mStateHolder;
    }
}
