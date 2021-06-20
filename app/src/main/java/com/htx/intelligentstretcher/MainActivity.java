package com.htx.intelligentstretcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

//Test
public class MainActivity extends AppCompatActivity {
//    private TextView Instant_flow_rate;
//    private SeekBar seekBar;
    private Slider slider;
    private CardView card;
    private Button Instant_flow_rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Instant_flow_rate = (Button) findViewById(R.id.textButton2);
//        seekBar = (SeekBar) findViewById(R.id.seekBar);
        slider = (Slider) findViewById(R.id.slider);

//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                Instant_flow_rate.setText("" + i + "SLPM");
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                //Use the value
                java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.0");
                String str = myformat.format(value);
                Instant_flow_rate.setText("" + str + "SLPM");
            }
        });



    }
}