package com.htx.intelligentstretcher.inventory.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.htx.intelligentstretcher.inventory.db.entity.InventorySubListItem;
import com.htx.intelligentstretcher.inventory.db.models.InventorySubListItemTotalQty;
import com.htx.intelligentstretcher.inventory.db.models.InventorySubListItemWithPage;
import com.htx.intelligentstretcher.inventory.db.models.InventorySubListItemWithSubListsAndPage;

import java.util.List;

@Dao
public interface InventorySubListItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(InventorySubListItem item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(List<InventorySubListItem> items);

    @Query("SELECT * FROM InventorySubListItem WHERE subListItemId = :subListItemId")
    LiveData<InventorySubListItem> loadSubListItem(int subListItemId);

    @Transaction
    @Query("SELECT * FROM InventorySubListItem WHERE subListItemId = :subListItemId")
    LiveData<InventorySubListItemWithPage> loadSubListItemWithPage(int subListItemId);

    @Query("SELECT * FROM InventorySubListItem WHERE parentName = :parentName")
    LiveData<List<InventorySubListItem>> loadSubListItemsByParentName(int parentName);

    @Transaction
    @Query("SELECT * FROM InventorySubListItem WHERE subListItemId = :subListItemId")
    InventorySubListItemWithSubListsAndPage getInventorySubListItemWithSubListItems(int subListItemId);

    @Query("SELECT * FROM InventorySubListItem WHERE parentSubListItemId = :parentSubListItemId")
    LiveData<List<InventorySubListItem>> loadInventorySubListItemsByParentSubListId(int parentSubListItemId);

    @Transaction
    @Query("SELECT * FROM InventorySubListItem WHERE parentSubListItemId = :parentSubListItemId")
    LiveData<List<InventorySubListItemWithPage>> loadSubListItemsWithPageByParentSubListId(int parentSubListItemId);

    @Transaction
    @Query("SELECT * from InventorySubListItem")
    List<InventorySubListItemWithSubListsAndPage> getSubListsWithSubListsAndPages();

    @Transaction
    @Query("SELECT S.subListItemId, S.parentSubListItemId, SUM(i.requiredQty) AS totalQty FROM " +
            "(SELECT subListItemId, parentSubListItemId FROM InventorySubListItem " +
            "WHERE parentSubListItemId = :subListItemId) S " +
            "INNER JOIN InventoryPage p ON S.subListItemId = p.subListItemId " +
            "INNER JOIN InventoryItem i ON p.pageId = i.parentPageId " +
            "GROUP BY S.subListItemId " +
            "ORDER BY S.subListItemId"
    )
    LiveData<List<InventorySubListItemTotalQty>> loadSubListTotalQtyBySubListId(int subListItemId);
}
