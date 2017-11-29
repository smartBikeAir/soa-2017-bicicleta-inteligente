package ar.so_unlam.edu.sba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import static ar.so_unlam.edu.sba.AppConstants.RECIEVE_MESSAGE;

public class FragmentConfiguracion extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String KEY_PREF_LUCES= "pref_luces";
    public static final String KEY_PREF_PROXIMIDAD="pref_proximidad";

    private static final AppService APP_SERVICE = AppServiceImpl.getInstance();

    private ConnectedThread connectedThread = ((ConnectedThread)APP_SERVICE.getConnectedThread());

    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.activity_preferences);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        PreferenceManager preferenceManager = getPreferenceManager();

        if (key.equals(KEY_PREF_LUCES))
        {
              if(preferenceManager.getSharedPreferences().getBoolean(KEY_PREF_LUCES,true)){

                  String message =  AppConstants.automaticLightDisabled+AppConstants.END_CMD_CHAR;
                  connectedThread.write(message);
                  preferenceManager.findPreference(KEY_PREF_LUCES).setDefaultValue(false);

              }else {

                  String message =  AppConstants.automaticLightEnabled+AppConstants.END_CMD_CHAR;
                  connectedThread.write(message);
                  preferenceManager.findPreference(KEY_PREF_LUCES).setDefaultValue(true);

              }
          }


        if (key.equals(KEY_PREF_PROXIMIDAD))
        {
            if(preferenceManager.getSharedPreferences().getBoolean(KEY_PREF_PROXIMIDAD,true)){

                String message =  AppConstants.nearObjectDisabled+AppConstants.END_CMD_CHAR;
                connectedThread.write(message);
                preferenceManager.findPreference(KEY_PREF_PROXIMIDAD).setDefaultValue(false);

            }else {
                String message =  AppConstants.nearObjectEnabled+AppConstants.END_CMD_CHAR;
                connectedThread.write(message);
                preferenceManager.findPreference(KEY_PREF_PROXIMIDAD).setDefaultValue(true);

            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        connectedThread.setHandler(handler);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }




}