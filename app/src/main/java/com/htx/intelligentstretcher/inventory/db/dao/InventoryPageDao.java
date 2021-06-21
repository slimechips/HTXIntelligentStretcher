package com.htx.intelligentstretcher.inventory.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryPage;
import com.htx.intelligentstretcher.inventory.db.models.InventoryPageWithItems;

import java.util.List;

@Dao
public interface InventoryPageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(InventoryPage page);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(List<InventoryPage> pages);

    @Query("SELECT * FROM InventoryPage WHERE pageId = :pageId")
    LiveData<InventoryPage> loadPageById(int pageId);

    @Query("SELECT * FROM inventorypage WHERE subListItemId = :subListItemId")
    LiveData<InventoryPage> loadPageBySubListItemId(int subListItemId);

    @Transaction
    @Query("SELECT * FROM inventorypage WHERE pageId = :pageId")
    LiveData<InventoryPageWithItems> loadPageWithItemsById(int pageId);

    @Query("SELECT * FROM inventorypage WHERE subListItemId IN (:subListItemIds)")
    LiveData<InventoryPage> loadPagesBySubListItemIds(List<Integer> subListItemIds);

    @Transaction
    @Query("SELECT * FROM InventoryPage WHERE pageId IN (:pageIds)")
    List<InventoryPageWithItems> getPagesWithItemsByIds(List<Integer> pageIds);

    @Transaction
    @Query("SELECT * FROM InventoryPage WHERE subListItemId IN (:subListItemIds)")
    List<InventoryPageWithItems> getPagesWithItemsBySubListItemIds(List<Integer> subListItemIds);

}
