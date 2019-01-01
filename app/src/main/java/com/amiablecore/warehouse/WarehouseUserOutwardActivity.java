package com.amiablecore.warehouse;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.amiablecore.warehouse.db.DbQueryExecutor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WarehouseUserOutwardActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText txtOutwardDate, txtTotalQuantity, txtBagWeight, txtTotalWeight, txtSelectedLot;
    Button btnSave, btnCancel;
    private int mYear, mMonth, mDay;
    private static final String TAG = "Warehouse Outward";
    SearchView searchView;
    private ListView listView;
    private DbQueryExecutor databaseObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_user_outward);
        initViews();
        addListeners();
    }

    private void initViews() {
        txtOutwardDate = (EditText) findViewById(R.id.txtOutwardDate);
        txtBagWeight = (EditText) findViewById(R.id.txtSingleBagWeight);
        txtTotalWeight = (EditText) findViewById(R.id.txtTotalWeightOutward);
        txtTotalQuantity = (EditText) findViewById(R.id.txtTotalQuantity);
        txtSelectedLot = (EditText) findViewById(R.id.selectedLot);
        txtSelectedLot.setEditableFactory(Editable.Factory.getInstance());
        btnSave = (Button) findViewById(R.id.btnSaveOutward);
        btnCancel = (Button) findViewById(R.id.btnOutwardCancel);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Enter Lot Name");

        databaseObject = new DbQueryExecutor(WarehouseUserOutwardActivity.this);
        listView = (ListView) findViewById(R.id.listView);
        txtSelectedLot = (EditText) findViewById(R.id.selectedLot);

    }

    public void closeList(String item) {
        LotSearchAdapter mLotSearchAdapter = new LotSearchAdapter(WarehouseUserOutwardActivity.this, new ArrayList<LotDetails>());
        listView.setAdapter(mLotSearchAdapter);
        txtSelectedLot.setText(item);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveOutward:
                pickUpOutwardDetails();
                Toast.makeText(getApplicationContext(),
                        "Outward is Done...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnOutwardCancel:
                startActivity(new Intent(WarehouseUserOutwardActivity.this, WarehouseUserActivity.class));//Redirect to User Dashboard Page
                break;
        }
    }

    public void addListeners() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<LotDetails> dictionaryObject = databaseObject.searchLotDetailsInDB(query);
                LotSearchAdapter mLotSearchAdapter = new LotSearchAdapter(WarehouseUserOutwardActivity.this, dictionaryObject);
                listView.setAdapter(mLotSearchAdapter);
                listView.setVisibility(View.VISIBLE);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        LotDetails item = (LotDetails) parent.getItemAtPosition(position);
                        Log.i("Selected Item :", item.getLotName());
                        closeList(item.getLotName());
                    }
                });
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        txtOutwardDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.i(TAG, "Focus Comes In");
                    openDatePicker();
                }
            }
        });
    }

    private void pickUpOutwardDetails() {
        Log.i("Total Weight : ", txtTotalWeight.getText().toString());
        Log.i("Total Quantity : ", txtTotalQuantity.getText().toString());
        Log.i("Weight/Bag : ", txtBagWeight.getText().toString());
        Log.i("Outward Date : ", txtOutwardDate.getText().toString());

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText(parent.getContext(),
                "Selected Lot : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void openDatePicker() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        txtOutwardDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}
