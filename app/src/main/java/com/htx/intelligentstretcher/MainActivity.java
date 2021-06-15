package com.htx.intelligentstretcher;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

//Test
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView Instant_flow_rate = (TextView) findViewById(R.id.textView3);
        Instant_flow_rate.setText("40.38");
    }
}