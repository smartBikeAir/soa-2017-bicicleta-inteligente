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

public class RealTimeActivity extends AppCompatActivity  implements SensorEventListener {

    private Chronometer chronometer;
    private Button geolocationButton;
    private Button endTripButton;
    private TextView velocimeterTextView;
    private Sensor gyroscope;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time);

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
    }

    private void saveTrip() {
        long elapsedSeconds = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
        Trip trip = new Trip(elapsedSeconds);
        TripsManager.getInstance().saveTrip(trip);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int velocity = intent.getIntExtra("velocity", 0);
            velocimeterTextView.setText(Integer.toString(velocity)+ " km/h");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }


    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

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

            Log.d("HomeActivity", "GYROSCOPE: x=" + x + " y=" + y + " z=" + z);

        }
    }

    public void lanzarDialogoEmergencia(){
        Intent i = new Intent(this, DialogoEmergencia.class);
        startActivity(i);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}

