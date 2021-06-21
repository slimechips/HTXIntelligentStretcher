package com.htx.intelligentstretcher.dosage;

public class Drug {

    private int drug_photo;
    private String drug_name;
    private String drug_info;

    public Drug(int drug_photo, String drug_name, String drug_info) {
        this.drug_photo = drug_photo;
        this.drug_name = drug_name;
        this.drug_info = drug_info;
    }

    public int getDrug_photo() {
        return drug_photo;
    }

    public void setDrug_photo(int drug_photo) {
        this.drug_photo = drug_photo;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getDrug_info() {
        return drug_info;
    }

    public void setDrug_info(String drug_info) {
        this.drug_info = drug_info;
    }
}