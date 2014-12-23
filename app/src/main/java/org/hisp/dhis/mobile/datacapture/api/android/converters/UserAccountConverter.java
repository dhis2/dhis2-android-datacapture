package org.hisp.dhis.mobile.datacapture.api.android.converters;

import com.google.gson.Gson;
import org.hisp.dhis.mobile.datacapture.api.managers.IJsonConverter;
import org.hisp.dhis.mobile.datacapture.api.models.UserAccount;

public class UserAccountConverter implements IJsonConverter<UserAccount> {

    @Override
    public UserAccount deserialize(String source) {
        if (source == null) {
            throw new IllegalArgumentException("JSON String object cannot be null");
        }

        Gson gson = new Gson();
        return gson.fromJson(source, UserAccount.class);
    }

    @Override
    public String serialize(UserAccount object) {
        if (object == null) {
            throw new IllegalArgumentException("UserAccount object cannot be null");
        }

        Gson gson = new Gson();
        return gson.toJson(object);
    }
}
