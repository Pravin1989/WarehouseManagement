package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amiablecore.warehouse.utils.HttpUtils;
import com.amiablecore.warehouse.utils.Session;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class WarehouseTraderForm extends AppCompatActivity implements View.OnClickListener {

    EditText txtTraderName, txtContactNo, txtTraderEmail, txtTraderCity, txtTraderState, txtTraderPinCode;
    Button btnRegisterTrader, btnTraderCancel;
    private Session session;//global variable
    private static final String TAG = "WarehouseTraderForm : ";
    static boolean traderCreated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        setContentView(R.layout.activity_warehouse_trader_form);
        initViews();
    }

    private void initViews() {
        txtTraderEmail = (EditText) findViewById(R.id.txtEmail);
        txtTraderCity = (EditText) findViewById(R.id.txtCity);
        txtTraderState = (EditText) findViewById(R.id.txtState);
        txtTraderPinCode = (EditText) findViewById(R.id.txtPinCode);
        txtTraderName = (EditText) findViewById(R.id.txtTraderName);
        txtContactNo = (EditText) findViewById(R.id.txtTraderContactNo);
        btnTraderCancel = findViewById(R.id.btnTraderCancel);
        btnRegisterTrader = findViewById(R.id.btnRegTrader);

        btnRegisterTrader.setOnClickListener(this);
        btnTraderCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegTrader:
                if (addTrader()) {
                    Toast.makeText(getApplicationContext(),
                            "Registered Trader Info...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(WarehouseTraderForm.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                }
                break;
            case R.id.btnTraderCancel:
                startActivity(new Intent(WarehouseTraderForm.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                break;
        }
    }

    public boolean addTrader() {

        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/trader/create";
                    try {
                        HttpURLConnection conn = HttpUtils.getPostConnection(urlAdress);

                        JSONObject payload = new JSONObject();
                        payload.put("traderName", txtTraderName.getText().toString());
                        payload.put("emailId", txtTraderEmail.getText().toString());
                        payload.put("city", txtTraderCity.getText().toString());
                        payload.put("contactNo", txtContactNo.getText().toString());
                        payload.put("traderState", txtTraderState.getText().toString());
                        payload.put("traderPinCode", txtTraderPinCode.getText().toString());
                        payload.put("whId", session.getFromSession("wh_id"));

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
                            Log.i("Response : ", answer.toString());
                            traderCreated = true;
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
        return traderCreated;
    }
}
