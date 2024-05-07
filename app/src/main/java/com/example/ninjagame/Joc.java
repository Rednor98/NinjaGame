package com.example.ninjagame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class Joc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joc);
        VistaJoc vistaJoc = findViewById(R.id.VistaJoc);
        vistaJoc.setPare(this);
    }
}