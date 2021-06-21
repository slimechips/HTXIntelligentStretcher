package com.htx.intelligentstretcher.control;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.htx.intelligentstretcher.R;

import java.util.concurrent.atomic.AtomicBoolean;

public class StretcherControlFragment extends Fragment {

    private ImageButton upButton;
    private ImageButton downButton;
    private ImageButton cotButton;
    private ImageButton chairButton;
    private ImageButton powerAssButton;

    private boolean cotSelected = false;
    private boolean powerAssOn = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stretcher_control, container, false);
        upButton = v.findViewById(R.id.up_button);
        downButton = v.findViewById(R.id.down_button);
        cotButton = v.findViewById(R.id.stretcher_cot_mode_image);
        chairButton = v.findViewById(R.id.stretcher_chair_mode_image);
        powerAssButton = v.findViewById(R.id.power_ass_button);

        cotButton.setOnClickListener(v1 -> toggleCot());
        chairButton.setOnClickListener(v1 -> toggleChair());
        powerAssButton.setOnClickListener(v1 -> {
            if (powerAssOn) {
                powerAssButton.setImageResource(R.drawable.power_button);
                powerAssButton.setAlpha(0.2f);
                powerAssOn = false;
            } else {
                powerAssButton.setImageResource(R.drawable.power_button_red);
                powerAssButton.setAlpha(1.0f);
                powerAssOn = true;
            }
        });
        cotButton.callOnClick();

        return v;
    }

    private synchronized void toggleCot() {
        if (!cotSelected) {
            cotButton.setAlpha(1.0f);
            chairButton.setAlpha(0.2f);
        }
        cotSelected = true;
    }

    private synchronized void toggleChair() {
        if (cotSelected) {
            cotButton.setAlpha(0.2f);
            chairButton.setAlpha(1.0f);
        }
        cotSelected = false;
    }
}
