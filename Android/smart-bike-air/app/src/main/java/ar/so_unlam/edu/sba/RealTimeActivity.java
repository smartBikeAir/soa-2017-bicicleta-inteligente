package ar.so_unlam.edu.sba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Spliterator;
import java.util.StringTokenizer;

import static ar.so_unlam.edu.sba.AppConstants.RECIEVE_MESSAGE;

public class RealTimeActivity extends AppCompatActivity  implements SensorEventListener {

    private Chronometer chronometer;
    private Button geolocationButton;
    private Button endTripButton;
    private TextView velocimeterTextView;
    private Sensor gyroscope;
    private Sensor proximity;
    private TextView objetocercano;

    private SensorManager sensorManager;

    private static final AppService APP_SERVICE = AppServiceImpl.getInstance();

    private ConnectedThread connectedThread = ((ConnectedThread)APP_SERVICE.getConnectedThread());

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String[] arrayMsg;
            String strIncom = "";
            try {
                switch (msg.what) {
                    case RECIEVE_MESSAGE: // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        arrayMsg = new String(readBuf, 0, msg.arg1).split("\r\n"); // create string from bytes array, and split msgs
                        StringTokenizer st = new StringTokenizer(arrayMsg[0], "|");

                        for (int i = 0; i < arrayMsg.length; i++) {

                            strIncom = st.nextToken();
                            if (!strIncom.isEmpty()) {


                                if(strIncom.equals(AppConstants.nearObject)) {

                                    objetocercano.setTextColor(getApplication().getResources().getColor(R.color.alerta));
                                    objetocercano.setText("ALERTA !! OBJETO CERCANO!!!!");
                                }
                                if(strIncom.equals(AppConstants.velocity)) {

                                        Intent intent = new Intent("new-velocity-event");
                                        intent.putExtra("velocity", st.nextToken() ); // value en km/h
                                        LocalBroadcastManager.getInstance(RealTimeActivity.this).sendBroadcast(intent);

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time);

        // Chronometer
        chronometer = (Chronometer)findViewById(R.id.chronometer);
        chronometer.start();

        objetocercano = (TextView)findViewById(R.id.objetocercano);

        // User location
        geolocationButton = (Button)findViewById(R.id.geolocationButton);
        geolocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RealTimeActivity.this, MapsActivity.class);
                startActivity(intent);

            }
        });

        // End trip
        endTripButton = (Button)findViewById(R.id.endTripButton);
        endTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrip();
                finish();
            }
        });

        velocimeterTextView = (TextView)findViewById(R.id.velocimeterTextView);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("new-velocity-event"));

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    private void saveTrip() {
        long elapsedSeconds = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
        Trip trip = new Trip(elapsedSeconds);
        TripsManager.getInstance().saveTrip(trip);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String velocity = intent.getStringExtra("velocity");
            velocimeterTextView.setText(velocity + " km/h");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }


    protected void onResume() {
        super.onResume();
        if (connectedThread != null) {
            connectedThread.setHandler(handler);
        }
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE){

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            if(y > 7 || z > 7){

                Log.d("RealTimeActivity", "GYROSCOPE: Lanzo Dialogo");
                lanzarDialogoEmergencia();

            }
        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY){

            float x = sensorEvent.values[0];
            /*
                El sensor de proximidad normalmente tiene a su valor x como el valor más
                alto posible (maximum range). Si en algún momento su valor es menor, significa
                que tenemos un objeto cercano.
             */
            if (x < proximity.getMaximumRange()) {
                Intent intent = new Intent(RealTimeActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        }
    }

    public void lanzarDialogoEmergencia(){
        Intent i = new Intent(this, DialogoEmergencia.class);
        startActivity(i);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}

