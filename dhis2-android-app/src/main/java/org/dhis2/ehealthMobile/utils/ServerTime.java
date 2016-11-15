package org.dhis2.ehealthMobile.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;

/**
 * Created by George on 8/25/16.
 */
//only logs out the time from the server when called for now.
public class ServerTime extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            TimeTCPClient client = new TimeTCPClient();
            try {
                // Set timeout of 60 seconds
                client.setDefaultTimeout(60000);
                // Connecting to time server
                // Other time servers can be found at : http://tf.nist.gov/tf-cgi/servers.cgi#
                client.connect("time-nw.nist.gov");
                System.out.println(client.getDate());
                Log.d("Time", client.getDate()+"");

            } finally {
                client.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
