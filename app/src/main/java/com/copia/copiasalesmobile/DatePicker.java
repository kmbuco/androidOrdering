package com.copia.copiasalesmobile;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

public class DatePicker extends AppCompatActivity {

    int year_x,month_x,day_x;
    static final int DIALOG_ID = 0;
    Button btnDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        //todays date
        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH)+1;
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        btnDate = (Button) findViewById(R.id.buttonDate);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });
    }
    public void showDialogOnButtonClick(){
        btnDate.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_ID);
                    }
                }
        );
    }
    @Override
    protected Dialog onCreateDialog(int id){
        if (id == DIALOG_ID){
            return new DatePickerDialog(this, dpickerListener,year_x,month_x,day_x);
        }
        return null;
    }
   private DatePickerDialog.OnDateSetListener dpickerListener =
           new DatePickerDialog.OnDateSetListener() {
               @Override
               public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                   year_x = year;
                   month_x = monthOfYear+1;//month is zero based
                   day_x = dayOfMonth;
                   Toast.makeText(DatePicker.this, year_x +"/"+month_x+"/"+day_x,Toast.LENGTH_LONG).show();
               }
           };

}
