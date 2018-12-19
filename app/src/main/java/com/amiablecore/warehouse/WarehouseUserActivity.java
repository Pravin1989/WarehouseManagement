package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.amiablecore.warehouse.utils.HttpUtils;
import com.amiablecore.warehouse.utils.Session;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class WarehouseUserActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardInward, cardOutward, cardSyncData;
    private DrawerLayout mDrawerLayout;
    private Session session;//global variable
    private static final String TAG = "WarehouseUserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        session.putToSession("userType", MainActivity.getUserType());
        session.putToSession("wh_id", MainActivity.getWhAdminId());
        session.putToSession("whUser_id", MainActivity.getWhUserId());
        setContentView(R.layout.activity_warehouse_user_dashboard);
        initViews();
    }

    private void initViews() {
        cardInward = (CardView) findViewById(R.id.cardInwardId);
        cardOutward = (CardView) findViewById(R.id.cardOutwardId);
        cardSyncData = (CardView) findViewById(R.id.cardSyncDataId);
        cardInward.setOnClickListener(this);
        cardOutward.setOnClickListener(this);
        cardSyncData.setOnClickListener(this);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        redirectToLoginPage(menuItem);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getApplicationContext(),
                "OnCartSelected : " + v.getId(),
                Toast.LENGTH_SHORT).show();
        switch (v.getId()) {
            case R.id.cardInwardId:
                startActivity(new Intent(this, WarehouseUserInwardActivity.class));
                break;
            case R.id.cardOutwardId:
                startActivity(new Intent(this, WarehouseUserOutwardActivity.class));
                break;
            case R.id.cardSyncDataId:
                startActivity(new Intent(this, WarehouseUserDataSync.class));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void redirectToLoginPage(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
                startActivity(new Intent(this, MainActivity.class));
        }
    }
}
