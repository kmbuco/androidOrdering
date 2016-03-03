package com.copia.copiasalesmobile.utilities;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.copia.copiasalesmobile.Adapter.CustomAutoCompleteAgentArrayAdapterAdd;
import com.copia.copiasalesmobile.Adapter.CustomAutoCompleteArrayAdapterAdd;
import com.copia.copiasalesmobile.OrderActivity;
import com.copia.copiasalesmobile.R;

/**
 * Created by mbuco on 2/19/16.
 */
public class CustomAutoCompleteAgentTextChangedListenerAdd implements TextWatcher {

    public static final String TAG = "CustomAutoCompleteTextChangedListenerAdd.java";
    Context context;

    public CustomAutoCompleteAgentTextChangedListenerAdd(Context context){
        this.context = context;
    }


    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {
        try{
            //Log.e("User input 2: ", userInput.toString());

            OrderActivity mainActivity = ((OrderActivity) context);
            if(userInput.toString().trim().equals("")){
                //do nothing
            }
            else{
                // update the adapter
                mainActivity.myAgentAdapter.notifyDataSetChanged();

                // get suggestions from the database
                AgentSearchObject[] myObjs = mainActivity.dbconnector.readAgentData(userInput.toString().trim());

                // update the adapter
                mainActivity.myAgentAdapter = new CustomAutoCompleteAgentArrayAdapterAdd(mainActivity, R.layout.list_item_search_product, myObjs);

                mainActivity.myAutoAgentComplete.setAdapter(mainActivity.myAgentAdapter);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
