package com.example.donatehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class thankpage extends AppCompatActivity {
    TextView thanktext;
    Button homebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankpage);
        thanktext=findViewById(R.id.thanktext);
        homebtn=findViewById(R.id.homebtn);


        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thankpage.this,homepage.class);
                startActivity(intent);
            }
        });
    }
}