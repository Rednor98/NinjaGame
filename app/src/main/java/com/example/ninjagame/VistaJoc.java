package com.example.ninjagame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import java.util.Vector;

public class VistaJoc extends View {
    // Increment estàndard de gir i acceleració
    private static final int INC_GIR = 5;
    private static final float INC_ACCELERACIO = 0.5f;
    //Cada quant temps volem processar canvis (ms)
    private static final int PERIODE_PROCES = 50;
    private static final int INC_VELOCITAT_GANIVET = 12;
    // //// NINJA //////
    private final Grafics ninja;// Gràfic del ninja
    // //// THREAD I TEMPS //////
    // Thread encarregat de processar el joc
    private final ThreadJoc thread = new ThreadJoc();
    private final int numObjectius;
    private final Drawable[] ninjaDraws = new Drawable[]{getContext().getDrawable(R.drawable.ninja01), getContext().getDrawable(R.drawable.ninja02), getContext().getDrawable(R.drawable.ninja03)};
    private final Vector<Grafics> objectius = new Vector<Grafics>();
    private int girNinja; // Increment de direcció
    private float acceleracioNinja; // augment de velocitat
    // Quan es va realitzar l'últim procés
    private long ultimProces = 0;
    private float mX = 0, mY = 0;
    private boolean llancament = false;
    // //// LLANÇAMENT //////
    private Grafics ganivet;
    private boolean ganivetActiu = false;
    private int tempsGanivet;
    private final Drawable[] drawableObjectiu = new Drawable[8];
    private final Drawable drawableNinja;
    private final Drawable drawableGanivet;
    private final Drawable drawableEnemic;

    private int lifeNinja = 0;

    private Bundle bundle;


    public VistaJoc(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Ninja Life
        lifeNinja = 1;
        // Obtenim referència al recurs ninja_enemic guardat en carpeta Res
        drawableEnemic = context.getResources().getDrawable(R.drawable.ninja_enemic, null);
        // Obtenim referència al recurs ninja guardat en carpeta Res
        drawableNinja = ninjaDraws[Integer.parseInt(MainActivity.prefNinja)];
        drawableGanivet = context.getResources().getDrawable(R.drawable.ganivet, null);
        numObjectius = Integer.parseInt(MainActivity.prefEnemigos);
        ninja = new Grafics(this, drawableNinja);
        ganivet = new Grafics(this, drawableGanivet);
        // Creem els objectius o blancs i inicialitzem la seva velocitat, angle i
        // rotació. La posició inicial no la podem obtenir
        // fins a conèixer ample i alt pantalla
        for (int i = 0; i < numObjectius; i++) {
            Grafics objectiu = new Grafics(this, drawableEnemic);
            objectiu.setIncY(Math.random() * 4 - 2);
            objectiu.setIncX(Math.random() * 4 - 2);
            objectiu.setAngle((int) (Math.random() * 360));
            objectiu.setRotacio((int) (Math.random() * 8 - 4));
            objectius.add(objectiu);
        }
        drawableObjectiu[0] = context.getResources().getDrawable(R.drawable.cap_ninja, null); //cap
        drawableObjectiu[1] = context.getResources().getDrawable(R.drawable.cos_ninja, null); //cos
        drawableObjectiu[2] = context.getResources().getDrawable(R.drawable.cua_ninja, null);
        drawableObjectiu[3] = context.getResources().getDrawable(R.drawable.cama_dreta, null);
        drawableObjectiu[4] = context.getResources().getDrawable(R.drawable.cama_esquerra, null);
        drawableObjectiu[5] = context.getResources().getDrawable(R.drawable.brac_dret, null);
        drawableObjectiu[6] = context.getResources().getDrawable(R.drawable.brac_esquerre, null);

    }

    // Métode que ens dóna ample i alt pantalla
    @Override
    protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
        // Una vegada que coneixem el nostre ample i alt situem els objectius de
        // forma aleatória
        for (Grafics objectiu : objectius) {
            do {
                objectiu.setPosX(Math.random() * (ancho - objectiu.getAmplada()));
                objectiu.setPosY(Math.random() * (alto - objectiu.getAltura()));
            } while (objectiu.distancia(ninja) < (ancho + alto) / 5);
        }
        ninja.setPosX(ancho / 2 - ninja.getAmplada() / 2);
        ninja.setPosY(alto / 2 - ninja.getAltura() / 2);

