package com.copia.copiasalesmobile.utilities;

import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by mbuco on 2/19/16.
 */
public class Validation {


    // Error Messages
    private static final String REQUIRED_MSG = "required";
    private static final String PHONE_MSG = "invalid phone";

    /**
     * Check if the phone number is valid
     *
     * @param editText EditText
     * @param required True/False
     * @return True/False where there it's a valid phone number.
     */
    public static boolean isPhoneNumber(EditText editText, boolean required) {
        return isValidLocal(editText, "^[0-9]{10}$", PHONE_MSG, required);
    }


    private static boolean isValidLocal(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        if (required && !hasText(editText)) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }

        return true;
    }

    /**
     * check the input field has any text or not
     *
     * @param editText EditText
     * @return true if it contains text otherwise false
     */
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }
}
