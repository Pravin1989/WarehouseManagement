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
import com.amiablecore.warehouse.db.DbQueryExecutor;
import com.amiablecore.warehouse.utils.HttpUtils;
import com.amiablecore.warehouse.utils.Session;
import com.amiablecore.warehouse.utils.StaticConstants;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseUserInwardActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText txtLotName, txtInwardDate, txtTotalQuantity, txtBagWeight, txtPhysicalAddress, txtTotalWeight, txtSelectedTrader;
    private Button btnAdd, btnCancel;
    private Spinner cmbTrader, cmbCommodity, cmbCategory;
    private int mYear, mMonth, mDay;
    private static final String TAG = "Warehouse Inward";
    private Session session;//global variable
    Map<String, Integer> commoditiesMap = new HashMap<>();
    Map<String, Integer> categoriesMap = new HashMap<>();
    String[] categoriesList;
    private DbQueryExecutor databaseObject;
    List<Trader> traderList;
    SearchView searchView;
    private ListView listView;
    private String searchQuery;

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
        txtSelectedTrader = (EditText) findViewById(R.id.selectedTrader);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        addListenerOnSpinnerItemSelection();
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Enter Trader Name");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                Toast.makeText(getApplicationContext(),
                        "User is added In DB...", Toast.LENGTH_SHORT).show();
                keepInwardDetailsToDb();
                startActivity(new Intent(WarehouseUserInwardActivity.this, WarehouseUserActivity.class));//Redirect to User Dashboard Page
                break;
            case R.id.btnCancel:
                startActivity(new Intent(WarehouseUserInwardActivity.this, WarehouseUserActivity.class));//Redirect to User Dashboard Page
                break;
        }
    }

    private void keepInwardDetailsToDb() {
        Log.i("Lot Name : ", txtLotName.getText().toString());
        Log.i("Trader Name : ", txtSelectedTrader.getText().toString());
        Log.i("Commodity Name : ", cmbCommodity.getSelectedItem().toString());
        Log.i("Category Name : ", cmbCategory.getSelectedItem().toString());
        Log.i("Total Weight : ", txtTotalWeight.getText().toString());
        Log.i("Total Quantity : ", txtTotalQuantity.getText().toString());
        Log.i("Weight/Bag : ", txtBagWeight.getText().toString());
        Log.i("Inward Date : ", txtInwardDate.getText().toString());
        Log.i("Physical Address : ", txtPhysicalAddress.getText().toString());
        Inward inward = new Inward();
        inward.setTraderId(txtSelectedTrader.getText().toString());
        inward.setLotName(txtLotName.getText().toString());
        inward.setCommodityId(commoditiesMap.get(cmbCommodity.getSelectedItem().toString()));
        inward.setCategoryId(categoriesMap.get(cmbCategory.getSelectedItem().toString()));
        inward.setTotalWeight(Double.parseDouble(txtTotalWeight.getText().toString()));
        inward.setTotalQuantity(Integer.parseInt(txtTotalQuantity.getText().toString()));
        inward.setWeightPerBag(Double.parseDouble(txtBagWeight.getText().toString()));
        inward.setInwardDate(txtInwardDate.getText().toString());
        inward.setPhysicalAddress(txtPhysicalAddress.getText().toString());
        databaseObject.addLotDetails(inward);
    }

    public void addListenerOnSpinnerItemSelection() {
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
        Log.i("Commodity  ", String.valueOf(parent.getItemAtPosition(0).toString() == StaticConstants.SELECT_COMMODITY));
        Log.i("Category ", String.valueOf(parent.getItemAtPosition(0).toString() == StaticConstants.SELECT_CATEGORY));
        Log.i("Trader ", String.valueOf(parent.getItemAtPosition(0).toString() == StaticConstants.SELECT_TRADER));
        if (parent.getItemAtPosition(pos).toString().equals(StaticConstants.SELECT_CATEGORY) || parent.getItemAtPosition(pos).toString().equals(StaticConstants.SELECT_COMMODITY) ||
                parent.getItemAtPosition(pos).toString().equals(StaticConstants.SELECT_TRADER)) {
            return;
        }
        if (parent.getItemAtPosition(0).toString().equals(StaticConstants.SELECT_COMMODITY)) {
            retrieveCategories();
            updateCategories();
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
                            commodities[0] = StaticConstants.SELECT_COMMODITY;
                            int j = 1;
                            for (int i = 0; i < obj.length(); i++) {
                                commodities[j] = obj.getJSONObject(i).get("commodityName").toString();
                                commoditiesMap.put(commodities[j], Integer.parseInt(obj.getJSONObject(i).get("commodityId").toString()));
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, commoditiesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbCommodity.setAdapter(adapter);
    }


    public void retrieveCategories() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/category/retrieveCategories/" + commoditiesMap.get(cmbCommodity.getSelectedItem().toString());
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
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbCategory.setAdapter(categoryAdapter);
    }

    public List<Trader> retrieveTraders(String query) {
        searchQuery = query;
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //String urlAdress = "/trader/retrieveTraders/" + session.getFromSession("wh_id");
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
                            for (int i = 0; i < obj.length(); i++) {
                                Trader trader = new Trader();
                                trader.setTraderName(obj.getJSONObject(i).get("traderName").toString());
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
        databaseObject = new DbQueryExecutor(WarehouseUserInwardActivity.this);
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
        //txtLotName.requestFocus();
    }
}
