package com.copia.copiasalesmobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;

import java.io.ByteArrayInputStream;

public class OrderItemDetailsActivity extends ActionBarActivity {
    //private long rowID;
    public String sCode="", sOrderID = "", sCPhone="", sCopiaID="", sType="";
    private TextView tvName, tvCode, tvComm, tvQuantity, tvTotal, tvPrice, tvNameTitle, tvIcon, tvDesc;
    private ImageView imgView;
    Button btnEdit, btnDelete;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item_details);
        setTitle(R.string.customer_item_details_title);


        setUpViews();
        Bundle extras = getIntent().getExtras();
        if(getIntent().getExtras() != null){
            //rowID = extras.getLong("row_id");
            sCode = extras.getString("code");
            sOrderID = extras.getString("order_id");
            sCPhone = extras.getString("cphone");
            sType = extras.getString("ctype");
        }
        else{
            sOrderID  = "";
        }

        //Toast.makeText(getApplicationContext(), sOrderID, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), sCPhone, Toast.LENGTH_SHORT).show();

        btnEdit = (Button) findViewById(R.id.btnEditItem);
        btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Call ProductDetailsEdit
                Intent inty = new Intent(getApplicationContext(), EnterItemsEditActivity.class);
                //inty.putExtra(CustomerOrderListActivity.ROW_ID, rowID);
                inty.putExtra("code", sCode);
                inty.putExtra("name", tvName.getText());
                inty.putExtra("code", tvCode.getText());
                inty.putExtra("comm", tvComm.getText());
                inty.putExtra("quantity", tvQuantity.getText());
                inty.putExtra("price", tvPrice.getText());
                inty.putExtra("total", tvTotal.getText());
                inty.putExtra("order_id", sOrderID);
                inty.putExtra("cphone", sCPhone);
                inty.putExtra("ctype", sType);
                //inty.putExtra("copia_id", sCopiaID);
                startActivity(inty);

            }
        });

        btnDelete = (Button) findViewById(R.id.btnDeleteItem);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteEntry();
            }
        });
    }

    private void setUpViews() {
        tvName = (TextView) findViewById(R.id.order_details_name);
        tvNameTitle = (TextView) findViewById(R.id.order_details_name_title);
        tvCode = (TextView) findViewById(R.id.order_details_code);
        tvComm = (TextView) findViewById(R.id.order_details_comm);
        tvDesc = (TextView) findViewById(R.id.order_details_desc);
        tvPrice = (TextView) findViewById(R.id.order_details_price);
        tvQuantity = (TextView) findViewById(R.id.order_details_quantity);
        tvTotal = (TextView) findViewById(R.id.order_details_total);
        tvIcon = (TextView) findViewById(R.id.order_details_name_image);
        imgView = (ImageView) findViewById(R.id.order_details_imageView);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        new LoadDetails().execute(sCode);
        new LoadImage().execute(sCode);
    }

    private class LoadDetails extends AsyncTask<String, Object, Cursor>
    {
        DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(OrderItemDetailsActivity.this);
        ProgressDialog progress = new ProgressDialog(OrderItemDetailsActivity.this);

        protected void onPreExecute() {

            progress.setMessage("Displaying...");
            progress.show();
        }
        @Override
        protected Cursor doInBackground(String... params)
        {
            dbConnector.open();
            return dbConnector.getOneItem(sCode);
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);
            result.moveToFirst();
            // get the column index for each data item
            int nameIndex = result.getColumnIndex("name_");
            int codeIndex = result.getColumnIndex("code_");
            int commIndex = result.getColumnIndex("comm_");
            int descIndex = result.getColumnIndex("desc_");
            int priceIndex = result.getColumnIndex("price_");
            int quantityIndex = result.getColumnIndex("quantity_");
            int totalIndex = result.getColumnIndex("total_");
            int copiaIDIndex = result.getColumnIndex("copia_product_id_");

            tvName.setText(result.getString(nameIndex));
            tvNameTitle.setText(result.getString(nameIndex));
            tvCode.setText(result.getString(codeIndex));
            tvComm.setText(result.getString(commIndex));
            tvDesc.setText(result.getString(descIndex));
            tvPrice.setText(result.getString(priceIndex));
            tvQuantity.setText(result.getString(quantityIndex));
            tvTotal.setText(result.getString(totalIndex));

            sCopiaID = result.getString(copiaIDIndex).toString();


            String sDPrice = tvPrice.getText().toString();
            String sDCode = tvCode.getText().toString();

            //Toast.makeText(getApplicationContext(), sDCode, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), sDPrice, Toast.LENGTH_SHORT).show();

            result.close();
            dbConnector.close();
            progress.dismiss();
        }
    }

    private class LoadImage extends AsyncTask<String, Object, Cursor>{

        DatabaseConnectorSqlite dbConnector = new DatabaseConnectorSqlite(OrderItemDetailsActivity.this);
        //ProgressDialog progress = new ProgressDialog(OrderItemDetailsActivity.this);

        protected void onPreExecute() {

            //progress.setMessage("Displaying...");
            //progress.show();
        }
        @Override
        protected Cursor doInBackground(String... params)
        {
            dbConnector.open();
            return dbConnector.getOneItemImage(sCode);
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);
            result.moveToFirst();
            // get the column index for each data item
            int imageIndex = result.getColumnIndex("image_");

            tvIcon.setText(result.getString(imageIndex));

            String sImage = tvIcon.getText().toString();

            try{
                byte[] outImage = Base64.decode(sImage, Base64.DEFAULT);
                ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
                Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                imgView.setImageBitmap(theImage);
            }catch(Exception e){
                imgView.setImageResource(R.drawable.noimage);
            }

            result.close();
            dbConnector.close();
            //progress.dismiss();
        }
    }

    public boolean onCreateOptionsMenu1(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back, menu);
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_order_edit_delete, menu);
        return true;
    }

    public boolean onOptionsItemSelected(
            MenuItem item) {
        // TODO Auto-generated method stub
			/*switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:

				Intent intbak = new Intent(getApplicationContext(), EnterItemsActivity.class);
				intbak.putExtra("order_id", sOrderID);
   				intbak.putExtra("cphone", sCPhone);
   				intbak.putExtra("ctype", sType);
   				startActivity(intbak);

				return true;

			case R.id.item_order_edit:
				//Call ProductDetailsEdit
   				Intent inty = new Intent(getApplicationContext(), EnterItemsEditActivity.class);
   				//inty.putExtra(CustomerOrderListActivity.ROW_ID, rowID);
   				inty.putExtra("name", tvName.getText());
   				inty.putExtra("code", tvCode.getText());
   				inty.putExtra("comm", tvComm.getText());
   				inty.putExtra("quantity", tvQuantity.getText());
   				inty.putExtra("price", tvPrice.getText());
   				inty.putExtra("total", tvTotal.getText());
   				inty.putExtra("order_id", sOrderID);
   				inty.putExtra("cphone", sCPhone);
   				inty.putExtra("ctype", sType);
   				//inty.putExtra("copia_id", sCopiaID);
   	            startActivity(inty);
				break;

			case R.id.item_order_delete:
				deleteEntry();
				break;
			}*/
        if(item.getItemId() == android.R.id.home){
            Intent intbak = new Intent(getApplicationContext(), OrderActivity.class);
            intbak.putExtra("order_id", sOrderID);
            intbak.putExtra("cphone", sCPhone);
            intbak.putExtra("ctype", sType);
            startActivity(intbak);

            return true;
        }
        else if(item.getItemId() == R.id.item_order_edit){
            //Call ProductDetailsEdit
            Intent inty = new Intent(getApplicationContext(), EnterItemsEditActivity.class);
            //inty.putExtra(CustomerOrderListActivity.ROW_ID, rowID);
            inty.putExtra("name", tvName.getText());
            inty.putExtra("code", tvCode.getText());
            inty.putExtra("comm", tvComm.getText());
            inty.putExtra("quantity", tvQuantity.getText());
            inty.putExtra("price", tvPrice.getText());
            inty.putExtra("total", tvTotal.getText());
            inty.putExtra("order_id", sOrderID);
            inty.putExtra("cphone", sCPhone);
            inty.putExtra("ctype", sType);
            //inty.putExtra("copia_id", sCopiaID);
            startActivity(inty);
        }
        else if(item.getItemId() == R.id.item_order_edit){
            deleteEntry();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteEntry() {
        AlertDialog.Builder alert = new AlertDialog.Builder(OrderItemDetailsActivity.this);

        alert.setTitle(R.string.confirmTitle);
        alert.setMessage(R.string.confirmMessage);

        alert.setPositiveButton(R.string.delete_btn,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int button)
                    {
                        final DatabaseConnectorSqlite dbConnector =
                                new DatabaseConnectorSqlite(OrderItemDetailsActivity.this);

                        AsyncTask<String, Object, Object> deleteTask =
                                new AsyncTask<String, Object, Object>()
                                {
                                    @Override
                                    protected Object doInBackground(String... params)
                                    {
                                        //dbConnector.deleteOrderLinesOneItem(params[0]);
                                        dbConnector.deleteOrderLinesOneItemUsingCode(sCode, sOrderID);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Object result)
                                    {
                                        //finish();
                                        Intent viewCon = new Intent(getApplicationContext(), OrderActivity.class);
                                        viewCon.putExtra("order_id", sOrderID);
                                        viewCon.putExtra("cphone", sCPhone);
                                        viewCon.putExtra("ctype", sType);
                                        startActivity(viewCon);
                                    }
                                };

                        deleteTask.execute(new String[] { sCode, sOrderID });
                    }
                });

        alert.setNegativeButton(R.string.cancel_btn, null).show();
    }

    // Detect when the back button is pressed
    public void onBackPressed() {
        // Don't let the system handle the back button
        Intent intbak = new Intent(getApplicationContext(), OrderActivity.class);
        intbak.putExtra("order_id", sOrderID);
        intbak.putExtra("cphone", sCPhone);
        intbak.putExtra("ctype", sType);
        startActivity(intbak);
    }
}
