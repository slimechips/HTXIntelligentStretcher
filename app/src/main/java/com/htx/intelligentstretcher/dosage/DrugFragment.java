//package com.htx.intelligentstretcher.dosage;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.SearchView;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.htx.intelligentstretcher.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DrugFragment extends Fragment {
//
//    private RecyclerView recyclerView;
//    private MyAdapter myAdapter;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_drug, container, false);
//        super.onCreate(savedInstanceState);
//
//        recyclerView = v.findViewById(R.id.rv);
//
//        List<Drug> drugList = new ArrayList<>();
//        Drug drug1 = new Drug(R.drawable.adenosine,"Adenosine","- 6mg/2mL(Adult with weight > 50Kg)\n- 0.05 to 0.1mg/Kg(Pediatric with weight < 50Kg)");
//        Drug drug2 = new Drug(R.drawable.amiodarone,"Amiodarone"," 15mg/min");
//        Drug drug3 = new Drug(R.drawable.gtn,"GTN"," 5mg/mL");
//        Drug drug4 = new Drug(R.drawable.normalsaline,"Normal Saline 0.9%"," 15mg/min");
//        Drug drug5 = new Drug(R.drawable.tramadol,"Tramadol"," 0.15mg/Kg");
//        Drug drug6 = new Drug(R.drawable.vs,"Ventolin Salbutamol"," 2.5mg/3mL");
//        drugList.add(drug1);
//        drugList.add(drug2);
//        drugList.add(drug3);
//        drugList.add(drug4);
//        drugList.add(drug5);
//        drugList.add(drug6);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setOrientation(RecyclerView.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//
//        myAdapter  = new MyAdapter(drugList);
//
//        recyclerView.setAdapter(myAdapter);
//        return v;
//    }
//}
