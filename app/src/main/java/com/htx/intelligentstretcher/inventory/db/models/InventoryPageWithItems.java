package com.htx.intelligentstretcher.inventory.db.models;

import androidx.room.Embedded;
import androidx.room.Relation;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryItem;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryPage;

import java.util.List;

public class InventoryPageWithItems {
    @Embedded
    public InventoryPage page;
    @Relation(
            parentColumn = "pageId",
            entityColumn = "parentPageId"
    )
    public List<InventoryItem> items;
}
