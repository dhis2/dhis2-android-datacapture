package org.hisp.dhis.mobile.datacapture.api.android.managers;

import org.hisp.dhis.mobile.datacapture.api.managers.INetworkManager;
import org.hisp.dhis.mobile.datacapture.api.network.Header;
import org.hisp.dhis.mobile.datacapture.api.network.Request;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.api.network.RestMethod;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public final class NetworkManager implements INetworkManager {
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final long TIME_OUT = 3000;

    private static com.squareup.okhttp.Request buildOkRequest(Request request) {
        if (request == null) {
            throw new IllegalArgumentException("Request object cannot be null");
        }

        com.squareup.okhttp.Request.Builder okRequestBuilder = new com.squareup.okhttp.Request.Builder();
        if (request.getHeaders() != null) {
            for (Header header : request.getHeaders()) {
                okRequestBuilder.addHeader(header.getName(), header.getValue());
            }
        }

        String url = request.getUrl();
        RestMethod method = request.getMethod();

        String body = new String();
        if (request.getBody() != null) {
            body = new String(request.getBody());
        }
        com.squareup.okhttp.RequestBody requestBody = com.squareup.okhttp.RequestBody.create(JSON, body);

        if (RestMethod.PUT.equals(method)) {
            return okRequestBuilder.put(requestBody).url(url).build();
        } else if (RestMethod.PATCH.equals(method)) {
            return okRequestBuilder.patch(requestBody).url(url).build();
        } else if (RestMethod.POST.equals(method)) {
            return okRequestBuilder.post(requestBody).url(url).build();
        } else if (RestMethod.DELETE.equals(method)) {
            return okRequestBuilder.delete().url(url).build();
        } else if (RestMethod.HEAD.equals(method)) {
            return okRequestBuilder.head().url(url).build();
        } else {
            return okRequestBuilder.get().url(url).build();
        }
    }

    private static Response buildResponse(com.squareup.okhttp.Response okResponse) throws IOException {
        if (okResponse == null) {
            throw new IllegalArgumentException("Response object cannot be null");
        }

        com.squareup.okhttp.Headers okHeaders = okResponse.headers();
        ArrayList<Header> headers = new ArrayList<Header>();
        if (okHeaders != null) {
            for (String headerName : okHeaders.names()) {
                headers.add(new Header(headerName, okHeaders.get(headerName)));
            }
        }

        return new Response(
                okResponse.request().urlString(),
                okResponse.code(),
                okResponse.message(),
                headers,
                okResponse.body().bytes()
        );
    }

    @Override
    public Response request(Request request) throws IOException {
        OkHttpClient client = new OkHttpClient();
        client.setFollowSslRedirects(true);
        client.setConnectTimeout(TIME_OUT, TimeUnit.MILLISECONDS);

        com.squareup.okhttp.Request okRequest = buildOkRequest(request);
        com.squareup.okhttp.Response okResponse = client.newCall(okRequest).execute();

        return buildResponse(okResponse);
    }
}
