package com.htx.intelligentstretcher.bean;

import android.graphics.Bitmap;

import java.io.File;


public class EventBus_Tag {
    private int tag;
    private int position;
    private boolean bl;
    private String content;
    private String content2;
    private String content3;
    private String content4;
    private int content5;
    private int content6;
    private Bitmap bitmap;
    private File file;

    public EventBus_Tag(int tag, String content, int content5) {
        this.tag = tag;
        this.content = content;
        this.content5 = content5;
    }

    public EventBus_Tag(int tag, int position, String content) {
        this.tag = tag;
        this.content = content;
        this.position = position;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public EventBus_Tag(int tag, Bitmap bitmap, File file) {
        this.tag = tag;
        this.bitmap = bitmap;
        this.file = file;
    }

    public EventBus_Tag(int tag, int content5, int content6) {
        this.tag = tag;
        this.content5 = content5;
        this.content6 = content6;
    }

    public EventBus_Tag(int tag) {
        this.tag = tag;
    }

    public EventBus_Tag(int tag, int position) {
        this.tag = tag;
        this.position = position;
    }

    public EventBus_Tag(int tag, boolean bl) {
        this.tag = tag;
        this.bl = bl;
    }

    public EventBus_Tag(int tag, String content) {
        this.tag = tag;
        this.content = content;
    }

    public EventBus_Tag(int tag, int position, boolean bl) {
        this.tag = tag;
        this.position = position;
        this.bl = bl;
    }

    public EventBus_Tag(int tag, String content, String content2) {
        this.tag = tag;
        this.content = content;
        this.content2 = content2;
    }

    public EventBus_Tag(int tag, String content, String content2, String content3) {
        this.tag = tag;
        this.content = content;
        this.content2 = content2;
        this.content3 = content3;
    }

    public EventBus_Tag(int tag, String content, String content2, String content3, String content4) {
        this.tag = tag;
        this.content = content;
        this.content2 = content2;
        this.content3 = content3;
        this.content4 = content4;
    }

    public int getContent5() {
        return content5;
    }

    public void setContent5(int content5) {
        this.content5 = content5;
    }

    public int getContent6() {
        return content6;
    }

    public void setContent6(int content6) {
        this.content6 = content6;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isBl() {
        return bl;
    }

    public void setBl(boolean bl) {
        this.bl = bl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent2() {
        return content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }

    public String getContent3() {
        return content3;
    }

    public void setContent3(String content3) {
        this.content3 = content3;
    }

    public String getContent4() {
        return content4;
    }

    public void setContent4(String content4) {
        this.content4 = content4;
    }
}
