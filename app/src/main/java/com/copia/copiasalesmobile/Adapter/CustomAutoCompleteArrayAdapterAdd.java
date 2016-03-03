package com.copia.copiasalesmobile.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.copia.copiasalesmobile.R;
import com.copia.copiasalesmobile.utilities.ProdSearchObject;

import java.io.ByteArrayInputStream;

/**
 * Created by mbuco on 2/18/16.
 */
public class CustomAutoCompleteArrayAdapterAdd extends ArrayAdapter<ProdSearchObject> {

    final String TAG = "CustomAutoCompleteArrayAdapterAdd.java";

    Context mContext;
    int layoutResourceId;
    ProdSearchObject data[] = null;

    public CustomAutoCompleteArrayAdapterAdd(Context mContext, int layoutResourceId, ProdSearchObject[] data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try{

            /*
             * The convertView argument is essentially a "ScrapView" as described is Lucas post
             * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
             * It will have a non-null value when ListView is asking you recycle the row layout.
             * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
             */
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(convertView == null){
                convertView = inflater.inflate(R.layout.list_item_search_product, null);
            }

            // object item based on the position
            ProdSearchObject objectItem = data[position];

            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView tvCode = (TextView) convertView.findViewById(R.id.search_code);
            TextView tvName = (TextView) convertView.findViewById(R.id.search_name);
            TextView tvPrice = (TextView) convertView.findViewById(R.id.search_price);
            TextView tvComm = (TextView) convertView.findViewById(R.id.search_comm);
            TextView tvImg = (TextView) convertView.findViewById(R.id.search_icon_tv);
            ImageView imgView = (ImageView) convertView.findViewById(R.id.search_icon);
            TextView tvTier = (TextView) convertView.findViewById(R.id.search_tier);
            TextView tvCopiaID = (TextView) convertView.findViewById(R.id.search_copia_id);

            tvCode.setText(objectItem.productcode);
            tvName.setText(objectItem.productname);
            tvPrice.setText(objectItem.productprice);
            tvComm.setText(objectItem.productcomm);
            tvImg.setText(objectItem.productimg);
            tvTier.setText(objectItem.productcomm);
            tvCopiaID.setText(objectItem.productcopiaID);


            String sImage = tvImg.getText().toString();

            try{
                byte[] outImage = Base64.decode(sImage, Base64.DEFAULT);
                ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
                Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                imgView.setImageBitmap(theImage);
            }catch(Exception e){
                imgView.setImageResource(R.drawable.ic_shopping_cart);
            }
            // add some style
            tvName.setBackgroundColor(Color.LTGRAY);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }
}