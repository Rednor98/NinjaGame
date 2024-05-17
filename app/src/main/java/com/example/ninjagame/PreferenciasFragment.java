package com.example.ninjagame;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class PreferenciasFragment extends PreferenceFragmentCompat {

    EditTextPreference textPreference = findPreference("prefEnemigos");
    Preference preference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        if (textPreference != null) {
            textPreference.setOnBindEditTextListener((editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER)));
        }

        preference = findPreference("prefEnemigos");
        if (preference != null) {
            preference.setOnPreferenceChangeListener((preference, newValue) -> {
                int newValueInt = Integer.parseInt((String) newValue);
                if (newValueInt < 3 || newValueInt > 50) {
                    Context context = requireContext();
                    Toast.makeText(context, "El valor debe estar entre 3 y 50", Toast.LENGTH_SHORT).show();

                    return false;
                }
                return true;
            });
        }

    }
}