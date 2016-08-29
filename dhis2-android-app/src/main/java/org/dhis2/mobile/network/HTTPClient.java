/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.dhis2.mobile.R;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Does HTTP requests.
 */

public class HTTPClient {
    private static final int CONNECTION_TIME_OUT = 1500;

	private HTTPClient() { }

    /**
     * Does a get call to the DHIS2 server
     * @param server String The URL for the DHIS2 instance set by the user.
     * @param creds String The users credentials aka username and password
     * @return Response The response message from the server. This is parsed in as a new Response object.
     * @see Response
     */
	public static Response get(String server, String creds) {
        Log.i("GET", server);
		int code = -1;
		String body = "";

		HttpURLConnection connection = null;
		try {
			URL url = new URL(server);
			connection = (HttpURLConnection) url.openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.setConnectTimeout(CONNECTION_TIME_OUT);
			connection.setRequestProperty("Authorization", "Basic " + creds);
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.connect();

			code = connection.getResponseCode();
			body = readInputStream(connection.getInputStream());
		} catch (MalformedURLException e) {
            code = HttpURLConnection.HTTP_NOT_FOUND;
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            code = HttpURLConnection.HTTP_NOT_FOUND;
		} catch (IOException one) {
			one.printStackTrace();
			try {
				if (connection != null) {
					code = connection.getResponseCode();
				}
			} catch (IOException two) {
				two.printStackTrace();
			}
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
        Log.i(Integer.toString(code), body);
        return (new Response(code, body));
	}

    /**
     *
     * @param server String The URL of the DHIS2 instance set by the user.
     * @param creds String The users credentials aka username and password
     * @param data String The data to be posted to the DHIS2 server.
     * @return Response The response from the DHIS2 server parsed in a new Response object
     * @see Response
     */

	public static Response post(String server, String creds, String data) {
        Log.i("POST", server);
		int code = -1;
		String body = "";

		HttpURLConnection connection = null;
		try {
			URL url = new URL(server);
			connection = (HttpURLConnection) url.openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.setConnectTimeout(CONNECTION_TIME_OUT);
			connection.setRequestProperty("Authorization", "Basic " + creds);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			OutputStream output = connection.getOutputStream();
			output.write(data.getBytes());
			output.close();
			
			connection.connect();
			code = connection.getResponseCode();
			body = readInputStream(connection.getInputStream());
		} catch (MalformedURLException e) {
			code = HttpURLConnection.HTTP_NOT_FOUND;
			e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            code = HttpURLConnection.HTTP_NOT_FOUND;
		} catch (IOException one) {
			one.printStackTrace();
			try {
				if (connection != null) {
					code = connection.getResponseCode();
				}
			} catch (IOException two) {
				two.printStackTrace();
			}
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
        Log.i(Integer.toString(code), body);
		return (new Response(code, body));
	}

    /**
     * Converts an InputStream of bytes to a String.
     * @param stream InputStream
     * @return String
     * @throws IOException
     */
	private static String readInputStream(InputStream stream)
			throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		try {
			StringBuilder builder = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append('\n');
			}

			return builder.toString();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isError(int code) {
		return code != HttpURLConnection.HTTP_OK;
	}
	
    public static String getErrorMessage(Context context, int code) {
    	switch (code) {
    	case HttpURLConnection.HTTP_UNAUTHORIZED:
    		return context.getString(R.string.wrong_username_password);
    	case HttpURLConnection.HTTP_NOT_FOUND:
    		return context.getString(R.string.wrong_url);
        case HttpURLConnection.HTTP_MOVED_PERM:
            return context.getString(R.string.wrong_url);
    	default:
    		return context.getString(R.string.try_again);
    	}	
    }
}
