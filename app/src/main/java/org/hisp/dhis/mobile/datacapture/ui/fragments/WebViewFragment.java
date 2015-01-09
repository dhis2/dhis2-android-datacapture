package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.hisp.dhis.mobile.datacapture.R;

public class WebViewFragment extends BaseFragment {
    public static final String WEB_URL_EXTRA = "webViewUrlExtra";
    private WebView mWebView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mWebView = (WebView) inflater.inflate(R.layout.fragment_web_view, container, false);
        return mWebView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (getArguments() != null && getArguments().getString(WEB_URL_EXTRA) != null) {
            // new DownloadDataAsyncTask<String>(getActivity(), this, getArguments()).execute();
        }
    }

    /*
    @Override
    public String doInBackground(Context context, Bundle params) {
        Response response = HTTPClient.get(context, params.getString(WEB_URL_EXTRA));
        if (!HTTPClient.isError(response.getCode())) {
            return response.getBody();
        } else {
            return null;
        }
    }

    @Override
    public void onPostExecute(String result) {
        if (result != null) {
            mWebView.loadData(result, "text/html", "UTF-8");
        }
    } */
}
