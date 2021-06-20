package com.htx.intelligentstretcher.inventory.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class InventoryCheck {
    @PrimaryKey()
    private int checkId;

    private String checker;

    private Date checkTime;

    @Ignore
    public static SimpleDateFormat CHECK_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public InventoryCheck(int checkId, String checker, Date checkTime) {
        this.checkId = checkId;
        this.checker = checker;
        this.checkTime = checkTime;
    }

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }
}
