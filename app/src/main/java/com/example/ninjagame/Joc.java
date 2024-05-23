
package com.example.ninjagame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class Joc extends AppCompatActivity {
    SharedPreferences preferences;
    private MediaPlayer mediaPlayer;
    boolean isMusicEnabled;
    private SharedPreferences generalPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joc);
        VistaJoc vistaJoc = findViewById(R.id.VistaJoc);
        vistaJoc.setPare(this);
        preferences = getSharedPreferences(getString(R.string.NinjaGame), Context.MODE_PRIVATE);
        mediaPlayer = MediaPlayer.create(this, R.raw.base);
        generalPref = PreferenceManager.getDefaultSharedPreferences(this);

    }
    @Override
    protected void onResume() {
        super.onResume();
        checkMusic();

    }
    private void checkMusic() {
        //Pendiente de revision
        isMusicEnabled = generalPref.getBoolean("boolMusic", false);

        if (isMusicEnabled) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            // Pausar la musica
            mediaPlayer.pause();
        }

    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

    }

    public void gameOver(String name, int score, int points, boolean winner) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        SharedPreferences.Editor editor = preferences.edit();
        if(winner){
            alert.setTitle(R.string.winner);
        } else if (!winner) {
            alert.setTitle(R.string.gameOverName);
        }

        alert.setMessage("Player: " + name + "\n" + "TopScore: " + score + " Score: " + points);
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
