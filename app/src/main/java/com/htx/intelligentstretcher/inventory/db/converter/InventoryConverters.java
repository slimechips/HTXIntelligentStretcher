package com.htx.intelligentstretcher.inventory.db.converter;

import android.net.Uri;
import androidx.room.TypeConverter;

import java.util.Date;

public class InventoryConverters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }


    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Uri uriFromString(String value) { return value == null ? null : Uri.parse(value); }

    @TypeConverter
    public static String uriToString(Uri uri) { return uri == null ? null : uri.toString(); }
}
