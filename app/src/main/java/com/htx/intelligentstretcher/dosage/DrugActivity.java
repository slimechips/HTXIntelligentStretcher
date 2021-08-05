package com.htx.intelligentstretcher.dosage;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.htx.intelligentstretcher.R;
import com.htx.intelligentstretcher.bean.EventBus_Tag;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DrugActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    public static List<Drug> drugList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_drug);
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        recyclerView = findViewById(R.id.rv);

        Drug drug1 = new Drug(R.drawable.adenosine, "Adenosine", "- 6mg/2mL(Adult with weight > 50Kg)\n- 0.05 to 0.1mg/Kg(Pediatric with weight < 50Kg)");
        Drug drug2 = new Drug(R.drawable.amiodarone, "Amiodarone", " 15mg/min");
        Drug drug3 = new Drug(R.drawable.gtn, "GTN", " 5mg/mL");
        Drug drug4 = new Drug(R.drawable.normalsaline, "Normal Saline 0.9%", " 15mg/min");
        Drug drug5 = new Drug(R.drawable.tramadol, "Tramadol", " 0.15mg/Kg");
        Drug drug6 = new Drug(R.drawable.vs, "Ventolin Salbutamol", " 2.5mg/3mL");
        drugList.clear();
        drugList.add(drug1);
        drugList.add(drug2);
        drugList.add(drug3);
        drugList.add(drug4);
        drugList.add(drug5);
        drugList.add(drug6);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        myAdapter = new MyAdapter(drugList);

        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drug_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventBus_Tag event) {
        switch (event.getTag()) {
            case 1:
                drugList.get(event.getPosition()).setEt(Double.parseDouble(event.getContent()));//accept the parameter change
                myAdapter.notifyDataSetChanged();//refresh the list
                break;
        }
    }

}