package com.htx.intelligentstretcher.inventory.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryItemRecord;
import com.htx.intelligentstretcher.inventory.db.models.InventoryItemRecordAndInstance;
import com.htx.intelligentstretcher.inventory.db.models.InventorySubListItemDoneQty;

import java.util.List;

@Dao
public interface InventoryItemRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(InventoryItemRecord record);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(List<InventoryItemRecord> records);

    @Query("SELECT record.*, instance.* FROM InventoryItemRecord record" +
            " INNER JOIN InventoryItemInstance instance ON record.linkedInstanceId = instance.instanceId" +
            " WHERE record.inventoryItemId = :itemId AND record.checkId = :checkId")
    LiveData<List<InventoryItemRecordAndInstance>> loadItemRecordsByCheckAndItemId(int checkId, int itemId);

    @Query("SELECT * FROM InventoryItemRecord WHERE checkId = :checkId AND inventoryItemId IN (:itemIds)")
    LiveData<List<InventoryItemRecord>> loadInventoryItemRecordsByCheckIdAndItemIds(int checkId, List<Integer> itemIds);

    @Transaction
    @Query("SELECT S.subListItemId, S.parentSubListItemId, COUNT(r.instanceCheckDate) AS totalQtyDone FROM " +
            "(SELECT subListItemId, parentSubListItemId FROM InventorySubListItem " +
            "WHERE parentSubListItemId = :subListItemId) S " +
            "INNER JOIN InventoryPage p ON S.subListItemId = p.subListItemId " +
            "INNER JOIN InventoryItem i ON p.pageId = i.parentPageId " +
            "INNER JOIN InventoryItemRecord r ON i.itemId = r.inventoryItemId " +
            "WHERE r.checkId = :checkId " +
            "GROUP BY S.subListItemId " +
            "ORDER BY S.subListItemId"
    )
    LiveData<List<InventorySubListItemDoneQty>> loadRecordCountByCheckIdAndParentSubListItemId(int checkId, int subListItemId);
}
