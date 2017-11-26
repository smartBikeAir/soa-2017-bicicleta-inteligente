package ar.so_unlam.edu.sba;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by irmab on 7/11/2017.
 */

public class FragmentConfiguracion extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String KEY_PREF_DISTANCIA= "pref_unidad_distancia";
    public static final String KEY_PREF_TIEMPO = "pref_unidad_tiempo";
    public static final String KEY_PREF_LUCES= "pref_luces";
    public static final String KEY_PREF_ALARMA= "pref_alarma";
    public static final String KEY_PREF_PROXIMIDAD="pref_proximidad";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.activity_preferences);

      //Debemos preguntar antes de mostrar la actividad si tenemos conexion a bluetooth.

        PreferenceManager preferenceManager = getPreferenceManager();
        // Si  no tenemos coneccion
        preferenceManager.findPreference(KEY_PREF_PROXIMIDAD).setEnabled(false);
        // si tenemos coneccion
        preferenceManager.findPreference(KEY_PREF_ALARMA).setEnabled(true);

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(KEY_PREF_DISTANCIA))
        {
            // Set summary to be the user-description for the selected value
            Preference Pref = findPreference(key);
                Pref.setSummary(sharedPreferences.getString(key, ""));
                Log.i("", "Modifique Distancia: " + Pref.getKey());
        }

        if (key.equals(KEY_PREF_TIEMPO))
        {


        }

        if (key.equals(KEY_PREF_ALARMA))
        {


        }

        if (key.equals(KEY_PREF_LUCES))
        {


        }

        if (key.equals(KEY_PREF_PROXIMIDAD))
        {


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);


    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }




}