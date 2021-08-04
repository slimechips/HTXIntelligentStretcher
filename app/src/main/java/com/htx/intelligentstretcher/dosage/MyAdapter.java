package com.htx.intelligentstretcher.dosage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.htx.intelligentstretcher.R;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHoder> implements Filterable {
    private List<Drug> drugList;
    private List<Drug> drugListFull;

    public MyAdapter(List<Drug> drugList) {

        this.drugList = drugList;
        drugListFull = new ArrayList<>(drugList);
    }

    @NonNull
    // @org.jetbrains.annotations.NotNull
    @Override
    public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        MyViewHoder myViewHoder = new MyViewHoder(itemView);
        return myViewHoder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHoder holder, int position) {
        Drug drug = drugList.get(position);
        holder.drug_name.setText(drug.getDrug_name());

        holder.drug_photo.setImageResource(drug.getDrug_photo());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
        holder.drug_info.setText(drug.getDrug_info());


    }

    @Override
    public int getItemCount() {
        return drugList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Drug> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(drugListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Drug drug : drugListFull) {
                    if (drug.getDrug_name().toLowerCase().contains(filterPattern)) {
                        filteredList.add(drug);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            drugList.clear();
            drugList.addAll((List) results.values);
            notifyDataSetChanged();

        }
    };

    class MyViewHoder extends RecyclerView.ViewHolder {
        TextView drug_info;
        TextView drug_name;
        ImageView drug_photo;
        RelativeLayout parentLayout;

        public MyViewHoder(View itemView) {
            super(itemView);
            this.drug_info = itemView.findViewById(R.id.drug_info);
            this.drug_name = itemView.findViewById(R.id.drug_name);
            this.drug_photo = itemView.findViewById(R.id.drug_photo);
            this.parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }
}