package ar.so_unlam.edu.sba;

import android.content.BroadcastReceiver;
import android.content.Context;

import android.Manifest;

import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import android.support.v4.content.LocalBroadcastManager;

import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

import static ar.so_unlam.edu.sba.AppConstants.RECIEVE_MESSAGE;


public class HomeActivity extends AppCompatActivity implements SensorEventListener {

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private Button startTripButton;
    private Button alarmaButton;
    private Button settingsButton;
    private Sensor acceletometer;
    private SensorManager sensorManager;

    private long acceletometerSensorTimestamp_inicial = 0;
    private long acceletometerSensorTimestamp = 0;

    private static final AppService APP_SERVICE = AppServiceImpl.getInstance();

    private ConnectedThread connectedThread = ((ConnectedThread)APP_SERVICE.getConnectedThread());


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String[] arrayMsg;
            String strIncom = "";
            Log.d("ArduinoCon_BT", "MSG: " + msg.arg1);
            try {
                switch (msg.what) {
                    case RECIEVE_MESSAGE: // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        arrayMsg = new String(readBuf, 0, msg.arg1).split("\r\n"); // create string from bytes array, and split msgs
                        for (int i = 0; i < arrayMsg.length; i++) {
                            strIncom = arrayMsg[i].replaceAll("\n", "").replaceAll("\r", "");
                            if (!strIncom.isEmpty()) {

                                if(strIncom.equals(AppConstants.startedTrip)) {

                                    Intent intent = new Intent(HomeActivity.this, RealTimeActivity.class);
                                    startActivity(intent);
                                }
                                if(strIncom.equals(AppConstants.activateAlarm)){

                                    Intent intent = new Intent("new-alarma-event");
                                    intent.putExtra("alarma", String.valueOf(AppConstants.ACTIVATE_ALARM) );
                                    LocalBroadcastManager.getInstance(HomeActivity.this).sendBroadcast(intent);
                                }
                                if(strIncom.equals(AppConstants.deactivateAlarm)){

                                    Intent intent = new Intent("new-alarma-event");
                                    intent.putExtra("alarma", String.valueOf(AppConstants.DEACTIVATE_ALARM) );
                                    LocalBroadcastManager.getInstance(HomeActivity.this).sendBroadcast(intent);


                                }
                                if(strIncom.equals(AppConstants.turnAlarmOn)){
                                    lanzarDialogoAlarma();
                                }

                                Log.d("ArduinoCon_BT", "MSG_ARDUINO-BOARD: " + strIncom);
                            }
                        }
                }
            } catch (Exception e) {
                String msj = "In handleMessage(), fail process info: " + e.getMessage() + "." + strIncom;
                Toast.makeText(getBaseContext(), "Fatal Error" + " - " + msj, Toast.LENGTH_LONG).show();
                finish();
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        startTripButton = (Button)findViewById(R.id.startTripButton);
        alarmaButton = (Button)findViewById(R.id.alarmaButton);
        settingsButton = (Button)findViewById(R.id.settingsButton);

        startTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, RealTimeActivity.class);
                startActivity(intent);
                connectedThread.write(AppConstants.startedTrip+"\n");
            }
        });

        /* Si es la primera vez que accedemos a la app cargamos los valores por defecto de configuracion*/
        PreferenceManager.setDefaultValues(this,R.xml.activity_preferences, false);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ConfiguracionActivity.class);
                startActivity(intent);
            }
        });

        alarmaButton.setText(APP_SERVICE.getAlarmaStatus());

        if(APP_SERVICE.getAlarmaStatus().equals(AppConstants.DEACTIVATE_ALARM)){
            alarmaButton.setTextColor(getApplication().getResources().getColor(R.color.colorPrimaryDark));
            startTripButton.setText("START");
            startTripButton.setTextColor(getApplication().getResources().getColor(R.color.colorPrimaryDark));
        } else{
            alarmaButton.setTextColor(getApplication().getResources().getColor(R.color.colorAccent));
            startTripButton.setEnabled(false);
            startTripButton.setText("NONSTART");
            startTripButton.setTextColor(getApplication().getResources().getColor(R.color.colorAccent));
        }


        alarmaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(alarmaButton.getText().toString().equals(AppConstants.DEACTIVATE_ALARM)){

                    connectedThread.write(AppConstants.activateAlarm+"\n");
                    Intent intent = new Intent("new-alarma-event");
                    intent.putExtra("alarma", String.valueOf(AppConstants.ACTIVATE_ALARM) );
                    LocalBroadcastManager.getInstance(HomeActivity.this).sendBroadcast(intent);

                }else{

                    connectedThread.write(AppConstants.deactivateAlarm+"\n");
                    Intent intent = new Intent("new-alarma-event");
                    intent.putExtra("alarma", String.valueOf(AppConstants.DEACTIVATE_ALARM) );
                    LocalBroadcastManager.getInstance(HomeActivity.this).sendBroadcast(intent);

                }


            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("new-alarma-event"));

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        acceletometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityCompat.requestPermissions(HomeActivity.this,
                new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_FINE_LOCATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (connectedThread != null) {
            connectedThread.setHandler(handler);
        }
        sensorManager.registerListener(this, acceletometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String alarma = intent.getStringExtra("alarma");

            alarmaButton.setText(alarma);
            APP_SERVICE.setAlarmaStatus(alarma);

            if(alarma.equals(AppConstants.DEACTIVATE_ALARM)){
                alarmaButton.setTextColor(getApplication().getResources().getColor(R.color.colorPrimaryDark));
                startTripButton.setEnabled(true);
                startTripButton.setText("START");
                startTripButton.setTextColor(getApplication().getResources().getColor(R.color.colorPrimaryDark));
            } else{
                alarmaButton.setTextColor(getApplication().getResources().getColor(R.color.colorAccent));
                startTripButton.setEnabled(false);
                startTripButton.setText("NONSTART");
                startTripButton.setTextColor(getApplication().getResources().getColor(R.color.colorAccent));
            }

        }
    };


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            float x = sensorEvent.values[0];
            float z = sensorEvent.values[2];

            Log.d("Arduino", "X: "+x+"Z: "+z);



            if(    (z > AppConstants.VALUE_MAX_ACCELEROMETER_Z || z < AppConstants.VALUE_MIN_ACCELEROMETER_Z)
                && (x > AppConstants.VALUE_MAX_ACCELEROMETER_X || x < AppConstants.VALUE_MIN_ACCELEROMETER_X)
                && !APP_SERVICE.getAlarmaStatus().equals(AppConstants.ACTIVATE_ALARM) ){

                    if(acceletometerSensorTimestamp_inicial <= 0){
                        acceletometerSensorTimestamp_inicial = Calendar.getInstance().getTime().getTime();
                    }else {
                        acceletometerSensorTimestamp = Calendar.getInstance().getTime().getTime();
                    }

                    if(acceletometerSensorTimestamp - acceletometerSensorTimestamp_inicial > 3000) {

                        Intent intent = new Intent(HomeActivity.this, RealTimeActivity.class);
                        startActivity(intent);

                        // envio msj arduino actualizando su modo a viaje
                        connectedThread.write(AppConstants.startedTrip + "\n");

                        acceletometerSensorTimestamp_inicial = 0;
                    }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void lanzarDialogoAlarma(){
        Intent i = new Intent(this, DialogoAlarmaSonando.class);
        startActivity(i);
    }



}
