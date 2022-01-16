package com.future.util;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author ：shenmegui
 * @date ：Created in 2021/12/20 20:20
 */
public class DialogUtils {

    public static void setDialog(Context context, String msg, View view, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage(msg);
        builder.setView(view);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", listener);
        builder.create().show();
    }

    public static void setConfirmDialog(Context context, String msg, View view, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage(msg);
        builder.setView(view);
        builder.setPositiveButton("确定", listener);
        builder.create().show();
    }

    public static void setDialog(Context context, String msg, DialogInterface.OnClickListener listener) {
        setDialog(context, msg, null, listener);
    }

    public static void setDateDialog(Context context,DatePickerDialog.OnDateSetListener dateListener){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        new DatePickerDialog(context,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public static void setTimeDialog(Context context, TimePickerDialog.OnTimeSetListener timeSetListener){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        new TimePickerDialog(context,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true).show();
    }
}
