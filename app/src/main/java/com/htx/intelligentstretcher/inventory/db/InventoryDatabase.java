package com.htx.intelligentstretcher.inventory.db;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.htx.intelligentstretcher.inventory.db.converter.InventoryConverters;
import com.htx.intelligentstretcher.inventory.db.dao.*;
import com.htx.intelligentstretcher.inventory.db.entity.*;
import com.htx.intelligentstretcher.R;


import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        InventoryCheck.class,
        InventorySubListItem.class,
        InventoryPage.class,
        InventoryItem.class,
        InventoryItemRecord.class,
        InventoryItemInstance.class,
}, version = 1)
@TypeConverters({InventoryConverters.class})
public abstract class InventoryDatabase extends RoomDatabase {
    private static InventoryDatabase instance;
    private static final String DATABASE_NAME = "Inventory database";
    public static final Integer[] dataResIds = new Integer[] { R.array.medical_bag_opts };

    public static boolean isDbCreated = false;

    public abstract InventoryCheckDao inventoryCheckDao();
    public abstract InventoryItemDao inventoryItemDao();
    public abstract InventoryItemInstanceDao inventoryItemInstanceDao();
    public abstract InventoryPageDao inventoryPageDao();
    public abstract InventorySubListItemDao inventorySubListItemDao();
    public abstract InventoryItemRecordDao inventoryItemRecordDao();

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static synchronized InventoryDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (InventoryDatabase.class) {
                if (instance == null) {
                    instance = buildDatabase(context);
                }
            }
        }
        return instance;
    }

    public static InventoryDatabase buildDatabase(final Context context) {
        InventoryDatabase database = Room.databaseBuilder(context, InventoryDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        databaseWriteExecutor.execute(() -> {
                            Log.i(DATABASE_NAME, "Database Created");
                            getAndInsertTableData(getInstance(context), context, dataResIds);
                            isDbCreated = true;
                        });
                    }
                })
                .build();
        database.query("select 1", null);
        return database;
    }


    private static void getAndInsertTableData(InventoryDatabase db, Context context, Integer[] dataResIds) {
        InventoryDataGenerator.addChecks(context, db, R.array.inventory_checks);
        Arrays.stream(dataResIds)
                .forEach((dataResId) -> {
                    TypedArray ta =  context.getResources().obtainTypedArray(dataResId);
                    InventoryDataGenerator.addInventorySubListItems(context, db, ta, dataResId);
                });
    }

    public static ExecutorService getDatabaseWriteExecutor() {
        return databaseWriteExecutor;
    }
}
