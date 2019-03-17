package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amiablecore.warehouse.beans.Trader;
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

public class WarehouseTraderForm extends AppCompatActivity implements View.OnClickListener {

    EditText txtTraderName, txtContactNo, txtTraderEmail, txtTraderCity, txtTraderState, txtTraderPinCode, txtTraderAddress;
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
        txtTraderAddress = (EditText) findViewById(R.id.traderAddress);
        btnTraderCancel = findViewById(R.id.btnTraderCancel);
        btnRegisterTrader = findViewById(R.id.btnRegTrader);

        btnRegisterTrader.setOnClickListener(this);
        btnTraderCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegTrader:
                if (validateFields()) {
                    break;
                }
                if (addTrader()) {
                    Toast.makeText(getApplicationContext(),
                            "Registered Trader Info...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(WarehouseTraderForm.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                } else {
                    showTraderPresentMessage();
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
                        payload.put("whAdminId", session.getFromSession("wh_id"));
                        payload.put("traderAddress", txtTraderAddress.getText().toString());

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

                        if (conn.getResponseCode() == 200) {
                            traderCreated = false;
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

    public void showTraderPresentMessage() {
        Toast.makeText(getApplicationContext(),
                "Trader Already Present",
                Toast.LENGTH_SHORT).show();
    }

    public boolean validateFields() {

        if (FieldsValidator.isEmpty(txtTraderEmail)) {
            FieldsValidator.setError(txtTraderEmail, StaticConstants.ERROR_ADD_TRADER_EMAIL_MSG);
            return true;
        }
        if (!FieldsValidator.isValidEmail(txtTraderEmail.getText().toString())) {
            FieldsValidator.setError(txtTraderEmail, StaticConstants.ERROR_VALID_TRADER_EMAIL_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtTraderName)) {
            FieldsValidator.setError(txtTraderName, StaticConstants.ERROR_ADD_TRADER_NAME_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtContactNo)) {
            FieldsValidator.setError(txtContactNo, StaticConstants.ERROR_ADD_CONTACT_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtTraderState)) {
            FieldsValidator.setError(txtTraderState, StaticConstants.ERROR_ADD_TRADER_STATE_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtTraderCity)) {
            FieldsValidator.setError(txtTraderCity, StaticConstants.ERROR_ADD_TRADER_CITY_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtTraderPinCode)) {
            FieldsValidator.setError(txtTraderPinCode, StaticConstants.ERROR_ADD_TRADER_PIN_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtTraderAddress)) {
            FieldsValidator.setError(txtTraderAddress, StaticConstants.ERROR_ADD_TRADER_ADDRESS);
            return true;
        }
        FieldsValidator.clearError(txtTraderEmail);
        FieldsValidator.clearError(txtTraderName);
        FieldsValidator.clearError(txtContactNo);
        FieldsValidator.clearError(txtTraderState);
        FieldsValidator.clearError(txtTraderCity);
        FieldsValidator.clearError(txtTraderPinCode);
        return false;
    }
}
