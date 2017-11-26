package ar.so_unlam.edu.local_sensors;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ar.so_unlam.edu.sba.AppConstants;
import ar.so_unlam.edu.sba.AppService;
import ar.so_unlam.edu.sba.AppServiceImpl;

/**
 * Created by A646241 on 18/11/2017.
 */

public class SensorManagerService extends IntentService implements SensorEventListener {

    private SensorManager sensorManager;

    private ResultReceiver receiver;

    private float lastValueX;
    private float lastValueY;
    private float lastValueZ;

    private static final int AXIS_X = 0;
    private static final int AXIS_Y = 1;
    private static final int AXIS_Z = 2;

    private long lastUpdate = 0L;

    private AtomicInteger atomicInteger;
    private static final AppService APP_SERVICE = AppServiceImpl.getInstance();

    private static final int SHAKE_THRESHOLD = 1300;

    private static final int SENSOR_SENSITIVITY = 0;


    public SensorManagerService() {
        super(SensorManagerService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        Log.d("MyService", "onCreate");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);          // get an instance of the SensorManager class, lets us access sensors.
        atomicInteger = APP_SERVICE.getAtomicInteger();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("MyService", "onStartCommand");
        receiver = intent.getParcelableExtra("receiver");

        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if (!sensorList.isEmpty()) {
            Sensor acelerometerSensor = sensorList.get(0);
            sensorManager.registerListener(this, acelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        sensorList = sensorManager.getSensorList(Sensor.TYPE_PROXIMITY);

        if (!sensorList.isEmpty()) {
            Sensor proximitySensor = sensorList.get(0);
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_UI);
        }

        sensorList = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);

        if (!sensorList.isEmpty()) {
            Sensor gyroscope = sensorList.get(0);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //    // if sensor value is changes, change the values in the respective textview.
    public void onSensorChanged(SensorEvent event) {
        float currentValueX;
        float currentValueY;
        float currentValueZ;
        synchronized (atomicInteger) {
            while (atomicInteger.get() == 0 && APP_SERVICE.getDeviceStatus() == AppConstants.STOPPED) {
                try {
                    atomicInteger.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                // PREGUNTAR SI EL USUARIO QUIERE HABILITAR EL ESTADO REAL-TIME
                float[] values = event.values;
                long curTime = System.currentTimeMillis();
                // only allow one update every 100ms.
                if ((curTime - lastUpdate) > 100) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;

                    currentValueX = values[AXIS_X];
                    currentValueY = values[AXIS_Y];
                    currentValueZ = values[AXIS_Z];
                    float sumCurrentValues = (currentValueX + currentValueY + currentValueZ);
                    float sumLastValues = (lastValueX + lastValueY + lastValueZ);
                    float speed = Math.abs(sumCurrentValues - sumLastValues) / diffTime * 10000;

                    if (speed > SHAKE_THRESHOLD) {
                        //sensorAppService.getSensorShake().setText("SensorShake_Value: " + speed);
                        //ConnectedThread connectedThread = APP_SERVICE.getConnectedThread();
                        // Activa la placa externa (Arduino)
                        if (APP_SERVICE.getDeviceStatus() == AppConstants.STOPPED) {
                            receiver.send(AppConstants.RUNNING, Bundle.EMPTY);
                        }
                        //Toast.makeText("text", "shake detected w/ speed: " + speed, Toast.LENGTH_SHORT).show();
                    }
                    lastValueX = currentValueX;
                    lastValueY = currentValueY;
                    lastValueZ = currentValueZ;
                }
            } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                // abrir la activity del Maps para mostrar ubicación.
                float currentReader = event.values[0];
                if (currentReader == SENSOR_SENSITIVITY) { //near
                    //sensorAppService.getSensorProximity().setText("SensorProximity_Value: Near - " + currentReader);
                    if (APP_SERVICE.getMapStatus() == AppConstants.MAP_STATUS_HIDDEN) {
                        Toast.makeText(SensorManagerService.this, "Abriendo Ubicación en Mapa!", Toast.LENGTH_SHORT).show();
                        APP_SERVICE.setMapStatus(AppConstants.MAP_STATUS_SHOWN);
                        receiver.send(AppConstants.CHANGE_MAP_STATUS, Bundle.EMPTY);
                    }
                } else {
                    Log.d("SENSOR_MANAGER", "Proximity is far...");
                    //sensorAppService.getSensorProximity().setText("SensorProximity_Value: Far - " + currentReader);
                    //receiver.send(4, Bundle.EMPTY);
                    //Toast.makeText(SensorManagerService.this, "Lejos !!!", Toast.LENGTH_SHORT).show();
                }
            } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                Log.d("HomeActivity", "GYROSCOPE: x=" + x + " y=" + y + " z=" + z);

            }
            if (APP_SERVICE.getDeviceStatus() == AppConstants.RUNNING && atomicInteger.get() > 0) {
                atomicInteger.incrementAndGet();
            }
            atomicInteger.notifyAll();
        }

        /* unregister if we just want one result. */
//        sensorManager.unregisterListener(this);

    }
}
