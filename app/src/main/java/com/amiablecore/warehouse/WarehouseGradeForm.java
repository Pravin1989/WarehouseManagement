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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseGradeForm extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText txtGradeName;
    private Button btnSaveGrade, btnGradeCancel;
    private Spinner cmbCommodity;
    private Session session;//global variable
    private static final String TAG = "WarehouseGradeForm: ";
    static boolean gradeAdded = false;
    private Map<String, Integer> commoditiesMap;
    private List<String> gradeList;
    private ListView listView;
    private Integer commodityId;
    private TextView lblAvailableGrades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        setContentView(R.layout.activity_warehouse_grade_form);
        initViews();
        retrieveCommodities();
    }

    private void initViews() {
        txtGradeName = (EditText) findViewById(R.id.txtGrade);
        btnGradeCancel = findViewById(R.id.btnCancelGrade);
        btnSaveGrade = findViewById(R.id.btnAddGrade);
        listView = findViewById(R.id.gradeListView);
        btnSaveGrade.setOnClickListener(this);
        btnGradeCancel.setOnClickListener(this);
        cmbCommodity = (Spinner) findViewById(R.id.cmbGradeComodity);
        cmbCommodity.setOnItemSelectedListener(this);
        lblAvailableGrades = findViewById(R.id.lblAvailableGrades);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddGrade:
                if (FieldsValidator.isEmpty(txtGradeName)) {
                    FieldsValidator.setError(txtGradeName, StaticConstants.ERROR_GRADE_MSG);
                    break;
                } else {
                    FieldsValidator.clearError(txtGradeName);
                }
                if (FieldsValidator.isItemSelectedInSpinner(cmbCommodity)) {
                    break;
                }
                if (addGrade()) {
                    Toast.makeText(getApplicationContext(),
                            "Grade is added...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(WarehouseGradeForm.this, WarehouseAdminItemActivity.class));//Redirect to Admin Dashboard Page
                }
                break;
            case R.id.btnCancelGrade:
                startActivity(new Intent(WarehouseGradeForm.this, WarehouseAdminItemActivity.class));//Redirect to Admin Dashboard Page
                break;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Log.i("Selected  : ", parent.getItemAtPosition(pos).toString());
        lblAvailableGrades.setVisibility(View.INVISIBLE);
        if (!parent.getItemAtPosition(pos).toString().equals(StaticConstants.SELECT_ITEM)) {
            commodityId = commoditiesMap.get(parent.getItemAtPosition(pos).toString());
            retrievedGrades();
            showAvailableGrades();
        }
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
                            String[] commoditiesList = new String[obj.length() + 1];
                            commoditiesList[0] = StaticConstants.SELECT_ITEM;
                            int j = 1;
                            commoditiesMap = new HashMap<>();
                            for (int i = 0; i < obj.length(); i++) {
                                commoditiesList[j] = obj.getJSONObject(i).get("commodityName").toString();
                                commoditiesMap.put(commoditiesList[j], Integer.parseInt(obj.getJSONObject(i).get("commodityId").toString()));
                                j++;
                            }
                            updateCommodities(commoditiesList);
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

    public boolean addGrade() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/grade/add/" + commoditiesMap.get(cmbCommodity.getSelectedItem().toString());
                    try {
                        HttpURLConnection conn = HttpUtils.getPostConnection(urlAdress);

                        JSONObject payload = new JSONObject();
                        payload.put("gradeName", txtGradeName.getText().toString());
                        payload.put("whAdminId", session.getFromSession("wh_id"));

                        Log.i("JSON", payload.toString());
                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(payload.toString());

                        os.flush();
                        os.close();

                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                        if (conn.getResponseCode() == 201) {
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
                            gradeAdded = true;
                            JSONObject obj = new JSONObject(answer.toString());
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
        return gradeAdded;
    }

    public void retrievedGrades() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/grade/retrieve/admin/" + commodityId;
                    try {
                        HttpURLConnection conn = HttpUtils.getGetConnection(urlAdress);

                        Log.i("Status Code :", String.valueOf(conn.getResponseCode()));
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
                            Log.i("Response : ", answer.toString());
                            JSONArray obj = new JSONArray(answer.toString());
                            gradeList = new ArrayList<>();
                            for (int i = 0; i < obj.length(); i++) {
                                gradeList.add(obj.getJSONObject(i).get("gradeName").toString());
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

    public void showAvailableGrades() {
        if (gradeList.size() != 0) {
            lblAvailableGrades.setVisibility(View.VISIBLE);
            final ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, gradeList);
            listView.setAdapter(adapter);
        } else {
            lblAvailableGrades.setVisibility(View.INVISIBLE);
            final ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, new ArrayList());
            listView.setAdapter(adapter);
        }
    }
}
