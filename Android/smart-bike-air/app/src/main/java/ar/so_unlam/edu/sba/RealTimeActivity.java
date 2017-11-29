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
import java.util.Calendar;


import static ar.so_unlam.edu.sba.AppConstants.RECIEVE_MESSAGE;

public class RealTimeActivity extends AppCompatActivity  implements SensorEventListener {

    private Chronometer chronometer;

    private Button geolocationButton;
    private Button endTripButton;

    private TextView velocimeterTextView;
    private TextView distanceTextView;

    private Sensor gyroscope;
    private Sensor proximity;

    private SensorManager sensorManager;

    private static final AppService APP_SERVICE = AppServiceImpl.getInstance();
    private ConnectedThread connectedThread = ((ConnectedThread)APP_SERVICE.getConnectedThread());

    private long proximitySensorTimestamp = 0;

    private long timestampLastVelocity;
    private double travelledDistance = 0.0;

    private int velocityAvg = 0; // m/s
    private int velocityCount = 0;

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
                        Log.d("receivedMessage", "arrayMsg " + arrayMsg[0]);
                        for (int i = 0; i < arrayMsg.length; i++) {
                            strIncom = arrayMsg[i].replaceAll("\n", "").replaceAll("\r", "");
                            Log.d("receivedMessage", "strIncom " + strIncom);
                            if (!strIncom.isEmpty()) {

                                if(strIncom.equals(AppConstants.nearObject)) {

                                    Toast.makeText(getBaseContext(), "OBJETO CERCANOOOO!!!!!!!!!!!!!: ", Toast.LENGTH_LONG).show();
                                    // Envío un Mensaje a la Arduino
                                }
                                if(strIncom.equals(AppConstants.endedTrip)) {

                                    Toast.makeText(getBaseContext(), "-------FIN VIAJE ------", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                if(Integer.valueOf(strIncom)>= AppConstants.VALUE_MSJ_VELOCITY){

                                    int velocidad = Integer.valueOf(strIncom) - AppConstants.VALUE_MSJ_VELOCITY;

                                    Intent intent = new Intent("new-velocity-event");
                                    intent.putExtra("velocity", String.valueOf(velocidad) );
                                    LocalBroadcastManager.getInstance(RealTimeActivity.this).sendBroadcast(intent);

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time);

        timestampLastVelocity = SystemClock.elapsedRealtime()/1000;
        distanceTextView = (TextView)findViewById(R.id.distance);

        // Chronometer
        chronometer = (Chronometer)findViewById(R.id.chronometer);
        chronometer.start();

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
                String message = AppConstants.endedTrip+AppConstants.END_CMD_CHAR;
                connectedThread.write(message);
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String velocity = intent.getStringExtra("velocity");

            // Si es un mensaje de velocidad válido
            if(velocity != null) {
                // Actualizo display de velocidad actual
                velocimeterTextView.setText(velocity + " km/h");
                Log.d("velocity", velocity);

                // Tomo el tiempo de recepción del mensaje (en segundos)
                final long timestampCurrentVelocity = SystemClock.elapsedRealtime()/1000;

                // Calculo la diferencia respecto de tiempo del último mensaje de velocidad (en segundos)
                final long timeSinceLastMessage = timestampCurrentVelocity - timestampLastVelocity;
                Log.d("velocity", "timeSinceLastMessage " + timeSinceLastMessage);

                // Paso la velocidad a Km/seg
                double velocityInKmS = Double.valueOf(velocity) / 3600;
                Log.d("velocity", "velocityInKms " + velocityInKmS);

                // Calculo distancia recorrida desde la última velocidad recibida
                double deltaDistance = velocityInKmS * timeSinceLastMessage;
                Log.d("velocity", "deltaDistance " + deltaDistance);

                // Acumulo distancia recorrida
                travelledDistance += deltaDistance;
                Log.d("velocity", "travelledDistance " + travelledDistance);

                // Actualizo el tiempo del último mensaje de velocidad recibido
                timestampLastVelocity = timestampCurrentVelocity;

                distanceTextView.setText(String.format("%.2f",travelledDistance) + " km");
                Log.d("velocity", "distanceTextView.getText " + distanceTextView.getText());
            } else {
                // Hubo un problema en la recepción de la velocidad
                velocimeterTextView.setText("--");
            }
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

            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            if(y > AppConstants.VALUE_MAX_GIROSCOPE_Y|| z > AppConstants.VALUE_MAX_GIROSCOPE_Z){
                lanzarDialogoEmergencia();
            }
        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {

            float x = sensorEvent.values[0];

            /*
                El sensor de proximidad normalmente tiene a su valor x como el valor más
                alto posible (maximum range). Si en algún momento su valor es menor, significa
                que tenemos un objeto cercano.
             */
            long differenceBetweenDates = Calendar.getInstance().getTime().getTime() - proximitySensorTimestamp;
            if (x < proximity.getMaximumRange() && differenceBetweenDates > 1500) {

                // Guardamos marca de tiempo asociada a la última vez que el sensor
                // detectó objeto cercano. Es necesario que pasen 1500 ms desde la última
                // vez que se detectó uno.
                proximitySensorTimestamp = Calendar.getInstance().getTime().getTime();

                Intent intent = new Intent(RealTimeActivity.this, MapsActivity.class);
                intent.putExtra("proximity_timestamp", proximitySensorTimestamp);
                startActivityForResult(intent, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            proximitySensorTimestamp = data.getLongExtra("proximity_timestamp", 0);
        }
    }

    public void lanzarDialogoEmergencia(){
        Intent i = new Intent(this, DialogoEmergencia.class);
        startActivity(i);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}

