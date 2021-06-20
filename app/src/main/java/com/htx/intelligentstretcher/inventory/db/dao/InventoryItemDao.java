package com.htx.intelligentstretcher.inventory.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryItem;
import com.htx.intelligentstretcher.inventory.db.models.InventoryItemDoneQty;

import java.util.List;

@Dao
public interface InventoryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(InventoryItem item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(List<InventoryItem> items);

    @Query("SELECT * FROM InventoryItem WHERE itemId = :itemId")
    LiveData<InventoryItem> loadInventoryItemByItemId(int itemId);

    @Query("SELECT requiredQty FROM InventoryItem WHERE itemId = :itemId")
    int getInventoryItemReqQty(int itemId);

    @Query("SELECT * FROM InventoryItem WHERE itemId = :itemId")
    LiveData<InventoryItem> loadInventoryItemById(int itemId);

    @Query("SELECT * FROM InventoryItem WHERE parentPageId IN (:pageId)")
    LiveData<List<InventoryItem>> loadInventoryItemByPageId(int pageId);

    @Transaction
    @Query("SELECT I.itemId, COUNT(r.linkedInstanceId) as doneQty FROM " +
            "(SELECT it.itemId FROM InventoryItem it " +
            "WHERE it.parentPageId = :pageId) I " +
            "INNER JOIN InventoryItemRecord r ON I.itemId = r.inventoryItemId " +
            "WHERE r.checkId = :checkId " +
            "GROUP BY I.itemId ")
    LiveData<List<InventoryItemDoneQty>> loadInventoryItemsDoneQtyByCheckAndPageId(int checkId, int pageId);

    @Transaction
    @Query("SELECT r.inventoryItemId AS itemId, COUNT(r.linkedInstanceId) as doneQty " +
            "FROM InventoryItemRecord r " +
            "WHERE r.checkId = :checkId AND r.inventoryItemId = :itemId")
    LiveData<InventoryItemDoneQty> loadInventoryItemDontQtyByCheckAndItemId(int checkId, int itemId);
}
