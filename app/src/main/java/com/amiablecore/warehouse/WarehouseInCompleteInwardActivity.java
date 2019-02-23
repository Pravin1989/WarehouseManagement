package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amiablecore.warehouse.utils.FieldsValidator;
import com.amiablecore.warehouse.utils.HttpUtils;
import com.amiablecore.warehouse.utils.Session;
import com.amiablecore.warehouse.utils.StaticConstants;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class WarehouseInCompleteInwardActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText txtTotalWeightInward;
    private Button btnUpdate, btnCancelUpdate;
    private Spinner cmbInward;
    private Session session;//global variable
    private Map<String, Integer> inwardLotMap;
    private Integer inwardId;
    private static final String TAG = "WHInCompleteInward";
    private boolean inwardUpdated;
    private Double totalWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        setContentView(R.layout.activity_incomplete_inward);
        initViews();
    }

    private void initViews() {
        txtTotalWeightInward = (EditText) findViewById(R.id.txtTotalWeightInCompleteIn);
        btnCancelUpdate = findViewById(R.id.btnCancelInwardUpdate);
        btnUpdate = findViewById(R.id.btnUpdateInward);
        cmbInward = (Spinner) findViewById(R.id.cmbInwardLot);
        btnUpdate.setOnClickListener(this);
        btnCancelUpdate.setOnClickListener(this);
        cmbInward.setOnItemSelectedListener(this);
        retrieveInCompleteInwardLot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdateInward:
                if (FieldsValidator.isEmpty(txtTotalWeightInward)) {
                    FieldsValidator.setError(txtTotalWeightInward, StaticConstants.ERROR_TOTAL_WEIGHT_MSG);
                    break;
                } else {
                    FieldsValidator.setError(txtTotalWeightInward, null);
                }
                if (FieldsValidator.isItemSelectedInSpinner(cmbInward)) {
                    break;
                }
                if (inwardLotMap.size() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "There is no incomplete lot",
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                inwardUpdated = false;
                totalWeight = Double.parseDouble(txtTotalWeightInward.getText().toString());
                updateInCompleteInwardLot();
                if (inwardUpdated) {
                    startActivity(new Intent(this, WarehouseInCompleteActivity.class));
                }
                break;
            case R.id.btnCancelInwardUpdate:
                startActivity(new Intent(this, WarehouseInCompleteActivity.class));
                break;
        }
    }

    public void retrieveInCompleteInwardLot() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/lot/inward/incomplete/" + session.getFromSession("whUser_id");
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
                            String[] outwardLotList = new String[obj.length() + 1];
                            outwardLotList[0] = StaticConstants.SELECT_INWARD;
                            int j = 1;
                            inwardLotMap = new HashMap<>();
                            for (int i = 0; i < obj.length(); i++) {
                                outwardLotList[j] = obj.getJSONObject(i).get("lotName").toString();
                                inwardLotMap.put(outwardLotList[j], Integer.parseInt(obj.getJSONObject(i).get("inwardId").toString()));
                                j++;
                            }
                            updateInwardList(outwardLotList);
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

    public void updateInwardList(String[] list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbInward.setAdapter(adapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (!parent.getItemAtPosition(pos).toString().equals(StaticConstants.SELECT_INWARD)) {
            inwardId = inwardLotMap.get(parent.getItemAtPosition(pos).toString());
        }
    }

    public void updateInCompleteInwardLot() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/lot/inward/update/" + inwardId + "/weight/" + totalWeight;
                    try {
                        HttpURLConnection conn = HttpUtils.getGetConnection(urlAdress);
                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                        if (conn.getResponseCode() == 200) {
                            Log.i(TAG, "Total Weight in Inward Updated");
                            inwardUpdated = true;
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

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}
