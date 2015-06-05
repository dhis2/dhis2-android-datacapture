package org.dhis2.mobile.sdk.network.retrofit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;

import org.dhis2.mobile.sdk.network.models.Credentials;
import org.dhis2.mobile.sdk.network.tasks.NetworkManager;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;


public final class RetrofitManager {

    public static DhisService buildService() {
        String serverUrl = NetworkManager.getInstance().getServerUri()
                .buildUpon().appendEncodedPath("api").toString();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(serverUrl)
                .setConverter(provideJacksonConverter())
                .setClient(provideOkClient())
                .setRequestInterceptor(new AuthInterceptor())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return restAdapter.create(DhisService.class);
    }

    private static String provideServerUrl() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("apps.dhis2.org/demo")
                .addPathSegment("api")
                .build()
                .toString();
    }

    private static Converter provideJacksonConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        return new JacksonConverter(mapper);
    }

    private static OkClient provideOkClient() {
        OkHttpClient okHttpClient =  new OkHttpClient();
        return new OkClient(okHttpClient);
    }

    private static class AuthInterceptor implements RequestInterceptor {

        @Override
        public void intercept(RequestFacade request) {
            Credentials credentials = NetworkManager.getInstance()
                    .getCredentials();
            String base64Credentials = NetworkManager.getInstance()
                    .getBase64Manager().toBase64(credentials);
            request.addHeader("Authorization", base64Credentials);
        }
    }
}
