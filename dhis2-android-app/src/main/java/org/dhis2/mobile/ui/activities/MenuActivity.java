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
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.WorkService;
import org.dhis2.mobile.ui.adapters.menuNavigation.NavigationListAdapter;
import org.dhis2.mobile.ui.adapters.menuNavigation.NavigationMenuItem;
import org.dhis2.mobile.ui.fragments.AggregateReportFragment2;
import org.dhis2.mobile.ui.fragments.MyProfileFragment;
import org.dhis2.mobile.ui.fragments.SEWRFragment;

import java.util.ArrayList;

public class MenuActivity extends BaseActivity {
    public static final String TAG = "org.dhis2.mobile.ui.activities.MenuActivity";
    private static final String SELECTED_FRAGMENT_POSITION = "selectedFragmentPosition";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;

    private ArrayList<NavigationMenuItem> mMenuItems;
    private int mDrawerSelection;
    private Runnable mPendingRunnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mMenuItems = new ArrayList<NavigationMenuItem>();

        mMenuItems.add(NavigationMenuItem.AGGREGATE_REPORT);
        // mMenuItems.add(NavigationMenuItem.SINGLE_EVENT_WITHOUT_REGISTRATION);
        mMenuItems.add(NavigationMenuItem.MY_PROFILE);
        mMenuItems.add(NavigationMenuItem.LOG_OUT);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        NavigationListAdapter adapter = new NavigationListAdapter(MenuActivity.this, mMenuItems);

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectItem(position);
            }
        });

        if (mDrawerLayout != null) {
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                    R.string.drawer_open, R.string.drawer_close) {

                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    supportInvalidateOptionsMenu();

                    if (mPendingRunnable != null) {
                        new Handler().post(mPendingRunnable);
                    }

                    mPendingRunnable = null;
                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    supportInvalidateOptionsMenu();

                    mPendingRunnable = null;
                }
            };

            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            setActionBarUpEnabled(true);
        } else {
            setActionBarUpEnabled(false);
        }

        if (savedInstanceState == null) {
            mDrawerSelection = 0;
            NavigationMenuItem item = mMenuItems.get(mDrawerSelection);
            attachFragment(item);
        } else {
            mDrawerSelection = savedInstanceState.getInt(SELECTED_FRAGMENT_POSITION);
        }

        mDrawerList.setItemChecked(mDrawerSelection, true);
        setTitle(mMenuItems.get(mDrawerSelection).getTitleId());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerLayout != null && mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerLayout != null && mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_FRAGMENT_POSITION, mDrawerSelection);
        super.onSaveInstanceState(outState);
    }

    private void setActionBarUpEnabled(boolean flag) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(flag);
        getSupportActionBar().setHomeButtonEnabled(flag);
    }

    private void selectItem(int position) {
        mDrawerSelection = position;
        mDrawerList.setItemChecked(position, true);

        final NavigationMenuItem menuItem = mMenuItems.get(position);
        if (menuItem == NavigationMenuItem.LOG_OUT) {
            logOut();
            return;
        }

        setTitle(menuItem.getTitleId());
        if (mDrawerLayout != null) {
            mPendingRunnable = new Runnable() {

                @Override
                public void run() {
                    attachFragment(menuItem);
                }
            };
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            attachFragment(menuItem);
            supportInvalidateOptionsMenu();
        }
    }

    private void attachFragment(NavigationMenuItem menuItem) {
        Fragment fragment;
        if (menuItem == NavigationMenuItem.AGGREGATE_REPORT) {
            fragment = new AggregateReportFragment2();
        } else if (menuItem == NavigationMenuItem.SINGLE_EVENT_WITHOUT_REGISTRATION) {
            fragment = new SEWRFragment();
        } else {
            fragment = new MyProfileFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
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
