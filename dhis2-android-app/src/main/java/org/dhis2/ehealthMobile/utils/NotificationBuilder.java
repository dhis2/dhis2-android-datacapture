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

package org.dhis2.ehealthMobile.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import org.dhis2.ehealthMobile.R;
import org.dhis2.ehealthMobile.ui.activities.LauncherActivity;
import org.dhis2.ehealthMobile.ui.activities.MenuActivity;

public class NotificationBuilder {
	public static final String NOTIFICATION_TITLE = "notificationTitle";
	public static final String NOTIFICATION_MESSAGE = "notificationMessage";
	private NotificationBuilder() { }
	
	public static void fireNotification(Context context, String title, String message) {
		long[] vibrationPattern = new long[] {0,1000};
		fireNotification(context, title, message, vibrationPattern);
	}

	public static void fireNotification(Context context, String title, String message, long[] vibrationPattern) {
		int id = (title + message).hashCode();
		Intent notificationIntent = new Intent(context, LauncherActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context,
				id, notificationIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		Notification notification = buildNotification(context, contentIntent, title, message, vibrationPattern );

		showNotification(context, notification, id);
	}

	private static Notification buildNotification(Context context, PendingIntent contentIntent, String title, String message, long[] vibrationPattern){
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		return new  NotificationCompat.Builder(context)
				.setContentIntent(contentIntent)
				.setContentTitle(title)
				.setContentText(message)
				.setSmallIcon(R.drawable.ic_notification)
				.setAutoCancel(true)
				.setSound(soundUri)
				.setVibrate(vibrationPattern)
				.build();
	}

	private static void showNotification(Context context, Notification notification, int id){
		//Define sound URI
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		notification.flags |=  Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(id, notification);
	}

}
