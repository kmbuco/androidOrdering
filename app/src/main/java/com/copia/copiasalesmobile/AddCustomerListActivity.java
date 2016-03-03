package com.copia.copiasalesmobile;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;

public class AddCustomerListActivity extends ListActivity {
    public static final String ROW_ID = "row_id";
    public EditText edFilter;
    public TextView tvPhone, tvDateTime;
    private ListView conListView;
    DatabaseConnectorSqlite dbConnector;
    public String sOrderID="";

    public CursorAdapter conAdapter;

    // JSON Node names
    private static final String TAG_ORDER_ID = "_id";
    private static final String TAG_CUST_PHONE = "cust_phone_";
    private static final String TAG_DATE_TIME = "date_time_";
    private static final String TAG_TYPE = "type_";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_cust_order_list);
        setTitle(R.string.add_cust_order_title);
        this.setFinishOnTouchOutside(false);

        conListView = getListView();
        conListView.setOnItemClickListener(viewConListener);

        tvPhone = (TextView) findViewById(R.id.pending_list_cust_phone);
        tvDateTime = (TextView) findViewById(R.id.pending_list_date_time);

        //map id, phone, datetime to a TextView
        conAdapter = new SimpleCursorAdapter(this, R.layout.list_item_customers_orders, null,
                new String[] {TAG_ORDER_ID, TAG_CUST_PHONE, TAG_DATE_TIME, TAG_TYPE},
                new int[] {R.id.pending_list_order_id, R.id.pending_list_cust_phone, R.id.pending_list_date_time,
                        R.id.pending_list_type});
        setListAdapter(conAdapter); // set adapter

    }

    @Override
    public void onResume()
    {
        super.onResume();
        new GetPendingLite().execute((Object[]) null);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStop()
    {
        Cursor cursor = conAdapter.getCursor();

        if (cursor != null)
            cursor.deactivate();

        conAdapter.changeCursor(null);
        super.onStop();
    }

    private class GetPendingLite extends AsyncTask<Object, Object, Cursor>
    {
        DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(
                AddCustomerListActivity.this);

        @Override
        protected Cursor doInBackground(Object... params)
        {
            dbConnector.open();
            return dbConnector.getAllOrders();
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            conAdapter.changeCursor(result); // set the adapter's Cursor
            dbConnector.close();

            if (result.getCount() == 0){
                //Orders do not exist
                //Delete Order Table and Order Lines Table and reCreate

                //new ClearOrders().execute((Object[]) null);
            }
        }
    }

    AdapterView.OnItemClickListener viewConListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3)
        {
            String sOrderID = ((TextView) view.findViewById(R.id.pending_list_order_id)).getText().toString();
            String sCPhone = ((TextView) view.findViewById(R.id.pending_list_cust_phone)).getText().toString();
            String sCType = ((TextView) view.findViewById(R.id.pending_list_type)).getText().toString();

            Bundle b = new Bundle();
            b.putString("p_order_id", sOrderID);
            b.putString("p_phone", sCPhone);
            b.putString("p_type", sCType);

            //Add the set of extended data to the intent and start it
            Intent intPush = new Intent();
            intPush.putExtras(b);
            setResult(RESULT_OK, intPush);
            finish();
        }
    };
}
