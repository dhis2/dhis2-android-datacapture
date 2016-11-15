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

package org.dhis2.ehealthMobile.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.dhis2.ehealthMobile.R;
import org.dhis2.ehealthMobile.WorkService;
import org.dhis2.ehealthMobile.io.handlers.UserAccountHandler;
import org.dhis2.ehealthMobile.ui.fragments.AggregateReportFragment;
import org.dhis2.ehealthMobile.ui.fragments.MyProfileFragment;
import org.dhis2.ehealthMobile.utils.LetterAvatar;
import org.dhis2.ehealthMobile.utils.NotificationBuilder;
import org.dhis2.ehealthMobile.utils.PrefUtils;
import org.dhis2.ehealthMobile.utils.TextFileUtils;

public class MenuActivity extends BaseActivity implements OnNavigationItemSelectedListener {
    private static final String STATE_TOOLBAR_TITLE = "state:toolbarTitle";

    // layout
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            navigationView.inflateMenu(R.menu.menu_drawer);
            navigationView.setNavigationItemSelectedListener(this);
            setupUserDetailsForSideNav();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleNavigationDrawer();
                }
            });
        }

        if (savedInstanceState == null) {
            onNavigationItemSelected(navigationView.getMenu()
                    .findItem(R.id.drawer_item_aggregate_report));
        } else if (savedInstanceState.containsKey(STATE_TOOLBAR_TITLE) && toolbar != null) {
            toolbar.setTitle(savedInstanceState.getString(STATE_TOOLBAR_TITLE));
        }


        if(getIntent().hasExtra(NotificationBuilder.NOTIFICATION_TITLE)
                && getIntent().hasExtra(NotificationBuilder.NOTIFICATION_MESSAGE)){
            String title = getIntent().getStringExtra(NotificationBuilder.NOTIFICATION_TITLE);
            String message = getIntent().getStringExtra(NotificationBuilder.NOTIFICATION_MESSAGE);
            showNotificationDialog(title, message);

        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_item_aggregate_report: {
                attachFragment(new AggregateReportFragment());
                break;
            }
            case R.id.drawer_item_profile: {
                attachFragment(new MyProfileFragment());
                break;
            }
            case R.id.drawer_item_logout: {
                logOut();
                break;
            }
        }

        toolbar.setTitle(item.getTitle());
        navigationView.setCheckedItem(R.id.drawer_item_aggregate_report);
        drawerLayout.closeDrawers();

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (toolbar != null) {
            outState.putString(STATE_TOOLBAR_TITLE, toolbar.getTitle().toString());
        }

        super.onSaveInstanceState(outState);
    }

    protected void attachFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    protected void toggleNavigationDrawer() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            drawerLayout.openDrawer(navigationView);
        }
    }

    private void logOut() {
        // start service in order to remove data
        Intent removeDataIntent = new Intent(MenuActivity.this, WorkService.class);
        removeDataIntent.putExtra(WorkService.METHOD, WorkService.METHOD_REMOVE_ALL_DATA);
        startService(removeDataIntent);

        // start LoginActivity
        Intent startLoginActivity = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(startLoginActivity);
        overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
        finish();
    }

    private void setupUserDetailsForSideNav(){
        View navHeader = navigationView.getHeaderView(0);
        TextView username = (TextView) navHeader.findViewById(R.id.side_nav_username);
        username.setText(PrefUtils.getUserName(getApplicationContext()));
        String userAccountDetails = TextFileUtils.readTextFile(getApplicationContext(),
                TextFileUtils.Directory.ROOT,
                TextFileUtils.FileNames.ACCOUNT_INFO);
        //Initial(s) used if user account details is unavailable
        String initials = getFirstLetterInUpperCase(username.getText().toString());

        if(userAccountDetails != null){
            JsonObject info = (new JsonParser()).parse(userAccountDetails).getAsJsonObject();
            String email = UserAccountHandler.getString(info.getAsJsonPrimitive(UserAccountHandler.EMAIL));
            String firstName = UserAccountHandler.getString(info.getAsJsonPrimitive(UserAccountHandler.FIRST_NAME));
            String surname = UserAccountHandler.getString(info.getAsJsonPrimitive(UserAccountHandler.SURNAME));
            TextView mEmail = (TextView) navHeader.findViewById(R.id.side_nav_email);
            mEmail.setText(email);

            initials = getUserInitials(firstName, surname);

        }

        ImageView avatar = (ImageView) navHeader.findViewById(R.id.side_nav_photo);
        Bitmap bitmap = Bitmap.createBitmap(150, 150,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        LetterAvatar letterAvatar = new LetterAvatar(getApplicationContext(), Color.parseColor("#FF0000"), initials, 10 );
        letterAvatar.draw(canvas);
        avatar.setImageBitmap(bitmap);

    }

    private String getFirstLetterInUpperCase(String string){
        return string.substring(0,1).toUpperCase();
    }

    private String getUserInitials(String firstName, String surname){
        return getFirstLetterInUpperCase(firstName) + getFirstLetterInUpperCase(surname);
    }

    private void showNotificationDialog(String title, String message){
        String confirmationText = getString(R.string.form_completion_dialog_confirmation);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, confirmationText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
