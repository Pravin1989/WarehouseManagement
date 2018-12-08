package com.amiablecore.warehouse;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class WarehouseUserOutwardActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText txtOutwardDate, txtTotalQuantity, txtBagWeight;
    Button btnSave, btnCancel, btnDatePicker;
    private Spinner cmbLotTypes;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_user_outward);
        initViews();
    }

    private void initViews() {
        txtOutwardDate = (EditText) findViewById(R.id.txtOutwardDate);
        txtBagWeight = (EditText) findViewById(R.id.txtSingleBagWeight);
        txtTotalQuantity = (EditText) findViewById(R.id.txtTotalQuantity);
        btnSave = (Button) findViewById(R.id.btnSaveOutward);
        btnCancel = (Button) findViewById(R.id.btnOutwardCancel);
        btnDatePicker = (Button) findViewById(R.id.btnOutwardDate);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);
        addListenerOnSpinnerItemSelection();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveOutward:
                Toast.makeText(getApplicationContext(),
                        "Outward is Done...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnOutwardCancel:
                startActivity(new Intent(WarehouseUserOutwardActivity.this, WarehouseUserActivity.class));//Redirect to User Dashboard Page
                break;
            case R.id.btnOutwardDate:
                openDatePicker();
                break;
        }
    }

    public void addListenerOnSpinnerItemSelection() {
        cmbLotTypes = (Spinner) findViewById(R.id.cmbLots);
        cmbLotTypes.setOnItemSelectedListener(this);
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
