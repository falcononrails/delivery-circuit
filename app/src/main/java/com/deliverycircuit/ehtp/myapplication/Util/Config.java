package com.deliverycircuit.ehtp.myapplication.Util;

public class Config {

    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "routesManager";

    // Table Names
    public static final String TABLE_ROUTE = "routes";
    public static final String TABLE_STOP = "stops";

    // ROUTES Table - column names
    public static final String COLUMN_ROUTE_ID = "id";
    public static final String COLUMN_ROUTE_NAME = "route_name";
    public static final String COLUMN_STARTING_POINT = "starting_point";
    public static final String COLUMN_ENDING_POINT = "ending_point";

    // STOPS Table - column names
    public static final String COLUMN_STOP_ID = "id";
    public static final String COLUMN_STOP_NAME = "stop_name";
    public static final String COLUMN_FK_ROUTE_ID = "fk_route_id";

    public static final String TITLE = "title";

    // Table Create Statements
    // Route table create statement
    public static final String CREATE_TABLE_ROUTE = "CREATE TABLE " + Config.TABLE_ROUTE + "(" +
            Config.COLUMN_ROUTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Config.COLUMN_ROUTE_NAME +  " TEXT, " +
            Config.COLUMN_STARTING_POINT + " TEXT, " +
            Config.COLUMN_ENDING_POINT + " TEXT" +
            ")";

    // Stop table create statement
    public static final String CREATE_TABLE_STOP = "CREATE TABLE " + Config.TABLE_STOP + "(" +
            Config.COLUMN_STOP_ID + " INTEGER PRIMARY KEY," +
            Config.COLUMN_STOP_NAME + " TEXT, " +
            Config.COLUMN_FK_ROUTE_ID + " INTEGER" +
            ")";
}
