package com.amiablecore.warehouse.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.amiablecore.warehouse.ItemObject;
import com.amiablecore.warehouse.beans.Inward;

import java.util.ArrayList;
import java.util.List;

public class DbQueryExecutor extends DbObject {
    public DbQueryExecutor(Context context) {
        super(context);
    }

    public List<ItemObject> searchLotDetailsInDB(String searchWord) {
        List<ItemObject> mItems = new ArrayList<>();
        String query = "Select * from dictionary where title like " + "'%" + searchWord + "%'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        ArrayList<String> wordTerms = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String word = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                mItems.add(new ItemObject(id, word));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return mItems;
    }

    public void addLotDetails(Inward inward) {
        try {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.SUBJECT, "");
            contentValue.put(DatabaseHelper.DESC, "");
            this.getDbConnection().insert(DatabaseHelper.TABLE_NAME, null, contentValue);

        } catch (Exception e) {
            Log.i("DbQueryExecutor :", String.valueOf(e));
        }
    }
}
