package com.amiablecore.warehouse;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amiablecore.warehouse.utils.HttpUtils;
import com.amiablecore.warehouse.utils.Session;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Calendar;

public class WarehouseUserInwardActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText txtLotName, txtInwardDate, txtTotalQuantity, txtBagWeight, txtPhysicalAddress;
    private Button btnAdd, btnCancel;
    private Spinner cmbTrader, cmbCommodity, cmbCategory;
    private int mYear, mMonth, mDay;
    private static final String TAG = "Warehouse Inward";
    private Session session;//global variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        setContentView(R.layout.activity_warehouse_user_inward);
        initViews();
        retrieveCommodities();
        retrieveCategories();
    }

    private void initViews() {
        txtLotName = (EditText) findViewById(R.id.txtLotName);
        txtInwardDate = (EditText) findViewById(R.id.txtInwardDate);
        txtBagWeight = (EditText) findViewById(R.id.txtSingleBagWeight);
        txtTotalQuantity = (EditText) findViewById(R.id.txtTotalQuantity);
        txtPhysicalAddress = (EditText) findViewById(R.id.txtPhysicalAddress);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        addListenerOnSpinnerItemSelection();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                Toast.makeText(getApplicationContext(),
                        "User is added In DB...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WarehouseUserInwardActivity.this, WarehouseUserActivity.class));//Redirect to User Dashboard Page
                break;
            case R.id.btnCancel:
                startActivity(new Intent(WarehouseUserInwardActivity.this, WarehouseUserActivity.class));//Redirect to User Dashboard Page
                break;
        }
    }

    public void addListenerOnSpinnerItemSelection() {
        cmbTrader = (Spinner) findViewById(R.id.cmbTraderNames);
        cmbTrader.setOnItemSelectedListener(this);
        cmbCommodity = (Spinner) findViewById(R.id.cmbCommodity);
        cmbCommodity.setOnItemSelectedListener(this);
        cmbCategory = (Spinner) findViewById(R.id.cmbCategory);
        cmbCategory.setOnItemSelectedListener(this);
        txtInwardDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.i(TAG, "Focus Comes In");
                    openDatePicker();
                }
            }
        });
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

    public void retrieveCommodities() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/commodity/retrieveCommodities/" + session.getFromSession("wh_id");
                    try {
                        HttpURLConnection conn = HttpUtils.getGetConnection(urlAdress);

                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                        if (conn.getResponseCode() == 200) {
                            BufferedReader in = null;
                            StringBuilder answer = new StringBuilder(100000);
                            try {
                                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String inputLine;
                            try {
                                while ((inputLine = in.readLine()) != null) {
                                    answer.append(inputLine);
                                    answer.append("\n");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.i(TAG, answer.toString());
                            JSONArray obj = new JSONArray(answer.toString());
                            Log.i(TAG, obj.toString());
                            String[] comm = new String[obj.length()];
                            for (int i = 0; i < obj.length(); i++) {
                                comm[i] = obj.getJSONObject(i).get("commodityName").toString();
                            }
                            updateCommodities(comm);
                        }
                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateCommodities(String[] list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbCommodity.setAdapter(adapter);
    }


    public void retrieveCategories() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/category/retrieveCategories/" + session.getFromSession("wh_id");
                    try {
                        HttpURLConnection conn = HttpUtils.getGetConnection(urlAdress);

                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                        if (conn.getResponseCode() == 200) {
                            BufferedReader in = null;
                            StringBuilder answer = new StringBuilder(100000);
                            try {
                                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String inputLine;
                            try {
                                while ((inputLine = in.readLine()) != null) {
                                    answer.append(inputLine);
                                    answer.append("\n");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.i(TAG, answer.toString());
                            JSONArray obj = new JSONArray(answer.toString());
                            Log.i(TAG, obj.toString());
                            String[] comm = new String[obj.length()];
                            for (int i = 0; i < obj.length(); i++) {
                                comm[i] = obj.getJSONObject(i).get("categoryName").toString();
                            }
                            updateCategories(comm);
                        }
                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateCategories(String[] list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbCategory.setAdapter(adapter);
    }

}
