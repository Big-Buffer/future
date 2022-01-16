package com.future.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.future.DB.DBHelper;
import com.future.bean.Event;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：shenmegui
 * @date ：Created in 2021/12/15 21:51
 */
public class DBUtils {
    private DBHelper dbHelper;

    public DBUtils(Context context) {
        dbHelper = new DBHelper(context);
    }

    public List<Event> selectAll() throws ParseException {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        List<Event> events = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from event", null);
        while (cursor.moveToNext()) {
            Event event = new Event();
            event.setId(cursor.getInt(0));
            event.setEndDate(cursor.getString(1));
            event.setContent(cursor.getString(2));
            events.add(event);
        }
        cursor.close();
        db.close();
        return events;
    }

    public List<Event> selectByInfo(String info) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from event where content like ?", new String[]{"%" + info + "%"});
        while (cursor.moveToNext()) {
            Event event = new Event();
            event.setId(cursor.getInt(0));
            event.setEndDate(cursor.getString(1));
            event.setContent(cursor.getString(2));
            events.add(event);
        }
        cursor.close();
        db.close();
        return events;
    }

    public List<Event> selectByDate(String info) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from event where endDate like ?", new String[]{"%" + info + "%"});
        while (cursor.moveToNext()) {
            Event event = new Event();
            event.setId(cursor.getInt(0));
            event.setEndDate(cursor.getString(1));
            event.setContent(cursor.getString(2));
            events.add(event);
        }
        cursor.close();
        db.close();
        return events;
    }

    public Event selectById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from event where id=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            Event event = new Event();
            cursor.moveToFirst();
            event.setId(cursor.getInt(0));
            event.setEndDate(cursor.getString(1));
            event.setContent(cursor.getString(2));
            cursor.close();
            db.close();
            return event;
        } else {
            return null;
        }

    }

    public void insert(Event event) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", event.getId());
        values.put("endDate", event.getEndDate());
        values.put("content", event.getContent());
        db.insert("event", null, values);
        db.close();
    }

    public int update(Event event, int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", event.getId());
        values.put("endDate", event.getEndDate());
        values.put("content", event.getContent());
        int lines = db.update("event", values, "id=?",
                new String[]{String.valueOf(id)});
        db.close();
        return lines;
    }

    public int delete(int id) {
        boolean stop = false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int lines = db.delete("event", "id = ?", new String[]{String.valueOf(id)});
        // 更新后续id
        while(!stop){
            Event event = selectById(id + 1);
            if (event != null) {
                event.setId(id);
                update(event, id + 1);
                id = id+1;
            } else {
                stop = true;
            }
        }
        db.close();
        return lines;
    }
}
