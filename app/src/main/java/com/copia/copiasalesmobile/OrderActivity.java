package com.copia.copiasalesmobile;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.gsm.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.copia.copiasalesmobile.Adapter.CustomAutoCompleteAgentArrayAdapterAdd;
import com.copia.copiasalesmobile.Adapter.CustomAutoCompleteArrayAdapterAdd;
import com.copia.copiasalesmobile.Adapter.DrawerListAdapter;
import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;
import com.copia.copiasalesmobile.odooConn.Login;
import com.copia.copiasalesmobile.openErp.Functions;
import com.copia.copiasalesmobile.services.ServiceSyncAgent;
import com.copia.copiasalesmobile.services.ServiceSyncProd;
import com.copia.copiasalesmobile.utilities.AgentSearchObject;
import com.copia.copiasalesmobile.utilities.AlertDialogManager;
import com.copia.copiasalesmobile.utilities.CheckConnection;
import com.copia.copiasalesmobile.utilities.CustomAutoCompleteAgentTextChangedListenerAdd;
import com.copia.copiasalesmobile.utilities.CustomAutoCompleteTextChangedListenerAdd;
import com.copia.copiasalesmobile.utilities.CustomAutoCompleteView;
import com.copia.copiasalesmobile.utilities.NavItem;
import com.copia.copiasalesmobile.utilities.Order;
import com.copia.copiasalesmobile.utilities.ProdSearchObject;
import com.copia.copiasalesmobile.utilities.getOrders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class OrderActivity extends AbstractBaseActivity {

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    String sOrderStatus;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    TextView serveLocation;
    public TextView tvCode, tvPrice, tvTotal, tvComm, tvGrandTotal, tvGrandTotalComm, tvDateTime, tvCheckCode, tvCopiaID, tvDesc;
    public Integer int_year, int_month, int_date, int_hour, int_min, int_sec;
    int commPercent;
    String sCode = "", sName = "", sComm = "", sPrice = "", sDesc = "", sImage = "", sCopiaID = "", slabel = "", sTotal = "";
    public String sOrderID = "", sCPhone = "", sNewOrderID = "", sNewCustomer = "", sEnterCode = "",
            sCheckCode = "", sCheckCopiaID = "", sCountValue = "", sCountUpdate = "", sType = "", sDeliveryDate = "";
    public ImageView imageView;
    public EditText edQuantity;
    String strSQuantity;
    public Bitmap bitmap;
    public TableLayout tableLayout;
    public TableRow row;
    public Button btnAdd, btnSendOrder, btnCommon;
    public ImageButton btnClear;
    public CheckBox cbLayaway;
    public String strAllOrders = "";
    private static final String TAG_TOTAL = "total_";
    private static final String TAG_COMM = "comm_";
    private static final String TAG_CODE = "code_";
    private static final String TAG_QUANTITY = "quantity_";

    String sAgentId,sAgentName,sAgentPhone;
    public CustomAutoCompleteView myAutoAgentComplete;
    public CustomAutoCompleteView myAutoProdComplete;
    public DatabaseConnectorSqlite dbconnector;
    CheckConnection utilityConn;
    ProgressDialog pDialog, PDialog;
    public Double dSelected, dPrice, dTotal;
    Button btnSend;
    String agentId;
    int order_id;

    AlertDialogManager alert_ = new AlertDialogManager();
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    String hrs = "hrs";
    String[] items = {"Edit Item", "Delete Item", "Item Details"};

    // adapter for auto-complete
    public CustomAutoCompleteArrayAdapterAdd myAdapter;
    public CustomAutoCompleteAgentArrayAdapterAdd myAgentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolBarIcon(R.drawable.ic_shopping_cart);
        setToolBarTitle(getResources().getString(R.string.app_name));

        Log.e("test:", "Test login");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        serveLocation = (TextView) findViewById(R.id.server_location);
        tvDateTime = (TextView) findViewById(R.id.enter_date_time);
        myAutoAgentComplete = (CustomAutoCompleteView) findViewById(R.id.input_agent_name);
        tvDesc = (TextView) findViewById(R.id.enter_desc);
        myAutoProdComplete = (CustomAutoCompleteView) findViewById(R.id.input_product);
        utilityConn = new CheckConnection();
        // ObjectItemData has no value at first
        ProdSearchObject[] ObjectProdItemData = new ProdSearchObject[0];
        AgentSearchObject[] ObjectAgentItemData = new AgentSearchObject[0];

        Bundle extras = getIntent().getExtras();

        if (getIntent().getExtras() != null) {
            //rowID = extras.getLong(PendingOrdersFragment.ROW_ID);
            //sOrderID = String.valueOf(rowID);
            sCPhone = extras.getString("cphone");
            sOrderID = extras.getString("order_id");
            sDeliveryDate = extras.getString("cdeliverydate");
            sType = extras.getString("ctype");
            sOrderStatus = extras.getString ("orderStatus");
            setTitle("Order List for " + sCPhone);

            Log.e("Order Type:", sType);
        } else {
            sOrderID = "";
            sCPhone = "";
        }

        dbconnector = new DatabaseConnectorSqlite(OrderActivity.this);

        myAutoProdComplete.addTextChangedListener(new CustomAutoCompleteTextChangedListenerAdd(this));
        myAdapter = new CustomAutoCompleteArrayAdapterAdd(this, R.layout.list_item_search_product, ObjectProdItemData);
        myAutoProdComplete.setAdapter(myAdapter);

        // textviews
        tvCode = (TextView) findViewById(R.id.enter_code);
        tvPrice = (TextView) findViewById(R.id.enter_price);
        tvTotal = (TextView) findViewById(R.id.enter_total);
        tvComm = (TextView) findViewById(R.id.enter_comm);
        tvCopiaID= (TextView) findViewById(R.id.enter_copia_ID);
        tvCheckCode = (TextView) findViewById(R.id.enter_check_code);
        tvGrandTotal = (TextView) findViewById(R.id.enter_grand_total);
        tvGrandTotalComm = (TextView) findViewById(R.id.enter_grand_total_comm);
        edQuantity = (EditText) findViewById(R.id.enter_qnty);
        imageView = (ImageView) findViewById(R.id.enter_imageView);
        tableLayout = (TableLayout) findViewById(R.id.tableLayoutItems);
        cbLayaway = (CheckBox) findViewById(R.id.cb_layaway);

        getDateTime();
        buildTable();
        try {
            getGrandTotal();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        myAutoProdComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                // getting values from selected ListItem
                sCode = ((TextView) arg1.findViewById(R.id.search_code)).getText().toString();
                sName = ((TextView) arg1.findViewById(R.id.search_name)).getText().toString();
                sComm = ((TextView) arg1.findViewById(R.id.search_comm)).getText().toString();
                sPrice = ((TextView) arg1.findViewById(R.id.search_price)).getText().toString();
                sImage = ((TextView) arg1.findViewById(R.id.search_icon_tv)).getText().toString();
                sCode = ((TextView) arg1.findViewById(R.id.search_code)).getText().toString();
                sDesc = ((TextView) arg1.findViewById(R.id.search_desc)).getText().toString();
                sCopiaID = ((TextView) arg1.findViewById(R.id.search_copia_id)).getText().toString();
                Log.e("The ID: ", sCopiaID);

                //calculate the commission
                commPercent = Integer.parseInt(sComm);
                int prodPrice = Integer.parseInt(sPrice);

                int prodComm = (prodPrice * commPercent) / 100;

                sComm = Integer.toString(prodComm);

                myAutoProdComplete.setText(sName);
                tvCode.setText(sCode);
                tvPrice.setText(sPrice);
                tvComm.setText(sComm);
//                tvDesc.setText(sDesc);
                tvCopiaID.setText(sCopiaID);
                tvCheckCode.setText(sCode);

                myAutoProdComplete.setSelection(myAutoProdComplete.getText().length());
                myAutoProdComplete.setFocusable(false);
                myAutoProdComplete.setFocusableInTouchMode(true);
                edQuantity.setText("");
                edQuantity.setFocusable(true);
                edQuantity.setSelection(0);
                //edQuantity.setFocusableInTouchMode(true);

                try {
                    byte[] outImage = Base64.decode(sImage, Base64.DEFAULT);
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
                    Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(theImage);
                } catch (Exception e) {
                    imageView.setImageResource(R.drawable.noimage);
                }
            }
        });
        Log.e("Selected CopiaID = ", sCopiaID);

        if (myAutoProdComplete.getText().length() == 0) {
            myAutoProdComplete.setText("");
            myAutoProdComplete.setFocusable(true);
            myAutoProdComplete.setSelection(0);
            tvCode.setText("");
            tvPrice.setText("");
            tvComm.setText("");
            edQuantity.setText("");
            edQuantity.setFocusable(false);
            edQuantity.setFocusableInTouchMode(true);
            tvTotal.setText("");
            imageView.setImageResource(R.drawable.noimage);
        }
        // add the listener so it will tries to suggest while the user types
        myAutoProdComplete.addTextChangedListener(new CustomAutoCompleteTextChangedListenerAdd(this));
        // set the custom ArrayAdapter
        myAdapter = new CustomAutoCompleteArrayAdapterAdd(this, R.layout.list_item_search_product, ObjectProdItemData);
        myAutoProdComplete.setAdapter(myAdapter);

        myAutoAgentComplete.addTextChangedListener(new CustomAutoCompleteAgentTextChangedListenerAdd(this));
        myAgentAdapter = new CustomAutoCompleteAgentArrayAdapterAdd(this, R.layout.list_item_search_agent, ObjectAgentItemData);
        myAutoAgentComplete.setAdapter(myAgentAdapter);

        myAutoAgentComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                sAgentId = ((TextView) arg1.findViewById(R.id.txt_agent_id)).getText().toString();
                sAgentName = ((TextView) arg1.findViewById(R.id.txt_agent_name)).getText().toString();
                sAgentPhone = ((TextView) arg1.findViewById(R.id.txt_agent_phone)).getText().toString();

                myAutoAgentComplete.setText(sAgentName);

            }
        });


        edQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strSQuantity = s.toString();
                Log.e("!!@@!!@@!!@@!! ", strSQuantity);
                if (strSQuantity != null && !strSQuantity.equals("")) {
                    dSelected = Double.parseDouble(strSQuantity);
                    dPrice = Double.parseDouble(sPrice);
                    double dComm = (dPrice * commPercent * dSelected) / 100;
                    sComm = String.valueOf(dComm);
                    dTotal = dSelected * dPrice;
                    sTotal = String.valueOf(dTotal);
                    tvComm.setText(sComm);
                    tvTotal.setText(sTotal);
                } else {
                    tvTotal.setText("");
                    tvComm.setText("");
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


        btnAdd = (Button) findViewById(R.id.btnAddItemToTable);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myAutoProdComplete.getText().length() != 0 && tvPrice.getText().length() != 0
                        && tvCode.getText().length() != 0 && tvTotal.getText().length() != 0
                        && edQuantity.getText().length() != 0) {

                    int qntyValue = Integer.parseInt(edQuantity.getText().toString());
                    if (qntyValue > 0) {


                        sEnterCode = tvCode.getText().toString().trim();
                        if (sOrderID.length() != 0) {
                            new CheckItemExists().execute(sEnterCode, sOrderID);
                        }
                    } else {
                        alert_.showAlertDialog(OrderActivity.this,
                                "Addition failed...",
                                "Please make sure you have entered a quantity more than zero. Thank you :-)", false);
                    }
                } else {
                    alert_.showAlertDialog(OrderActivity.this,
                            "Addition failed...",
                            "Please make sure you have entered the required fields. Thank you :-)", false);
                }
            }
        });

        //send the Order here

        btnSend = (Button) findViewById(R.id.btnEnterSendOrder);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send SMS
                if (tableLayout.getChildCount() != 0) {
                    if (sCPhone.length() != 0) {

                        btnSend.setEnabled(false);
                        btnSend.setVisibility(View.INVISIBLE);
                        tvGrandTotal.setText("");
                        tvGrandTotalComm.setText("");
                        new SendOrder().execute();
                    } else {
                        alert_.showAlertDialog(OrderActivity.this,
                                "Submission Failed...",
                                "Customer not added"
                                        + "\n" + "Please add customer phone number to complete Order :-)", false);
                    }
                } else {
                    alert_.showAlertDialog(OrderActivity.this,
                            "Submission Failed...",
                            "There are no items available to send an Order"
                                    + "\n" + "Please add items to complete Order :-)", false);
                }
            }
        });

        if(sOrderStatus.equals("1")){ //order has already been made disable the controls.
            btnSend.setVisibility(View.GONE);
            btnAdd.setVisibility(View.GONE);
            TextInputLayout txtAgentName = (TextInputLayout) findViewById(R.id.wrapper_name);
            TextInputLayout txtProdName = (TextInputLayout) findViewById(R.id.wrapper_prod_search);
            ImageButton searchProd = (ImageButton) findViewById(R.id.btnClear);
            ImageButton searchName = (ImageButton) findViewById(R.id.btnSearchName);
            txtAgentName.setVisibility(View.GONE);
            txtProdName.setVisibility(View.GONE);
            searchProd.setVisibility(View.GONE);
            searchName.setVisibility(View.GONE);
        }if(sOrderStatus.equals("0")) { //order has already been made disable the controls.
            btnSend.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.VISIBLE);
            TextInputLayout txtAgentName = (TextInputLayout) findViewById(R.id.wrapper_name);
            TextInputLayout txtProdName = (TextInputLayout) findViewById(R.id.wrapper_prod_search);
            ImageButton searchProd = (ImageButton) findViewById(R.id.btnClear);
            ImageButton searchName = (ImageButton) findViewById(R.id.btnSearchName);
            txtAgentName.setVisibility(View.VISIBLE);
            txtProdName.setVisibility(View.VISIBLE);
            searchProd.setVisibility(View.VISIBLE);
            searchName.setVisibility(View.VISIBLE);
        }


        mNavItems.add(new NavItem("Sync Products", "Get new products, update prices and offers", R.drawable.new_prod));
        mNavItems.add(new NavItem("Sync Your Agent", "Get new agent.", R.drawable.person_sync));
        mNavItems.add(new NavItem("Preferences", "Change your preferences", R.drawable.settings));

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });


    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_order;
    }

    //handles the drawer menu buttons
    private void selectItemFromDrawer(int position) {
        /*Fragment fragment = new PreferencesFragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();

        mDrawerList.setItemChecked(position, true);*/

        setTitle(mNavItems.get(position).mTitle);

        Log.e("get string: ", mNavItems.get(position).mTitle);

        String select = mNavItems.get(position).mTitle;

        if (utilityConn.isOnline() == true) {
            //when product sync is selected
            if (select.contains("Products")) {
                Intent srv = new Intent(getApplicationContext(), ServiceSyncProd.class);
                startService(srv);
            }
        } else {
            //tell the user to connect to the internet
        }


        if (select.contains("Agent")) {
            Intent srv = new Intent(getApplicationContext(), ServiceSyncAgent.class);
            startService(srv);
        }
        // get prompts.xml view
        if (select.contains("Preferences")) {
            LayoutInflater layoutInflater = LayoutInflater.from(OrderActivity.this);

            View promptView = layoutInflater.inflate(R.layout.prompt_config, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderActivity.this);

            // set prompts.xml to be the layout file of the alertdialog builder
            alertDialogBuilder.setView(promptView);

            final EditText input = (EditText) promptView.findViewById(R.id.userInput);

            // setup a dialog window
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // get user input and set it to result
                            serveLocation.append(input.getText());
                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create an alert dialog
            AlertDialog alertD = alertDialogBuilder.create();

            alertD.show();

        }


        // Close the drawer
        //mDrawerLayout.closeDrawer(mDrawerPane);
    }

    private void getDateTime() {
        // get the current date and time
        final Calendar c = Calendar.getInstance();
        int_year = c.get(Calendar.YEAR);
        int_month = c.get(Calendar.MONTH);
        int_date = c.get(Calendar.DAY_OF_MONTH);
        int_hour = c.get(Calendar.HOUR_OF_DAY);
        int_min = c.get(Calendar.MINUTE);

        displayDateTime();
    }

    private void displayDateTime() {
        tvDateTime.setText(new StringBuilder()
                .append(int_year).append("-").append(int_month + 1).append("-").append(int_date)
                .append("  ").append(int_hour).append(":").append(int_min).append(" ").append(hrs));
    }


    private void buildTable() {
        dbconnector.open();
        Cursor cursor = dbconnector.getAllOrderTableLines(sOrderID);
        int rows = cursor.getCount();
        int cols = cursor.getColumnCount();

        cursor.moveToFirst();

        // outer for loop
        for (int i = 0; i < rows; i++) {

            row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            row.setOnClickListener(tablerowOnClickListener);
            row.setOnLongClickListener(tablerowOnClickListenerLong);

            // inner for loop
            for (int j = 0; j < cols; j++) {

                TextView tv = new TextView(this);
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                //tv.setBottom(android.R.drawable.list_selector_background);
                tv.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame);
                tv.setLines(2);
                tv.setGravity(Gravity.LEFT);
                tv.setTextSize(14);
                tv.setPadding(10, 10, 10, 10);

                tv.setText(cursor.getString(j));


                row.addView(tv);
            }

            cursor.moveToNext();
            tableLayout.addView(row);
        }
        dbconnector.close();
    }

    private void getGrandTotal() throws JSONException {
        // Opening database
        DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(OrderActivity.this);
        dbConnector.open();

        Cursor cursor = dbConnector.getAllOrderTableLines(sOrderID);

        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {

                        if (cursor.getString(i) != null) {
                            //Log.d("TAG_NAME", cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i),
                                    cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        //Log.d("TAG_NAME", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        Log.d("TAG_NAME", resultSet.toString());
        dbConnector.close();

        strAllOrders = resultSet.toString();

        JSONArray arrT = new JSONArray(strAllOrders);
        double myGrand = 0;
        double myComm = 0;
        for (int i = 0; i < arrT.length(); i++) {
            JSONObject c = arrT.getJSONObject(i);

            String commGT = c.getString(TAG_COMM);
            myComm += Double.parseDouble(commGT);
            String sGT = c.getString(TAG_TOTAL);
            myGrand += Double.parseDouble(sGT);
        }
        String sGrandTotalComm = String.valueOf(myComm);
        String sGrandTotal = String.valueOf(myGrand);
        tvGrandTotal.setText(sGrandTotal);
        tvGrandTotalComm.setText(sGrandTotalComm);
    }

    private class CheckItemExists extends AsyncTask<String, Object, Cursor> {
        DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(OrderActivity.this);

        @Override
        protected Cursor doInBackground(String... params) {
            dbConnector.open();
            return dbConnector.checkCodeExists(sEnterCode, sOrderID);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            if (result.moveToFirst()) {
                do {
                    // get the column index for the data item
                    int codeIndex = result.getColumnIndex("code_");
                    tvCheckCode.setText(result.getString(codeIndex));

                    sCheckCode = tvCheckCode.getText().toString();

                } while (result.moveToNext());

                if (sEnterCode.equals(sCheckCode))
                    alert_.showAlertDialog(OrderActivity.this,
                            "Alert!",
                            "Item already added in the Order. " +
                                    "Thank you :-)", false);
            } else {
                Log.e("Save Item CopiaID = ", sCopiaID + " " + sName);
                new saveOrderList().execute();
            }
            result.close();
            dbConnector.close();
        }
    }

    private class saveOrderList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tableLayout.removeAllViews();

            PDialog = new ProgressDialog(OrderActivity.this);
            PDialog.setTitle("Please Wait!");
            PDialog.setMessage("Adding Item...");
            PDialog.setCancelable(false);
            PDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(OrderActivity.this);

            if (getIntent().getExtras() != null) {

                dbConnector.insertOrderTableLines(
                        tvCode.getText().toString(),
                        myAutoProdComplete.getText().toString(),
                        tvComm.getText().toString(),
                        tvPrice.getText().toString(),
                        strSQuantity,
                        tvTotal.getText().toString(),
                        tvDateTime.getText().toString(),
                        sOrderID,
                        tvDesc.getText().toString(),
                        sCopiaID);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            buildTable();
            try {
                getGrandTotal();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            myAutoProdComplete.setText("");
            myAutoProdComplete.setFocusable(true);
            myAutoProdComplete.setSelection(0);
            tvCode.setText("");
            tvPrice.setText("");
            tvComm.setText("");
            edQuantity.setText("");
            edQuantity.setFocusable(false);
            edQuantity.setFocusableInTouchMode(true);
            tvTotal.setText("");
            imageView.setImageResource(R.drawable.noimage);

            //loadSpinnerData();
            PDialog.dismiss();
        }
    }





        ///////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////******/////////////////////////////////
        /////////////////////////////////// Test the out put from the copy ////////////////////////////////////////
        private void clearOrder(final String sOrderID) {
            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(OrderActivity.this);

            alert.setTitle(R.string.confirmTitle);
            alert.setMessage(R.string.confirmMessage);

            alert.setPositiveButton(R.string.delete_btn,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int button) {

                            final DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(OrderActivity.this);

                            AsyncTask<String, Object, Object> deleteTask = new AsyncTask<String, Object, Object>() {
                                @Override
                                protected Object doInBackground(String... params) {
                                    //dbConnector.deleteOrderLines(params[0]);
                                    dbConnector.deleteOrder(sOrderID);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Object result) {
                                    //finish() to be changed to the dashboard
                                    Intent hm = new Intent(getApplicationContext(), Login.class);
                                    startActivity(hm);
                                }
                            };
                            deleteTask.execute(sOrderID);
                        }
                    });
            alert.setNegativeButton(R.string.cancel_btn, null).show();
        }

        View.OnClickListener tablerowOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow row = (TableRow) view;
                //TextView tvID = (TextView) row.getChildAt(0);
                TextView tvCode = (TextView) row.getChildAt(0);
                TextView tvProduct = (TextView) row.getChildAt(1);
                TextView tvComm = (TextView) row.getChildAt(2);
                TextView tvPrice = (TextView) row.getChildAt(3);
                TextView tvQnty = (TextView) row.getChildAt(4);
                TextView tvTotal = (TextView) row.getChildAt(5);

                //String scID = tvID.getText().toString();
                String scCode = tvCode.getText().toString();
                String scProduct = tvProduct.getText().toString();
                String scComm = tvComm.getText().toString();
                String scPrice = tvPrice.getText().toString();
                String scQnty = tvQnty.getText().toString();
                String scTotal = tvTotal.getText().toString();

                rowClicked(scCode, scProduct, scComm, scPrice, scQnty, scTotal);
            }
        };

        View.OnLongClickListener tablerowOnClickListenerLong = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                TableRow row = (TableRow) view;
                //TextView tvID = (TextView) row.getChildAt(0);
                TextView tvCode = (TextView) row.getChildAt(0);
                TextView tvProduct = (TextView) row.getChildAt(1);
                // , price, quantity,quantity, comm,total

                TextView tvPrice = (TextView) row.getChildAt(2);
                TextView tvQnty = (TextView) row.getChildAt(3);
                TextView tvComm = (TextView) row.getChildAt(4);
                TextView tvTotal = (TextView) row.getChildAt(5);

                //String scID = tvID.getText().toString();
                String scCode = tvCode.getText().toString();
                String scProduct = tvProduct.getText().toString();


                String scPrice = tvPrice.getText().toString();
                String scQnty = tvQnty.getText().toString();
                String scTotal = tvTotal.getText().toString();

                String scComm = tvComm.getText().toString();
                Double comm = Double.parseDouble(scComm) / Double.parseDouble(scQnty);


                rowClicked(scCode, scProduct, scComm, scPrice, scQnty, scTotal);
                return false;
            }
        };

        private void rowClicked(final String scCode, final String scProduct, final String scComm, final String scPrice, final String scQnty, final String scTotal) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OrderActivity.this);
            builder.setTitle("Choose:").setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                /*switch (items[which]) {
                    case "Edit Item":
                        editOrderItem(scCode, scProduct, scComm, scPrice, scQnty, scTotal);
                        break;
                    case "Delete Item":
                        deleteOrderItem(scCode);
                        break;
                    case "Item Details":
                        detailsOrderItem(scCode);
                        break;
                }*/

                    if (items[which] == "Edit Item") {
                        editOrderItem(scCode, scProduct, scComm, scPrice, scQnty, scTotal);
                    } else if (items[which] == "Delete Item") {
                        deleteOrderItem(scCode);
                    } else if (items[which] == "Item Details") {
                        detailsOrderItem(scCode);
                    }
                }
            });
            builder.show();

        }

        private void editOrderItem(String scCode, String scProduct, String scComm, String scPrice,
                                   String scQnty, String scTotal) {

            Intent inty = new Intent(OrderActivity.this, EnterItemsEditActivity.class);
            //inty.putExtra(ROW_ID, longID);
            inty.putExtra("code", scCode);
            inty.putExtra("name", scProduct);
            inty.putExtra("price", scPrice);
            inty.putExtra("comm", scComm);
            inty.putExtra("quantity", scQnty);
            inty.putExtra("total", scTotal);
            inty.putExtra("order_id", sOrderID);
            inty.putExtra("cphone", sCPhone);
            inty.putExtra("ctype", sType);
            //inty.putExtra("copia_id", sCopiaID);
            startActivity(inty);
        }

        public void deleteOrderItem(final String scCode) {
            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(OrderActivity.this);

            alert.setTitle(R.string.confirmTitle);
            alert.setMessage(R.string.confirmMessage);

            alert.setPositiveButton(R.string.delete_btn,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int button) {

                            final DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(
                                    OrderActivity.this);

                            AsyncTask<String, Object, Object> deleteTask = new AsyncTask<String, Object, Object>() {
                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                    tableLayout.removeAllViews();
                                }

                                @Override
                                protected Object doInBackground(String... params) {
                                    dbConnector.deleteOrderLinesOneItemUsingCode(scCode, sOrderID);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Object result) {
                                    buildTable();
                                    try {
                                        getGrandTotal();
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            };
                            deleteTask.execute(scCode);
                        }
                    });
            alert.setNegativeButton(R.string.cancel_btn, null).show();
        }

        private void detailsOrderItem(String scCode) {
            Intent viewCon = new Intent(OrderActivity.this, OrderItemDetailsActivity.class);
            viewCon.putExtra("code", scCode);
            viewCon.putExtra("order_id", sOrderID);
            viewCon.putExtra("cphone", sCPhone);
            viewCon.putExtra("ctype", sType);
            startActivity(viewCon);
        }

        /**
         * Background Async Task to Send Order
         */
        class SendOrder extends AsyncTask<String, String, Integer> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tableLayout.removeAllViews();
                tvGrandTotal.setText("");
                tvGrandTotalComm.setText("");
                TextView TvAgentId = (TextView) findViewById(R.id.txt_agent_id);

                pDialog = new ProgressDialog(OrderActivity.this);
                pDialog.setMessage("Sending Order...Please wait!");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            protected Integer doInBackground(String... args) {
                // Opening database
                DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(OrderActivity.this);
                dbConnector.open();
                Cursor cursor = dbConnector.getAllOrderTableLines(sOrderID);
                JSONArray resultSet = new JSONArray();
                String product_codes = "";
                String product_quantity = "";
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    int totalColumn = cursor.getColumnCount();
                    JSONObject rowObject = new JSONObject();

                    for (int i = 0; i < totalColumn; i++) {
                        if (cursor.getColumnName(i) != null) {
                            try {
                                if (cursor.getString(i) != null) {
                                    //Log.d("TAG_NAME", cursor.getString(i));
                                   if(cursor.getColumnName(i).equals("copia_product_id_")){
                                       if (product_codes == "")
                                       {
                                           //String copia_id = dbConnector.readId(cursor.getString(i));
                                           product_codes = cursor.getString(i);
                                       }else{
                                           //String copia_id = dbConnector.readId(cursor.getString(i));
                                           product_codes = product_codes +","+cursor.getString(i);

                                       }
                                       Log.e("Them prod codes: ",product_codes);
                                    }

                                    if(cursor.getColumnName(i).equals("quantity_")){
                                        if (product_quantity == "")
                                        {
                                            product_quantity = cursor.getString(i);
                                        }else{
                                            product_quantity = product_quantity +","+cursor.getString(i);
                                        }
                                    }

                                    rowObject.put(cursor.getColumnName(i),
                                            cursor.getString(i));
                                } else {
                                    rowObject.put(cursor.getColumnName(i), "");
                                }
                            } catch (Exception e) {
                                //Log.d("TAG_NAME", e.getMessage());
                            }
                        }
                    }
                    resultSet.put(rowObject);
                    cursor.moveToNext();
                }
                Log.d("TAG_NAME", resultSet.toString());
                dbConnector.close();

                strAllOrders = resultSet.toString();

                Log.e("All the orders are: ",strAllOrders);
                Log.e("All the prod codes: ",product_codes);
                //send the Order using xmlRpc
                Functions func = new Functions(dbConnector);
                //createOrder(String productIds, int agentIds, String phone, String quantities)

                Log.e("The product IDs: ", product_codes);
                if(utilityConn.isOnline()){
                    order_id = func.createOrder(product_codes, sAgentId ,sCPhone,product_quantity,sDeliveryDate);
                }else{
                    getOrders getorder = new getOrders();
                    Order order = getorder.getOrder(dbConnector, sCPhone);
                    String date_time = order.getDate_time_();
                    //store the order to be sent later
                    Log.e("The Order: ", "OrderId "+ sOrderID + "Phone"+ sCPhone +" date_time "+ order.getDate_time_() + " sDeliveryDate "+ sDeliveryDate);
                    dbConnector.updateOrderTable(sOrderID,
                            sCPhone,
                            date_time,
                            sDeliveryDate
                            ,"1");
                    PDialog.dismiss();
                }
                Log.e("The order ID", Integer.toString(order_id));

                return order_id;
            }

            protected void onPostExecute(Integer order_id) {
                Log.e("The order ID", order_id.toString());
                PDialog.dismiss();
            }

        }





    private class checkCopiaIDExists extends AsyncTask<String, Object, Cursor> {
        DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(OrderActivity.this);

        @Override
        protected Cursor doInBackground(String... params) {
            dbConnector.open();
            return dbConnector.checkCommonCopiaIDExists(sCopiaID);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            if (result.moveToFirst()) {
                do {
                    // get the column index for the data item
                    int copiaIDIndex = result.getColumnIndex("copia_product_id_");

                    sCheckCopiaID = result.getString(copiaIDIndex).toString();

                    System.out.println("*****Sent copia_id***** = " + sCopiaID);
                    System.out.println("*****Returned copia_id***** = " + sCheckCopiaID);

                    //Toast.makeText(getApplicationContext(), sCheckPhone, Toast.LENGTH_SHORT).show();
                } while (result.moveToNext());

                if (sCopiaID.equals(sCheckCopiaID))
                    new updateCount().execute();
                System.out.println("^^^^^^^^Update method called^^^^^^^^^^^");
            } else {
                System.out.println("^^^^^^^^Insert method called^^^^^^^^^");
                dbConnector.insertCommonTable(
                        sCopiaID, 1);
                //loadSpinnerData();
            }
            result.close();
            dbConnector.close();
            new getValueCount().execute();
        }
    }
    private class updateCount extends AsyncTask<String, Object, Cursor> {
        DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(OrderActivity.this);

        @Override
        protected Cursor doInBackground(String... params) {
            dbConnector.open();
            return dbConnector.getValueCount(sCopiaID);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);
            int intNewCount;
            if (result.moveToFirst()) {
                do {
                    // get the column index for the data item
                    int valueIndex = result.getColumnIndex("count_");
                    String sValueIndex = result.getString(valueIndex).toString();
                    int intOldCount = Integer.parseInt(sValueIndex);

                    System.out.println("*****String of old count***** = " + sValueIndex);
                    System.out.println("*****Int of old count***** = " + intOldCount);

                    int intAddCount = 1;
                    System.out.println("*****Count to add***** = " + intAddCount);

                    intNewCount = intOldCount + intAddCount;

                    System.out.println("*****New update count***** = " + intNewCount);
                } while (result.moveToNext());

                dbConnector.updateCommonTable(sCopiaID, intNewCount);
                result.close();
                dbConnector.close();
            }
            //loadSpinnerData();
        }
    }

    private class getValueCount extends AsyncTask<String, Object, Cursor> {
        DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(OrderActivity.this);

        @Override
        protected Cursor doInBackground(String... params) {
            dbConnector.open();
            return dbConnector.getValueCount(sCopiaID);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            if (result.moveToFirst()) {
                do {
                    // get the column index for the data item
                    int valueIndex = result.getColumnIndex("count_");
                    sCountValue = result.getString(valueIndex).toString();

                    System.out.println("*****Value of count***** = " + sCountValue);
                } while (result.moveToNext());
                result.close();
                dbConnector.close();
            }
        }
    }


}

