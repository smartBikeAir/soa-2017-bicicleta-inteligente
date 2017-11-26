package ar.so_unlam.edu.sba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ar.so_unlam.edu.local_sensors.SensorManagerReceiver;
import ar.so_unlam.edu.local_sensors.SensorManagerService;

import static ar.so_unlam.edu.sba.AppConstants.RECIEVE_MESSAGE;

public class HomeActivity extends AppCompatActivity implements SensorManagerReceiver.Receiver {

    private Button startTripButton;
    private Button alarmaButton;
    private Button settingsButton;
    private Button buttonConnect;

    private SensorManagerReceiver receiver;

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
                        for (int i = 0; i < arrayMsg.length; i++) {
                            strIncom = arrayMsg[i].replaceAll("\n", "").replaceAll("\r", "");
                            if (!strIncom.isEmpty()) {

                                if(strIncom.equals("1")) {

                                    // Envío un Mensaje a la Arduino
                                    connectedThread.write("Start Trip OK..\n");
                                    Intent intent = new Intent(HomeActivity.this, RealTimeActivity.class);
                                    startActivity(intent);
                                }

                                Log.d("ArduinoCon_BT", "MSG_ARDUINO-BOARD: " + strIncom);
                                Toast.makeText(getBaseContext(), "MSG_ARDUINO-BOARD: " + strIncom, Toast.LENGTH_LONG).show();
                                // Envío un Mensaje a la Arduino
                                connectedThread.write("Hello SBA-2017 BLUETOOTH. SOA-2017...\n");


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
        buttonConnect = (Button)findViewById(R.id.buttonConnect);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientSocketTask clientSocketTask;
                /*String address = editTextAddress.getText().toString();
                int port = Integer.parseInt(editTextPort.getText().toString());
                clientSocketTask = new ClientSocketTask(address, port, textResponse);
                clientSocketTask.execute();*/
            }
        });

        startTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, RealTimeActivity.class);
                startActivity(intent);
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

                if(alarmaButton.getText()=="DEACTIVATED"){
                    connectedThread.write("3#");
                    alarmaButton.setText("Active");
                    alarmaButton.setTextColor(getApplication().getResources().getColor(R.color.colorAccent));
                }else{

                    connectedThread.write("4#");
                    alarmaButton.setText("DEACTIVATED");
                    alarmaButton.setTextColor(getApplication().getResources().getColor(R.color.colorPrimaryDark));

                }


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        connectedThread.setHandler(handler);

        // Inicia el servicio del Control de los sensores
        // Se podría utilizar bindService, por si ya se encuentra en ejecución.
        try {
            receiver = new SensorManagerReceiver(new Handler());
            receiver.setReceiver(this);
            Intent sensorMngIntent = new Intent(Intent.ACTION_SYNC, null, this, SensorManagerService.class);
            sensorMngIntent.putExtra("receiver", receiver);
            startService(sensorMngIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    /**
     * Acá Realizo las operaciones provenientes de los eventos de los
     * sensores de android
     * */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case AppConstants.STOPPED:
                // askIfRealTimeControlON();
                Log.d("MSG_FROM_SENSOR_MANAGER", "run real time?");
                break;
            case AppConstants.RUNNING:
                Log.d("MSG_FROM_SENSOR_MANAGER", "stop real time or another action?");
                // anotherActionWhenRealTimeOn();
                break;
            case AppConstants.CHANGE_MAP_STATUS:
                Log.d("MSG_FROM_SENSOR_MANAGER", "show MapsActivity?");
                Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
                startActivity(intent);
        }
    }
}
