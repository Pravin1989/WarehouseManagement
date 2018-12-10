package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class WarehouseCategoryForm extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText txtCategoryName;
    Button btnSaveCategory, btnCategoryCancel;
    private Spinner cmbCommodity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_category_form);
        initViews();
    }

    private void initViews() {
        txtCategoryName = (EditText) findViewById(R.id.txtCategory);
        btnCategoryCancel = findViewById(R.id.btnCancelCategory);
        btnSaveCategory = findViewById(R.id.btnAddCategory);

        btnSaveCategory.setOnClickListener(this);
        btnCategoryCancel.setOnClickListener(this);
        cmbCommodity = (Spinner) findViewById(R.id.cmbCategoryComodity);
        cmbCommodity.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddCategory:
                Toast.makeText(getApplicationContext(),
                        "Category is added...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnCancelCategory:
                startActivity(new Intent(WarehouseCategoryForm.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                break;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText(parent.getContext(),
                "Selected  : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public boolean addCategory() {
        //The database code will go here to add trader by Admin
        return true;
    }
}
