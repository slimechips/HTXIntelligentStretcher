package com.htx.intelligentstretcher.inventory.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class InventoryPage {
    @PrimaryKey
    private int pageId;

    private int subListItemId;

    private String name;

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public int getSubListItemId() {
        return subListItemId;
    }

    public void setSubListItemId(int subListItemId) {
        this.subListItemId = subListItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InventoryPage(int pageId, int subListItemId, String name) {
        this.pageId = pageId;
        this.subListItemId = subListItemId;
        this.name = name;
    }
}
