package org.dhis2.mobile.sdk.network.retrofit;

import org.dhis2.mobile.sdk.persistence.models.UserAccount;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

/**
 * Created by arazabishov on 6/5/15.
 */
public interface UserService {

    @GET("{serverAddress}/me")
    UserAccount loginUser(@Path("serverAddress") String serverAddress,
                          @Header("Authorization") String credentials);
}
