package com.myfinance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.myfiance.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar _toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(_toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, _toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        MenuItem mnu=navigationView.getMenu().getItem(0);

        if(mnu==null)
            return;

        mnu.setChecked(true);
        onNavigationItemSelected(mnu);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fTran = fragmentManager.beginTransaction();
        Fragment fragment = null;

        if (id == R.id.nav_expence) {
            fragment = new ExpenseFragment();
            _toolbar.setTitle(getString(R.string.title_section1));
        } else if (id == R.id.nav_category) {
            fragment = new CategoryFragment();
            _toolbar.setTitle(getString(R.string.title_section2));
        } else if (id == R.id.nav_totals) {
            fragment = new TotalsFragment();
            _toolbar.setTitle(getString(R.string.title_section3));
        }else if (id == R.id.nav_about) {
            fragment = new AboutFragment();
            _toolbar.setTitle(getString(R.string.title_section4));
        }

        fTran.replace(R.id.container, fragment);
        fTran.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
