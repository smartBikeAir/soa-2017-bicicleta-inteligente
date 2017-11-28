package ar.so_unlam.edu.sba;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static ar.so_unlam.edu.sba.AppConstants.RECIEVE_MESSAGE;


public class HomeActivity extends AppCompatActivity implements SensorEventListener {

    private Button startTripButton;
    private Button alarmaButton;
    private Button settingsButton;

    private Sensor acceletometer;

    private SensorManager sensorManager;

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

                                    // Envío un Mensaje a la Arduino
                                    Intent intent = new Intent(HomeActivity.this, RealTimeActivity.class);
                                    startActivity(intent);
                                }
                                if(strIncom.equals(AppConstants.activateAlarm)){



                                }
                                if(strIncom.equals(AppConstants.deactivateAlarm)){


                                }
                                if(strIncom.equals(AppConstants.turnAlarmOn)){


                                }

                                Log.d("ArduinoCon_BT", "MSG_ARDUINO-BOARD: " + strIncom);
                                Toast.makeText(getBaseContext(), "MSG_ARDUINO-BOARD: " + strIncom, Toast.LENGTH_LONG).show();
                                // Envío un Mensaje a la Arduino
                                connectedThread.write("ok\n");


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

        alarmaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(alarmaButton.getText()=="DEACTIVATE"){
                    connectedThread.write(AppConstants.activateAlarm+"\n");
                    alarmaButton.setText("ACTIVATE");
                    alarmaButton.setTextColor(getApplication().getResources().getColor(R.color.colorAccent));
                }else{

                    connectedThread.write(AppConstants.deactivateAlarm+"\n");
                    alarmaButton.setText("DEACTIVATE");
                    alarmaButton.setTextColor(getApplication().getResources().getColor(R.color.colorPrimaryDark));

                }


            }
        });

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        acceletometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            Log.d("HomeActivity", " z:"+z);
            if(z > 10 || z < -10){
                Log.d("HomeActivity", "ACELEROMETRO: ACTIVO REALTIME");
                // Envío un Mensaje a la Arduino
                Intent intent = new Intent(HomeActivity.this, RealTimeActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
