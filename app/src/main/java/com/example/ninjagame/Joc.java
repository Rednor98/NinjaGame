package com.example.ninjagame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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
    public void gameOver(String name, int score) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.gameOverName);
        alert.setMessage("Player: " + name +  score);
        alert.setPositiveButton("Salir", ((dialog, which) -> {
            finish();
        }));
        //TODO No se puede hacer un alertDialog en VistaJoc porque es un View, Mirar a ver si se puede y si no en la activity
        alert.show();
    }
}