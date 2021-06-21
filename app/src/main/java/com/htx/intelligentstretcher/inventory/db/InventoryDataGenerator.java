package com.htx.intelligentstretcher.inventory.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.htx.intelligentstretcher.inventory.InventoryMainFragment;
import com.htx.intelligentstretcher.inventory.db.entity.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventoryDataGenerator {
    private static final String TAG = "DataGen";
    private static int currentItemRecordId = -1;
    private static int currentItemId = -1;

    public static void addChecks(Context context, InventoryDatabase db, int optRefsId) {
        TypedArray checksTa = context.getResources().obtainTypedArray(optRefsId);
        @StyleableRes final int checkIdIdx = 0;
        @StyleableRes final int nameIdx = 1;
        @StyleableRes final int timeIdx = 2;
        for (int i = 0; i < checksTa.length(); ++i) {
            TypedArray checkTa = context.getResources().obtainTypedArray(checksTa.getResourceId(i, 0));
            int checkId = checkTa.getInt(checkIdIdx, 0);
            String name = checkTa.getString(nameIdx);
            String timeStr = checkTa.getString(timeIdx);
            try {
                InventoryCheck check = new InventoryCheck(
                        checkId,
                        name,
                        InventoryCheck.CHECK_DATE_FORMAT.parse(timeStr)
                );
                db.inventoryCheckDao().insertData(check);
            } catch (ParseException ex) {
                Log.e(TAG, "Failed to add check");
            }
        }

    }

    @SuppressLint("ResourceType")
    public static void addInventorySubListItems(Context context, InventoryDatabase db, TypedArray ta, int optRefsId) {
        int subListItemId = -1;
        int parentSubListItemId = -1;
        int pageId = -1;
        for (int i = 0; i < ta.length(); ++i) {
            int subRef = ta.getResourceId(i, 0);
            TypedArray ta2 = context.getResources().obtainTypedArray(subRef);
            String title = ta2.getString(1);
            String nestedOrNot = ta2.getString(0);
            parentSubListItemId = ++subListItemId;

            if (nestedOrNot.equals("true")) {
                InventorySubListItem mainListItem = new InventorySubListItem(
                        parentSubListItemId,
                        title,
                        -1,
                        optRefsId
                );
                db.inventorySubListItemDao().insertData(mainListItem);

                for (int j = 2; j < ta2.length(); ++j) {
                    TypedArray ta3 = context.getResources().obtainTypedArray(ta2.getResourceId(j, 0));
                    String subTitle = ta3.getString(0);
                    InventorySubListItem subListItem = new InventorySubListItem(
                            ++subListItemId,
                            subTitle,
                            parentSubListItemId,
                            null
                    );
                    db.inventorySubListItemDao().insertData(subListItem);
                    db.inventoryPageDao().insertData(new InventoryPage(
                            ++pageId, subListItemId, subTitle
                    ));
                    insertItemsFromTa(context, db, ta3, pageId, 1);
                }
            } else {
                String subtitle = ta2.getString(1);
                InventorySubListItem subListItem = new InventorySubListItem(
                        parentSubListItemId,
                        subtitle,
                        -1,
                        optRefsId
                );
                db.inventorySubListItemDao().insertData(subListItem);
                db.inventoryPageDao().insertData(new InventoryPage(
                        ++pageId, parentSubListItemId, subtitle
                ));
                insertItemsFromTa(context, db, ta2, pageId, 2);
            }
        }
    }

    private static void insertItemsFromTa(Context context, InventoryDatabase db,
                                          TypedArray _ta, int parentPageId, int arrayIdx) {
        List<InventoryItem> items = new ArrayList<>();
        int taRes = _ta.getResourceId(arrayIdx, 0);
        TypedArray ta2 = context.getResources().obtainTypedArray(taRes);
        for (int i = 0; i < ta2.length(); ++i) {
            @StyleableRes final int nameIndex = 0;
            @StyleableRes final int catIndex = 1;
            @StyleableRes final int qtyIndex = 2;
            @StyleableRes final int drawableIndex = 3;
            @StyleableRes final int descIndex = 4;
            TypedArray ta3 = context.getResources().obtainTypedArray(ta2.getResourceId(i, 0));
            String name = ta3.getString(nameIndex);
            int totalQty = ta3.getInt(qtyIndex, 0);
            String category = ta3.getString(catIndex);
            int drawableResId = ta3.getResourceId(drawableIndex, 0);
            String desc = ta3.getString(descIndex);
            InventoryItem item = new InventoryItem(++currentItemId, name, category, parentPageId,
                    totalQty, null, drawableResId, desc);
            db.inventoryItemDao().insertData(item);
            insertItemInstancesFromTa(context, db, ta3, currentItemId);
            insertRecordsFromTa(context, db, ta3, currentItemId);
        }
    }

    private static void insertItemInstancesFromTa(Context context, InventoryDatabase db, TypedArray ta, int itemId) {
        @StyleableRes final int instancesArrIdx = 5;
        TypedArray instancesArr = context.getResources().obtainTypedArray(ta.getResourceId(instancesArrIdx, 0));
        @StyleableRes final int instanceIdIdx = 0;
        @StyleableRes final int expiryIdx = 1;
        for (int i = 0; i < instancesArr.length(); ++i) {
            TypedArray instanceArr = context.getResources().obtainTypedArray(instancesArr.getResourceId(i, 0));
            int instanceId = instanceArr.getInt(instanceIdIdx, 0);
            String expiryStr = instanceArr.getString(expiryIdx);
            try {
                InventoryItemInstance instance = new InventoryItemInstance(
                        instanceId,
                        itemId,
                        InventoryItemInstance.EXPIRY_DATE_FORMAT.parse(expiryStr)
                );
                db.inventoryItemInstanceDao().insertData(instance);
            } catch (ParseException ex) {
                Log.e(TAG, "Failed to add item instance");
            }
        }
    }

    private static void insertRecordsFromTa(Context context, InventoryDatabase db, TypedArray ta, int itemId) {
        @StyleableRes final int recordsArrayIdx = 6;
        int recordsArrayRes = ta.getResourceId(recordsArrayIdx, 0);
        TypedArray recordsArray = context.getResources().obtainTypedArray(recordsArrayRes);
        @StyleableRes final int itemInstanceIdIdx = 0;
        @StyleableRes final int checkIdIdx = 1;
        @StyleableRes final int checkDateIdx = 2;
        @StyleableRes final int notesIdx = 3;
        for (int i = 0; i < recordsArray.length(); ++i) {
            TypedArray recordArr = context.getResources().obtainTypedArray(recordsArray.getResourceId(i, 0));
            int itemInstanceId = recordArr.getInt(itemInstanceIdIdx, 0);
            int checkId = recordArr.getInt(checkIdIdx, 0);
            String checkDateStr = recordArr.getString(checkDateIdx);
            String notes = recordArr.getString(notesIdx);
            try {
                InventoryItemRecord record = new InventoryItemRecord(
                        checkId,
                        itemId,
                        itemInstanceId,
                        InventoryItemRecord.INSTANCE_CHECK_DATE_FORMAT.parse(checkDateStr),
                        notes
                );
                db.inventoryItemRecordDao().insertData(record);
            } catch (ParseException ex) {
                Log.e(TAG, "Failed to add item record");
            }
        }
    }
}
