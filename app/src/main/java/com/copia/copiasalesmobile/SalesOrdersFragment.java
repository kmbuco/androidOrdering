package com.copia.copiasalesmobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;
import com.copia.copiasalesmobile.utilities.MyApp;
import com.copia.copiasalesmobile.utilities.Order;
import com.copia.copiasalesmobile.utilities.getOrders;

public class SalesOrdersFragment extends ListFragment {
    public static final String ROW_ID = "row_id";
    public TextView tvPhone, tvDateTime;
    String[] items = {"Details...", "Edit Customer", "Delete Order"};
    public String sOrderID = "";

    public CursorAdapter conAdapter;

    private static final String TAG_ORDER_ID = "_id";
    private static final String TAG_CUST_PHONE = "cust_phone_";
    private static final String TAG_DATE_TIME = "date_time_";
    private static final String TAG_TYPE = "type_";


    //Mandatory Constructor
    public SalesOrdersFragment() {
    }


	@SuppressWarnings("deprecation")
	 @Override
	 public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		ListView conListView = getListView();
		conListView.setOnItemClickListener(viewConListener);
		conListView.setOnItemLongClickListener(viewConListenerLong);




		tvPhone = (TextView) getActivity().findViewById(R.id.pending_list_cust_phone);
		tvDateTime = (TextView) getActivity().findViewById(R.id.pending_list_date_time);

		//map id, phone, datetime to a TextView
		conAdapter = new SimpleCursorAdapter(getActivity(), R.layout.order_item_card, null,
				new String[]{TAG_ORDER_ID, TAG_CUST_PHONE, TAG_DATE_TIME, TAG_TYPE},
				new int[]{R.id.pending_list_order_id, R.id.pending_list_cust_phone, R.id.pending_list_date_time,
						R.id.pending_list_type});
		setListAdapter(conAdapter); // set adapter

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.listview_order_main, container, false);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		new GetPendingLite().execute();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStop() {
		Cursor cursor = conAdapter.getCursor();

		if (cursor != null)
			cursor.deactivate();

		conAdapter.changeCursor(null);
		super.onStop();
	}

	private class GetPendingLite extends AsyncTask<Object, Object, Cursor> {
		DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(getActivity());
		ProgressDialog progress = new ProgressDialog(getActivity());

		protected void onPreExecute() {
			try {
				progress.setMessage("Fetching...");
				progress.show();
			} catch (Exception e) {
			}
		}

		@Override
		protected Cursor doInBackground(Object... params) {
			dbConnector.open();
			try {
				//return dbConnector.getAllOrders();
				return dbConnector.getOrders("0");
			} catch (Exception e) {
				Log.e("MYAPP", "exception", e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(Cursor result) {
			if(result.getCount() ==0){

			}else{
				conAdapter.changeCursor(result); // set the adapter's Cursor
			}
			dbConnector.close();
			if(progress != null){
				progress.dismiss();
			}

		}
	}


	AdapterView.OnItemClickListener viewConListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
//		    	   rowID = extras.getLong(PendingOrdersFragment.ROW_ID);
//		        	sOrderID = String.valueOf(rowID);

			sOrderID = String.valueOf(arg3);
			//Toast.makeText(getActivity(), sOrderID, Toast.LENGTH_SHORT).show();
			//detailsOrder(arg3, view);
			detailsOrder(sOrderID, view);
		}
	};

	AdapterView.OnItemLongClickListener viewConListenerLong = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, final View view,
									   int position, final long arg3) {
			//rowID = Long.parseLong(ROW_ID);
			//String sOrderID = ((TextView) view.findViewById(R.id.pending_list_order_id)).getText().toString();

			sOrderID = String.valueOf(arg3);
			//Toast.makeText(getActivity(), sOrderID, Toast.LENGTH_SHORT).show();

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Choose:").setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

					if (items[which].equals("Edit Customer")) {
						editOrder(sOrderID, view);
					} else if (items[which].equals("Delete Order")) {
						deleteOrder(sOrderID);
					} else if (items[which].equals("Details..."))
					//{detailsOrder(arg3, view);}
					{
						detailsOrder(sOrderID, view);
					}

					//Toast.makeText(getActivity(), "U clicked "+items[which], Toast.LENGTH_LONG).show();
				}
			});
			builder.show();

			return false;
		}
	};

	private void editOrder(String sOrderID, View view) {
		String sCustPhone = ((TextView) view.findViewById(R.id.pending_list_cust_phone)).getText().toString();

		Intent upCust = new Intent(getActivity(), AddCustomerActivity.class);
		//upCust.putExtra(ROW_ID, arg3);
		upCust.putExtra("order_id", sOrderID);
		upCust.putExtra("update_phone", sCustPhone);
		startActivity(upCust);
	}

	private void detailsOrder(String sOrderID, View view) {
		String sCustPhone = ((TextView) view.findViewById(R.id.pending_list_cust_phone)).getText().toString();
		String sCustType = ((TextView) view.findViewById(R.id.pending_list_type)).getText().toString();
		MyApp myapp = new MyApp();
		DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(getActivity());

		getOrders getorders = new getOrders();
		Order order = new Order();
		order = getorders.getOrder(dbConnector,sCustPhone);
		//Intent viewCon = new Intent(getActivity(), CustomerOrderListActivity.class);
		Intent viewCon = new Intent(getActivity(), OrderActivity.class);
		viewCon.putExtra("order_id", sOrderID);
		viewCon.putExtra("cphone", sCustPhone);
		viewCon.putExtra("ctype", sCustType);
		viewCon.putExtra("cagent_id", order.getAgent_id_());
		viewCon.putExtra("orderStatus", "0");
		startActivity(viewCon);
	}

	private void deleteOrder(final String sOrderID) {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle(R.string.confirmTitle);
		alert.setMessage(R.string.confirmMessage);

		alert.setPositiveButton(R.string.delete_btn,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int button) {

						final DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(
								getActivity());

						AsyncTask<String, Object, Object> deleteTask = new AsyncTask<String, Object, Object>() {

							@Override
							protected Object doInBackground(String... params) {
								//dbConnector.deleteOrderLines(params[0]);
								dbConnector.deleteOrder(sOrderID);
								return null;
							}

							@Override
							protected void onPostExecute(Object result) {
								//finish();
								//new GetPendingLite().execute((Object[]) null);
								Intent hm = new Intent(getActivity(), HomeScreen.class);
								startActivity(hm);
							}
						};

						deleteTask.execute(new String[]{sOrderID});
					}
				});

		alert.setNegativeButton(R.string.cancel_btn, null).show();
	}

	private class ClearOrders extends AsyncTask<Object, Object, Cursor> {
		DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(getActivity());

		@Override
		protected Cursor doInBackground(Object... params) {
			dbConnector.open();
			return dbConnector.deleteOrderTable();
			//return dbConnector.deleteOrderLinesTable();
		}

		@Override
		protected void onPostExecute(Cursor result) {
			conAdapter.changeCursor(result); // set the adapter's Cursor
			dbConnector.close();
		}

	}
}
