package com.htx.intelligentstretcher.inventory.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryCheck;

@Dao
public interface InventoryCheckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(InventoryCheck check);
}
