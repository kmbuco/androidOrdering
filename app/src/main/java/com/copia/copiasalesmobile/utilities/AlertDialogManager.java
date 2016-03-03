package com.copia.copiasalesmobile.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.copia.copiasalesmobile.R;

/**
 * Created by mbuco on 2/19/16.
 */
public class AlertDialogManager {

    @SuppressWarnings("deprecation")
    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        if(status != null)
            // Setting alert dialog icon
            alertDialog.setIcon((status) ? R.drawable.good_dark : R.drawable.bad_dark);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}