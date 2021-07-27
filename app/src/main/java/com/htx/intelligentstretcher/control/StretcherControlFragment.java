package com.htx.intelligentstretcher.control;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.htx.intelligentstretcher.R;
import com.htx.intelligentstretcher.utils.CotToChairJSON;
import com.htx.intelligentstretcher.utils.HeightControlJSON;
import com.htx.intelligentstretcher.utils.PowerAssControlJSON;

//import static com.htx.intelligentstretcher.MainActivity.client;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import javax.security.auth.login.LoginException;

public class StretcherControlFragment extends Fragment {

    private ImageButton upButton;
    private ImageButton downButton;
    private ImageButton cotButton;
    private ImageButton chairButton;
    private ImageButton powerAssButton;

    static public boolean cotSelected = false;
    static public boolean powerAssOn = false;
    private HeightControlJSON heightCommands = new HeightControlJSON();
    private PowerAssControlJSON powerAssCommands = new PowerAssControlJSON();
    private CotToChairJSON cotToChairCommands = new CotToChairJSON();
    private Gson gson = new Gson();
    private String json;



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
                powerAssCommands.setPowerAssCommand("off");
            } else {
                powerAssButton.setImageResource(R.drawable.power_button_red);
                powerAssButton.setAlpha(1.0f);
                powerAssOn = true;
                powerAssCommands.setPowerAssCommand("on");
            }
            json = gson.toJson(powerAssCommands);
            Log.i("powerAss", json);
        });
        cotButton.callOnClick();
        upButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        heightCommands.setHeightCommand("up");
                        json = gson.toJson(heightCommands);
                        Log.i("height", json);
                        //pub(json, "Height");
                        return false;

                    case MotionEvent.ACTION_UP:
                        heightCommands.setHeightCommand("stop");
                        json = gson.toJson(heightCommands);
                        Log.i("height", json);
                        //pub(json, "Height");
                        return false;
                }
                return false;
            }
        });

        downButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        heightCommands.setHeightCommand("down");
                        json = gson.toJson(heightCommands);
                        Log.i("height", json);
                        return false;

                    case MotionEvent.ACTION_UP:
                        heightCommands.setHeightCommand("stop");
                        json = gson.toJson(heightCommands);
                        Log.i("height", json);
                        return false;
                }
                return false;
            }
        });

        return v;
    }



    @Override
    public void onResume() {
        super.onResume();
        togglePower();
        changeToCot();
    }

    private synchronized void toggleCot() {
        if (!cotSelected) {
            cotButton.setAlpha(1.0f);
            chairButton.setAlpha(0.2f);
        }
        cotToChairCommands.setCotToChairCommand("cot");
        json = gson.toJson(cotToChairCommands);
        Log.i("Cot to Chair", json);
        cotSelected = true;
    }

    private synchronized void toggleChair() {
        if (cotSelected) {
            cotButton.setAlpha(0.2f);
            chairButton.setAlpha(1.0f);
        }
        cotToChairCommands.setCotToChairCommand("chair");
        json = gson.toJson(cotToChairCommands);
        Log.i("Cot to Chair", json);
        cotSelected = false;
    }

    public void changeToCot() {
        if (cotSelected) {
            cotButton.setAlpha(1.0f);
            chairButton.setAlpha(0.2f);
        } else {
            cotButton.setAlpha(0.2f);
            chairButton.setAlpha(1.0f);
        }
    }

    public void togglePower() {
        if (powerAssOn) {
            powerAssButton.setImageResource(R.drawable.power_button_red);
            powerAssButton.setAlpha(1.0f);
        }
    }

//    public void pub(String msg, String topicStr){
//        String topic = topicStr;
//        String message = msg;
//        Log.i("test", "pub: ");
//        try {
//            client.publish(topic, message.getBytes(),0,false);
//
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }


}
