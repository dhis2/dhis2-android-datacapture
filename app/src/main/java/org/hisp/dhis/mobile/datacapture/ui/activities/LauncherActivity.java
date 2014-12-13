package org.hisp.dhis.mobile.datacapture.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.hisp.dhis.mobile.datacapture.R;


public class LauncherActivity extends ActionBarActivity {
    private Button mLaunchMenuActivity;
    private Button mLaunchLoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        mLaunchLoginActivity = (Button) findViewById(R.id.launch_login_activity_button);
        mLaunchMenuActivity = (Button) findViewById(R.id.launch_menu_activity_button);

        mLaunchLoginActivity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mLaunchMenuActivity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LauncherActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }
}
