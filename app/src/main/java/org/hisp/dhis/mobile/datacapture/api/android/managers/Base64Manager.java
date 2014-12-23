package org.hisp.dhis.mobile.datacapture.api.android.managers;

import com.squareup.okhttp.Credentials;
import org.hisp.dhis.mobile.datacapture.api.managers.IBase64Manager;

public final class Base64Manager implements IBase64Manager {

    @Override
    public String toBase64(String username, String password) {
        return Credentials.basic(username, password);
    }
}