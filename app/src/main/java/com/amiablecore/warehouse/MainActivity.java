package com.amiablecore.warehouse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amiablecore.warehouse.utils.FieldsValidator;
import com.amiablecore.warehouse.utils.HttpUtils;
import com.amiablecore.warehouse.utils.StaticConstants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button btnLogin, btnCancel;
    private Spinner cmbUserTypes;
    private EditText txtUserName, txtPassword;
    private static final String TAG = "Warehouse Main";
    private boolean userPresent;

    private TextView attemptText;
    private int counter = 3;
    private boolean itemChanged = false;
    private String itemValue;
    private boolean adminSubscriptionExpired;

    public static String getUserType() {
        return userType;
    }

    static String userType, whUserId, whAdminId;

    public static String getWhUserId() {
        return whUserId;
    }

    public static String getWhAdminId() {
        return whAdminId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtUserName = (EditText) findViewById(R.id.txtUserName);
        txtPassword = (EditText) findViewById(R.id.txtUserPassword);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        attemptText = (TextView) findViewById(R.id.textView2);
        initViews();
    }

    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        addListenerOnSpinnerItemSelection();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (validateFields()) {
                    break;
                }
                adminSubscriptionExpired = false;
                if (verifyUserCredentials()) {
                    if (adminSubscriptionExpired) {
                        showSubscriptionExpiredMessage();
                        return;
                    }
                    redirectToDashboard();
                    Log.i(TAG, "Logged In...");
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                    txtUserName.setVisibility(View.VISIBLE);
                    txtPassword.setBackgroundColor(Color.RED);
                    counter--;
                    attemptText.setVisibility(View.VISIBLE);
                    attemptText.setText("Attempts Left: " + counter);
//                    if (counter == 0) {
//                        btnLogin.setEnabled(false);
//                    }
                }
                break;
            case R.id.btnCancel:
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                break;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Log.i(TAG, cmbUserTypes.getPrompt().toString());
        this.itemChanged = true;
        this.itemValue = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void addListenerOnSpinnerItemSelection() {
        cmbUserTypes = (Spinner) findViewById(R.id.cmbUserTypes);
        cmbUserTypes.setOnItemSelectedListener(this);
    }

    private boolean verifyUserCredentials() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String loginId = txtUserName.getText().toString();
                    String password = txtPassword.getText().toString();

                    if (cmbUserTypes.getSelectedItem().toString().equals("Admin")) {
                        userType = StaticConstants.WH_ADMIN;
                    } else {
                        userType = StaticConstants.WH_USER;
                    }
                    String urlAdress = HttpUtils.BASE_URL + "/login/" + userType;
                    try {
                        URL url = new URL(urlAdress);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.connect();
                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("loginId", loginId);
                        jsonParam.put("loginPassword", password);

                        Log.i("JSON", jsonParam.toString());
                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(jsonParam.toString());

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
                            Log.i("Response : ", answer.toString());
                            JSONObject obj = new JSONObject(answer.toString());
                            userPresent = (boolean) obj.get("loginIndicator");
                            Log.i("Login Message : ", obj.get("loggedInMessage").toString());
                            Log.i("Warehouse ID : ", obj.get("whId").toString());
                            if (userPresent && StaticConstants.WH_ADMIN.equals(userType)) {
                                whAdminId = obj.get("whId").toString();
                                adminSubscriptionExpired = (boolean) obj.get("adminSubscriptionExpired");
                            } else if (userPresent && StaticConstants.WH_USER.equals(userType)) {
                                whAdminId = obj.get("whId").toString();
                                whUserId = obj.get("userId").toString();
                                adminSubscriptionExpired = (boolean) obj.get("adminSubscriptionExpired");
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
        return userPresent;
    }

    public void showSubscriptionExpiredMessage() {
        Toast.makeText(getApplicationContext(),
                "Admin Subscription Expired ",
                Toast.LENGTH_SHORT).show();
    }

    private void redirectToDashboard() {
        if (StaticConstants.WH_ADMIN.equals(userType)) {
            startActivity(new Intent(MainActivity.this, WarehouseAdminActivity.class));
        } else {
            startActivity(new Intent(MainActivity.this, WarehouseUserActivity.class));
        }
    }

    public boolean validateFields() {

        if (FieldsValidator.isEmpty(txtUserName)) {
            FieldsValidator.setError(txtUserName, StaticConstants.ERROR_LOGIN_ID_MSG);
            return true;
        }
        if (FieldsValidator.isEmpty(txtPassword)) {
            FieldsValidator.setError(txtPassword, StaticConstants.ERROR_LOGIN_PASSWORD_MSG);
            return true;
        }
        FieldsValidator.clearError(txtUserName);
        FieldsValidator.clearError(txtPassword);
        return false;
    }
}
