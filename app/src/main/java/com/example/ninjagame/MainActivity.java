package com.example.ninjagame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    public static String prefEnemigos;
    public static String prefNinja;
    boolean isMusicEnabled;
    private Button btJugar;
    private Button btPuntuacions;
    private Button btSalir;
    private MediaPlayer mediaPlayer;
    private SharedPreferences generalPref;
    private SharedPreferences scorePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        btJugar = findViewById(R.id.btJugar);
        btPuntuacions = findViewById(R.id.btPuntuacions);
        btSalir = findViewById(R.id.btSalir);

        btJugar.setOnClickListener(v -> checkNameUser());

        btSalir.setOnClickListener(view -> setBtSalir());

        btPuntuacions.setOnClickListener(view -> lsPuntuaciones());

        mediaPlayer = MediaPlayer.create(this, R.raw.base);
        generalPref = PreferenceManager.getDefaultSharedPreferences(this);

        scorePref = getSharedPreferences(getString(R.string.NinjaGame), Context.MODE_PRIVATE);
        checkMusic();
        getPrefs();

    }

    private void getPrefs() {
        prefEnemigos = generalPref.getString("prefEnemigos", "");
        prefNinja = generalPref.getString("prefNinja", "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkMusic();
        getPrefs();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

    }

    private void checkNameUser() {


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Nombre jugador");
        alert.setMessage("Por favor introduzca su nombre");
        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = input.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(MainActivity.this, "The field is empty", Toast.LENGTH_SHORT).show();
                } else {
                    saveDates(name);
                }
                //podemos guardar el nombre del jugador en las SharedPreferences
            }
        });
        alert.show();
    }

    private void saveDates(String name) {
        SharedPreferences.Editor editor = scorePref.edit();
        Intent i = new Intent(MainActivity.this, Joc.class);
        i.putExtra("userName", name);
        if (scorePref.contains(name)) {
            Toast.makeText(this, getString(R.string.nameExist) + name + "!!!", Toast.LENGTH_SHORT).show();
            startActivity(i);
        } else {
            editor.putInt(name, 0);
            startActivity(i);
        }
    }


    public void lsPuntuaciones() {
        scorePref = getSharedPreferences(getString(R.string.NinjaGame), Context.MODE_PRIVATE);

        if (scorePref.getAll().isEmpty()) {
            Toast.makeText(this, "La lista de las puntuaciones esta vacia", Toast.LENGTH_SHORT).show();
        }else {
            Map<String, ?> allEntries = scorePref.getAll();
            Map<String, Integer> mapPuntuaciones = new HashMap<>();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                mapPuntuaciones.put(entry.getKey(), (Integer) entry.getValue());
            }


            ArrayList<Map.Entry<String, Integer>> listaPuntuaciones = new ArrayList<>(mapPuntuaciones.entrySet());

            Collections.sort(listaPuntuaciones, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

            ArrayList<String> listaMostrada = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : listaPuntuaciones) {
                listaMostrada.add(entry.getKey() + " \t\t " + entry.getValue());
            }

            View view = (getLayoutInflater().inflate(R.layout.list_layout, null));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getList(listaMostrada));

            ListView lsView = view.findViewById(R.id.lvListPunt);

            lsView.setAdapter(adapter);

            createAlertDialogList(view);
        }

    }

    @NonNull
    private static List<String> getList(ArrayList<String> listaMostrada) {
        if(listaMostrada.size() < 5){
            return listaMostrada;
        }else {
            return listaMostrada.subList(0, 5);
        }

    }


    private void createAlertDialogList(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.puntuacions);
        alert.setMessage(R.string.top5);
        alert.setPositiveButton("Salir", ((dialog, which) -> {
        }));
        alert.setView(view);
        alert.show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_information) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Information");
            alert.setMessage("Creator: Adrián Pereira Espinosa \nName of Game: Ninja Game");
            alert.setPositiveButton("Salir", ((dialog, which) -> {
            }));
            alert.show();
            return true;
        }
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, PreferenciasActivity.class));
            return true;
        }
        return false;
    }

    public void setBtSalir() {
        finish();
        mediaPlayer.pause();
    }

}                                                                                   