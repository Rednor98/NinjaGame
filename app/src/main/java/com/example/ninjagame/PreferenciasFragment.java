package com.example.ninjagame;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.text.InputType;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class PreferenciasFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        EditTextPreference textPreference = findPreference("prefEnemigos");
        if(textPreference !=null){
            textPreference.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        }
    }
}
