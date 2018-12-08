package com.amiablecore.warehouse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button btnLogin, btnCancel;
    private Spinner cmbUserTypes;
    EditText txtUserName, txtPassword;

    TextView attemptText;
    int counter = 3;
    boolean itemChanged = false;
    String itemValue;

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
                if (verifyUserCredentials(this.itemValue)) {
                    Toast.makeText(getApplicationContext(),
                            "Logged In...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                    txtUserName.setVisibility(View.VISIBLE);
                    txtPassword.setBackgroundColor(Color.RED);
                    counter--;
                    attemptText.setVisibility(View.VISIBLE);
                    attemptText.setText("Attempts Left: " + counter);
                    if (counter == 0) {
                        btnLogin.setEnabled(false);
                    }
                }
                break;
            case R.id.btnCancel:
                finish();
                break;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
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

    private boolean verifyUserCredentials(String userType) {
        //The database code will go here to check user is present in DB with its user type
        if (txtUserName.getText().toString().equals("admin") && txtPassword.getText().toString().equals("admin")) {
            if (userType.equals("Admin")) {
                startActivity(new Intent(MainActivity.this, WarehouseAdminActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, WarehouseUserActivity.class));
            }
            return true;
        }
        return false;
    }
}
