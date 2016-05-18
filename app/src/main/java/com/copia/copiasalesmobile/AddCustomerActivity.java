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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddCustomerActivity extends AppCompatActivity {
    //add the reference if exists
    public static final String MyPREFERENCES = "MyPrefs" ;
    public EditText edPhone, edRef;
    public TextView tvDateTime, tvPhoneExists, tvOrderID;
    public DatabaseConnectorSqlite db;
    public Button btnAdd;
    public Integer int_year, int_month, int_date, int_hour, int_min, int_sec,current_int_year,current_int_month,current_int_date;
    AlertDialogManager alert = new AlertDialogManager();
    String hrs = "hrs";
    String sAgent_id = "";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public SimpleAdapter conAdapter;
    public String sOrderID = "", sEnterPhone = "", sCheckPhone = "", sNewID = "", sType = "",sDeliveryDate= "", sOrder_Status ="", sDate_time ="",sRef;

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
        edRef = (EditText) findViewById(R.id.input_ref);

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
                //sDeliveryDate = getDeliveryDate();
                if (checkValidation()) {
                    sEnterPhone = edPhone.getText().toString().trim();
                    sRef = edRef.getText().toString().trim();
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
            return dbConnector.checkReferenceExists(sEnterPhone,sRef);
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
                    moveToOrder();
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

                                /*newCon.put("cust_phone_", sPhone);
                                newCon.put("date_time_", sDateTime);
                                newCon.put("type_", sType);
                                newCon.put("expected_delivery_date_", sDeliveryDate);
                                newCon.put("order_status_", sStatus);
                                newCon.put("agent_id_", "");*/

                                dbConnector.insertOrderTable(
                                        edPhone.getText().toString().trim(),
                                        tvDateTime.getText().toString().trim(),
                                        sType,
                                        sDeliveryDate,
                                        "0",sRef);
                            } else {
                                dbConnector.updateOrderTable(sOrderID,
                                        edPhone.getText().toString().trim(),
                                        tvDateTime.getText().toString().trim(),
                                        sDeliveryDate
                                        ,"0",sRef);
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

    private void moveToOrder() {
        AsyncTask<Object, Object, Object> moveToOrderTask =
                new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        updateOrderTable();
                        return null;
                    }

                    private void updateOrderTable() {
                        DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(
                                AddCustomerActivity.this);
                        //sDeliveryDate = getDeliveryDate();
                        dbConnector.open();
                        Cursor result =  dbConnector.getOrderID(sEnterPhone);

                        result.moveToFirst();
                        do {
                            // get the column index for the data item
                            int idIndex = result.getColumnIndex("_id");
                            int agentIdIndex = result.getColumnIndex("agent_id_");
                            int statusIndex = result.getColumnIndex("order_status_");
                            int phoneIndex = result.getColumnIndex("cust_phone_");
                            int datesIndex = result.getColumnIndex("date_time_");
                            int deliveryDateIndex = result.getColumnIndex("expected_delivery_date_");
                            tvOrderID.setText(result.getString(idIndex));
                            sOrderID = result.getString(idIndex);
                            sCheckPhone = result.getString(phoneIndex);
                            sAgent_id = result.getString(agentIdIndex);
                            sOrder_Status = result.getString(statusIndex);
                            sDate_time = result.getString(datesIndex);
                            sDeliveryDate = result.getString(deliveryDateIndex);
                            sNewID = tvOrderID.getText().toString();
                        } while (result.moveToNext());

                        result.close();
                        dbConnector.close();

                        Log.e("The update: ", sOrderID + " " + sCheckPhone + " " + sDate_time+" "+ sDeliveryDate+ " " +sAgent_id +" "+sOrder_Status);
                        dbConnector.updateOrderTable(sOrderID,
                                sCheckPhone,
                                sDate_time,
                                sDeliveryDate
                                , "0",sAgent_id);

                        //finish();
                    }

                    @Override
                    protected void onPostExecute(Object result) {
                        new getOrderID().execute(sEnterPhone);
                    }
                };
        moveToOrderTask.execute((Object[]) null);
    }
    private String getDeliveryDate() {
        String date = "";
        String current_date = "";
        String current_day;
        int current_hour,current_minute;
        String default_delivery_date = "";
        //getDateTime();
        final Calendar c = Calendar.getInstance();
        current_int_year = c.get(Calendar.YEAR);
        current_int_month = c.get(Calendar.MONTH);
        current_int_date = c.get(Calendar.DAY_OF_MONTH);
        current_hour = c.get(Calendar.HOUR_OF_DAY);
        current_minute = c.get(Calendar.MINUTE);

        current_date = (new StringBuilder().append(current_int_year).append("-").append(current_int_month+1).append("-").append(current_int_date)).toString();


        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.UK);
        Calendar calendar = Calendar.getInstance();
        current_day = dayFormat.format(calendar.getTime());
        Log.e("The day today: ", current_day);

        current_day = current_day.toLowerCase();
        String[] matches = new String[] {"mon", "tue", "wed", "thu"};
        for (String s : matches)
        {
            if (current_day.contains(s))
            {
                if(current_hour <= 9 && current_minute <= 30){
                    current_int_date = current_int_date+1;
                }else{
                    current_int_date = current_int_date+2;
                }
                break;
            }else if(current_day.contains("fri")){
                if(current_hour <= 9 && current_minute <= 30){
                    current_int_date = current_int_date+1;
                }else{
                    current_int_date = current_int_date+3;
                }
                break;
            }else if(current_day.contains("sat")){
                if(current_hour <= 9 && current_minute <= 30){
                    current_int_date = current_int_date+2;
                }else{
                    current_int_date = current_int_date+3;
                }
                break;
            }else if(current_day.contains("sun")){

                current_int_date = +2;

                break;
            }
        }

        date = (new StringBuilder().append(current_int_year).append("-").append(current_int_month+1).append("-").append(current_int_date)).toString();
        Log.e("The current date is : ",current_date);

        if(tv_show_date_for.getText().toString().contains("eg")){
            //date = (new StringBuilder().append(int_year).append("-").append(int_month).append("-").append(int_date+2)).toString();
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
                    .append(int_year).append("-").append(int_month + 1).append("-").append(int_date));

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
                int agentIdIndex = result.getColumnIndex("agent_id_");
                int statusIndex = result.getColumnIndex("order_status_");
                int datesIndex = result.getColumnIndex("date_time_");
                int deliveryDateIndex = result.getColumnIndex("expected_delivery_date_");
                tvOrderID.setText(result.getString(idIndex));
                sAgent_id = result.getString(agentIdIndex);
                sOrder_Status = result.getString(statusIndex);
                sDate_time = result.getString(datesIndex);
                sDeliveryDate = result.getString(deliveryDateIndex);
                sNewID = tvOrderID.getText().toString();
            } while (result.moveToNext());

            result.close();
            dbConnector.close();


            //moveToOrder();

            Intent nwIntent = new Intent(AddCustomerActivity.this, OrderActivity.class);
            nwIntent.putExtra("order_id", sNewID);
            nwIntent.putExtra("cphone", sEnterPhone);
            nwIntent.putExtra("ctype", sType);
            nwIntent.putExtra("cdate", sType);
            nwIntent.putExtra("cdeliverydate", sDeliveryDate);
            if(sAgent_id != null && !sAgent_id.equals("")){
                nwIntent.putExtra("cagent_id", sAgent_id);
            }else{
                nwIntent.putExtra("cagent_id", "");
            }
            nwIntent.putExtra("orderStatus", "0");
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
            int_month = monthOfYear+1; //month is zero based so add 1
            int_date = dayOfMonth;

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
        sDeliveryDate = int_year+"-"+int_month+"-"+int_date;
        tv_show_date_for.setText(new StringBuilder()
                .append(int_year).append("-").append(int_month).append("-").append(int_date));
    }


    // Detect when the back button is pressed
    public void onBackPressed() {
        // Don't let the system handle the back button
        //Intent intDB = new Intent(getApplicationContext(), DashBoard.class);
        //startActivity(intDB);
    }
}