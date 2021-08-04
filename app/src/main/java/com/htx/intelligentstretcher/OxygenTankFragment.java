package com.htx.intelligentstretcher;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.android.material.slider.Slider;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OxygenTankFragment extends Fragment {
    private Slider slider;
    private CardView card;
    private TextView accumulatedText;
    private TextView remainingText;
    private TextView Instant_flow_rate;
    private static final DecimalFormat OXYGEN_LEVEL = new java.text.DecimalFormat("0.0");
    public static String accumulatedVol = "0";
    public static String oxygenStr = "0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_oxygen_tank, container, false);
        Instant_flow_rate = v.findViewById(R.id.textButton2);
//        seekBar = (SeekBar) findViewById(R.id.seekBar);
//        slider = (Slider) v.findViewById(R.id.slider);
        accumulatedText = v.findViewById(R.id.accumulated_vol_text);
        remainingText = v.findViewById(R.id.remaining_vol_text);
        //accumulatedText.setText(Float.toString(accumulatedVol));
        accumulatedText.setText(accumulatedVol);
        remainingText.setText("20 m\u00B3");

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

//        slider.addOnChangeListener(new Slider.OnChangeListener() {
//            @Override
//            public void onValueChange(Slider slider, float value, boolean fromUser) {
//                //Use the value
//                oxygenStr  = OXYGEN_LEVEL.format(value);
//                Instant_flow_rate.setText("" + oxygenStr + "SLPM");
//            }
//        });

        return v;
    }

    public TextView getAccumulatedText() {
        return accumulatedText;
    }

}
