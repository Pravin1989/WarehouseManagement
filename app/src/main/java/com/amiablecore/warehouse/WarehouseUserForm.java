package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WarehouseUserForm extends AppCompatActivity implements View.OnClickListener {

    EditText txtUserName, txtPassword;
    Button btnLogin, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_user_form);
        initViews();
    }

    private void initViews() {
        txtUserName = (EditText) findViewById(R.id.txtUserName);
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
                Toast.makeText(getApplicationContext(),
                        "User is added In DB...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnAdminCancel:
                startActivity(new Intent(WarehouseUserForm.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                break;
        }
    }

    public boolean addUser() {
        //The database code will go here to add user by Admin
        return true;
    }
}
