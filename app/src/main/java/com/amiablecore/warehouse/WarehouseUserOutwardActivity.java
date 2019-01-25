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

import com.amiablecore.warehouse.beans.Inward;
import com.amiablecore.warehouse.beans.Outward;
import com.amiablecore.warehouse.utils.FieldsValidator;
import com.amiablecore.warehouse.utils.HttpUtils;
import com.amiablecore.warehouse.utils.Session;
import com.amiablecore.warehouse.utils.StaticConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseUserOutwardActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText txtOutwardDate, txtTotalQuantity, txtBagWeight, txtTotalWeight, txtSelectedLot;
    Button btnSave, btnCancel;
    private int mYear, mMonth, mDay;
    private static final String TAG = "Warehouse Outward";
    private SearchView searchView;
    private ListView listView;
    private String searchQuery;
    private Integer inwardId;
    private Map<String, Integer> inwardMap;
    private List<Inward> inwardList;
    private Inward inward;
    private Outward outward;
    private boolean outwardDone;

    public static Inward getBackupInward() {
        return backupInward;
    }

    private static Inward backupInward;
    private Session session;//global variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
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

        listView = (ListView) findViewById(R.id.listView);
        txtSelectedLot = (EditText) findViewById(R.id.selectedLot);

    }

    public void closeList(String item) {
        LotSearchAdapter mLotSearchAdapter = new LotSearchAdapter(WarehouseUserOutwardActivity.this, new ArrayList<Inward>());
        listView.setAdapter(mLotSearchAdapter);
        txtSelectedLot.setText(item);
        retrieveSelectedInward(inwardMap.get(item));
        autoFillSelectedLotDetails(inward);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveOutward:
                if (validateFields()) {
                    break;
                }
                pickUpOutwardDetails();
                Toast.makeText(getApplicationContext(),
                        "Outward is Done...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WarehouseUserOutwardActivity.this, WarehouseUserActivity.class));//Redirect to User Dashboard Page
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
                retrieveInwardList(query);
                LotSearchAdapter mLotSearchAdapter = new LotSearchAdapter(WarehouseUserOutwardActivity.this, inwardList);
                listView.setAdapter(mLotSearchAdapter);
                listView.setVisibility(View.VISIBLE);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Inward item = (Inward) parent.getItemAtPosition(position);
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
        outward = new Outward();
        outward.setInwardId(inward.getInwardId());
        outward.setTraderId(inward.getTraderId());
        outward.setLotName(inward.getLotName());
        outward.setWhAdminId(Integer.parseInt(session.getFromSession("wh_id")));
        outward.setWhUserId(Integer.parseInt(session.getFromSession("whUser_id")));
        if (txtTotalWeight.getText().toString().length() != 0)
            outward.setTotalWeight(Double.parseDouble(txtTotalWeight.getText().toString()));
        outward.setBagWeight(Double.parseDouble(txtBagWeight.getText().toString()));
        outward.setTotalQuantity(Integer.parseInt(txtTotalQuantity.getText().toString()));
        outward.setOutwardDate(txtOutwardDate.getText().toString());
        outwardDone = false;
        storeOutwardDataToDB();
        if (outwardDone) {
            showOutwardConfirmMessage();
        }
    }

    public void storeOutwardDataToDB() {
        Log.i(TAG, "StoreOutwardDetailsToDB");
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/lot/outward/";
                    try {
                        HttpURLConnection conn = HttpUtils.getPostConnection(urlAdress);

                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(convertOutwardToJson().toString());
                        os.flush();
                        os.close();
                        Log.i("Request : ", convertOutwardToJson().toString());
                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                        if (conn.getResponseCode() == 201) {
                            outwardDone = true;
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

    public void showOutwardConfirmMessage() {
        Toast.makeText(getApplicationContext(),
                "Outward Completed : ",
                Toast.LENGTH_SHORT).show();
    }

    public JSONObject convertOutwardToJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("outwardId", outward.getOutwardId());
            jsonObject.put("inwardId", outward.getInwardId());
            jsonObject.put("outwardDate", outward.getOutwardDate());
            jsonObject.put("traderId", outward.getTraderId());
            jsonObject.put("totalQuantity", outward.getTotalQuantity());
            jsonObject.put("bagWeight", outward.getBagWeight());
            jsonObject.put("totalWeight", outward.getTotalWeight());
            jsonObject.put("whAdminId", outward.getWhAdminId());
            jsonObject.put("whUserId", outward.getWhUserId());
        } catch (Exception e) {
            Log.e(TAG, "List to JSON Failed");
            e.printStackTrace();
        }
        return jsonObject;
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

    public boolean validateFields() {

        if (FieldsValidator.isEmpty(txtSelectedLot)) {
            FieldsValidator.setError(txtSelectedLot, StaticConstants.ERROR_SELECT_LOT_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtOutwardDate)) {
            FieldsValidator.setError(txtOutwardDate, StaticConstants.ERROR_OUTWARD_DATE_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtTotalQuantity)) {
            FieldsValidator.setError(txtTotalQuantity, StaticConstants.ERROR_TOTAL_QTY_MSG);
            return true;
        }

        if (FieldsValidator.isEmpty(txtBagWeight)) {
            FieldsValidator.setError(txtBagWeight, StaticConstants.ERROR_BAG_WEIGHT_MSG);
            return true;
        }
        FieldsValidator.clearError(txtBagWeight);
        FieldsValidator.clearError(txtTotalQuantity);
        FieldsValidator.clearError(txtSelectedLot);
        FieldsValidator.clearError(txtOutwardDate);
        return false;
    }

    public void retrieveInwardList(String query) {
        searchQuery = query;
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/lots/retrieveLotList/" + searchQuery;
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
                            Log.i("Response :", answer.toString());
                            JSONArray obj = new JSONArray(answer.toString());
                            inwardList = new ArrayList<>();
                            inwardMap = new HashMap<>();
                            for (int i = 0; i < obj.length(); i++) {
                                Inward inward = new Inward();
                                inward.setLotName(obj.getJSONObject(i).get("lotName").toString());
                                inwardMap.put(obj.getJSONObject(i).get("lotName").toString(), Integer.parseInt(obj.getJSONObject(i).get("inwardId").toString()));
                                inwardList.add(inward);
                            }
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

    public void retrieveSelectedInward(Integer id) {
        inwardId = id;
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/lots/retrieveLotDetails/" + inwardId.toString();
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
                            Log.i("Response :", answer.toString());
                            JSONObject obj = new JSONObject(answer.toString());
                            inward = new Inward();
                            inward.setTraderId(Integer.parseInt(obj.get("traderId").toString()));
                            inward.setInwardId(Integer.parseInt(obj.get("inwardId").toString()));
                            inward.setLotName(obj.get("lotName").toString());
                            inward.setTotalQuantity(Integer.parseInt(obj.get("totalQuantity").toString()));
                            inward.setWeightPerBag(Double.parseDouble(obj.get("weightPerBag").toString()));
                            inward.setTotalWeight(Double.parseDouble(obj.get("totalWeight").toString()));
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

    public void autoFillSelectedLotDetails(Inward inward) {
        txtTotalQuantity.setText(inward.getTotalQuantity().toString());
        txtBagWeight.setText(inward.getWeightPerBag().toString());
        txtTotalWeight.setText(inward.getTotalWeight().toString());
        backupInward = new Inward();
        backupInward.setTotalWeight(inward.getTotalWeight());
        backupInward.setTotalQuantity(inward.getTotalQuantity());
    }
}
