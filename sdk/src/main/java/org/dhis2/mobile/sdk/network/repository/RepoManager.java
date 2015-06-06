package org.dhis2.mobile.sdk.network.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;

import org.dhis2.mobile.sdk.network.models.Credentials;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;

import static com.squareup.okhttp.Credentials.basic;


public final class RepoManager {

    private RepoManager() {
        // no instances
    }

    public static DhisService createService(HttpUrl serverUrl, Credentials credentials) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(provideServerUrl(serverUrl))
                .setConverter(provideJacksonConverter())
                .setClient(provideOkClient())
                .setRequestInterceptor(provideInterceptor(credentials))
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
        return restAdapter.create(DhisService.class);
    }

    private static String provideServerUrl(HttpUrl httpUrl) {
        return httpUrl.newBuilder()
                .addPathSegment("api")
                .build().toString();
    }

    private static Converter provideJacksonConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return new JacksonConverter(mapper);
    }

    private static OkClient provideOkClient() {
        return new OkClient(new OkHttpClient());
    }

    private static AuthInterceptor provideInterceptor(Credentials credentials) {
        return new AuthInterceptor(credentials.getUsername(), credentials.getPassword());
    }

    private static class AuthInterceptor implements RequestInterceptor {
        private final String mUsername;
        private final String mPassword;

        public AuthInterceptor(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        public void intercept(RequestFacade request) {
            String base64Credentials = basic(mUsername, mPassword);
            request.addHeader("Authorization", base64Credentials);
        }
    }
}
