package org.dhis2.ehealthMobile.ui.fragments;

import android.content.Context;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.dhis2.ehealthMobile.R;
import org.dhis2.ehealthMobile.network.HTTPClient;
import org.dhis2.ehealthMobile.network.NetworkUtils;
import org.dhis2.ehealthMobile.network.Response;
import org.dhis2.ehealthMobile.network.URLConstants;
import org.dhis2.ehealthMobile.utils.PrefUtils;
import org.dhis2.ehealthMobile.utils.TextFileUtils;
import org.dhis2.ehealthMobile.utils.ToastManager;
import org.dhis2.ehealthMobile.utils.ViewUtils;

import java.net.HttpURLConnection;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginFragment extends BaseFragment {

	public interface LoginFragmentListener {
		void onLoginSuccess();
		void onLoginError(int code);
	}

	@BindView(R.id.login_button) protected Button mLoginButton;
	@BindView(R.id.username) protected EditText mUsername;
	@BindView(R.id.password) protected EditText mPassword;
	@BindView(R.id.dhis2_logo) protected ImageView mDhis2Logo;

	// Disabled serverUrl EditText in order to allow
	// developers to build app with custom server address
	@BindView(R.id.server_url) protected EditText mServerUrl;
	@BindView(R.id.progress_bar) protected ProgressBar mProgressBar;

	private LoginFragmentListener listener;
	private LoginTask loginTask;

	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable edit) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			checkEditTextFields();
		}
	};

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.listener = (LoginFragmentListener) context;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mProgressBar.setVisibility(View.GONE);

		mServerUrl.addTextChangedListener(textWatcher);
		mUsername.addTextChangedListener(textWatcher);
		mPassword.addTextChangedListener(textWatcher);

		// Call method in order to check the fields
		// and change state of login button
		checkEditTextFields();

		if (loginTask != null) {
			ViewUtils.hideAndDisableViews(mDhis2Logo, mServerUrl, mUsername, mPassword, mLoginButton);
			//ViewUtils.hideAndDisableViews(mDhis2Logo, mUsername, mPassword, mLoginButton);
			showProgress();

		}
	}

	// Activates *login button*,
	// if all necessary fields are full
	private void checkEditTextFields() {
		String tempUrl = mServerUrl.getText().toString();
		//Server address will be retrieved from .xml resources
		//String tempUrl = getString(R.string.default_server_url);
		String tempUsername = mUsername.getText().toString();
		String tempPassword = mPassword.getText().toString();

		if (tempUrl.equals("") || tempUsername.equals("") || tempPassword.equals("")) {
			mLoginButton.setEnabled(false);
		} else {
			mLoginButton.setEnabled(true);
		}
	}

	@OnClick(R.id.login_button)
	public void startLogin() {

		Context context = getActivity();
		if(context == null) return;
		context = context.getApplicationContext();

		String tmpServer = mServerUrl.getText().toString();
		//Server address will be retrieved from .xml resources
		//String tmpServer = getString(R.string.default_server_url);

		String user = mUsername.getText().toString();
		String pass = mPassword.getText().toString();
		String pair = String.format("%s:%s", user, pass);

		if (NetworkUtils.checkConnection(context)) {
			showProgress();

			String server = tmpServer + (tmpServer.endsWith("/") ? "" : "/");
			String creds = Base64.encodeToString(pair.getBytes(), Base64.NO_WRAP);

			loginTask = new LoginTask(context, server, user, creds);
		} else {
			showMessage(getString(R.string.check_connection));
		}
	}

	public void showMessage(String message) {
		Context context = getActivity();
		if(context == null) return;

		ToastManager.makeToast(context, message, Toast.LENGTH_LONG).show();
	}

	private void showProgress() {
		Context context = getActivity();
		if(context == null) return;

		ViewUtils.perfomOutAnimation(context, R.anim.out_up, true,
				mDhis2Logo, mServerUrl, mUsername, mPassword, mLoginButton);
		ViewUtils.enableViews(mProgressBar);
	}

	public void hideProgress() {
		Context context = getActivity();
		if(context == null) return;

		ViewUtils.perfomInAnimation(
				context, R.anim.in_down,
				mDhis2Logo, mServerUrl, mUsername, mPassword, mLoginButton);
		ViewUtils.hideAndDisableViews(mProgressBar);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_login;
	}

	private class LoginTask extends AsyncTask<String, Void, Integer> {

		private Context context;

		public LoginTask(Context context, String server, String user, String credentials){
			this.context = context;
			execute(server, user, credentials);
		}

		@Override
		protected Integer doInBackground(String... loginData) {
			String server = loginData[0];
			String username = loginData[1];
			String credentials = loginData[2];

			String url = prepareUrl(server, credentials);

			if(!URLUtil.isValidUrl(url))
				return HttpURLConnection.HTTP_NOT_FOUND;

			Response resp = tryToLogIn(url, credentials);

			int resultCode = resp.getCode();

			if(!HTTPClient.isError(resultCode)){
				PrefUtils.initAppData(context, credentials, username, url);
				TextFileUtils.writeTextFile(context, TextFileUtils.Directory.ROOT,
						TextFileUtils.FileNames.ACCOUNT_INFO, resp.getBody());
			}

			return resultCode;
		}

		@Override
		protected void onPostExecute(Integer resultCode) {
			if(resultCode == null) resultCode = 0;

			if(listener != null) {
				if (HTTPClient.isError(resultCode)) {
					listener.onLoginError(resultCode);
				} else {
					listener.onLoginSuccess();
				}
			}

			loginTask = null;
		}

		private String prepareUrl(String initialUrl, String creds) {
			if (initialUrl.startsWith(HTTPClient.HTTPS_SCHEME) || initialUrl.startsWith(HTTPClient.HTTP_SCHEME)) {
				return initialUrl;
			}

			// try to use https
			Response response = tryToLogIn(HTTPClient.HTTPS_SCHEME + initialUrl, creds);
			if (response.getCode() != HttpURLConnection.HTTP_MOVED_PERM) {
				return HTTPClient.HTTPS_SCHEME + initialUrl;
			} else {
				return HTTPClient.HTTP_SCHEME + initialUrl;
			}
		}

		private Response tryToLogIn(String server, String creds) {
			String url = server + URLConstants.API_USER_ACCOUNT_URL;
			return HTTPClient.get(url, creds);
		}
	}
}