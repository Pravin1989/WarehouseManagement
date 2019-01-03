package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amiablecore.warehouse.utils.FieldsValidator;
import com.amiablecore.warehouse.utils.HttpUtils;
import com.amiablecore.warehouse.utils.Session;
import com.amiablecore.warehouse.utils.StaticConstants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class WarehouseCommodityForm extends AppCompatActivity implements View.OnClickListener {

    EditText txtCommodityName;
    Button btnAddCommodity, btnCancelCommodity;
    private Session session;//global variable
    private static final String TAG = "WarehouseCommodityForm";
    static boolean commodityAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        setContentView(R.layout.activity_warehouse_commodity_form);
        initViews();
    }

    private void initViews() {
        txtCommodityName = (EditText) findViewById(R.id.txtCommodity);
        btnCancelCommodity = findViewById(R.id.btnCancelCommodity);
        btnAddCommodity = findViewById(R.id.btnAddCommodity);

        btnAddCommodity.setOnClickListener(this);
        btnCancelCommodity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddCommodity:
                if (FieldsValidator.isEmpty(txtCommodityName)) {
                    FieldsValidator.setError(txtCommodityName, StaticConstants.ERROR_COMMODITY_MSG);
                    break;
                } else {
                    FieldsValidator.setError(txtCommodityName, null);
                }
                if (addCommodity()) {
                    Toast.makeText(getApplicationContext(),
                            "Commodity is added...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(WarehouseCommodityForm.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                }
                break;
            case R.id.btnCancelCommodity:
                startActivity(new Intent(WarehouseCommodityForm.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                break;
        }
    }

    public boolean addCommodity() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/commodity/add";
                    try {
                        HttpURLConnection conn = HttpUtils.getPostConnection(urlAdress);

                        JSONObject payload = new JSONObject();
                        payload.put("commodityName", txtCommodityName.getText().toString());
                        payload.put("whAdminId", session.getFromSession("wh_id"));

                        Log.i("JSON", payload.toString());
                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(payload.toString());

                        os.flush();
                        os.close();

                        Log.i("Status Code :", String.valueOf(conn.getResponseCode()));
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
                            Log.i("Response : ", answer.toString());
                            commodityAdded = true;
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
        return commodityAdded;
    }
}
