package com.copia.copiasalesmobile;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;
import com.copia.copiasalesmobile.utilities.AlertDialogManager;
import com.copia.copiasalesmobile.utilities.Validation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddCustomerActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs" ;
    public EditText edPhone;
    public TextView tvDateTime, tvPhoneExists, tvOrderID;
    public DatabaseConnectorSqlite db;
    public Button btnAdd;
    public Integer int_year, int_month, int_date, int_hour, int_min, int_sec;
    AlertDialogManager alert = new AlertDialogManager();
    String hrs = "hrs";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public SimpleAdapter conAdapter;
    public String sOrderID = "", sEnterPhone = "", sCheckPhone = "", sNewID = "", sType = "",sDeliveryDate= "";

    public static final int DATE_DIALOG_ID_FOR = 0;
    public DatePicker date_picker;
    Button btn_GetDateFor;
    private TextView tv_show_date_for;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        setTitle(R.string.customer_add_title);
        this.setFinishOnTouchOutside(false);

        db = new DatabaseConnectorSqlite(AddCustomerActivity.this);
        sType = "normal";

        btn_GetDateFor = (Button) findViewById(R.id.datepicker_for);
        btn_GetDateFor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "From Clicked...", Toast.LENGTH_SHORT).show();
                showDialog(DATE_DIALOG_ID_FOR);
            }
        });


        tv_show_date_for = (TextView) findViewById(R.id.show_date_for);
        edPhone = (EditText) findViewById(R.id.customer_add_cust_phone);
        tvDateTime = (TextView) findViewById(R.id.customer_add_date_time);
        tvPhoneExists = (TextView) findViewById(R.id.customer_check_exists);
        tvOrderID = (TextView) findViewById(R.id.customer_get_id);

        getDateTime();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            //rowID = extras.getLong(PendingOrdersFragment.ROW_ID);
            sOrderID = extras.getString("order_id");
            edPhone.setText(extras.getString("update_phone"));
        }

        edPhone.setSelection(edPhone.getText().length());
        edPhone.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.isPhoneNumber(edPhone, true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        btnAdd = (Button) findViewById(R.id.btnAddCustPhone);
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    sEnterPhone = edPhone.getText().toString().trim();
                    new CheckExists().execute(sEnterPhone);
                } else {
                    alert.showAlertDialog(AddCustomerActivity.this,
                            "Submission failed...",
                            "Please make sure you have entered the required field. Thank you.", false);
                }
            }

            private boolean checkValidation() {
                boolean ret = true;
                if (!Validation.isPhoneNumber(edPhone, true)) ret = false;
                return ret;
            }
        });
    }

    private class CheckExists extends AsyncTask<String, Object, Cursor> {
        DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(AddCustomerActivity.this);

        @Override
        protected Cursor doInBackground(String... params) {
            dbConnector.open();
            return dbConnector.checkPhoneExists(sEnterPhone);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);
            //check if customer is saved to the customer table
            //ArrayList<Customer> customerArrayList = new ArrayList<Customer>();
            //customerArrayList = db.getCustomerDetails(sEnterPhone);

            //Log.e("The cust reg ", "PROCESS customer exists: " + customerArrayList.isEmpty());
            //if (customerArrayList.size() > 0){
            if (1 > 2){
                //register agent first
                //update the global variable to return agent to Order Process and set the phone number for the registration process.
                editor.putString("flagOrder", "1");
                editor.putString("PhoneOrder", sEnterPhone);
                editor.commit();

                //Intent custReg = new Intent(getApplicationContext(), CustomerRegistation.class);
                //startActivity(custReg);
                finish();
            }else {

                if (result.moveToFirst()) {
                    do {
                        // get the column index for the data item
                        int phoneIndex = result.getColumnIndex("cust_phone_");
                        tvPhoneExists.setText(result.getString(phoneIndex));

                        sCheckPhone = tvPhoneExists.getText().toString();

                        //Toast.makeText(getApplicationContext(), sCheckPhone, Toast.LENGTH_SHORT).show();
                    } while (result.moveToNext());

                    if (sEnterPhone.equals(sCheckPhone))
                        alert.showAlertDialog(AddCustomerActivity.this,
                                "Alert!",
                                "Order for the entered customer phone number is already created. " +
                                        "Thank you :-)", false);
                } else {
                    saveOrder();
                }
                result.close();
                dbConnector.close();
            }
        }

        private void saveOrder() {
            AsyncTask<Object, Object, Object> saveOrderTask =
                    new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Object doInBackground(Object... params) {
                            saveOrderTable();
                            return null;
                        }

                        private void saveOrderTable() {
                            DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(
                                    AddCustomerActivity.this);
                            sDeliveryDate = getDeliveryDate();
                            if (getIntent().getExtras() == null) {

                                dbConnector.insertOrderTable(
                                        edPhone.getText().toString().trim(),
                                        tvDateTime.getText().toString(),
                                        sType,
                                        sDeliveryDate,
                                        "0");
                            } else {
                                dbConnector.updateOrderTable(sOrderID,
                                        edPhone.getText().toString().trim(),
                                        tvDateTime.getText().toString(),
                                        sDeliveryDate
                                ,"0");
                            }
                            //finish();
                        }

                        @Override
                        protected void onPostExecute(Object result) {
                            new getOrderID().execute(sEnterPhone);
                        }
                    };
            saveOrderTask.execute((Object[]) null);
        }
    }

    private String getDeliveryDate() {
        String date = "";
        String current_date;
        String default_delivery_date;
        final Calendar c = Calendar.getInstance();
        int_year = c.get(Calendar.YEAR);
        int_month = c.get(Calendar.MONTH);
        int_date = c.get(Calendar.DAY_OF_MONTH);

        current_date = (new StringBuilder().append(int_year).append("-").append(int_month).append("-").append(int_date)).toString();
        Log.e("The current date is : ",current_date);

        if(tv_show_date_for.getText().toString().contains("eg")){
            date = (new StringBuilder().append(int_year).append("-").append(int_month).append("-").append(int_date+2)).toString();
            Log.e("The default date is : ",date);
        }else{
            date = tv_show_date_for.getText().toString();
            Log.e("The selected date is : ",date);
        }
        return date;
    }

    private void getDateTime() {
        // get the current date and time
        final Calendar c = Calendar.getInstance();
        int_year = c.get(Calendar.YEAR);
        int_month = c.get(Calendar.MONTH);
        int_date = c.get(Calendar.DAY_OF_MONTH);
        int_hour = c.get(Calendar.HOUR_OF_DAY);
        int_min = c.get(Calendar.MINUTE);
        //int_sec = c.get(Calendar.SECOND);

        displayDateTime();
    }

    private void displayDateTime() {
        tvDateTime.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(int_year).append("-").append(int_month + 1).append("-").append(int_date)
                .append("  ").append(int_hour).append(":").append(int_min).append(" ").append(hrs));
    }


    private class getOrderID extends AsyncTask<String, Object, Cursor> {
        DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(AddCustomerActivity.this);

        @Override
        protected Cursor doInBackground(String... params) {
            dbConnector.open();
            return dbConnector.getOrderID(sEnterPhone);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            result.moveToFirst();
            do {
                // get the column index for the data item
                int idIndex = result.getColumnIndex("_id");
                tvOrderID.setText(result.getString(idIndex));
                sNewID = tvOrderID.getText().toString();
            } while (result.moveToNext());

            result.close();
            dbConnector.close();

            Intent nwIntent = new Intent(AddCustomerActivity.this, OrderActivity.class);
            nwIntent.putExtra("order_id", sNewID);
            nwIntent.putExtra("cphone", sEnterPhone);
            nwIntent.putExtra("ctype", sType);
            nwIntent.putExtra("cdate", sType);
            nwIntent.putExtra("cdeliverydate", sDeliveryDate);
            startActivity(nwIntent);
        }
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DATE_DIALOG_ID_FOR){
            DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListenerFor, 2016, 1, 1);
            /*try {
                java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
                for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                    if (datePickerDialogField.getName().equals("mDatePicker")) {
                        datePickerDialogField.setAccessible(true);
                        date_picker = (DatePicker) datePickerDialogField.get(dpd);
                        java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                        for (java.lang.reflect.Field datePickerField : datePickerFields) {
                            Log.i("test", datePickerField.getName());
                            if ("mDaySpinner".equals(datePickerField.getName())) {
                                datePickerField.setAccessible(true);
                                Object dayPicker = datePickerField.get(date_picker);
                                ((View) dayPicker).setVisibility(View.GONE);
                            }
                        }
                    }
                    if(datePickerDialogField.getName().equals("mTitle")){
                        datePickerDialogField.setAccessible(true);
                    }
                }

            }
            catch (Exception ex) {
            }*/
            return dpd;



            /*DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
            Field datePickerFields[] = datePickerDialogField.getType().getDeclaredFields();
            for (Field datePickerField : datePickerFields) {
                if ("mDayPicker".equals(datePickerField.getName())) {
                    datePickerField.setAccessible(true);
                    Object dayPicker = new Object();
                    dayPicker = datePickerField.get(datePicker);
                    ((View) dayPicker).setVisibility(View.GONE);
                }
            }*/
            /*return new DatePickerDialog(this, mDateSetListenerFor, int_year,
                    int_month, int_date);*/
        }
        return null;
    }

    DatePickerDialog.OnDateSetListener mDateSetListenerFor = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            int_year = year;
            int_month = monthOfYear;

            //get last day of month
            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.DATE, dayOfMonth);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.YEAR, year);

                //lastDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                //firstDate = 1;
                displayDate();

        }
    };
    private void displayDate() {
        tv_show_date_for.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(int_year).append("-").append(int_month + 1).append("-").append(int_date));
    }


    // Detect when the back button is pressed
    public void onBackPressed() {
        // Don't let the system handle the back button
        //Intent intDB = new Intent(getApplicationContext(), DashBoard.class);
        //startActivity(intDB);
    }
}