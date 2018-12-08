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

public class WarehouseUserInwardActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText txtLotName, txtInwardDate, txtTotalQuantity, txtBagWeight, txtPhysicalAddress;
    private Button btnAdd, btnCancel, btnDatePicker;
    private Spinner cmbTrader, cmbCommodity, cmbCategory;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_user_inward);
        initViews();
    }

    private void initViews() {
        txtLotName = (EditText) findViewById(R.id.txtLotName);
        txtInwardDate = (EditText) findViewById(R.id.txtInwardDate);
        txtBagWeight = (EditText) findViewById(R.id.txtSingleBagWeight);
        txtTotalQuantity = (EditText) findViewById(R.id.txtTotalQuantity);
        txtPhysicalAddress = (EditText) findViewById(R.id.txtPhysicalAddress);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnDatePicker = (Button) findViewById(R.id.btnInwardDate);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);
        addListenerOnSpinnerItemSelection();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                Toast.makeText(getApplicationContext(),
                        "User is added In DB...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnCancel:
                startActivity(new Intent(WarehouseUserInwardActivity.this, WarehouseUserActivity.class));//Redirect to User Dashboard Page
                break;
            case R.id.btnInwardDate:
                openDatePicker();
                break;
        }
    }

    public void addListenerOnSpinnerItemSelection() {
        cmbTrader = (Spinner) findViewById(R.id.cmbTrader);
        cmbTrader.setOnItemSelectedListener(this);
        cmbCommodity = (Spinner) findViewById(R.id.cmbCommodity);
        cmbCommodity.setOnItemSelectedListener(this);
        cmbCategory = (Spinner) findViewById(R.id.cmbCategory);
        cmbCategory.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText(parent.getContext(),
                "Selected  : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
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

                        txtInwardDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}
