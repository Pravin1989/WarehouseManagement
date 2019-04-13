package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.Map;

public class WarehouseAdminRemoveItemActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button btnRemoveItem, btnRemoveGrade, btnRemoveCategory, btnItemRemoveCancel;
    private Spinner cmbItems, cmbCategory, cmbGrade;
    private Session session;//global variable
    private static final String TAG = "WH Remove Item: ";
    static boolean itemRemoved = false;
    static boolean categoryRemoved = false;
    static boolean gradeRemoved = false;
    private Map<String, Integer> itemsMap;
    private Map<String, Integer> gradesMap;
    private Map<String, Integer> categoriesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        setContentView(R.layout.activity_warehouse_admin_remove_item);
        initViews();
        retrieveCommodities();
        retrieveCategories();
        retrieveGrades();
    }

    private void initViews() {
        btnItemRemoveCancel = findViewById(R.id.btnCancelRemoveItem);
        btnRemoveItem = findViewById(R.id.btnRemoveItem);
        btnRemoveCategory = findViewById(R.id.btnRemoveCategory);
        btnRemoveGrade = findViewById(R.id.btnRemoveGrade);
        cmbItems = (Spinner) findViewById(R.id.cmbItemRemove);
        cmbCategory = (Spinner) findViewById(R.id.cmbCategoryRemove);
        cmbGrade = (Spinner) findViewById(R.id.cmbGradeRemove);

        btnRemoveItem.setOnClickListener(this);
        btnRemoveCategory.setOnClickListener(this);
        btnRemoveGrade.setOnClickListener(this);
        btnItemRemoveCancel.setOnClickListener(this);

        cmbItems.setOnItemSelectedListener(this);
        cmbCategory.setOnItemSelectedListener(this);
        cmbGrade.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRemoveItem:
                if (FieldsValidator.isItemSelectedInSpinner(cmbItems)) {
                    break;
                }
                if (removeItem()) {
                    Toast.makeText(getApplicationContext(),
                            "Item is removed...", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(WarehouseAdminRemoveItemActivity.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                break;
            case R.id.btnRemoveCategory:
                if (FieldsValidator.isItemSelectedInSpinner(cmbCategory)) {
                    break;
                }

                if (removeCategory()) {
                    Toast.makeText(getApplicationContext(),
                            "Category is removed...", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(WarehouseAdminRemoveItemActivity.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                break;
            case R.id.btnRemoveGrade:
                if (FieldsValidator.isItemSelectedInSpinner(cmbGrade)) {
                    break;
                }
                if (removeGrade()) {
                    Toast.makeText(getApplicationContext(),
                            "Grade is removed...", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(WarehouseAdminRemoveItemActivity.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                break;
            case R.id.btnCancelRemoveItem:
                startActivity(new Intent(WarehouseAdminRemoveItemActivity.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                break;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Log.i("Selected  : ", parent.getItemAtPosition(pos).toString());
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
                            itemsMap = new HashMap<>();
                            for (int i = 0; i < obj.length(); i++) {
                                commoditiesList[j] = obj.getJSONObject(i).get("commodityName").toString();
                                itemsMap.put(commoditiesList[j], Integer.parseInt(obj.getJSONObject(i).get("commodityId").toString()));
                                j++;
                            }
                            updateItems(commoditiesList);
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

    public void updateItems(String[] list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbItems.setAdapter(adapter);
    }

    public boolean removeItem() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/commodity/remove/";
                    try {
                        HttpURLConnection conn = HttpUtils.getDeleteConnection(urlAdress);

                        JSONObject payload = new JSONObject();
                        payload.put("whAdminId", session.getFromSession("wh_id"));
                        payload.put("commodityId", itemsMap.get(cmbItems.getSelectedItem().toString()));

                        Log.i("JSON", payload.toString());
                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(payload.toString());

                        os.flush();
                        os.close();

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
                            if (Boolean.parseBoolean(answer.toString())) {
                                itemRemoved = true;
                            } else {
                                itemRemoved = false;
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
        return itemRemoved;
    }

    //Retrieve Categories
    public void retrieveCategories() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/category/retrieve/" + session.getFromSession("wh_id");
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
                            String[] categoryList = new String[obj.length() + 1];
                            categoryList[0] = StaticConstants.SELECT_CATEGORY;
                            int j = 1;
                            categoriesMap = new HashMap<>();
                            for (int i = 0; i < obj.length(); i++) {
                                categoryList[j] = obj.getJSONObject(i).get("categoryName").toString();
                                categoriesMap.put(categoryList[j], Integer.parseInt(obj.getJSONObject(i).get("categoryId").toString()));
                                j++;
                            }
                            updateCategories(categoryList);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbCategory.setAdapter(adapter);
    }

    public boolean removeCategory() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/category/remove/";
                    try {
                        HttpURLConnection conn = HttpUtils.getDeleteConnection(urlAdress);

                        JSONObject payload = new JSONObject();
                        payload.put("whAdminId", session.getFromSession("wh_id"));
                        payload.put("categoryId", categoriesMap.get(cmbCategory.getSelectedItem().toString()));

                        Log.i("JSON", payload.toString());
                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(payload.toString());

                        os.flush();
                        os.close();

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
                            if (Boolean.parseBoolean(answer.toString())) {
                                categoryRemoved = true;
                            } else {
                                categoryRemoved = false;
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
        return categoryRemoved;
    }

    //Retrieve Grades
    public void retrieveGrades() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/grades/retrieve/" + session.getFromSession("wh_id");
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
                            String[] gradeList = new String[obj.length() + 1];
                            gradeList[0] = StaticConstants.SELECT_GRADE;
                            int j = 1;
                            gradesMap = new HashMap<>();
                            for (int i = 0; i < obj.length(); i++) {
                                gradeList[j] = obj.getJSONObject(i).get("gradeName").toString();
                                gradesMap.put(gradeList[j], Integer.parseInt(obj.getJSONObject(i).get("gradeId").toString()));
                                j++;
                            }
                            updateGrades(gradeList);
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

    public void updateGrades(String[] list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbGrade.setAdapter(adapter);
    }

    public boolean removeGrade() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/grade/remove/";
                    try {
                        HttpURLConnection conn = HttpUtils.getDeleteConnection(urlAdress);

                        JSONObject payload = new JSONObject();
                        payload.put("whAdminId", session.getFromSession("wh_id"));
                        payload.put("gradeId", gradesMap.get(cmbGrade.getSelectedItem().toString()));

                        Log.i("JSON", payload.toString());
                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(payload.toString());

                        os.flush();
                        os.close();

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
                            if (Boolean.parseBoolean(answer.toString())) {
                                gradeRemoved = true;
                            } else {
                                gradeRemoved = false;
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
        return gradeRemoved;
    }
}