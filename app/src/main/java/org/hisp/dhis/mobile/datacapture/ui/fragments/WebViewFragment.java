package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.GetReportTableEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnGotReportTableEvent;

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
            GetReportTableEvent event = new GetReportTableEvent();
            event.setUrl(getArguments().getString(WEB_URL_EXTRA));
            BusProvider.getInstance().post(event);
        }
    }

    @Subscribe
    public void onGotReportTable(OnGotReportTableEvent event) {
        if (event.getApiException() == null) {
            mWebView.loadData(event.getReportTable(), "text/html", "UTF-8");
        }
    }
}
