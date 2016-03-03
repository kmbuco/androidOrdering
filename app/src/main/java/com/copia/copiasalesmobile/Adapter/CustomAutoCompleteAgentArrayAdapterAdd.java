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
import com.copia.copiasalesmobile.utilities.AgentSearchObject;
import com.copia.copiasalesmobile.utilities.ProdSearchObject;

import java.io.ByteArrayInputStream;

/**
 * Created by mbuco on 2/19/16.
 */
public class CustomAutoCompleteAgentArrayAdapterAdd extends ArrayAdapter<AgentSearchObject> {

        final String TAG = "CustomAutoCompleteArrayAdapterAdd.java";

        Context mContext;
        int layoutResourceId;
        AgentSearchObject data[] = null;

        public CustomAutoCompleteAgentArrayAdapterAdd(Context mContext, int layoutResourceId, AgentSearchObject[] data){
        super(mContext,layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
        }

        @Override
        public View getView ( int position, View convertView, ViewGroup parent){

        try {

            /*
             * The convertView argument is essentially a "ScrapView" as described is Lucas post
             * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
             * It will have a non-null value when ListView is asking you recycle the row layout.
             * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
             */
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_search_agent, null);
            }

            // object item based on the position
            AgentSearchObject objectItem = data[position];

            TextView tvName = (TextView) convertView.findViewById(R.id.txt_agent_name);
            TextView tvPhone = (TextView) convertView.findViewById(R.id.txt_agent_phone);
            TextView tvAgentId = (TextView) convertView.findViewById(R.id.txt_agent_id);

            tvPhone.setText(objectItem.agentphone);
            tvName.setText(objectItem.agentname);
            tvAgentId.setText(objectItem.agentId);
            // add some style
            tvPhone.setBackgroundColor(Color.LTGRAY);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }
}

