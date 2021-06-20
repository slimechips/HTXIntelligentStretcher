package com.htx.intelligentstretcher.inventory.db.models;

import androidx.room.Embedded;
import androidx.room.Relation;
import com.htx.intelligentstretcher.inventory.db.entity.InventorySubListItem;

import java.util.List;

public class InventorySubListItemWithSubListsAndPage {
    @Embedded public InventorySubListItemWithPage subListItemWithPage;

    @Relation(
            entity = InventorySubListItem.class,
            parentColumn = "subListItemId",
            entityColumn = "parentSubListItemId"
    )
    public List<InventorySubListItemWithPage> subListItemsWithPage;
}
