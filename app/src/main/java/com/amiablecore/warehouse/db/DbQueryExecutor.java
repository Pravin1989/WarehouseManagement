package com.amiablecore.warehouse.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.amiablecore.warehouse.WarehouseUserOutwardActivity;
import com.amiablecore.warehouse.beans.Inward;
import com.amiablecore.warehouse.beans.Outward;

import java.util.ArrayList;
import java.util.List;

public class DbQueryExecutor extends DbObject {
    public DbQueryExecutor(Context context) {
        super(context);
    }

    private static final String TAG = "DbQueryExecutor";

    public void addInwardLotDetails(Inward inward) {
        try {
            ContentValues contentValue = new ContentValues();
            contentValue.put("trader_id", inward.getTraderId());
            contentValue.put("lot_name", inward.getLotName());
            contentValue.put("commodity_id", inward.getCommodityId());
            contentValue.put("category_id", inward.getCategoryId());
            inward.setTotalWeight(inward.getTotalQuantity() * inward.getWeightPerBag());
            contentValue.put("total_weight", inward.getTotalWeight());
            contentValue.put("total_quantity", inward.getTotalQuantity());
            contentValue.put("weight_per_bag", inward.getWeightPerBag());
            contentValue.put("inward_date", inward.getInwardDate().toString());
            contentValue.put("physical_address", inward.getPhysicalAddress());
            contentValue.put("wh_admin_id", inward.getWhAdminId());
            contentValue.put("wh_user_id", inward.getWhUserId());
            long id = this.getDbConnection().insert(DatabaseHelper.TABLE_INWARD, null, contentValue);
            Log.i("Record Id : ", String.valueOf(id));

        } catch (Exception e) {
            Log.e("DbQueryExecutor :", String.valueOf(e), e);
        }
    }

