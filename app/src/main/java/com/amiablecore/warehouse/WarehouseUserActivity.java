package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amiablecore.warehouse.utils.Session;

public class WarehouseUserActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardInward, cardOutward, cardcardInCompleteTask;
    private DrawerLayout mDrawerLayout;
    private Session session;//global variable
    private static final String TAG = "WarehouseUserActivity";
    boolean doubleBackToExitPressedOnce = false;

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
        cardcardInCompleteTask = (CardView) findViewById(R.id.cardInCompleteTaskId);
        cardInward.setOnClickListener(this);
        cardOutward.setOnClickListener(this);
        cardcardInCompleteTask.setOnClickListener(this);

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
        switch (v.getId()) {
            case R.id.cardInwardId:
                startActivity(new Intent(this, WarehouseUserInwardActivity.class));
                break;
            case R.id.cardOutwardId:
                startActivity(new Intent(this, WarehouseUserOutwardActivity.class));
                break;
            case R.id.cardInCompleteTaskId:
                startActivity(new Intent(this, WarehouseInCompleteActivity.class));
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
                clearSession();
                startActivity(new Intent(this, MainActivity.class));
        }
    }

    public void clearSession() {
        session.clearSession();
        Intent i = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
        i.addCategory(Intent.CATEGORY_HOME);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            clearSession();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
