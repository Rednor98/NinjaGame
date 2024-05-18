package com.example.ninjagame;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicManager {
    private static MusicManager instance;
    private static MediaPlayer mediaPlayer;
    private boolean isMusicEnabled;

    private MusicManager() { }

    public static synchronized MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public void initialize(Context context, int resourceId) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resourceId);
            mediaPlayer.setLooping(true);
        }
    }

    public void startMusic(boolean isMusicEnabled) {
        this.isMusicEnabled = isMusicEnabled;
        if (isMusicEnabled && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public static void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public boolean isMusicEnabled() {
        return isMusicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        isMusicEnabled = musicEnabled;
    }
}

