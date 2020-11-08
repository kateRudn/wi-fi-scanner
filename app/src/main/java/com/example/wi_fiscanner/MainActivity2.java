package com.example.wi_fiscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;


public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        String result = getIntent().getExtras().getString("result");
        final TextView TextView= findViewById(R.id.txt);
        String information="";
        String [] name_feauters={"SSID: ", "BSSID: ", "Protection: ", "Level: ", "Frequency: "};
        String [] feauters={"", "", "", "", ""};
            String[] vector_item = result.split(",");
            String item_essid = vector_item[0];
            String item_bssid = vector_item[1];
            String item_capabilities = vector_item[2];
            String item_level = vector_item[3];
            String item_frequency= vector_item[4];
            feauters[0] = item_essid.split(": ")[1];
            feauters[1] = item_bssid.split(": ")[1];
            feauters[2] = item_capabilities.split(": ")[1];
            feauters[3] = func_level(item_level.split(": ")[1]);
            feauters[4]=item_frequency.split(": ")[1];
        for (int i=0; i< name_feauters.length; i++)
        {
            information=information.concat(name_feauters[i]);
            information=information.concat(feauters[i]);
            information=information.concat("\r\n\n");
        }
        TextView.setText(information);
    }

    private String func_level(String lvl)
    {
        String str_lvl="none";
        int int_lvl = Integer.parseInt(lvl);
        if (int_lvl>-35){
            str_lvl="High";
        } else if (int_lvl<=-35 && int_lvl>-65){
            str_lvl="Middle";
        } else if (int_lvl<=-65){
            str_lvl="Low";
        }
        return str_lvl;
    }

}