    public List<Inward> findInwardDetailsInDB() {
        List<Inward> mItems = new ArrayList<>();
        String query = "Select * from " + DatabaseHelper.TABLE_INWARD;
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        Log.i(TAG, "Looking into Inward DB");
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String date = cursor.getString(cursor.getColumnIndexOrThrow("inward_date"));
                String lotName = cursor.getString(cursor.getColumnIndexOrThrow("lot_name"));
                Integer traderId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("trader_id")));
                Integer commodityId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("commodity_id")));
                Integer categoryId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("category_id")));
                Integer totalCurrentQty = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("total_quantity")));
                Double bagWeight = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("weight_per_bag")));
                Double totalWeight = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("total_weight")));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("physical_address"));
                Integer adminId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("wh_admin_id")));
                Integer userId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("wh_user_id")));
                mItems.add(new Inward(id, date, lotName, traderId, commodityId, categoryId, totalCurrentQty, bagWeight, totalWeight, address, adminId, userId));
            } while (cursor.moveToNext());
        }
        Log.i(TAG, String.valueOf(mItems.size()));
        cursor.close();
        return mItems;
    }

    public void deleteTableData(String tableName) {
        Log.i(TAG, "Table Delete Started");
        String query = "delete from " + tableName;
        this.getDbConnection().execSQL(query);
        Log.i(TAG, "Table Deleted");
    }

    public void addOutwarddLotDetails(Outward outward) {
        Outward out = verifyOutwardDetailsInDb(outward);
        if (out.getInwardId() != null) {
            if (outward.getTotalQuantity().equals(WarehouseUserOutwardActivity.getBackupInward().getTotalQuantity())) {
                outward.setTotalQuantity(WarehouseUserOutwardActivity.getBackupInward().getTotalQuantity());
                outward.setTotalWeight(WarehouseUserOutwardActivity.getBackupInward().getTotalWeight());
                updateOutwardDetails(outward);
            } else {
                int totCount = outward.getTotalQuantity() + out.getTotalQuantity();
                double totWeight = outward.getTotalWeight() + out.getTotalWeight();
                outward.setTotalQuantity(totCount);
                outward.setTotalWeight(totWeight);
                updateOutwardDetails(outward);
            }
            return;
        }
        try {
            ContentValues contentValue = new ContentValues();
            contentValue.put("inward_id", outward.getInwardId());
            contentValue.put("trader_id", outward.getTraderId());
            outward.setTotalWeight(outward.getTotalQuantity() * outward.getBagWeight());
            contentValue.put("total_weight", outward.getTotalWeight());
            contentValue.put("total_quantity", outward.getTotalQuantity());
            contentValue.put("weight_per_bag", outward.getBagWeight());
            contentValue.put("outward_date", outward.getOutwardDate().toString());
            contentValue.put("wh_admin_id", outward.getWhAdminId());
            contentValue.put("lot_name", outward.getLotName());
            contentValue.put("wh_user_id", outward.getWhUserId());
            long id = this.getDbConnection().insert(DatabaseHelper.TABLE_OUTWARD, null, contentValue);
            Log.i("Record Id : ", String.valueOf(id));

        } catch (Exception e) {
            Log.e("DbQueryExecutor :", String.valueOf(e), e);
        }
    }

    public Outward verifyOutwardDetailsInDb(Outward outward) {
        String query = "Select * from " + DatabaseHelper.TABLE_OUTWARD + " where inward_id=?";
        Cursor cursor = this.getDbConnection().rawQuery(query, new String[]{outward.getInwardId().toString()});
        Log.i(TAG, "Inward ID :" + String.valueOf(outward.getInwardId()));
        Outward out = new Outward();
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            Log.i(TAG, "Found Record");
            Integer inwardId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("inward_id")));
            Integer totalCurrentQty = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("total_quantity")));
            Double totalWeight = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("total_weight")));
            out.setInwardId(inwardId);
            out.setTotalQuantity(totalCurrentQty);
            out.setTotalWeight(totalWeight);
        }
        Log.i(TAG, "Fetched Inward ID :" + String.valueOf(out.getInwardId()));
        Log.i(TAG, "VerifyOutwardDetailsInDB");
        return out;
    }

    public Integer updateOutwardDetails(Outward outward) {
        ContentValues contentValue = new ContentValues();
        contentValue.put("trader_id", outward.getTraderId());
        outward.setTotalWeight(outward.getTotalQuantity() * outward.getBagWeight());
        contentValue.put("total_weight", outward.getTotalWeight());
        contentValue.put("total_quantity", outward.getTotalQuantity());
        contentValue.put("weight_per_bag", outward.getBagWeight());
        contentValue.put("outward_date", outward.getOutwardDate().toString());
        contentValue.put("wh_admin_id", outward.getWhAdminId());
        contentValue.put("lot_name", outward.getLotName());
        contentValue.put("wh_user_id", outward.getWhUserId());
        int i = this.getDbConnection().update(DatabaseHelper.TABLE_OUTWARD, contentValue, outward.getInwardId().toString(), null);
        Log.i("Record ID : ", "Updated " + String.valueOf(i));
        return i;
    }

    public List<Outward> findOutwardDetailsInDB() {
        List<Outward> mItems = new ArrayList<>();
        String query = "Select * from " + DatabaseHelper.TABLE_OUTWARD;
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        Log.i(TAG, "Looking into Outward DB");
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String date = cursor.getString(cursor.getColumnIndexOrThrow("outward_date"));
                Integer traderId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("trader_id")));
                Integer inwardId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("inward_id")));
                Integer totalCurrentQty = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("total_quantity")));
                Double bagWeight = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("weight_per_bag")));
                Double totalWeight = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("total_weight")));
                Integer adminId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("wh_admin_id")));
                Integer userId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("wh_user_id")));
                String lotName = cursor.getString(cursor.getColumnIndexOrThrow("lot_name"));
                mItems.add(new Outward(id, traderId, inwardId, adminId, userId, date, totalCurrentQty, bagWeight * totalCurrentQty, bagWeight, lotName));
            } while (cursor.moveToNext());
        }
        Log.i(TAG, String.valueOf(mItems.size()));
        cursor.close();
        return mItems;
    }

    public List<Outward> verifyTotalWeightInOutwardDB() {
        List<Outward> mItems = new ArrayList<>();
        String query = "Select * from " + DatabaseHelper.TABLE_OUTWARD + " where total_weight is null";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        Log.i(TAG, "Looking into Outward DB");
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String date = cursor.getString(cursor.getColumnIndexOrThrow("outward_date"));
                Integer traderId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("trader_id")));
                Integer inwardId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("inward_id")));
                Integer totalCurrentQty = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("total_quantity")));
                Double bagWeight = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("weight_per_bag")));
                Double totalWeight = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("total_weight")));
                Integer adminId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("wh_admin_id")));
                Integer userId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("wh_user_id")));
                String lotName = cursor.getString(cursor.getColumnIndexOrThrow("lot_name"));
                ;
                mItems.add(new Outward(id, traderId, inwardId, adminId, userId, date, totalCurrentQty, totalWeight, bagWeight, lotName));
            } while (cursor.moveToNext());
        }
        Log.i(TAG, String.valueOf(mItems.size()));
        cursor.close();
        return mItems;
    }
}
