package com.amiablecore.warehouse.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.amiablecore.warehouse.utils.StaticConstants;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "TABLE_INWARD_LOT";

    // Table columns
    public static final String inwardId = "inward_id";

    // Database Information
    static final String DB_NAME = "WAREHOUSEAPP.DB";

    // database version
    static final int DB_VERSION = 3;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + inwardId
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + "trader_id TEXT NOT NULL, lot_name TEXT NOT NULL, commodity_id INTEGER NOT NULL, category_id INTEGER NOT NULL, total_weight REAL, total_quantity INTEGER NOT NULL, weight_per_bag REAL NOT NULL, inward_date TEXT NOT NULL, physical_address TEXT NOT NULL, wh_admin_id TEXT NOT NULL, wh_user_id TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Log.i("DatabaseHelper  ", "Table Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        Log.i("DatabaseHelper  ", "Table Dropped");
        onCreate(db);
    }
}
