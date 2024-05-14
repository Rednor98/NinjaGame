package com.example.ninjagame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Joc extends AppCompatActivity {
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joc);
        VistaJoc vistaJoc = findViewById(R.id.VistaJoc);
        vistaJoc.setPare(this);
        preferences = getSharedPreferences(getString(R.string.NinjaGame), Context.MODE_PRIVATE);

    }

    public void gameOver(String name, int score, int points) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        SharedPreferences.Editor editor = preferences.edit();
        alert.setTitle(R.string.gameOverName);
        alert.setMessage("Player: " + name + " TopScore: " + score + "\n" + "Score: " + points);
        if (points > score) {
            editor.putInt(name, points);
            editor.commit();
        }
        alert.setNegativeButton("Salir", ((dialog, which) -> {
            finish();
        }));
        alert.setPositiveButton("Reintentar", ((dialog, which) -> {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }));
        alert.show();
    }
}