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

package org.dhis2.mobile.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import org.dhis2.mobile.R;
import org.dhis2.mobile.WorkService;
import org.dhis2.mobile.ui.fragments.AggregateReportFragment2;
import org.dhis2.mobile.ui.fragments.MyProfileFragment;

import static org.dhis2.mobile.utils.Preconditions.checkNotNull;

public class MenuActivity extends BaseActivity
        implements OnNavigationItemSelectedListener, DrawerListener {

    // layout
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Delaying attachment of fragment
    // in order to avoid animation lag
    private Runnable pendingRunnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            drawerLayout.addDrawerListener(this);
        }

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            navigationView.inflateMenu(R.menu.menu_drawer_default);
            navigationView.setNavigationItemSelectedListener(this);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleNavigationDrawer();
                }
            });
        }

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.drawer_item_aggregate_report);
            toolbar.setTitle(R.string.aggregate_reporting);
            attachFragment(new AggregateReportFragment2());
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_item_aggregate_report: {
                attachFragmentDelayed(new AggregateReportFragment2());
                break;
            }
            case R.id.drawer_item_profile: {
                attachFragmentDelayed(new MyProfileFragment());
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
    public void onDrawerOpened(View drawerView) {
        pendingRunnable = null;
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (pendingRunnable != null) {
            new Handler().post(pendingRunnable);
        }

        pendingRunnable = null;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        // stub implementation
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        // stub implementation
    }

    protected void attachFragmentDelayed(final Fragment fragment) {
        checkNotNull(fragment, "Fragment must not be null");

        pendingRunnable = new Runnable() {
            @Override
            public void run() {
                attachFragment(fragment);
            }
        };
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
}
