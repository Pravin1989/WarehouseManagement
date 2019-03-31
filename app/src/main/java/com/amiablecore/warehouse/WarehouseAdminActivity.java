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
import android.widget.Toast;

import com.amiablecore.warehouse.utils.Session;

public class WarehouseAdminActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardWarehouseUser, cardTrader, cardCommodity;
    private DrawerLayout mDrawerLayout;
    private static final String TAG = "Warehouse Admin : ";
    private Session session;//global variable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        session.putToSession("userType", MainActivity.getUserType());
        session.putToSession("wh_id", MainActivity.getWhAdminId());
        Log.i(TAG, MainActivity.getUserType());
        Log.i(TAG, MainActivity.getWhAdminId());
        setContentView(R.layout.activity_warehouse_admin);
        initViews();
    }

    private void initViews() {
        cardWarehouseUser = (CardView) findViewById(R.id.cardWHUserId);
        cardTrader = (CardView) findViewById(R.id.cardTraderId);
        cardCommodity = (CardView) findViewById(R.id.cardItemId);
        cardWarehouseUser.setOnClickListener(this);
        cardTrader.setOnClickListener(this);
        cardCommodity.setOnClickListener(this);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        redirectToLogin(menuItem);
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
            case R.id.cardWHUserId:
                startActivity(new Intent(this, WarehouseUserForm.class));
                break;
            case R.id.cardTraderId:
                startActivity(new Intent(this, WarehouseTraderForm.class));
                break;
            case R.id.cardItemId:
                startActivity(new Intent(this, WarehouseAdminItemActivity.class));
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

    public void redirectToLogin(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
                session.clearSession();
                clearSession();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.nav_removeItem:
                startActivity(new Intent(this, WarehouseAdminRemoveItemActivity.class));
        }
    }

    public void clearSession() {
        Intent i = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //i.addCategory(Intent.CATEGORY_HOME);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(i);
    }

}