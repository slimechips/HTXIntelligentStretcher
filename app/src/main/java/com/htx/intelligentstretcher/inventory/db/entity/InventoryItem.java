package com.htx.intelligentstretcher.inventory.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class InventoryItem {
    @PrimaryKey
    private int itemId;

    private String name;

    private String category;

    private int parentPageId;

    @Ignore
    private int doneQty;

    private int requiredQty;

    private String imageUri;

    @ColumnInfo(defaultValue = "-1")
    private int resourceId;

    private String description;

    @Ignore
    private Date latestExpiry;

    public InventoryItem(int itemId, String name, String category, int parentPageId, int requiredQty, String imageUri, int resourceId,
                         String description) {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.parentPageId = parentPageId;
        this.requiredQty = requiredQty;
        this.imageUri = imageUri;
        this.resourceId = resourceId;
        this.description = description;
        this.doneQty = 0;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getParentPageId() {
        return parentPageId;
    }

    public void setParentPageId(int parentPageId) {
        this.parentPageId = parentPageId;
    }

    public int getRequiredQty() {
        return requiredQty;
    }

    public void setRequiredQty(int requiredQty) {
        this.requiredQty = requiredQty;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getDoneQty() {
        return doneQty;
    }

    public void setDoneQty(int doneQty) {
        this.doneQty = doneQty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLatestExpiry() {
        return latestExpiry;
    }

    public void setLatestExpiry(Date latestExpiry) {
        this.latestExpiry = latestExpiry;
    }
}
