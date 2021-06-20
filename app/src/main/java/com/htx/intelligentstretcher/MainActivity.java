package com.htx.intelligentstretcher;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentContainerView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.htx.intelligentstretcher.inventory.InventoryMainFragment;
import com.htx.intelligentstretcher.inventory.db.InventoryDatabase;

//Test
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InventoryDatabase.buildDatabase(getApplicationContext());
    }
}