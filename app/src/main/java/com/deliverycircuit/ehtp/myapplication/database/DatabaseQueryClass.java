package com.deliverycircuit.ehtp.myapplication.database;

import com.deliverycircuit.ehtp.myapplication.Util.*;
import com.deliverycircuit.ehtp.myapplication.model.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseQueryClass {
    private Context context;

    public DatabaseQueryClass(Context context){
        this.context = context;
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public long insertRoute(Route route){

        long id = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_ROUTE_NAME, route.getNameRoute());
        contentValues.put(Config.COLUMN_STARTING_POINT, route.getStartStop());
        contentValues.put(Config.COLUMN_ENDING_POINT, route.getEndStop());

        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_ROUTE, null, contentValues);
        } catch (SQLiteException e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return id;
    }

    public long insertStop(Stop stop, long idRoute){

        long id = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_STOP_NAME, stop.getStopName());
        contentValues.put(Config.COLUMN_FK_ROUTE_ID, idRoute);


        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_STOP, null, contentValues);
        } catch (SQLiteException e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return id;
    }

    public List<Route> getAllRoutes(){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Config.TABLE_ROUTE, null, null, null, null, null, null, null);

            /**
             // If you want to execute raw query then uncomment below 2 lines. And comment out above line.

             String SELECT_QUERY = String.format("SELECT %s, %s, %s, %s, %s FROM %s", Config.COLUMN_STUDENT_ID, Config.COLUMN_ROUTE_NAME, Config.COLUMN_STUDENT_REGISTRATION, Config.COLUMN_STUDENT_EMAIL, Config.COLUMN_STUDENT_PHONE, Config.TABLE_STUDENT);
             cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
             */

            if(cursor!=null)
                if(cursor.moveToFirst()){
                    List<Route> routeList = new ArrayList<>();
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ROUTE_ID));
                        String name = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ROUTE_NAME));
                        String startStop = cursor.getString(cursor.getColumnIndex(Config.COLUMN_STARTING_POINT));
                        String endStop = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ENDING_POINT));

                        routeList.add(new Route(id, name, startStop, endStop));
                    }   while (cursor.moveToNext());

                    return routeList;
                }
        } catch (Exception e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    public long updateRouteInfo(Route route){

        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_ROUTE_NAME, route.getNameRoute());
        contentValues.put(Config.COLUMN_STARTING_POINT, route.getStartStop());
        contentValues.put(Config.COLUMN_ENDING_POINT, route.getEndStop());

        try {
            rowCount = sqLiteDatabase.update(Config.TABLE_ROUTE, contentValues,
                    Config.COLUMN_ROUTE_ID + " = ? ",
                    new String[] {String.valueOf(route.getIdRoute())});
        } catch (SQLiteException e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return rowCount;
    }

    public long deleteRoute(long id) {
        long deletedRowCount = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            deletedRowCount = sqLiteDatabase.delete(Config.TABLE_ROUTE,
                    Config.COLUMN_ROUTE_ID+ " = ? ",
                    new String[]{ String.valueOf(id)});
        } catch (SQLiteException e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return deletedRowCount;
    }

    public boolean deleteAllRoutes(){
        boolean deleteStatus = false;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            //for "1" delete() method returns number of deleted rows
            //if you don't want row count just use delete(TABLE_NAME, null, null)
            sqLiteDatabase.delete(Config.TABLE_ROUTE, null, null);

            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_ROUTE);

            if(count==0)
                deleteStatus = true;

        } catch (SQLiteException e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return deleteStatus;
    }

    public long getNumberOfRoutes(){
        long count = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_ROUTE);
        } catch (SQLiteException e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return count;
    }
    public Route getRoute(String nom){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        Route route = null;
        try {

            cursor = sqLiteDatabase.query(Config.TABLE_ROUTE, null,
                    Config.COLUMN_ROUTE_NAME + " = ? ", new String[]{String.valueOf(nom)},
                    null, null, null);

            if(cursor.moveToFirst()){
                int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ROUTE_ID));
                String name = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ROUTE_NAME));
                String starting_point = cursor.getString(cursor.getColumnIndex(Config.COLUMN_STARTING_POINT));
                String ending_point = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ENDING_POINT));

                route = new Route(id, name,starting_point,ending_point);
            }
        } catch (Exception e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return route;
    }
    public List<Stop> getAllStops(long fkStop){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = sqLiteDatabase.query(Config.TABLE_STOP, null,
                    Config.COLUMN_FK_ROUTE_ID + " = ? ", new String[]{String.valueOf(fkStop)},
                    null, null, null);

            if(cursor!=null)
                if(cursor.moveToFirst()){
                    List<Stop> stopList = new ArrayList<>();
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STOP_ID));
                        String name = cursor.getString(cursor.getColumnIndex(Config.COLUMN_STOP_NAME));
                        long fk_Route = cursor.getLong(cursor.getColumnIndex(Config.COLUMN_FK_ROUTE_ID));

                        stopList.add(new Stop(id, name));
                    }   while (cursor.moveToNext());

                    return stopList;
                }
        } catch (Exception e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

}
