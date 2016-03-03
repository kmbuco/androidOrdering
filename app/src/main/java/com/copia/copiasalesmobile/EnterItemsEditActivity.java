package com.copia.copiasalesmobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;
import com.copia.copiasalesmobile.utilities.AlertDialogManager;

import java.util.Calendar;

public class EnterItemsEditActivity extends ActionBarActivity {

    public String strAllOrders = "";
    public TextView tvProduct, tvCode, tvPrice, tvTotal, tvComm, tvDateTime;
    public EditText edQuantity;
    public Button btnEdit;
    public Integer int_year, int_month, int_date, int_hour, int_min, int_sec;
    String sCode="", sName="", sComm="", sPrice="", sDesc="";
    String hrs = "hrs";
    ProgressDialog pDialog, PDialog;

    // for database operations
    public DatabaseConnectorSqlite dbconnector;

    AlertDialogManager alert_ = new AlertDialogManager();

    //private long rowID;
    String strSQuantity;
    static final int ADD_CUSTOMER_DIALOG_ID = 1;
    static final int ADD_ORDER_LIST_ID = 2;

    public String sCPhone="", sOrderID="", sType="";
    public String sNewOrderID="", sNewCustomer="";
    String myOrder="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_items_edit);
        setTitle(R.string.edit_title);
        this.setFinishOnTouchOutside(false);

        try{
            // instantiate database handler
            dbconnector = new DatabaseConnectorSqlite(EnterItemsEditActivity.this);

            // textviews
            tvProduct = (TextView) findViewById(R.id.enter_edit_product);
            tvCode = (TextView) findViewById(R.id.enter_edit_code);
            tvPrice = (TextView) findViewById(R.id.enter_edit_price);
            tvTotal = (TextView) findViewById(R.id.enter_edit_total);
            tvComm = (TextView) findViewById(R.id.enter_edit_comm);
            tvDateTime = (TextView) findViewById(R.id.enter_edit_date_time);
            edQuantity = (EditText) findViewById(R.id.enter_edit_qnty);

            Bundle extras = getIntent().getExtras();
            if (extras != null)
            {
                //rowID = extras.getLong("row_id");
                tvProduct.setText(extras.getString("name"));
                tvCode.setText(extras.getString("code"));
                tvComm.setText(extras.getString("comm"));
                //edQuantity.setText(extras.getString("quantity"));
                tvPrice.setText(extras.getString("price"));
                //tvTotal.setText(extras.getString("total"));

                sCPhone = extras.getString("cphone");
                sOrderID = extras.getString("order_id");
                sType = extras.getString("ctype");
                //sCopiaID = extras.getString("copia_id");
            }

            else{
                sOrderID  = "";
            }

            sCode = tvCode.getText().toString();
            sPrice = tvPrice.getText().toString();
            edQuantity.setText("");

            getDateTime();

            edQuantity.setSelection(edQuantity.getText().length());

            edQuantity.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    strSQuantity = s.toString();
                    //Toast.makeText(getApplicationContext(), strSQuantity, Toast.LENGTH_SHORT).show();

                    if(strSQuantity != null && !strSQuantity.equals(""))
                    {
                        Bundle extras = getIntent().getExtras();
                        Double dSelected = Double.parseDouble(strSQuantity);
                        Double commPercent =  Double.parseDouble(extras.getString("comm"))*100/((Integer.parseInt(extras.getString("quantity"))) * (Double.parseDouble(extras.getString("price"))));

                        Double dPrice = Double.parseDouble(sPrice);
                        Double dComm = (dPrice *commPercent*dSelected)/100;
                        String strComm = String.valueOf(dComm);
                        Double dTotal = dSelected * dPrice;
                        String sTotal = String.valueOf(dTotal);
                        Log.e("Commision", strComm + "QUantity is" + extras.getString("quantity") + " Commission is: " + extras.getString("comm") + "commission percentage: " + commPercent);
                        tvComm.setText(strComm);
                        tvTotal.setText(sTotal);
                    }

                    else
                    {
                        tvTotal.setText("0");
                        tvComm.setText("0");
                        //Toast.makeText(getApplicationContext(), "Enter quantity to complete order :-)", Toast.LENGTH_SHORT).show();
                        //strSQuantity = "1";
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            btnEdit = (Button) findViewById(R.id.btnEditItemToTable);
            btnEdit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //Initiate Sqlite CustomerOrderListActivity

                    if (tvProduct.getText().length() !=0 && tvPrice.getText().length()!=0
                            && tvCode.getText().length()!=0 && tvTotal.getText().length()!=0
                            && edQuantity.getText().length()!=0)
                    {
                        if(sOrderID.length() != 0){
                            new updateOrderList().execute();
                        }
                        else
                        {createDialog();}
                    }
                    else
                    {
                        alert_.showAlertDialog(EnterItemsEditActivity.this,
                                "Addition failed...",
                                "Please make sure you have entered the required fields. Thank you :-)", false);
                    }
                }
            });

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void createDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(EnterItemsEditActivity.this);

        alert.setTitle(R.string.failedTitle);
        alert.setMessage(R.string.failedMessage);

        alert.setPositiveButton(R.string.select_btn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int button) {

                        Intent nwOrder = new Intent(getApplicationContext(), AddCustomerListActivity.class);
                        startActivityForResult(nwOrder, ADD_ORDER_LIST_ID);
                    }
                });

        alert.setNegativeButton(R.string.create_btn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int button) {

                        Intent nwOrder = new Intent(getApplicationContext(), AddCustomerActivity.class);
                        startActivityForResult(nwOrder, ADD_CUSTOMER_DIALOG_ID);
                    }
                }).show();
    }

    private class updateOrderList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            PDialog = new ProgressDialog(EnterItemsEditActivity.this);
            PDialog.setTitle("Please Wait..");
            PDialog.setMessage("Updating Item...");
            PDialog.setCancelable(false);
            PDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(EnterItemsEditActivity.this);

            dbConnector.updateOrderTableLinesUsingCode(
                    tvCode.getText().toString(),
                    tvProduct.getText().toString(),
                    tvComm.getText().toString(),
                    tvPrice.getText().toString(),
                    strSQuantity,
                    tvTotal.getText().toString(),
                    tvDateTime.getText().toString(),
                    sOrderID);

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            PDialog.dismiss();
            Intent inbf = new Intent(getApplicationContext(), OrderActivity.class);
            inbf.putExtra("order_id", sOrderID);
            inbf.putExtra("cphone", sCPhone);
            inbf.putExtra("ctype", sType);
            startActivity(inbf);
        }
    }


    // Detect when the back button is pressed
    public void onBackPressed() {
        // Don't let the system handle the back button
        Intent inbf = new Intent(getApplicationContext(), OrderActivity.class);
        inbf.putExtra("order_id", sOrderID);
        inbf.putExtra("cphone", sCPhone);
        inbf.putExtra("ctype", sType);
        startActivity(inbf);
    }

}