package com.amiablecore.warehouse.utils;

import android.graphics.Color;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class FieldsValidator {

    public static boolean isEmpty(EditText editText) {
        String input = editText.getText().toString().trim();
        return input.length() == 0;

    }

    public static void setError(EditText editText, String errorString) {
        editText.requestFocus();
        editText.setError(errorString);

    }

    public static void clearError(EditText editText) {
        editText.setError(null);

    }

    public static void clearErrorToSpinner(Spinner spinner) {
        TextView errorText = (TextView) spinner.getSelectedView();
        errorText.setError(null);
    }

    public static boolean isItemSelectedInSpinner(Spinner spinner) {
        TextView errorText = (TextView) spinner.getSelectedView();
        if (spinner.getSelectedItem().toString().equals(StaticConstants.SELECT_CATEGORY)) {
            errorText.setError(" ");
            errorText.setTextColor(Color.RED);
            errorText.setText(StaticConstants.ERROR_CATEGORY_MSG_SELECT);
            return true;
        } else if (spinner.getSelectedItem().toString().equals(StaticConstants.SELECT_COMMODITY)) {
            errorText.setError(" ");
            errorText.setTextColor(Color.RED);
            errorText.setText(StaticConstants.ERROR_COMMODITY_MSG_SELECT);
            return true;
        }
        clearErrorToSpinner(spinner);
        return false;
    }
}
