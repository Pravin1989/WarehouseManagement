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

public class WarehouseCategoryForm extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText txtCategoryName;
    private Button btnSaveCategory, btnCategoryCancel;
    private Spinner cmbCommodity;
    private Session session;//global variable
    private static final String TAG = "WarehouseCategoryForm: ";
    static boolean categoryAdded = false;
    private Map<String, Integer> commoditiesMap;
    private List<String> categoriesList;
    private ListView listView;
    private Integer commodityId;
    private TextView lblAvailableCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        setContentView(R.layout.activity_warehouse_category_form);
        initViews();
        retrieveCommodities();
    }

    private void initViews() {
        txtCategoryName = (EditText) findViewById(R.id.txtCategory);
        btnCategoryCancel = findViewById(R.id.btnCancelCategory);
        btnSaveCategory = findViewById(R.id.btnAddCategory);
        listView = findViewById(R.id.categoriesListView);
        btnSaveCategory.setOnClickListener(this);
        btnCategoryCancel.setOnClickListener(this);
        cmbCommodity = (Spinner) findViewById(R.id.cmbCategoryComodity);
        cmbCommodity.setOnItemSelectedListener(this);
        lblAvailableCategories = findViewById(R.id.lblAvailableCategories);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddCategory:
                if (FieldsValidator.isEmpty(txtCategoryName)) {
                    FieldsValidator.setError(txtCategoryName, StaticConstants.ERROR_CATEGORY_MSG);
                    break;
                } else {
                    FieldsValidator.clearError(txtCategoryName);
                }
                if (FieldsValidator.isItemSelectedInSpinner(cmbCommodity)) {
                    break;
                }
                if (addCategory()) {
                    Toast.makeText(getApplicationContext(),
                            "Category is added...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(WarehouseCategoryForm.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                }
                break;
            case R.id.btnCancelCategory:
                startActivity(new Intent(WarehouseCategoryForm.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                break;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText(parent.getContext(),
                "Selected  : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
        lblAvailableCategories.setVisibility(View.INVISIBLE);
        if (!parent.getItemAtPosition(pos).toString().equals(StaticConstants.SELECT_COMMODITY)) {
            commodityId = commoditiesMap.get(parent.getItemAtPosition(pos).toString());
            retrievedCategories();
            showAvailableCategories();
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
                            commoditiesList[0] = StaticConstants.SELECT_COMMODITY;
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

    public boolean addCategory() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/category/add/" + commoditiesMap.get(cmbCommodity.getSelectedItem().toString());
                    try {
                        HttpURLConnection conn = HttpUtils.getPostConnection(urlAdress);

                        JSONObject payload = new JSONObject();
                        payload.put("categoryName", txtCategoryName.getText().toString());
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
                            categoryAdded = true;
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
        return categoryAdded;
    }

    public void retrievedCategories() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/category/retrieve/admin/" + commodityId;
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
                            categoriesList = new ArrayList<>();
                            for (int i = 0; i < obj.length(); i++) {
                                categoriesList.add(obj.getJSONObject(i).get("categoryName").toString());
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

    public void showAvailableCategories() {
        if (categoriesList.size() != 0) {
            lblAvailableCategories.setVisibility(View.VISIBLE);
            final ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, categoriesList);
            listView.setAdapter(adapter);
        } else {
            lblAvailableCategories.setVisibility(View.INVISIBLE);
            final ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, new ArrayList());
            listView.setAdapter(adapter);
        }
    }
}
