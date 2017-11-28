package ar.so_unlam.edu.sba;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    private static final int googleMapZoom = 16;

    // Atributos asociados al sensor de proximidad.
    private Sensor proximity;
    private SensorManager sensorManager;
    private long proximitySensorTimestamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtenemos el fragment a partir de un xml (layout).
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Obtenemos el mapa de forma asincrónica. Una vez que esté listo,
        // se va a llamar a: public void onMapReady(GoogleMap googleMap).
        // Como vemos, por parámetro recibimos al mapa propiamente dicho.
        mapFragment.getMapAsync(this);

        // No queremos que el SO apague la pantalla automáticamente. Setteamos que se mantenga
        // encendida. Sólo el usuario podría apagarla.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Inicializamos atributos asociados a los sensores.
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // Timestamp compartido con la pantalla RealTimeActivity.
        proximitySensorTimestamp = getIntent().getLongExtra("proximity_timestamp", 0);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Necesariamente el usuario tuvo que haber dado permisos de ACCESS_FINE_LOCATION (alta
        // precisión).
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Habilitamos capa "My location". Este habilita un botón de "mi ubicación" que se verá
            // en la esquina superior derecha del mapa.
            mMap.setMyLocationEnabled(true);

            // Obtenemos referencia a LocationManager para así poder hacer uso de los servicios de
            // localización del sistema.
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Instancia necesaria para poder obtener un provider.
            Criteria criteria = new Criteria();

            // Obtendo un proveedor para mi localización.
            String provider = locationManager.getBestProvider(criteria, true);

            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Movemos el mapa en mi ubicación.
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), googleMapZoom));
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY){

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

                Intent result = new Intent();
                result.putExtra("proximity_timestamp", proximitySensorTimestamp);
                setResult(1, result);
                this.finish();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
