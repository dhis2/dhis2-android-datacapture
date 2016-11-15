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

package org.dhis2.ehealthMobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.dhis2.ehealthMobile.processors.OfflineDataProcessor;

public class NetworkStateReceiver extends BroadcastReceiver {
	private static final String TAG = NetworkStateReceiver.class.getSimpleName();

	@Override
	public void onReceive(final Context context, final Intent intent) {
		Log.i(TAG, "Network connectivity change");
	
		if (intent.getExtras() != null) {
			final ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

			if (ni != null && ni.isConnectedOrConnecting()) {
				Log.i(TAG, "Network " + ni.getTypeName() + " connected");
				if (!OfflineDataProcessor.isRunning()) {
					Intent task = new Intent(context, WorkService.class);
					task.putExtra(WorkService.METHOD, WorkService.METHOD_OFFLINE_DATA_UPLOAD);
					context.startService(task);
				}
			} else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,
					Boolean.FALSE)) {
				Log.d(TAG, "There's no network connectivity");
			}
		}
	}
}