        ultimProces = System.currentTimeMillis();
        thread.start();


    }

    // Métode que dibuixa la vista
    @Override
    synchronized protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Grafics objetiu : objectius) {
            objetiu.dibuixaGrafic(canvas);
        }
        if (ganivetActiu) {
            ganivet.dibuixaGrafic(canvas);
        }
        ninja.dibuixaGrafic(canvas);
    }

    synchronized protected void actualitzaMoviment() {
        long instant_actual = System.currentTimeMillis();
        // No facis res si el període de procés no s'ha complert.
        if (ultimProces + PERIODE_PROCES > instant_actual) {
            return;
        }
        // Per una execució en temps real calculem retard
        double retard = (instant_actual - ultimProces) / PERIODE_PROCES;
        ultimProces = instant_actual; // Per a la propera vegada
        ultimProces = instant_actual; // Per a la propera vegada
        // Actualitzem velocitat i direcció del personatge Ninja a partir de
        // girNinja i acceleracioNinja (segons l'entrada del jugador)
        ninja.setAngle((int) (ninja.getAngle() + girNinja * retard));
        double nIncX = ninja.getIncX() + acceleracioNinja * Math.cos(Math.toRadians(ninja.getAngle())) * retard;
        double nIncY = ninja.getIncY() + acceleracioNinja * Math.sin(Math.toRadians(ninja.getAngle())) * retard;
        // Actualitzem si el módul de la velocitat no és més gran que el màxim
        if (Math.hypot(nIncX, nIncY) <= Grafics.MAX_VELOCITAT) {
            ninja.setIncX(nIncX);
            ninja.setIncY(nIncY);
        }
        // Actualitzem posicions X i Y
        ninja.incrementaPos(retard);
        for (Grafics objectiu : objectius) {
            objectiu.incrementaPos(retard);
        }
        // Actualitzem posició de ganivet
        if (ganivetActiu) {
            ganivet.incrementaPos(retard);
            tempsGanivet -= retard;
            if (tempsGanivet < 0) {
                ganivetActiu = false;
            } else {
                for (int i = 0; i < objectius.size(); i++)
                    if (ganivet.verificaColision(objectius.elementAt(i))) {
                        destrueixObjectiu(i);
                        break;
                    }
                for (int i = 0; i < objectius.size(); i++){
                    if(ninja.verificaColision(objectius.elementAt(i))){
                        lifeNinja--;
                        gameOver();
                        break;
                    }
                }

            }
        }
    }

    private void gameOver() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.gameOverName);
        alert.setMessage("Player: " );
        alert.setPositiveButton("Salir", ((dialog, which) -> {
        }));

        alert.show();
    }

    private void destrueixObjectiu(int i) {
        objectius.remove(i);
        ganivetActiu = false;
        int numParts = 3;
        if (objectius.get(i).getDrawable() == drawableEnemic) {
            for (int n = 0; n < numParts; n++) {
                Grafics objectiu = new Grafics(this, drawableObjectiu[n]);
                objectiu.setPosX(objectius.get(i).getPosX());
                objectiu.setPosY(objectius.get(i).getPosY());
                objectiu.setIncX(Math.random() * 7 - 3);
                objectiu.setIncY(Math.random() * 7 - 3);
                objectiu.setAngle((int) (Math.random() * 360));
                objectiu.setRotacio((int) (Math.random() * 8 - 4));
                objectius.add(objectiu);
            }
        }
    }

    private void DisparaGanivet() {
        ganivet.setPosX(ninja.getPosX() + ninja.getAmplada() / 2 - ganivet.getAmplada() / 2);
        ganivet.setPosY(ninja.getPosY() + ninja.getAltura() / 2 - ganivet.getAltura() / 2);
        ganivet.setAngle(ninja.getAngle());
        ganivet.setIncX(Math.cos(Math.toRadians(ganivet.getAngle())) * INC_VELOCITAT_GANIVET);
        ganivet.setIncY(Math.sin(Math.toRadians(ganivet.getAngle())) * INC_VELOCITAT_GANIVET);
        tempsGanivet = (int) Math.min(this.getWidth() / Math.abs(ganivet.getIncX()), this.getHeight() / Math.abs(ganivet.getIncY())) - 2;
        ganivetActiu = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                llancament = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dy < 6 && dx > 6) {
                    girNinja = Math.round((x - mX) / 2);
                    llancament = false;
                } else if (dx < 6 && dy > 6) {
                    acceleracioNinja = Math.round((mY - y) / 25);
                    llancament = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                girNinja = 0;
                acceleracioNinja = 0;
                if (llancament) {
                    DisparaGanivet();
                }
                break;
        }
        mX = x;
        mY = y;
        return true;
    }

    @Override
    public boolean onKeyDown(int codiTecla, KeyEvent event) {
        super.onKeyDown(codiTecla, event);
        // Suposem que processarem la pulsació
        boolean procesada = true;
        switch (codiTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                acceleracioNinja = +INC_ACCELERACIO;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                acceleracioNinja = -INC_ACCELERACIO;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                girNinja = -INC_GIR;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                girNinja = +INC_GIR;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                DisparaGanivet();
                break;
            default:
                // Si estem aquí, no hi ha pulsació que ens interessi
                procesada = false;
                break;
        }
        return procesada;
    }

    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        super.onKeyUp(codigoTecla, evento);
        // Suposem que processarem la pulsació
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                acceleracioNinja = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                acceleracioNinja = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                girNinja = 0;
                break;
            default:
                // Si estem aquí, no hi ha pulsació que ens interessi
                procesada = false;
                break;
        }
        return procesada;
    }

    class ThreadJoc extends Thread {
        @Override
        public void run() {
            while (lifeNinja >= 1) {
                actualitzaMoviment();
            }
        }
    }
}
