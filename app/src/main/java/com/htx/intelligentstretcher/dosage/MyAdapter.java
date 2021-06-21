package com.htx.intelligentstretcher.dosage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.htx.intelligentstretcher.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHoder> {
    private List<Drug> drugList;

    public MyAdapter(List<Drug> drugList) {
        this.drugList = drugList;
    }

    @NonNull
    // @org.jetbrains.annotations.NotNull
    @Override
    public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dosage_recycler_item, parent,false);
        MyViewHoder myViewHoder = new MyViewHoder(itemView);
        return myViewHoder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHoder holder, int position) {
        Drug drug = drugList.get(position);
        holder.drug_name.setText(drug.getDrug_name());
        holder.drug_info.setText(drug.getDrug_info());
        holder.drug_photo.setImageResource(drug.getDrug_photo());
    }

    @Override
    public int getItemCount() {
        return drugList.size();
    }

    class MyViewHoder extends RecyclerView.ViewHolder {
        TextView drug_info;
        TextView drug_name;
        ImageView drug_photo;

        public MyViewHoder(View itemView) {
            super(itemView);
            this.drug_info = itemView.findViewById(R.id.drug_info);
            this.drug_name = itemView.findViewById(R.id.drug_name);
            this.drug_photo = itemView.findViewById(R.id.drug_photo);
        }
    }
}