package com.amiablecore.warehouse.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.amiablecore.warehouse.LotDetails;
import com.amiablecore.warehouse.beans.Inward;

import java.util.ArrayList;
import java.util.List;

public class DbQueryExecutor extends DbObject {
    public DbQueryExecutor(Context context) {
        super(context);
    }

    public List<LotDetails> searchLotDetailsInDB(String searchWord) {
        List<LotDetails> mItems = new ArrayList<>();
        String query = "Select * from " + DatabaseHelper.TABLE_NAME + " where lot_name like " + "'%" + searchWord + "%'";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        ArrayList<String> wordTerms = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String lotName = cursor.getString(cursor.getColumnIndexOrThrow("lot_name"));
                Double bagWeight = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("weight_per_bag")));
                Double totalWeight = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("total_weight")));
                Integer totalCurrentQty = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("total_quantity")));
                mItems.add(new LotDetails(id, lotName, bagWeight, totalWeight, totalCurrentQty));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return mItems;
    }

    public void addLotDetails(Inward inward) {
        try {
            ContentValues contentValue = new ContentValues();
            contentValue.put("trader_id", inward.getTraderId());
            contentValue.put("lot_name", inward.getLotName());
            contentValue.put("commodity_id", inward.getCommodityId());
            contentValue.put("category_id", inward.getCategoryId());
            contentValue.put("total_weight", inward.getTotalWeight());
            contentValue.put("total_quantity", inward.getTotalQuantity());
            contentValue.put("weight_per_bag", inward.getWeightPerBag());
            contentValue.put("inward_date", inward.getInwardDate().toString());
            contentValue.put("physical_address", inward.getPhysicalAddress());
            contentValue.put("wh_admin_id", inward.getWhAdminId());
            contentValue.put("wh_user_id", inward.getWhUserId());
            long id = this.getDbConnection().insert(DatabaseHelper.TABLE_NAME, null, contentValue);
            Log.i("Record Id : ", String.valueOf(id));

        } catch (Exception e) {
            Log.e("DbQueryExecutor :", String.valueOf(e), e);
        }
    }
}
