package com.amiablecore.warehouse.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_INWARD = "TABLE_INWARD_LOT";
    public static final String TABLE_OUTWARD = "TABLE_OUTWARD_LOT";

    // Table columns
    public static final String inwardId = "inward_id";

    public static final String outwardId = "outward_id";

    // Database Information
    static final String DB_NAME = "WAREHOUSEAPP.DB";

    // database version
    static final int DB_VERSION = 7;

    // Creating table query
    private static final String CREATE_INWARD_TABLE = "create table " + TABLE_INWARD + "(" + inwardId
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + "trader_id INTEGER NOT NULL, lot_name TEXT NOT NULL, commodity_id INTEGER NOT NULL, category_id INTEGER NOT NULL, total_weight REAL, total_quantity INTEGER NOT NULL, weight_per_bag REAL NOT NULL, inward_date TEXT NOT NULL, physical_address TEXT NOT NULL, wh_admin_id INTEGER NOT NULL, wh_user_id INTEGER NOT NULL);";

    private static final String CREATE_OUTWARD_TABLE = "create table " + TABLE_OUTWARD + "(" + outwardId
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + "inward_id INTEGER NOT NULL,lot_name TEXT NOT NULL, trader_id INTEGER NOT NULL, total_weight REAL, total_quantity INTEGER NOT NULL, weight_per_bag REAL NOT NULL, outward_date TEXT NOT NULL, wh_admin_id INTEGER NOT NULL, wh_user_id INTEGER NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_INWARD_TABLE);
        db.execSQL(CREATE_OUTWARD_TABLE);
        Log.i("DatabaseHelper  ", "Table Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INWARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OUTWARD);
        Log.i("DatabaseHelper  ", "Table Dropped");
        onCreate(db);
    }
}
