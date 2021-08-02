package com.htx.intelligentstretcher.dosage;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.htx.intelligentstretcher.R;
import com.htx.intelligentstretcher.bean.EventBus_Tag;

import org.greenrobot.eventbus.EventBus;

public class DetailActivity extends AppCompatActivity {
    ImageView img;

    TextView tv;
    EditText et;
    int position;
    public static float weight_value = 60;
    public static float updated_weight_value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        img = findViewById(R.id.img);
        tv = findViewById(R.id.tv);
        et = findViewById(R.id.et);

        position = getIntent().getIntExtra("position", 0);
        //set item
        img.setBackgroundResource(DrugActivity.drugList.get(position).getDrug_photo());
        if (DrugActivity.drugList.get(position).getEt() == 0 && weight_value != updated_weight_value) {
//            et.setText("");
            et.setText(Float.toString(weight_value));
            updated_weight_value = weight_value;
        } else
            et.setText(DrugActivity.drugList.get(position).getEt() + "");

        EventBus.getDefault().post(new EventBus_Tag(1, position, et.getText().toString()));
        setValue();
        //click event
        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sd = et.getText().toString();
                if (isEmpty(sd)) {
                    Toast.makeText(DetailActivity.this, " Cannot be empty ", Toast.LENGTH_SHORT).show();
                    return;
                }
                EventBus.getDefault().post(new EventBus_Tag(1, position, sd));
                setValue();
            }
        });

    }

    private void setValue(){
        switch (position) {
            case 0:
                if (DrugActivity.drugList.get(position).getEt() > 50) {
                    tv.setText("6mg/2mL");
                } else if (DrugActivity.drugList.get(position).getEt() <= 50 && DrugActivity.drugList.get(position).getEt()>0){
                    tv.setText("0.05 to 0.1mg/Kg");
                }
                else {
                    tv.setText("");
                }

                break;
            default:
                tv.setText(DrugActivity.drugList.get(position).getDrug_info());
                break;
        }
    }

    //is string empty?
    public static boolean isEmpty(String str) {
        return str == null || str.equals("");
    }
}