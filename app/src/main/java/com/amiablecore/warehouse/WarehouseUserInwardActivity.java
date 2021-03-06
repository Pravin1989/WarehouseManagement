package com.amiablecore.warehouse;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.amiablecore.warehouse.beans.Inward;
import com.amiablecore.warehouse.beans.Trader;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseUserInwardActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText txtLotName, txtInwardDate, txtTotalQuantity, txtBagWeight, txtPhysicalAddress, txtTotalWeight, txtSelectedTrader, txtVehicleNo;
    private Button btnAdd, btnCancel;
    private Spinner cmbUnits, cmbCommodity, cmbCategory, cmbGrade;
    private int mYear, mMonth, mDay;
    private static final String TAG = "Warehouse Inward";
    private Session session;//global variable
    private Map<String, Integer> commoditiesMap;
    private Map<String, Integer> tradersMap;
    private Map<String, Integer> categoriesMap;
    private String[] categoriesList;
    private List<Trader> traderList;
    private SearchView searchView;
    private ListView listView;
    private String searchQuery;
    private Inward inward;
    private boolean inwardDone = false;
    private List<String> unitList;
    private List<String> gradeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        setContentView(R.layout.activity_warehouse_user_inward);
        initViews();
        updateTraders();
        retrieveCommodities();
    }

    private void initViews() {
        txtLotName = (EditText) findViewById(R.id.txtLotName);
        txtInwardDate = (EditText) findViewById(R.id.txtInwardDate);
        txtBagWeight = (EditText) findViewById(R.id.txtSingleBagWeight);
        txtTotalWeight = (EditText) findViewById(R.id.txtTotalWeightInward);
        txtTotalQuantity = (EditText) findViewById(R.id.txtTotalQuantity);
        txtPhysicalAddress = (EditText) findViewById(R.id.txtPhysicalAddress);
        txtVehicleNo = (EditText) findViewById(R.id.txtVehicleNo);
        txtSelectedTrader = (EditText) findViewById(R.id.selectedTrader);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        addListenerOnSpinnerItemSelection();
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Enter Trader Name/ID");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                if (validateFields()) {
                    break;
                }
                keepInwardDetailsToDb();
                startActivity(new Intent(WarehouseUserInwardActivity.this, WarehouseUserActivity.class));//Redirect to User Dashboard Page
                break;
            case R.id.btnCancel:
                startActivity(new Intent(WarehouseUserInwardActivity.this, WarehouseUserActivity.class));//Redirect to User Dashboard Page
                break;
        }
    }

    private void keepInwardDetailsToDb() {
        inward = new Inward();
        inward.setTraderId(tradersMap.get(txtSelectedTrader.getText().toString().trim()));
        inward.setLotName(txtLotName.getText().toString().trim());
        inward.setCommodityId(commoditiesMap.get(cmbCommodity.getSelectedItem().toString().trim()));
        inward.setCategoryId(categoriesMap.get(cmbCategory.getSelectedItem().toString().trim()));
        if (txtTotalWeight.getText().toString().trim().length() != 0)
            inward.setTotalWeight(Double.parseDouble(txtTotalWeight.getText().toString().trim()));
        inward.setTotalQuantity(Integer.parseInt(txtTotalQuantity.getText().toString().trim()));
        inward.setWeightPerBag(Double.parseDouble(txtBagWeight.getText().toString().trim()));
        inward.setInwardDate(txtInwardDate.getText().toString().trim());
        inward.setPhysicalAddress(txtPhysicalAddress.getText().toString().trim());
        inward.setWhAdminId(Integer.parseInt(session.getFromSession("wh_id")));
        inward.setWhUserId(Integer.parseInt(session.getFromSession("whUser_id")));
        inward.setUnit(cmbUnits.getSelectedItem().toString().trim());
        inward.setGrade(cmbGrade.getSelectedItem().toString().trim());
        inward.setVehicleNo(txtVehicleNo.getText().toString().trim());
        storeInwardDataToDB();
        if (inwardDone) {
            Toast.makeText(getApplicationContext(),
                    "Lot Number is already Present...", Toast.LENGTH_SHORT).show();
        } else {
            showInwardConfirmMessage();
        }
    }

    public void storeInwardDataToDB() {
        Log.i(TAG, "StoreInwardDetailsToDB");
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/lot/inward/";
                    try {
                        HttpURLConnection conn = HttpUtils.getPostConnection(urlAdress);

                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(convertInwardToJson().toString());
                        os.flush();
                        os.close();
                        Log.i("Request : ", convertInwardToJson().toString());
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
                            Log.i("Response Inward : ", answer.toString());
                            JSONObject obj = new JSONObject(answer.toString());
                            inwardDone = Boolean.parseBoolean(obj.get("lotAlreadyPresent").toString());
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

    public void showInwardConfirmMessage() {
        Toast.makeText(getApplicationContext(),
                "Inward Completed : ",
                Toast.LENGTH_SHORT).show();
    }

    public JSONObject convertInwardToJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("inwardId", inward.getInwardId());
            jsonObject.put("inwardDate", inward.getInwardDate());
            jsonObject.put("lotName", inward.getLotName());
            jsonObject.put("traderId", inward.getTraderId());
            jsonObject.put("commodityId", inward.getCommodityId());
            jsonObject.put("categoryId", inward.getCategoryId());
            jsonObject.put("totalQuantity", inward.getTotalQuantity());
            jsonObject.put("weightPerBag", inward.getWeightPerBag());
            jsonObject.put("totalWeight", inward.getTotalWeight());
            jsonObject.put("physicalAddress", inward.getPhysicalAddress());
            jsonObject.put("whAdminId", inward.getWhAdminId());
            jsonObject.put("whUserId", inward.getWhUserId());
            jsonObject.put("unit", inward.getUnit());
            jsonObject.put("grade", inward.getGrade());
            jsonObject.put("vehicleNo", inward.getVehicleNo());

        } catch (Exception e) {
            Log.e(TAG, "List to JSON Failed");
        }
        return jsonObject;
    }

    public void addListenerOnSpinnerItemSelection() {
        cmbCommodity = (Spinner) findViewById(R.id.cmbCommodity);
        cmbCommodity.setOnItemSelectedListener(this);
        cmbCategory = (Spinner) findViewById(R.id.cmbCategory);
        cmbCategory.setOnItemSelectedListener(this);
        cmbUnits = (Spinner) findViewById(R.id.cmbUnits);
        cmbUnits.setOnItemSelectedListener(this);
        cmbGrade = (Spinner) findViewById(R.id.cmbGrades);
        cmbGrade.setOnItemSelectedListener(this);
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
        Log.i("Commodity  ", String.valueOf(parent.getItemAtPosition(0).toString() == StaticConstants.SELECT_ITEM));
        Log.i("Category ", String.valueOf(parent.getItemAtPosition(0).toString() == StaticConstants.SELECT_CATEGORY));
        if (parent.getItemAtPosition(pos).toString().trim().equals(StaticConstants.SELECT_CATEGORY) || parent.getItemAtPosition(pos).toString().trim().equals(StaticConstants.SELECT_ITEM) ||
                parent.getItemAtPosition(pos).toString().equals(StaticConstants.SELECT_TRADER) || parent.getItemAtPosition(pos).toString().trim().equals(StaticConstants.SELECT_UNIT)) {
            return;
        }
        if (parent.getItemAtPosition(0).toString().equals(StaticConstants.SELECT_ITEM)) {
            retrieveCategories();
            updateCategories();
            retrieveUnits();
            updateUnits();
            retrieveGrades();
            updateGrades();
        }
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
        Log.i("Nothing  ", "Nothing Selected");
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
                            Log.i("Response :", answer.toString());
                            JSONArray obj = new JSONArray(answer.toString());
                            Log.i("JSON :", obj.toString());
                            String[] commodities = new String[obj.length() + 1];
                            commodities[0] = StaticConstants.SELECT_ITEM;
                            commoditiesMap = new HashMap<>();
                            int j = 1;
                            for (int i = 0; i < obj.length(); i++) {
                                commodities[j] = obj.getJSONObject(i).get("commodityName").toString().trim();
                                commoditiesMap.put(commodities[j], Integer.parseInt(obj.getJSONObject(i).get("commodityId").toString().trim()));
                                j++;
                            }
                            Log.i("Comm Length:", String.valueOf(commodities.length));
                            updateCommodities(commodities);
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

    public void updateCommodities(String[] commoditiesList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, commoditiesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbCommodity.setAdapter(adapter);
    }


    public void retrieveCategories() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/category/retrieveCategories/" + commoditiesMap.get(cmbCommodity.getSelectedItem().toString().trim());
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
                            categoriesList = new String[obj.length() + 1];
                            categoriesList[0] = StaticConstants.SELECT_CATEGORY;
                            categoriesMap = new HashMap<>();
                            int j = 1;
                            for (int i = 0; i < obj.length(); i++) {
                                categoriesList[j] = obj.getJSONObject(i).get("categoryName").toString();
                                categoriesMap.put(categoriesList[j], Integer.parseInt(obj.getJSONObject(i).get("categoryId").toString()));
                                j++;
                            }
                            Log.i("Categories:", String.valueOf(categoriesList.length));
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

    public void updateCategories() {
        Log.i("Categories Length", String.valueOf(categoriesList.length));
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, categoriesList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbCategory.setAdapter(categoryAdapter);
    }

    public void retrieveUnits() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/retrieve/units/";
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
                            String response = answer.toString().substring(1, answer.toString().length() - 1);
                            Log.i("Response :", response);
                            unitList = new ArrayList<>(Arrays.asList(response.replaceAll("[]]", "").replaceAll("\"", "").split(",")));
                            unitList.add(0, StaticConstants.SELECT_UNIT);
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

    public void updateUnits() {
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, unitList);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbUnits.setAdapter(unitAdapter);
    }


    public void retrieveGrades() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/retrieve/grades/" + commoditiesMap.get(cmbCommodity.getSelectedItem().toString().trim());
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
                            String response = answer.toString().substring(1, answer.toString().length() - 1);
                            Log.i("Response :", response);
                            gradeList = new ArrayList<>(Arrays.asList(response.replaceAll("[]]", "").replaceAll("\"", "").split(",")));
                            gradeList.add(0, StaticConstants.SELECT_GRADE);
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

    public void updateGrades() {
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, gradeList);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbGrade.setAdapter(gradeAdapter);
    }

    public List<Trader> retrieveTraders(String query) {
        searchQuery = query;
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/trader/retrieveTraders/" + searchQuery;
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
                            traderList = new ArrayList<>();
                            tradersMap = new HashMap<>();
                            for (int i = 0; i < obj.length(); i++) {
                                Trader trader = new Trader();
                                trader.setTraderName(obj.getJSONObject(i).get("traderName").toString().trim());
                                tradersMap.put(obj.getJSONObject(i).get("traderName").toString().trim(), Integer.parseInt(obj.getJSONObject(i).get("traderId").toString().trim()));
                                traderList.add(trader);
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
        return traderList;
    }

    public void updateTraders() {
        listView = (ListView) findViewById(R.id.listView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Trader> traders = retrieveTraders(query);
                TraderSearchAdapter mLotSearchAdapter = new TraderSearchAdapter(WarehouseUserInwardActivity.this, traders);
                listView.setAdapter(mLotSearchAdapter);
                listView.setVisibility(View.VISIBLE);
                Log.i("Size :", String.valueOf(listView.getAdapter().getCount()));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Trader item = (Trader) parent.getItemAtPosition(position);
                        Log.i("Selected Item :", item.getTraderName());
                        closeList(item.getTraderName());
                    }
                });
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    public void closeList(String item) {
        TraderSearchAdapter mSearchAdapter = new TraderSearchAdapter(WarehouseUserInwardActivity.this, new ArrayList<Trader>());
        listView.setAdapter(mSearchAdapter);
        txtSelectedTrader.setText(item);
        searchView.setQuery("", false);
        searchView.clearFocus();
    }

    public boolean validateFields() {

        if (FieldsValidator.isEmpty(txtSelectedTrader)) {
            FieldsValidator.setError(txtSelectedTrader, StaticConstants.ERROR_INWARD_SELECT_TRADER_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtLotName)) {
            FieldsValidator.setError(txtLotName, StaticConstants.ERROR_INWARD_lOT_NAME_MSG);
            return true;
        }
        if (FieldsValidator.isItemSelectedInSpinner(cmbCommodity)) {
            return true;
        }
        if (FieldsValidator.isItemSelectedInSpinner(cmbCategory)) {
            return true;
        }
        if (FieldsValidator.isItemSelectedInSpinner(cmbUnits)) {
            return true;
        }
        if (FieldsValidator.isItemSelectedInSpinner(cmbGrade)) {
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
        if (FieldsValidator.isEmpty(txtInwardDate)) {
            FieldsValidator.setError(txtInwardDate, StaticConstants.ERROR_INWARD_DATE_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtPhysicalAddress)) {
            FieldsValidator.setError(txtPhysicalAddress, StaticConstants.ERROR_INWARD_PHYSICAL_ADDRESS_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtVehicleNo)) {
            FieldsValidator.setError(txtVehicleNo, StaticConstants.ERROR_INWARD_VEHICLE_NO_MSG);
            return true;
        }
        FieldsValidator.clearError(txtSelectedTrader);
        FieldsValidator.clearError(txtLotName);
        FieldsValidator.clearError(txtTotalQuantity);
        FieldsValidator.clearError(txtBagWeight);
        FieldsValidator.clearError(txtInwardDate);
        FieldsValidator.clearError(txtPhysicalAddress);
        FieldsValidator.clearError(txtVehicleNo);
        return false;
    }
}