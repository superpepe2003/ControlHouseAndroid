package com.controlhouse.utopiasoft.controlhouse.Utilidades;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;


import java.util.Calendar;

public class DatePickerSinDia extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    Boolean isMes;
    int d,m,y;

    public OnDateChangeListener listener;

    public interface OnDateChangeListener{
        void onDateChange(int year, int month, int day);
    }

    public void setOnDateChangeListener(OnDateChangeListener listener){this.listener=listener;}

    public DatePickerSinDia(Boolean isMes, int d, int m, int y){
        this.isMes=isMes;
        this.d=d;
        this.m=m;
        this.y=y;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current date as the default date in the date picker
        //final Calendar c = Calendar.getInstance();
        //int year = c.get(Calendar.YEAR);
        //int month = c.get(Calendar.MONTH);
        //int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd;

        if(isMes) {
             dpd = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_DARK, this, y, m,d) {
                //DatePickerDialog dpd = new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT,this,year, month, day){
                // DatePickerDialog dpd = new DatePickerDialog(getActivity(), AlertDialog.THEME_TRADITIONAL,this,year, month, day){
                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    int day = getContext().getResources().getIdentifier("android:id/day", null, null);
                    if (day != 0) {
                        View dayPicker = findViewById(day);
                        if (dayPicker != null) {
                            //Set Day view visibility Off/Gone
                            dayPicker.setVisibility(View.GONE);
                        }
                    }
                }
            };
        }
        else
        {
            dpd = new DatePickerDialog(getActivity(),this, y, m-1, d);
                //DatePickerDialog dpd = new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT,this,year, month, day){
                // DatePickerDialog dpd = new DatePickerDialog(getActivity(), AlertDialog.THEME_TRADITIONAL,this,year, month, day){
        }
        return dpd;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        //Set the Month & Year to TextView which chosen by the user
        if(listener!=null) {
            //TextView tv = (TextView) getActivity().findViewById(R.id.txtMonthYear);
            listener.onDateChange(year, month, day);
        }
    }
}