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
import java.util.HashMap;
import java.util.Map;

public class WarehouseInCompleteOutwardActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText txtTotalWeightOutward;
    private Button btnUpdateOut, btnCancelUpdateOut;
    private Spinner cmbOutward;
    private Session session;//global variable
    private Map<String, Integer> outwardLotMap;
    private Map<String, Outward> outwardListMap;
    private Outward outward;
    private boolean outwardUpdated;

    private static final String TAG = "WHInCompleteOutward";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        setContentView(R.layout.activity_incomplete_outward);
        initViews();
    }

    private void initViews() {
        txtTotalWeightOutward = (EditText) findViewById(R.id.txtTotalWeightInCompleteOutward);
        btnCancelUpdateOut = findViewById(R.id.btnCancelOutward);
        btnUpdateOut = findViewById(R.id.btnUpdateOutward);
        cmbOutward = (Spinner) findViewById(R.id.cmbOutwardLot);
        cmbOutward.setOnItemSelectedListener(this);
        btnUpdateOut.setOnClickListener(this);
        btnCancelUpdateOut.setOnClickListener(this);
        retrieveInCompleteOutwardLot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdateOutward:
                Log.i(TAG, "btnUpdateOutward ");
                if (FieldsValidator.isEmpty(txtTotalWeightOutward)) {
                    FieldsValidator.setError(txtTotalWeightOutward, StaticConstants.ERROR_TOTAL_WEIGHT_MSG);
                    break;
                } else {
                    FieldsValidator.setError(txtTotalWeightOutward, null);
                }
                if (FieldsValidator.isItemSelectedInSpinner(cmbOutward)) {
                    break;
                }
                outwardUpdated = false;
                outward.setTotalWeight(Double.parseDouble(txtTotalWeightOutward.getText().toString()));
                updateOutwardDataToDB();
                if (outwardUpdated) {
                    Toast.makeText(getApplicationContext(),
                            "Outward and Inward Updated: ",
                            Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(this, WarehouseInCompleteActivity.class));
                break;
            case R.id.btnCancelOutward:
                Log.i(TAG, "btnCancelOutward ");
                startActivity(new Intent(this, WarehouseInCompleteActivity.class));
                break;
        }
    }

    public void retrieveInCompleteOutwardLot() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/lot/outward/incomplete/" + session.getFromSession("whUser_id");
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
                            String[] outwardLotList = new String[obj.length() + 1];
                            outwardLotList[0] = StaticConstants.SELECT_OUTWARD;
                            int j = 1;
                            outwardLotMap = new HashMap<>();
                            outwardListMap = new HashMap<>();
                            for (int i = 0; i < obj.length(); i++) {
                                String lotName = obj.getJSONObject(i).get("lotName").toString();
                                Integer inwardId = Integer.parseInt(obj.getJSONObject(i).get("inwardId").toString());
                                Integer outwardId = Integer.parseInt(obj.getJSONObject(i).get("outwardId").toString());
                                Integer totalQuantity = Integer.parseInt(obj.getJSONObject(i).get("totalQuantity").toString());
                                Double totalWeight = Double.parseDouble(obj.getJSONObject(i).get("totalWeight").toString());
                                outwardLotList[j] = lotName;
                                outwardLotMap.put(outwardLotList[j], Integer.parseInt(obj.getJSONObject(i).get("outwardId").toString()));
                                outwardListMap.put(obj.getJSONObject(i).get("lotName").toString(), new Outward(outwardId, inwardId, totalQuantity, totalWeight, lotName));
                                j++;
                            }
                            updateOutwardList(outwardLotList);
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

    public void updateOutwardList(String[] list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbOutward.setAdapter(adapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText(parent.getContext(),
                "Selected  : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
        if (!parent.getItemAtPosition(pos).toString().equals(StaticConstants.SELECT_OUTWARD)) {
            outward = outwardListMap.get(parent.getItemAtPosition(pos).toString());
        }
    }

    public JSONObject convertOutwardToJson(Outward outward) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("outwardId", outward.getOutwardId());
            jsonObject.put("inwardId", outward.getInwardId());
            jsonObject.put("totalQuantity", outward.getTotalQuantity());
            jsonObject.put("totalWeight", outward.getTotalWeight());
            jsonObject.put("lotName", outward.getLotName());
        } catch (Exception e) {
            Log.e(TAG, "List to JSON Failed");
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void updateOutwardDataToDB() {
        Log.i(TAG, "StoreOutwardDetailsToDB");
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/lot/outward/update/";
                    try {
                        HttpURLConnection conn = HttpUtils.getPostConnection(urlAdress);

                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(convertOutwardToJson(outward).toString());
                        os.flush();
                        os.close();
                        Log.i("Request : ", convertOutwardToJson(outward).toString());
                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                        if (conn.getResponseCode() == 200) {
                            outwardUpdated = true;
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
