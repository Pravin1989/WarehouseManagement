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

public class WarehouseUserForm extends AppCompatActivity implements View.OnClickListener {

    EditText txtName, txtPassword, txtLoginId, txtContactNo;
    Button btnLogin, btnCancel;
    private Session session;//global variable
    private static final String TAG = "WarehouseUserForm : ";
    static boolean userCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new Session(getApplicationContext());
        Log.i(TAG, session.getFromSession("userType"));
        super.onCreate(savedInstanceState);
        Log.i(TAG, session.getFromSession("wh_id"));
        setContentView(R.layout.activity_warehouse_user_form);
        initViews();
    }

    private void initViews() {
        txtName = (EditText) findViewById(R.id.txtUserName);
        txtLoginId = (EditText) findViewById(R.id.txtLoginId);
        txtContactNo = (EditText) findViewById(R.id.txtUserContactNo);
        txtPassword = (EditText) findViewById(R.id.txtUserPassword);
        btnLogin = (Button) findViewById(R.id.btnRegister);
        btnCancel = (Button) findViewById(R.id.btnAdminCancel);
        btnLogin.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                if(validateFields()){
                    break;
                }
                if (addUser()) {
                    Toast.makeText(getApplicationContext(),
                            "User is added ...", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(WarehouseUserForm.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                }

                break;
            case R.id.btnAdminCancel:
                startActivity(new Intent(WarehouseUserForm.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                break;
        }
    }

    public boolean addUser() {

        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/user/create";
                    try {
                        HttpURLConnection conn = HttpUtils.getPostConnection(urlAdress);

                        JSONObject payload = new JSONObject();
                        payload.put("name", txtName.getText().toString());
                        payload.put("loginId", txtLoginId.getText().toString());
                        payload.put("password", txtPassword.getText().toString());
                        payload.put("contactNo", txtContactNo.getText().toString());
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
                            Log.i("Response : ", answer.toString());
                            userCreated = true;
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
        return userCreated;
    }

    public boolean validateFields() {

        if (FieldsValidator.isEmpty(txtName)) {
            FieldsValidator.setError(txtName, StaticConstants.ERROR_ADD_USER_NAME_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtContactNo)) {
            FieldsValidator.setError(txtContactNo, StaticConstants.ERROR_ADD_CONTACT_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtLoginId)) {
            FieldsValidator.setError(txtLoginId, StaticConstants.ERROR_ADD_LOGIN_ID_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtPassword)) {
            FieldsValidator.setError(txtPassword, StaticConstants.ERROR_ADD_PASSWORD_MSG);
            return true;
        }
        FieldsValidator.clearError(txtName);
        FieldsValidator.clearError(txtContactNo);
        FieldsValidator.clearError(txtLoginId);
        FieldsValidator.clearError(txtPassword);
        return false;
    }
}
